<%@page import="com.blogspot.bwgypyth.lotro.model.PacketGroup"%>
<%@page import="com.google.appengine.api.users.UserService"%>
<%@page import="java.util.List"%>
<%@page import="com.blogspot.bwgypyth.lotro.model.Packet"%>
<%@page import="com.blogspot.bwgypyth.lotro.EMF"%>
<%@page import="javax.persistence.EntityManager"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
EntityManager em = EMF.get().createEntityManager();
try {
	List<Packet> packets = em.createQuery("select packet from Packet packet order by packet.group, packet.name").getResultList();
%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Available packets</title>
		<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.2/themes/smoothness/jquery-ui.css" />
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
					<td colspan="3" class="packetgroup">${group.name}</td>
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
					<td colspan="3"><a href="packet_upload.jsp">Upload packet</a> <a href="/import/packet">Import packet in JSON format</a></td>
				</tr>
			</tfoot>
<%
}
%>
		</table>
	</body>
</html>
<%
} finally {
	em.close();
}
%>