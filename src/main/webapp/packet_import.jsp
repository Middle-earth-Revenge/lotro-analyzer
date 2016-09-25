<%@page import="com.blogspot.bwgypyth.lotro.model.PacketGroup"%>
<%@page import="java.util.List"%>
<%@page import="com.blogspot.bwgypyth.lotro.EMF"%>
<%@page import="javax.persistence.EntityManager"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
EntityManager em = EMF.get().createEntityManager();
try {
	List<PacketGroup> groups = em.createQuery("select group from PacketGroup group", PacketGroup.class).getResultList();
%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title></title>
		<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.2/themes/smoothness/jquery-ui.css" />
		<script type="text/javascript" src="http://code.jquery.com/jquery-1.9.1.js"></script>
		<script type="text/javascript" src="http://code.jquery.com/ui/1.10.2/jquery-ui.js"></script>
		<link rel="stylesheet" href="css/file.css">
	</head>
	<body>
		<%@ include file="navigation.jsp" %>
		<h1>Import packet</h1>
		<form action="/import/packet" method="post">
			<table>
				<tr>
					<th>Group</th>
					<td>
						<select name="group_key" id="group">
<%
	for (PacketGroup group : groups) {
		pageContext.setAttribute("group", group);
%>
							<option value="${group.key}"${group.name == 'Default' ? ' selected="selected"' : ''}>${group.name}</option>
<%
	}
%>
						</select>
						<a onclick="createGroupDialog(); return false;" href="#">Add group</a>
					</td>
				</tr>
				<tr>
					<th>JSON-Data</th>
					<td><textarea rows="10" cols="50" name="packet_data"></textarea></td>
				</tr>
				<tr>
					<td colspan="2" style="text-align: center;"><input type="submit" value="save" /></td>
				</tr>
			</table>
		</form>
		<script type="text/javascript" src="js/group.edit.js"></script>
		<div id="create_group_dialog"></div>
	</body>
</html>
<%
} finally {
	em.close();
}
%>