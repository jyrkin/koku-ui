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

<portlet:actionURL var="saveRegistrationActionUrl">
	<portlet:param name="action" value="saveRegistrationAAA" />
</portlet:actionURL>

<div class="koku-signin">
	<div class="portlet-section-body">

		<form:form name="saveRegistrationForm" commandName="registration"
			method="post" action="${saveRegistrationActionUrl}">

<%--
	<c:if test="${not empty error}">
			<div class="error">
				<spring:message code="${error}"></spring:message>
			</div>
		</c:if>
		--%>

			<div>

				<table border="0">
					<tr>
						<td>Etunimi</td>
						<td><div class="portlet-form-field">
								<span class="kks-left"> <form:input class="defaultText"
										readonly="true" disabled="true" path="firstname" />
								</span>
							</div></td>
							<td><form:errors path="firstname" /></td>
					</tr>

					<tr>
						<td>Sukunimi</td>
						<td><div class="portlet-form-field">
								<span class="kks-left"> <form:input class="defaultText"
										disabled="true" path="lastname" />
								</span>
							</div></td>
							<td><form:errors path="lastname" /></td>
					</tr>

					<tr>
						<td>Syntymaaika</td>
						<td><div class="portlet-form-field">
								<span class="kks-left"> <form:input class="defaultText"
										disabled="true" path="dayOfBirth" />
								</span>

							</div></td>
					<td><form:errors path="dayOfBirth" /></td>
					</tr>

					<tr>
						<td>Email</td>
						<td><div class="portlet-form-field">
								<span class="kks-left"> <form:input class="defaultText"
										path="email" />
								</span>
							</div></td>
					<td><form:errors path="email" /></td>
					</tr>

					<tr>
						<td>Puhelinnumero</td>
						<td><div class="portlet-form-field">
								<span class="kks-left"> <form:input class="defaultText"
										path="phonenumber" />
								</span>
							</div></td>
							<td><form:errors path="phonenumber" /></td>
					</tr>

					<tr>
						<td>Kayttajatunnus</td>
						<td><div class="portlet-form-field">
								<span class="kks-left"> <form:input class="defaultText"
										path="useraccount" />
								</span>
							</div></td>
							<td><form:errors path="useraccount" /></td>
					</tr>

					<tr>
						<td>Salasana</td>
						<td><div class="portlet-form-field">
								<span class="kks-left"> <form:input class="defaultText"
										path="password" />
								</span>
							</div></td>
							<td><form:errors path="password" /></td>
					</tr>

					<tr>
						<td>Salasana2</td>
						<td></td>
					</tr>

					<tr>
						<td></td>
						<td></td>
					</tr>

					<tr>
						<td></td>
						<td></td>
					</tr>

					<tr>
						<td></td>
						<td></td>
					</tr>

					<tr>
						<td>&nbsp;</td>
						<td><div class="portlet-form-field">
								<span class="kks-left"> <input type="submit"
									class="portlet-form-button"
									value="<spring:message code="ui.registration.save.button"/>">
								</span>

							</div></td>
					</tr>

				</table>

				<div class="koku-signin-reset-floating"></div>
			</div>

			<div class="koku-signin-reset-floating"></div>

		</form:form>


		<%--      
	   <div class="koku-signin-main" style="padding: 5px; padding-top: 10px; padding-bottom: 10px;">     
       <c:if test="${not empty fullname }">         
			<span class="koku-signin-right">
				<spring:message code="ui.signin.welcome" /> <b>${ fullname }</b> <c:if test="${ pwdSupported }"> <span class="koku-signin-tabulator"><spring:message code="ui.signin.separator" /></span> <a class="koku-signin-account" onclick="showKokuAccountSettings();"><spring:message code="ui.signin.account" /></a> </c:if> <span class="koku-signin-tabulator"><spring:message code="ui.signin.separator" /></span> <a class="koku-signin-logout" onclick="logoutKokuUser();"><spring:message code="ui.signin.logout" /></a>   
			</span> 			
       </c:if>
       
       
       <c:if test="${ empty fullname }">
       	   <span class="koku-signin-right">
       	     <c:if test="${ empty loginUrl }">
			 	<b><a class="koku-signin-login" onclick="loginKokuUser();"><spring:message code="ui.signin.login" /></a> </b>
			 </c:if>
			 
			 <c:if test="${ not empty loginUrl }">
			 	<b><a class="koku-signin-login" href="${loginUrl}"><spring:message code="ui.signin.login" /></a></b> 
			 </c:if>
			</span>
       </c:if>
       </div>
       <div class="koku-signin-reset-floating"></div>
     
		--%>
	</div>
</div>

<script type="text/javascript">
	/*
	function logoutKokuUser() {
		eXo.portal.logout();
	}
	
	function loginKokuUser() {
		if(document.getElementById('UIMaskWorkspace')) ajaxGet(eXo.env.server.createPortalURL('UIPortal', 'ShowLoginForm', true)); 
	}
	
	function showKokuAccountSettings() {
		if(document.getElementById('UIMaskWorkspace')) ajaxGet(eXo.env.server.createPortalURL('UIPortal', 'AccountSettings', true));
	}
	*/
</script>
