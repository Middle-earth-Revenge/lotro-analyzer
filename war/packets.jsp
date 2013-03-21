<%@page import="com.google.appengine.api.users.UserService"%>
<%@page import="java.util.List"%>
<%@page import="com.blogspot.bwgypyth.lotro.model.Packet"%>
<%@page import="com.blogspot.bwgypyth.lotro.EMF"%>
<%@page import="javax.persistence.EntityManager"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
EntityManager em = EMF.get().createEntityManager();
try {
	List<Packet> packets = em.createQuery("select packet from Packet packet order by name").getResultList();
%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Available packets</title>
		<link rel="stylesheet" href="css/file.css">
	</head>
	<body>
		<%@ include file="navigation.jsp" %>
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
for (Packet packet : packets) {
	pageContext.setAttribute("packet", packet);
%>
				<tr>
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