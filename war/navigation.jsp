<%@page import="com.google.appengine.api.users.User"%>
<%@page import="com.google.appengine.api.users.UserService"%>
<%@page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
UserService userService = UserServiceFactory.getUserService();
User user = userService.getCurrentUser();
%>
<nav class="navigation">
	<a href="packets.jsp">Packets</a>
<%
StringBuffer currentUrl = request.getRequestURL();
if (request.getQueryString() != null) {
	currentUrl.append('?').append(request.getQueryString());
}
if (user != null) {
%>
	<a href="<%= userService.createLogoutURL(currentUrl.toString()) %>">Sign out <%= user.getNickname() %></a>
<%
} else {
%>
	<a href="<%=userService.createLoginURL(currentUrl.toString())%>">Sign in</a>
<%
}
%>
</nav>