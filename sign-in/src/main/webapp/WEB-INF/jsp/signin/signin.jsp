<%--
 Copyright 2012 Ixonos Plc, Finland. All rights reserved.
  
 This file is part of Kohti kumppanuutta.
 
 This file is licensed under GNU LGPL version 3.
 Please see the 'license.txt' file in the root directory of the package you received.
 If you did not receive a license, please contact the copyright holder
 (kohtikumppanuutta@ixonos.com).
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>

<%@ page import="javax.portlet.PortletPreferences"%>
<%@ page import="javax.portlet.WindowState"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page contentType="text/html" isELIgnored="false"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<portlet:defineObjects />

<div class="koku-signin">
	<div class="portlet-section-body">

		<div class="koku-signin-main"
			style="padding: 5px; padding-top: 10px; padding-bottom: 10px;">
			<c:if test="${not empty fullname }">
			
				
			
				<span class="koku-signin-right"> <spring:message
						code="ui.signin.welcome" /> <b>${ fullname }</b> <c:if
						test="${ pwdSupported }">
						<span class="koku-signin-tabulator"><spring:message
								code="ui.signin.separator" /></span>
						<a class="koku-signin-account"
							onclick="showKokuAccountSettings();"><spring:message
								code="ui.signin.account" /></a>
					</c:if>
					
					
					
				
					<span class="koku-signin-tabulator"><spring:message
								code="ui.signin.separator" /> <a href="${ editUserinfoUrl }"><spring:message
						code="ui.registration.edit.userinformation.link" /></a></span>
				
								 <span class="koku-signin-tabulator"><spring:message
							code="ui.signin.separator" /></span> <a class="koku-signin-logout"
					onclick="logoutKokuUser();"><spring:message
							code="ui.signin.logout" /></a>
				</span>
			</c:if>


			<c:if test="${ empty fullname }">
				<span class="koku-signin-right"> <c:if
						test="${ empty loginUrl }">
						<b><a class="koku-signin-login" onclick="loginKokuUser();"><spring:message
									code="ui.signin.login" /></a> </b>
					</c:if> 
					
					<span class="koku-signin-tabulator"><spring:message
								code="ui.signin.separator" /> <b><a href="/portal/public/classic/registration"><spring:message
									code="ui.signin.register" /></a></b></span>
						
					<c:if test="${ not empty loginUrl }">
						<b><a class="koku-signin-login" href="${loginUrl}"><spring:message
									code="ui.signin.login" /></a></b>
					</c:if>
				</span>
			</c:if>
		</div>
		<div class="koku-signin-reset-floating"></div>
	</div>
</div>

<script type="text/javascript">
	function logoutKokuUser() {
		eXo.portal.logout();
	}

	function loginKokuUser() {
		if (document.getElementById('UIMaskWorkspace'))
			ajaxGet(eXo.env.server.createPortalURL('UIPortal', 'ShowLoginForm',
					true));
	}

	function showKokuAccountSettings() {
		if (document.getElementById('UIMaskWorkspace'))
			ajaxGet(eXo.env.server.createPortalURL('UIPortal',
					'AccountSettings', true));
	}
</script>
