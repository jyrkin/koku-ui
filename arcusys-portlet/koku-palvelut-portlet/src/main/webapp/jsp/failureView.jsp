
<%@ include file="/jsp/init.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page import="fi.arcusys.koku.palvelut.controller.ViewController"%>


<%@page import="javax.portlet.PortletPreferences"%>

<div class="portlet-linkit-container">		
	<div class="portlet-header">
		<div class="failureJsp">
			<div class="failureMessage firstMessage">Oops.. Jotain meni serverillä vikaan. Kokeile ladata sivu uudestaan tai ota yhtettä sivuston ylläpitäjään.</div>
			<div class="failureMessage">Oops.. Something went wrong on the server. Please refresh the page and try again or contact to Administrator.</div>			
		</div>
	</div>
</div>
