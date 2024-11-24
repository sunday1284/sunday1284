<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String userid = request.getParameter("userid");
	String userpw = request.getParameter("userpw");
	
	if(userid.equals(userpw)){
		response.sendRedirect("/main.jsp");
	} else {
		response.sendRedirect("/login.jsp");
	}
	
%>