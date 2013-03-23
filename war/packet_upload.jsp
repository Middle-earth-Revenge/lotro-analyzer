<%@page import="com.blogspot.bwgypyth.lotro.model.PacketGroup"%>
<%@page import="java.util.List"%>
<%@page import="com.blogspot.bwgypyth.lotro.EMF"%>
<%@page import="javax.persistence.EntityManager"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
EntityManager em = EMF.get().createEntityManager();
try {
	List<PacketGroup> groups = em.createQuery("select group from PacketGroup group").getResultList();
%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title></title>
		<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.2/themes/smoothness/jquery-ui.css" />
		<link rel="stylesheet" href="css/file.css">
	</head>
	<body>
		<%@ include file="navigation.jsp" %>
		<h1>Upload new packet</h1>
		<form action="packet_upload" method="post">
			<table>
				<tr>
					<th>Name</th>
					<td><input type="text" name="name" size="50" /></td>
				</tr>
				<tr>
					<th>Group</th>
					<td>
						<select name="group_key">
<%
	for (PacketGroup group : groups) {
		pageContext.setAttribute("group", group);
%>
							<option value="${group.key.id}"${group.name == 'Default' ? ' selected="selected"' : ''}>${group.name}</option>
<%
	}
%>
						</select>
					</td>
				</tr>
				<tr>
					<th>Data</th>
					<td><textarea rows="10" cols="50" name="data"></textarea></td>
				</tr>
				<tr>
					<td colspan="2" style="text-align: center;"><input type="submit" value="save" /></td>
				</tr>
			</table>
		</form>
	</body>
</html>
<%
} finally {
	em.close();
}
%>