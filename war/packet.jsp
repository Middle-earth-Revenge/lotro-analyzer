<%@page import="com.blogspot.bwgypyth.lotro.model.PacketGroup"%>
<%@page import="com.blogspot.bwgypyth.lotro.json.PacketGroupConverter"%>
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

	// Load our packet
	if (request.getParameter("packet") == null) {
		throw new ServletException("Missing parameter 'packet'");
	}
	Packet packet = em.find(Packet.class, Long.valueOf(request.getParameter("packet")));

	// Load either the given analysis or the first one
	Analysis analysis = new Analysis();
	if (request.getParameter("analysis") != null) {
		analysis = em.find(Analysis.class, Long.valueOf(request.getParameter("analysis")));
		if (!analysis.getPacket().getKey().equals(packet.getKey())) {
			analysis = new Analysis();
		}
	} else if (!packet.getAnalyses().isEmpty()) {
		analysis = packet.getAnalyses().get(0);
	}

	// Load the available groups
	List<PacketGroup> groups = em.createQuery("select group from PacketGroup group order by name").getResultList();

	// Hand it over to the JSP
	pageContext.setAttribute("packet", packet);
	pageContext.setAttribute("analysis", analysis);
	pageContext.setAttribute("groups", groups);
%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>${packet.name} - ${analysis.name}</title>
		<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.2/themes/smoothness/jquery-ui.css" />
		<script type="text/javascript" src="http://code.jquery.com/jquery-1.9.1.js"></script>
		<script type="text/javascript" src="http://code.jquery.com/ui/1.10.2/jquery-ui.js"></script>
		<link rel="stylesheet" href="css/packet.css" />
<% if (UserServiceFactory.getUserService().getCurrentUser() != null) { %>
		<link rel="stylesheet" href="css/packet.edit.css" />
<% } %>
		<link rel="stylesheet" href="css/file.css">
	</head>
	<body>
		<%@ include file="navigation.jsp" %>
		<script type="text/javascript" src="js/packet.js"></script>
		<script type="text/javascript">

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
				var nameString = '<h1>' + packetName + ' - ' + analysisName + '</h1>';
				nameString += '<div id="toolbar" class="ui-widget-header ui-corner-all">';
<%
if (user != null) {
%>
				var groupsString = '';
				$.each(groups, function(index, element) {
					groupsString += '<option value="' + element.key + '"';
					if (packet.group == element.key) {
						groupsString += ' selected="selected"';
					}
					groupsString += '>' + element.name + '</option>';
				});
				nameString += 'Group: <select name="group" id="group" onchange="selectGroup();">' + groupsString + '</select><a href="#" onclick="createGroupDialog(); return false;" class="button">Add group</a> ';
<%
}
%>
				nameString += '<a href="/export/packet?packet=' + packet.key + '"class="button">Export</a><a href="/export/packet?packet=' + packet.key + '&amp;type=binary"class="button">Export binary</a>';
				nameString += '</div>';
				$('#name').html(nameString);
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
							var analysis_entry_dialog = $('#analysis_entry_dialog');
							analysis_entry_dialog.html(getAnalysisEntryDialogContent('', '', hex_element_start, hex_element_current, '', '', ''));
							$('a.button, input[type=submit], input[type=button]').button();
							$('#entry_color').htmlautocomplete({
								source: "packet_ajax?operation=autocomplete_color",
								minLength: 1
							});
							$('#entry_foregroundcolor').htmlautocomplete({
								source: "packet_ajax?operation=autocomplete_color",
								minLength: 1
							});
							$('#entry_name').htmlautocomplete({
								source: "packet_ajax?operation=autocomplete_name",
								minLength: 1
							});
							analysis_entry_dialog.dialog({
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

				var analysis_entry_dialog = $('#analysis_entry_dialog');
				analysis_entry_dialog.html(getAnalysisEntryDialogContent(entryToEdit.key, entryToEdit.name,  integerToHexString(entryToEdit.start), integerToHexString(entryToEdit.end), entryToEdit.description, entryToEdit.color, entryToEdit.foregroundColor));
				$('a.button, input[type=submit], input[type=button]').button();
				$('#entry_color').htmlautocomplete({
					source: "packet_ajax?operation=autocomplete_color",
					minLength: 1
				});
				analysis_entry_dialog.dialog({
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
					// TODO: check if is parseable as an int
				});
			}

			function selectGroup() {
				$.ajax({
					url: "packet_ajax",
					data: {
						"operation": "select_group",
						"packet_key": packet.key,
						"group_key": $('#group').val()
					}
				}).done(function(data) {
					if (data == 'ok') {
						// TODO: what to do here?
					}
				});
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
						$('#analysis_entry_dialog').dialog('close');
					} else if (data == 'duplicate') {
						$('#entry_name').focus();
						$('#entry_name').addClass('duplicate');
					}
				});
			}
<%
}
%>

			var hex_element_start, packet, analysis, groups;

			$(function() {
				packet = <%= new PacketConverter(IncludeUserdata.INCLUDE_NONE, IncludeKey.INCLUDE_ALL).toJson(packet).toString() %>;
				analysis = <%= new AnalysisConverter(IncludeUserdata.INCLUDE_NONE, IncludeKey.INCLUDE_ALL).toJson(analysis).toString() %>;
				groups = <%= new PacketGroupConverter(IncludeUserdata.INCLUDE_NONE, IncludeKey.INCLUDE_ALL).toJson(groups).toString() %>;
				renderPacket();
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
<% if (user != null) { %>
		<script type="text/javascript" src="js/packet.edit.js"></script>
		<script type="text/javascript" src="js/group.edit.js"></script>
<% } %>
<% if (user != null && userService.isUserAdmin()) { %>
		<script type="text/javascript" src="js/packet.admin.js"></script>
<% } %>
		<div class="content">
			<div id="name"></div>
			<div id="packet_offset"></div>
			<div id="packet_hex"></div>
			<div id="packet_decoded"></div>
			<div id="legend"></div>
		</div>
		<div id="analysis_entry_dialog"></div>
		<div id="create_group_dialog"></div>
		<script type="text/javascript">$(function() { $('a.button, input[type=submit], input[type=button]').button(); });</script>
	</body>
</html>
<%
} finally {
	em.close();
}
%>
