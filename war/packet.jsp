<%@page import="com.blogspot.bwgypyth.lotro.json.PacketConverter"%>
<%@page import="java.util.List"%>
<%@page import="javax.persistence.EntityManager"%>
<%@page import="com.google.appengine.api.datastore.Key"%>
<%@page import="com.blogspot.bwgypyth.lotro.model.Analysis"%>
<%@page import="com.blogspot.bwgypyth.lotro.json.AnalysisConverter"%>
<%@page import="com.blogspot.bwgypyth.lotro.model.Packet"%>
<%@page import="com.blogspot.bwgypyth.lotro.EMF"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
EntityManager em = EMF.get().createEntityManager();
try {
	Packet packet;
	if (request.getParameter("packet") != null) { 
		packet = em.find(Packet.class, Long.valueOf(request.getParameter("packet")));
	} else {
	packet = (Packet) em.createQuery("select packet from Packet packet").setMaxResults(1).getSingleResult();
	}
	Analysis analysis = new Analysis();
	if (request.getParameter("analysis") != null) {
		analysis = em.find(Analysis.class, Long.valueOf(request.getParameter("analysis")));
		if (!analysis.getPacket().getKey().equals(packet.getKey())) {
			analysis = new Analysis();
		}
	} else if (!packet.getAnalyses().isEmpty()) {
		analysis = packet.getAnalyses().get(0);
	}
	if (analysis.getKey() == null) {
		analysis.setName("New Analysis");
		packet.getAnalyses().add(analysis);
		analysis.setPacket(packet);
	}
	pageContext.setAttribute("packet", packet);
	pageContext.setAttribute("analysis", analysis);
%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title></title>
		<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.2/themes/smoothness/jquery-ui.css" />
		<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
		<script src="http://code.jquery.com/ui/1.10.2/jquery-ui.js"></script>
		<style type="text/css">
			div#packet_offset, div#packet_hex, div#packet_decoded {
				font-family: monospace;
				float: left;
				line-height: 150%;
				white-space: nowrap;
			}
			div#legend {
				clear: both;
				margin-top: 5px;
			}
			div#packet_decoded, div#packet_hex {
				margin-left: 8px;
			}
<%
if (UserServiceFactory.getUserService().getCurrentUser() != null) {
%>
			span.decoded_or_hex_element_selected {
				border: 1px dashed #333333;
				margin: -1px;
			}
			span.decoded_or_hex_element {
				cursor: pointer;
			}
			[contenteditable] {
				outline: 1px dotted #CCCCCC;
			}
			input.duplicate {
				border: 1px solid red;
			}

			.ui-widget {
				font-size: 0.8em
			}
			div#analysis_entry td.description {
				font-size: 0.7em;
			}
			div#analysis_entry th {
				text-align: left;
			}

			/* Workaround: autocomplete is behind the dialog */
			ul.ui-autocomplete {
				z-index: 101;
			}
