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
if (user != null) {
%>
	<a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">Sign out <%= user.getNickname() %></a>
<%
} else {
%>
	<a href="<%=userService.createLoginURL(request.getRequestURI())%>">Sign in</a>
<%
}
%>
</nav>