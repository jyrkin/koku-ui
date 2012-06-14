<%--
 Copyright 2012 Ixonos Plc, Finland. All rights reserved.
  
 This file is part of Kohti kumppanuutta.
 
 This file is licensed under GNU LGPL version 3.
 Please see the 'license.txt' file in the root directory of the package you received.
 If you did not receive a license, please contact the copyright holder
 (kohtikumppanuutta@ixonos.com).
--%>
<%@ include file="imports.jsp" %>

<portlet:actionURL var="saveUserinformationActionUrl">
	<portlet:param name="action" value="kokuregistrationsaveUserinformation" />
</portlet:actionURL>


<div class="koku-registration">
	<div class="portlet-section-body">

<h2><spring:message	code="ui.registration.userinformation.header" /></h2>

		<form:form name="saveUserinformationForm" commandName="userinformation"
			method="post" action="${saveUserinformationActionUrl}">

			<div>

				<table border="0">
					
					<tr><td colspan="3"><p><b><spring:message	code="ui.registration.userinformation" /></b></p></td></tr>

					<tr>
						<td><spring:message	code="ui.registration.firstname" /></td>
						<td><c:out value="${userinformation.firstname}" /></td>
						<td></td>
						
					</tr>

					<tr>
						<td><spring:message	code="ui.registration.lastname" /></td>
						<td><c:out value="${userinformation.lastname}" /></td>
						<td></td>	
					</tr>

					<tr>
						<td><spring:message	code="ui.registration.birthday" /></td>
						<td><c:out value="${userinformation.dayOfBirth}" /></td>
						<td></td>	
					</tr>

					<tr>
						<td><spring:message	code="ui.registration.useraccount" /></td>
						<td><c:out value="${userinformation.useraccount}" /></td>
						<td></td>	
					</tr>

					<tr><td colspan="3"><p><b><spring:message code="ui.registration.editable.information" /></b></p></td></tr>

					<tr>
						<td><spring:message	code="ui.registration.email" /></td>
						<td><div class="portlet-form-field">
								<span class="kks-left"> <form:input class="defaultText"
										path="email" />
								</span>
								<spring:message var="uiEmailHelp" code="ui.registration.form.field.help.email" />
								<img class="koku-registration-help" title="${ uiEmailHelp }" src="${pageContext.request.contextPath}/css/images/help-button-small.png"/>
							</div></td>
					<td><form:errors path="email" cssClass="error"/></td>
					</tr>
					
					<tr>
						<td><spring:message	code="ui.registration.phonenumber" /></td>
						<td><div class="portlet-form-field">
								<span class="kks-left"> <form:input class="defaultText"
										path="phonenumber" />
								</span>
								<spring:message var="uiPhonenumberHelp" code="ui.registration.form.field.help.phonenumber" />
								<img class="koku-registration-help" title="${ uiPhonenumberHelp }" src="${pageContext.request.contextPath}/css/images/help-button-small.png"/>
							</div></td>
							<td><form:errors path="phonenumber" cssClass="error"/></td>
					</tr>
					
 					
					<tr>
						<td><spring:message	code="ui.registration.newpassword" /></td>
						<td><div class="portlet-form-field">
								<span class="kks-left"> 
								<form:password class="defaultText"	path="newpassword" />
								</span>
								<spring:message var="uiPasswordHelp" code="ui.registration.form.field.help.password" />
								<img class="koku-registration-help" title="${ uiPasswordHelp }" src="${pageContext.request.contextPath}/css/images/help-button-small.png"/>
							</div></td>
							<td><form:errors path="newpassword" cssClass="error"/></td>
					</tr>

					<tr>
						<td><spring:message	code="ui.registration.newpassword.confirm" /></td>
						<td>
						<div class="portlet-form-field">
								<span class="kks-left"> 
								<form:password class="defaultText"	path="newpassword2" /> 
								</span>
						</div></td>
						<td><form:errors path="newpassword2" cssClass="error"/></td>
					</tr>
					
					
					<tr>
						<td><spring:message	code="ui.registration.oldpassword" /> <span style="color: red">*</span></td>
						<td>
						<div class="portlet-form-field">
								<span class="kks-left"> 
								<form:password class="defaultText"	path="currentpassword" /> 
								</span>
						</div></td>
						<td><form:errors path="currentpassword" cssClass="error"/></td>
					</tr>
					
					<tr>
						<td>&nbsp;</td>
						<td><div class="portlet-form-field">
								<span class="kks-left"> <input type="submit"
									class="portlet-form-button"
									value="<spring:message code="ui.registration.save.userinformation.button"/>">
								</span>

							</div></td>
					</tr>

				</table>

				<div class="koku-registration-reset-floating"></div>
			</div>

			<div class="koku-registration-reset-floating"></div>

		</form:form>

	</div>
</div>