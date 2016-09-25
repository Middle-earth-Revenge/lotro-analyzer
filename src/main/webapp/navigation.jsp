<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<nav class="navigation">
	<a href="packets.jsp" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only ui-state-focus"><span class="ui-button-text">Packets</span></a>
<%
StringBuffer currentUrl = request.getRequestURL();
if (request.getQueryString() != null) {
	currentUrl.append('?').append(request.getQueryString());
}
%>
</nav>