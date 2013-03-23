<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
		<h1>Import packet</h1>
		<form action="/import/packet" method="post">
			<table>
				<tr>
					<th>JSON-Data</th>
					<td><textarea rows="10" cols="50" name="packet_data"></textarea></td>
				</tr>
				<tr>
					<td colspan="2" style="text-align: center;"><input type="submit" value="save" /></td>
				</tr>
			</table>
		</form>
	</body>
</html>