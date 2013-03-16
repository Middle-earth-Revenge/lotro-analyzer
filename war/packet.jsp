<%@page import="java.util.List"%>
<%@page import="javax.persistence.EntityManager"%>
<%@page import="com.google.appengine.api.datastore.Key"%>
<%@page import="com.blogspot.bwgypyth.lotro.model.Analysis"%>
<%@page import="com.blogspot.bwgypyth.lotro.json.AnalysisConverter"%>
<%@page import="com.blogspot.bwgypyth.lotro.model.Packet"%>
<%@page import="com.blogspot.bwgypyth.lotro.EMF"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
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
		/*Query q = pm.newQuery(Analysis.class);
		q.setFilter("key == keyParam && packet == packetParam");
		q.declareParameters(Key.class.getName() + " keyParam, Packet packetParam");
		List<Analysis> analyses = (List<Analysis>) q.execute(Long.valueOf(request.getParameter("analysis")), packet.getKey());
		out.print(analyses.size());
		if (!analyses.isEmpty()) {
			analysis = analyses.get(0);
		}*/
		analysis = em.find(Analysis.class, Long.valueOf(request.getParameter("analysis")));
		out.print(analysis);
	} else if (!packet.getAnalyses().isEmpty()) {
		analysis = packet.getAnalyses().get(0);
	}
	pageContext.setAttribute("packet", packet);
	pageContext.setAttribute("analysis", analysis);
%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title></title>
		<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
		<style type="text/css">
			body {
				font-family: sans-serif;
			}
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
		</style>
		<link rel="stylesheet" href="css/file.css">
	</head>
	<body>
		<script type="text/javascript">
			function padLeadingZeros(number) {
				var tmp = '' + number;
				while (tmp.length < 8) {
					tmp = '0' + tmp;
				}
				return tmp;
			}

			function renderPacket(data, metadata, name) {
				var generatedCss = '<style type="text/css">';
				$(metadata).each(function(index, element) {
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
				packet_offset = 'Offset(h)<br/>' + padLeadingZeros(offset);
				packet_hex = '00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F<br/>';
				packet_decoded = '<br/>';
				legend = '';
				for (var i = 0; i < data.length; i+=2) {
					$(metadata).each(function(index, element) {
						if (element.start*2 == i) {
							packet_hex += '<span class="hoverable ' + element.name + '">';
							packet_decoded += '<span class="hoverable ' + element.name + '">';
							legend += '<div class="hoverable ' + element.name + '">' + element.description;
						}
					});

					packet_hex += data.substr(i, 2);
					var tmp = parseInt(data.substr(i, 2), 16);
					if (tmp < 33 || (tmp > 126 && tmp < 161)) {
						tmp = '.';
					} else {
						tmp = String.fromCharCode(tmp);
					}
					packet_decoded += tmp;

					$(metadata).each(function(index, element) {
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
						packet_offset += '<br/>' + padLeadingZeros(offset);
						packet_hex += '<br/>';
						packet_decoded += '<br/>';
					}

				}

				$('#packet_name').html('<h1>' + name + '</h1>');
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
			}

			$(function() {
				var data = '${packet.data}';
				var metadata = <%= new AnalysisConverter().toJson(analysis).get("analysisEntries").toString() %>;
				var name = '<%= packet.getName() + " - " + analysis.getName() %>';
				renderPacket(data, metadata, name);

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
			});
		</script>
		<div class="button_left button">
			<div class="button_left_img"></div>
		</div>
		<div class="button_right button">
			<div class="button_right_img"></div>
		</div>
		<%@ include file="navigation.jsp" %>
		<div class="content">
			<div id="packet_name"></div>
			<div id="packet_offset"></div>
			<div id="packet_hex"></div>
			<div id="packet_decoded"></div>
			<div id="legend"></div>
		</div>
	</body>
</html>
<%
} finally {
	em.close();
}
%>
