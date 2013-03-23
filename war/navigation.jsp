<%@page import="com.google.appengine.api.users.User"%>
<%@page import="com.google.appengine.api.users.UserService"%>
<%@page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
UserService userService = UserServiceFactory.getUserService();
User user = userService.getCurrentUser();
%>
<nav class="navigation">
	<a href="packets.jsp" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only ui-state-focus"><span class="ui-button-text">Packets</span></a>
<%
StringBuffer currentUrl = request.getRequestURL();
if (request.getQueryString() != null) {
	currentUrl.append('?').append(request.getQueryString());
}
if (user != null) {
%>
	<a href="<%= userService.createLogoutURL(currentUrl.toString()) %>" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only"><span class="ui-button-text">Sign out <%= user.getNickname() %></span></a>
<%
} else {
%>
	<a href="<%=userService.createLoginURL(currentUrl.toString())%>" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only"><span class="ui-button-text">Sign in</span></a>
<%
}
%>
</nav>