<%
}
%>
		</style>
		<link rel="stylesheet" href="css/file.css">
	</head>
	<body>
		<%@ include file="navigation.jsp" %>
		<script type="text/javascript">
			function padLeadingZeros(number, width) {
				var tmp = '' + number;
				while (tmp.length < width) {
					tmp = '0' + tmp;
				}
				return tmp;
			}

			function generateCss(name, color, foregroundColor) {
				return '.' + name + ' { margin-left: -1px; margin-right: -1px; border: 1px solid ' + color + '; color: ' + color + '; }\n' + 
				'.' + name + '_hovered { background-color: ' + color + '; color: ' + foregroundColor + '; }\n';
			}

			function getForgroundColor(foregroundColor) {
				if (foregroundColor && foregroundColor != '') {
					return foregroundColor;
				}
				return '#000000';
			}

			function registerHovering(element) {
				console.log(element);
				$(element).on('mouseover', function(event) {
					$($(this).attr('class').split(' ')).each(function(index1, element1) {
						if (element1.indexOf('hover') == -1) {
							$('.' + element1).each(function (index2, element2) {
								$(element2).addClass(element1 + '_hovered');
							});
						}
					});
					event.stopPropagation();
				});
				$(element).on('mouseout', function(event) {
					$($(this).attr('class').split(' ')).each(function(index1, element1) {
						if (element1.indexOf('hover') == -1) {
							$('.' + element1).each(function (index2, element2) {
								$(element2).removeClass(element1 + '_hovered');
							});
						}
					});
					event.stopPropagation();
				});
			}

			function renderPacket() {

				// Generate CSS classes for all known analysis entries
				var generatedCss = '<style type="text/css">';
				$(analysis.analysisEntries).each(function(index, element) {
					if (element.color) {
						var foregroundColor = getForgroundColor(element.foregroundColor);
						generatedCss += generateCss(element.name, element.color, foregroundColor);
					}
				});
				$(generatedCss + '</style>').appendTo('head');

				var packet_offset, offset, packet_hex, packet_decoded, legend;
				offset = 0;
				packet_offset = 'Offset(h)<br/>' + padLeadingZeros(offset, 8);
				packet_hex = '00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F<br/>';
				packet_decoded = '<br/>';
				legend = '';
				for (var i = 0; i < packet.data.length; i+=2) {
					var hexOffset = padLeadingZeros((i/2).toString(16).toUpperCase(), 2);

					// Check if the current byte is a starting byte of any analysis entry
					$(analysis.analysisEntries).each(function(index, element) {
						if (element.start*2 == i) {
							packet_hex += '<span class="hoverable ' + element.name + '">';
							packet_decoded += '<span class="hoverable ' + element.name + '">';
							legend += '<div class="hoverable ' + element.name + '">' + element.description;
						}
					});

					// Render the bytes
					packet_hex += '<span class="decoded_or_hex_element" id="hex_element_' + hexOffset + '">' + packet.data.substr(i, 2) + '</span>';

					// Render the decoded bytes
					var tmp = parseInt(packet.data.substr(i, 2), 16);
					if (tmp < 33 || (tmp > 126 && tmp < 161)) {
						tmp = '.';
					} else {
						tmp = String.fromCharCode(tmp);
					}
					packet_decoded += '<span class="decoded_or_hex_element" id="decoded_element_' + hexOffset + '">' + tmp + '</span>';

					$(analysis.analysisEntries).each(function(index, element) {
						if (element.end*2 == i) {
							packet_hex += '</span>';
							packet_decoded += '</span>';
							legend += '</div>';
						}
					});

					packet_hex += ' ';

					// Add line breaks where necessary
					if (i % 32 == 30) {
						offset += 10;
						packet_offset += '<br/>' + padLeadingZeros(offset, 8);
						packet_hex += '<br/>';
						packet_decoded += '<br/>';
					}

				}

				$('#name').html('<h1><span id="packet_name">' + packet.name + '</span> - <span id="analysis_name">' + analysis.name + '</span></h1><a href="/export/packet?packet=' + packet.key + '">Export</a>');
				$('#packet_offset').html(packet_offset);
				$('#packet_hex').html(packet_hex);
				$('#packet_decoded').html(packet_decoded);
				$('#legend').html(legend);

				$('.hoverable').each(function(index, element) {
					registerHovering(element);
				});
<%
if (user != null) {
%>
				$('.decoded_or_hex_element').click(function() {
					var hex_element_current = this.id.substring(12);
					$(this).addClass("decoded_or_hex_element_selected");
					if (hex_element_start == undefined) {
						hex_element_start = hex_element_current;
					} else {
						// Make sure hex_element_start is the first element
						if (hex_element_current < hex_element_start) {
							var tmp = hex_element_current;
							hex_element_current = hex_element_start;
							hex_element_start = tmp;
						}

						$(function() {
							var analysis_entry = $('#analysis_entry');
							analysis_entry.html('<table>' +
									'<tr><th>Name*</th><td><input type="text" id="entry_name" /></td><td class="description">Unique name of this datapart within the packet (e.g. \'header0\')</td></tr>' +
									'<tr><th>Start</th><td><input type="text" disabled="disabled" id="entry_start" value="0x' + hex_element_start + '" /></td><td class="description">First element of datapart in packet</td></tr>' +
									'<tr><th>End</th><td><input type="text" disabled="disabled" id="entry_end" value="0x' + hex_element_current + '" /></td><td class="description">Last element of datapart in packet</td></tr>' +
									'<tr><th>Description*</th><td><input type="text" id="entry_description" /></td><td class="description">Human readable description of this datapart</td></tr>' +
									'<tr><th>Color*</th><td><div class="ui-widget"><input type="text" id="entry_color" /></div></td><td class="description">Color to be used for this datapart</td></tr>' +
									'<tr><th>Foregroundcolor</th><td><div class="ui-widget"><input type="text" id="entry_foregroundcolor" /></div></td><td class="description">Foregroundcolor to be used for this datapart (optional)</td></tr>' +
									'<tr><td></td><td><input type="button" onclick="submitAnalysisEntry();" value="save" /></td><td></td></tr>' +
									'</table>');

							$.widget('custom.htmlautocomplete', $.ui.autocomplete, {
								_renderItem: function(ul, item) {
									return $('<li>')
									.append($('<a>').html(item.label))
									.appendTo(ul);
								}
							});

							$('#entry_color').htmlautocomplete({
								source: "packet_ajax?operation=autocomplete_color",
								minLength: 1,
								select: function(event, ui) {
									console.log(ui);
								}
							});
							analysis_entry.dialog({
								title: 'Annotate packet',
								width: 700,
								height: 260,
								beforeClose: function() {
									$('#hex_element_' + $('#entry_start').val().substring(2)).removeClass("decoded_or_hex_element_selected");
									$('#hex_element_' + $('#entry_end').val().substring(2)).removeClass("decoded_or_hex_element_selected");
									$('#decoded_element_' + $('#entry_start').val().substring(2)).removeClass("decoded_or_hex_element_selected");
									$('#decoded_element_' + $('#entry_end').val().substring(2)).removeClass("decoded_or_hex_element_selected");
								}
							});
						});

						hex_element_start = undefined;
					}
				});
<%
}
%>

			}
