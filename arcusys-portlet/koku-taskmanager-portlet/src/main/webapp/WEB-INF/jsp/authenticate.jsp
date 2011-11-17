<%--
 Copyright 2011 Ixonos Plc, Finland. All rights reserved.

 You should have received a copy of the license text along with this program.
 If not, please contact the copyright holder (http://www.ixonos.com/).
--%>
<%-- <%@ include file="init.jsp"%> --%>
<%-- <%@ include file="kks\imports.jsp" %> --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" import="java.util.*" %>
<%@ page contentType="text/html" isELIgnored="false"%>

<portlet:defineObjects />
	
<!-- <fmt:setBundle basename="Language-ext"/> -->

<c:choose>
	<c:when test="${not empty requestScope.authenticationURL}">
	  <div class="portlet-section-text">
<%-- 	   	<spring:message code="ui.requires.strong.authentication"/> --%>
		T�m� sovellus edellytt�� vahvaa tunnistautumista
	  </div>
	  <div class="kks-link">
	  	<a href="${requestScope.authenticationURL}">Klikkaa t�st� tunnistautuaksesi vahvasti
<%-- 	  	<spring:message code="ui.log.in.using.strong.authentication"/></a> --%>

	  </div>
	</c:when>
	<c:otherwise>
		<div class="portlet-section-text">
<%-- 	  		<spring:message code="ui.not.logged.in"/> --%>
			Kirjaudu j�rjestelm��n
	  	</div>
	</c:otherwise>
</c:choose>

