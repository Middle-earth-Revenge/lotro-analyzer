<%@page import="com.blogspot.bwgypyth.lotro.json.IncludeKey"%>
<%@page import="com.blogspot.bwgypyth.lotro.json.IncludeUserdata"%>
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
		analysis.setName("Unnamed Analysis");
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
		<title>${packet.name} - ${analysis.name}</title>
		<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.2/themes/smoothness/jquery-ui.css" />
		<script type="text/javascript" src="http://code.jquery.com/jquery-1.9.1.js"></script>
		<script type="text/javascript" src="http://code.jquery.com/ui/1.10.2/jquery-ui.js"></script>
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
			#legend div.hoverable {
				margin: 0 2px;
			}
			span.display_header {
				color: #5c5c5c;
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
				vertical-align: top;
				padding-top: 0.5em;
			}
			div#analysis_entry th {
				text-align: left;
				vertical-align: top;
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

			function integerToHexString(integer) {
				return padLeadingZeros(integer.toString(16).toUpperCase(), 4);
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
				packet_offset = '<span class="display_header">Offset(h)</span><br/><span class="display_header">' + padLeadingZeros(offset, 8) + '</span>';
				packet_hex = '<span class="display_header">00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F</span><br/>';
				packet_decoded = '<br/>';
				legend = '';
				for (var i = 0; i < packet.data.length; i+=2) {
					var hexOffset = integerToHexString(i / 2);

					// Check if the current byte is a starting byte of any analysis entry
					$(analysis.analysisEntries).each(function(index, element) {
						if (element.start*2 == i) {
							packet_hex += '<span class="hoverable ' + element.name + '">';
							packet_decoded += '<span class="hoverable ' + element.name + '">';
							legend += '<div class="hoverable ' + element.name + '">' + element.description + ' (' + (element.end - element.start + 1) + ' Bytes)'
<%
if (user != null) {
%>
							+ ' <a class="editlink" href="#" onclick="editAnalysisEntry(\'' + element.name + '\'); return false;">edit</a>'
<%
	if (userService.isUserAdmin()) {
%>
							+ ' <a class="deletelink" href="#" onclick="deleteAnalysisentry(' + packet.key + ',' + analysis.key + ',' + element.key + '); return false;">delete</a>'
<%
	}
}
%>
							;
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
						offset += 16;
						packet_offset += '<br/><span class="display_header">' + padLeadingZeros(offset.toString(16).toUpperCase(), 8) + "</span>";
						packet_hex += '<br/>';
						packet_decoded += '<br/>';
					}

				}

				var packetName = '<span id="packet_name">' + packet.name + '</span>';
<%
if (user != null && userService.isUserAdmin()) {
%>
				packetName += ' <a href="#" onclick="deletePacket(' + packet.key + '); return false;">delete</a>';
<%
}
%>
				var analysisName = '<span id="analysis_name">' + analysis.name + '</span>';
<%
if (user != null && userService.isUserAdmin()) {
%>
				analysisName += ' <a href="#" onclick="deleteAnalysis(' + packet.key + ',' + analysis.key + '); return false;">delete</a>';
<%
}
%>

				$('#name').html('<h1>' + packetName + ' - ' + analysisName + '</h1><a href="/export/packet?packet=' + packet.key + '">Export</a> <a href="/export/packet?packet=' + packet.key + '&amp;type=binary">Export binary</a>');
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
					var hex_element_current;
					if (this.id.indexOf("hex_element_") == 0) {
						hex_element_current = this.id.substring(12);
					} else if (this.id.indexOf("decoded_element_") == 0) {
						hex_element_current = this.id.substring(16);
					} else {
						return;
					}
					$('#hex_element_' + hex_element_current).addClass("decoded_or_hex_element_selected");
					$('#decoded_element_' + hex_element_current).addClass("decoded_or_hex_element_selected");
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
							analysis_entry.html(getAnalysisEntryDialogContent('', '', hex_element_start, hex_element_current, '', '', ''));
							$('#entry_color').htmlautocomplete({
								source: "packet_ajax?operation=autocomplete_color",
								minLength: 1
							});
							analysis_entry.dialog({
								title: 'Annotate packet',
								width: 730,
								height: 300,
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

			function editAnalysisEntry(name) {
				var entryToEdit = undefined;

				$(analysis.analysisEntries).each(function(index, element) {
					if (element.name == name) {
						entryToEdit = element;
					}
				});

				var analysis_entry = $('#analysis_entry');
				analysis_entry.html(getAnalysisEntryDialogContent(entryToEdit.key, entryToEdit.name,  integerToHexString(entryToEdit.start), integerToHexString(entryToEdit.end), entryToEdit.description, entryToEdit.color, entryToEdit.foregroundColor));
				$('#entry_color').htmlautocomplete({
					source: "packet_ajax?operation=autocomplete_color",
					minLength: 1
				});
				analysis_entry.dialog({
					title: 'Annotate packet',
					width: 730,
					height: 300
				});
			}

			function createAnalysis() {
				$.ajax({
					url: "packet_ajax",
					data: { "operation": "create_analysis" }
				}).done(function(data) {
					if (data == 'ok') {
						// Reload page
						window.location.reload();
						$('#analysis_entry').dialog('close');
					} else if (data == 'duplicate') {
						$('#entry_name').focus();
						$('#entry_name').addClass('duplicate');
					}
				});
			}

			function getAnalysisEntryDialogContent(key, name, start, end, description, color, foregroundcolor) {
				var retval;
				retval = '<table>' +
					'<tr><th>Name*</th><td><input type="text" id="entry_name" value="' + name +'" style="width: 250px;" /><input type="hidden" id="entry_name_old" value="' + name +'" style="width: 250px;" /></td><td class="description">Unique name of this datapart within the packet (e.g. \'header0\')</td></tr>' +
					'<tr><th>Start / End</th><td><input type="text" id="entry_start" value="0x' + start + '" style="width: 67px;" /><input type="text" id="entry_end" value="0x' + end + '" style="margin-left: 10px; width: 67px;" /> (' + (parseInt(end, 16) - parseInt(start, 16) + 1) + ' Bytes)</td><td class="description">First and last byte of datapart in packet</td></tr>' +
					'<tr><th>Description*</th><td><textarea id="entry_description" rows="4" cols="30" style="width: 250px;">' + description +'</textarea></td><td class="description">Human readable description of this datapart</td></tr>' +
					'<tr><th>Color*</th><td><div class="ui-widget"><input type="text" id="entry_color" value="' + color +'" style="width: 250px;" /></div></td><td class="description">Color to be used for this datapart</td></tr>' +
					'<tr><th>Foregroundcolor</th><td><div class="ui-widget"><input type="text" id="entry_foregroundcolor" value="' + (foregroundcolor == undefined ? '' : foregroundcolor) +'" style="width: 250px;" /></div></td><td class="description">Foregroundcolor to be used for this datapart (optional)</td></tr>' +
					'<tr><td></td><td><input type="button" onclick="submitAnalysisEntry(this);" value="save" />';
				if (key && key != '') {
					retval += '<input type="hidden" id="entry_key" value="' + key + '" />'
				}
				retval += '</td><td></td></tr>' +
					'</table>';
				return retval;
			}

			function submitAnalysisEntry(saveButton) {
				var editdata = {
					"operation": "create_analysisentry",
					"analysis_key": analysis.key,
					"packet_key": packet.key,
					"entry_name": $('#entry_name').val(),
					"entry_start": $('#entry_start').val(),
					"entry_end": $('#entry_end').val(),
					"entry_description": $('#entry_description').val(),
					"entry_color": $('#entry_color').val(),
					"entry_foregroundcolor": $('#entry_foregroundcolor').val(),
				};
				if ($('#entry_key') && $('#entry_key').val()) {
					editdata['entry_key'] = $('#entry_key').val();
					editdata['operation'] = 'update_analysisentry';
				}
				$(saveButton).attr('disabled', 'disabled');
				$('body').css('cursor', 'progress');
				$.ajax({
					url: "packet_ajax",
					data: editdata
				}).done(function(data) {
					$(saveButton).removeAttr('disabled');
					$('body').css('cursor', 'auto');
					if (data == 'ok') {
						// Reload page
						window.location.reload();
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
				packet = <%= new PacketConverter(IncludeUserdata.INCLUDE_NONE, IncludeKey.INCLUDE_ALL).toJson(packet).toString() %>;
				analysis = <%= new AnalysisConverter(IncludeUserdata.INCLUDE_NONE, IncludeKey.INCLUDE_ALL).toJson(analysis).toString() %>;
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

				$.widget('custom.htmlautocomplete', $.ui.autocomplete, {
					_renderItem: function(ul, item) {
						return $('<li>')
						.append($('<a>').html(item.label))
						.appendTo(ul);
					}
				});

<%
}
%>
			});
		</script>
<%
if (user != null && userService.isUserAdmin()) {
%>
		<script type="text/javascript" src="js/packet.edit.js"></script>
<%
}
%>
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