<%
if (user != null) {
%>
			function submitAnalysisEntry() {
				$.ajax({
					url: "packet_ajax",
					data: {
						"operation": "create_analysisentry",
						"analysis_key": analysis.key,
						"packet_key": packet.key,
						"entry_name": $('#entry_name').val(),
						"entry_start": $('#entry_start').val(),
						"entry_end": $('#entry_end').val(),
						"entry_description": $('#entry_description').val(),
						"entry_color": $('#entry_color').val(),
						"entry_foregroundcolor": $('#entry_foregroundcolor').val(),
					}
				}).done(function(data) {
					if (data == 'ok') {
						// In case we added it remove the class so it's not
						// shown when reopening the dialog
						$('#entry_start').removeClass('duplicate');

						var startposition = $('#entry_start').val().substring(2);
						var endposition = $('#entry_end').val().substring(2);
						var newHexElement = $('<span class="hoverable ' + $('#entry_name').val() + '"></span>');
						newHexElement.insertBefore($('#hex_element_' + startposition));
						var newDecodedElement = $('<span class="hoverable ' + $('#entry_name').val() + '"></span>');
						newDecodedElement.insertBefore($('#decoded_element_' + startposition));
						for (var i = parseInt(startposition, 16); i <= parseInt(endposition, 16); i++) {
							var hexOffset = padLeadingZeros(i.toString(16).toUpperCase(), 2);
							var nextHexElement = $('#hex_element_' + hexOffset).next()[0];
							var nextDecodedElement = $('#decoded_element_' + hexOffset).next()[0];
							newHexElement.append($('#hex_element_' + hexOffset));
							newDecodedElement.append($('#decoded_element_' + hexOffset));
							if (i < parseInt(endposition, 16)) {
								if (nextHexElement instanceof HTMLBRElement) {
									newHexElement.append(nextHexElement);
									newDecodedElement.append(nextDecodedElement);
								} else {
									newHexElement.append(' ');
								}
							}
						}

						var generatedCss = '<style type="text/css">';
						var foregroundColor = getForgroundColor($('#entry_foregroundcolor').val());
						generatedCss += generateCss($('#entry_name').val(), $('#entry_color').val(), foregroundColor)
						$(generatedCss + '</style>').appendTo('head');

						// Append legend
						$('#legend').append('<div class="hoverable ' + $('#entry_name').val() + '">' + $('#entry_description').val() + '</div>')

						$('.hoverable').each(function(index, element) {
							registerHovering(element);
						});

						$('#analysis_entry').dialog('close');
					} else if (data == 'duplicate') {
						$('#entry_name').focus();
						$('#entry_name').addClass('duplicate');
					}
				});
			}
<%
}
%>

			var hex_element_start, packet, analysis;

			$(function() {
				packet = <%= new PacketConverter().toJson(packet).toString() %>;
				analysis = <%= new AnalysisConverter().toJson(analysis).toString() %>;
				renderPacket();

				$('.button_left').mouseover(function() {
					$('.button_left_img').addClass('button_left_img_hover');
				});
				$('.button_left').mouseout(function() {
					$('.button_left_img').removeClass('button_left_img_hover');
				});
				$('.button_right').mouseover(function() {
					$('.button_right_img').addClass('button_right_img_hover');
				});
				$('.button_right').mouseout(function() {
					$('.button_right_img').removeClass('button_right_img_hover');
				});
<%
if (user != null) {
%>
				$('#packet_name').click(function() {
					$(this).attr("contenteditable", "true");
				});
				$('#packet_name').blur(function() {
					var packet_name = $(this);
					$.ajax({
						url: "packet_ajax",
						data: {
							"operation": "update_packetname",
							"packet_key": packet.key,
							"packet_name": packet_name.html()
						}
					}).done(function() {
						packet_name.removeAttr("contenteditable");
					});
				});
				$('#analysis_name').click(function() {
					$(this).attr("contenteditable", "true");
				});
				$('#analysis_name').blur(function() {
					var packet_name = $(this);
					$.ajax({
						url: "packet_ajax",
						data: {
							"operation": "update_analysisname",
							"packet_key": packet.key,
							"analysis_key": analysis.key,
							"analysis_name": packet_name.html()
						}
					}).done(function() {
						packet_name.removeAttr("contenteditable");
					});
				});
<%
}
%>
			});
		</script>
		<div class="button_left button">
			<div class="button_left_img"></div>
		</div>
		<div class="button_right button">
			<div class="button_right_img"></div>
		</div>
		<div class="content">
			<div id="name"></div>
			<div id="packet_offset"></div>
			<div id="packet_hex"></div>
			<div id="packet_decoded"></div>
			<div id="legend"></div>
		</div>
		<div id="analysis_entry"></div>
	</body>
</html>
<%
} finally {
	em.close();
}
%>
