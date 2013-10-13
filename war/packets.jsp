<%@page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@page import="java.util.Comparator"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="javax.persistence.EntityManager"%>
<%@page import="com.google.appengine.api.users.UserService"%>
<%@page import="com.blogspot.bwgypyth.lotro.model.Packet"%>
<%@page import="com.blogspot.bwgypyth.lotro.model.PacketGroup"%>
<%@page import="com.blogspot.bwgypyth.lotro.EMF"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
EntityManager em = EMF.get().createEntityManager();
try {
	List<Packet> packets;
	if (request.getParameter("group") == null || request.getParameter("group").isEmpty()) {
		packets = em.createQuery("select packet from Packet packet").getResultList();
	} else {
		packets = em.createQuery("select packet from Packet packet where groupKey = :groupKey").setParameter("groupKey", KeyFactory.createKey("PacketGroup", Long.parseLong(request.getParameter("group")))).getResultList();
	}
	Collections.sort(packets, new Comparator<Packet>() {
		public int compare(Packet o1, Packet o2) {
			int compareTo = o1.getGroup().getName().compareTo(o2.getGroup().getName());
			if (compareTo == 0) {
				compareTo = o1.getName().compareTo(o2.getName());
				if (compareTo == 0) {
					compareTo = o1.getKey().compareTo(o2.getKey());
				}
			}
			return compareTo;
		}
	});
%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Available packets</title>
		<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.2/themes/smoothness/jquery-ui.css" />
		<script type="text/javascript" src="http://code.jquery.com/jquery-1.9.1.js"></script>
		<script type="text/javascript" src="http://code.jquery.com/ui/1.10.2/jquery-ui.js"></script>
		<link rel="stylesheet" href="css/file.css">
		<style type="text/css">
			table {
				border-spacing: 0px;
			}
			td.packetgroup {
				padding-top: 0.5em;
				font-weight: bold;
				padding-left: 0.5em;
				border-bottom: 1px solid #cccccc;
			}
			tr.packetrow:hover td {
				background-color: #aed0ff;
			}
		</style>
	</head>
	<body>
		<%@ include file="navigation.jsp" %>
		<h1>Available packets</h1>
		<table>
			<thead>
				<tr>
					<th>Packet</th>
					<th>No. of analyses</th>
					<th>Actions</th>
				</tr>
			</thead>
			<tbody>
<%
PacketGroup group = null;
for (Packet packet : packets) {
	if ((group == null && packet.getGroup() != null) || (group != null && !group.equals(packet.getGroup()))) {
		group = packet.getGroup();
		pageContext.setAttribute("group", group);
%>
				<tr>
					<td colspan="3" class="packetgroup"><a href="packets.jsp?group=${group.key.id}">${group.name}</a> <a style="font-weight: normal;" href="/export/packetgroup?group=${group.key.id}">Export</a></td>
				</tr>
<%
	}
	pageContext.setAttribute("packet", packet);
%>
				<tr class="packetrow">
					<td><a href="packet.jsp?packet=${packet.key.id}">${packet.name}</a></td>
					<td>${packet.analysesSize}</td>
					<td><a href="/export/packet?packet=${packet.key.id}">Export</a> <a href="/export/packet?packet=${packet.key.id}&amp;type=binary">Export binary</a></td>
				</tr>
<%
}
%>
			</tbody>
<%
if (user != null) {
%>
			<tfoot>
				<tr>
					<td colspan="3"><a href="packet_upload.jsp" class="button">Upload packet</a> <a href="packet_import.jsp" class="button">Import packet in JSON format</a></td>
				</tr>
			</tfoot>
<%
}
%>
		</table>
		<script type="text/javascript">$(function() { $('a.button, input[type=submit], input[type=button]').button(); });</script>
	</body>
</html>
<%
} finally {
	em.close();
}
%>