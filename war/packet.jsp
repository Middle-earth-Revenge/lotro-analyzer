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
		<script src="http://code.jquery.com/jquery-1.9.1.min.js"></script>
		<script src="http://code.jquery.com/ui/1.10.2/jquery-ui.min.js"></script>
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
			span.hex_element_selected {
				border: 1px dashed #333333;
				margin: -1px;
			}
			span.hex_element {
				cursor: pointer;
			}
			[contenteditable] {
				outline: 1px dotted #CCCCCC;
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

			function renderPacket() {
				var generatedCss = '<style type="text/css">';
				$(analysis.analysisEntries).each(function(index, element) {
					if (element.color) {
						var foregroundColor;
						if (element.foregroundColor) {
							foregroundColor = element.foregroundColor;
						} else {
							foregroundColor = '#000000';
						}
						generatedCss += '.' + element.name + ' { margin-left: -1px; margin-right: -1px; border: 1px solid ' + element.color + '; color: ' + element.color + '; }\n' + 
							'.' + element.name + '_hovered { background-color: ' + element.color + '; color: ' + foregroundColor + '; }\n';
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
					$(analysis.analysisEntries).each(function(index, element) {
						if (element.start*2 == i) {
							packet_hex += '<span class="hoverable ' + element.name + '">';
							packet_decoded += '<span class="hoverable ' + element.name + '">';
							legend += '<div class="hoverable ' + element.name + '">' + element.description;
						}
					});

					packet_hex += '<span class="hex_element" id="hex_element_' + padLeadingZeros((i/2).toString(16).toUpperCase(), 2) + '">' + packet.data.substr(i, 2) + '</span>';
					var tmp = parseInt(packet.data.substr(i, 2), 16);
					if (tmp < 33 || (tmp > 126 && tmp < 161)) {
						tmp = '.';
					} else {
						tmp = String.fromCharCode(tmp);
					}
					packet_decoded += tmp;

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

				$('#name').html('<h1><span id="packet_name">' + packet.name + '</span> - <span id="analysis_name">' + analysis.name + '</span></h1>');
				$('#packet_offset').html(packet_offset);
				$('#packet_hex').html(packet_hex);
				$('#packet_decoded').html(packet_decoded);
				$('#legend').html(legend);

				$('.hoverable').each(function(index, element) {
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
				});
<%
if (user != null) {
%>
				$('.hex_element').click(function() {
					var hex_element_current = this.id.substring(12);
					$(this).addClass("hex_element_selected");
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
									'<tr><th>Name*</th><td><input type="text" id="entry_name" /></td></tr>' +
									'<tr><th>Start</th><td><input type="text" disabled="disabled" id="entry_start" value="0x' + hex_element_start + '" /></td></tr>' +
									'<tr><th>End</th><td><input type="text" disabled="disabled" id="entry_end" value="0x' + hex_element_current + '" /></td></tr>' +
									'<tr><th>Description*</th><td><input type="text" id="entry_description" /></td></tr>' +
									'<tr><th>Color*</th><td><div class="ui-widget"><input type="text" id="entry_color" /></div></td></tr>' +
									'<tr><th>Foregroundcolor</th><td><div class="ui-widget"><input type="text" id="entry_foregroundcolor" /></div></td></tr>' +
									'<tr><td colspan="2"><input type="button" onclick="submitAnalysisEntry();" /></td></tr>' +
									'</table>');
							$('#entry_color').autocomplete({
								source: "packet_ajax?operation=autocomplete_color",
								minLength: 1,
								select: function(event, ui) {
									console.log(ui);
								}
							});
							analysis_entry.dialog({
								width: 800,
								height: 400,
								beforeClose: function() {
									$('#hex_element_' + $('#entry_start').val().substring(2)).removeClass("hex_element_selected");
									$('#hex_element_' + $('#entry_end').val().substring(2)).removeClass("hex_element_selected");
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
				}).done(function() {
					$('#analysis_entry').dialog('close');
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
