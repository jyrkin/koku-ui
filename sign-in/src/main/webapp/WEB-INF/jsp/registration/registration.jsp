<%--
 Copyright 2012 Ixonos Plc, Finland. All rights reserved.
  
 This file is part of Kohti kumppanuutta.
 
 This file is licensed under GNU LGPL version 3.
 Please see the 'license.txt' file in the root directory of the package you received.
 If you did not receive a license, please contact the copyright holder
 (kohtikumppanuutta@ixonos.com).
--%>


<%@ include file="imports.jsp" %>

<portlet:actionURL var="saveRegistrationActionUrl">
	<portlet:param name="action" value="saveRegistrationAAA" />
</portlet:actionURL>


<div class="koku-registration">
	<div class="portlet-section-body">

<h2><spring:message	code="ui.registration.authentication.success" /></h2>

		<form:form name="saveRegistrationForm" commandName="registration"
			method="post" action="${saveRegistrationActionUrl}">

			<div>

				<table border="0">
					<tr><th width="25%"></th><th width="30%"></th><th width="45%"></th></tr>
					<tr><td colspan="3"><p><b><spring:message	code="ui.registration.authentication.service.information" /></b></p></td></tr>

					<tr>
						<td><spring:message	code="ui.registration.firstname" /></td>
						<td><c:out value="${registration.firstname}" /></td>
						<td></td>
						
					</tr>

					<tr>
						<td><spring:message	code="ui.registration.lastname" /></td>
						<td><c:out value="${registration.lastname}" /></td>
						<td></td>	
					</tr>

					<tr>
						<td><spring:message	code="ui.registration.birthday" /></td>
						<td><c:out value="${registration.dayOfBirth}" /></td>
						<td></td>	
					</tr>

					<tr><td colspan="3"><p><b><spring:message	code="ui.registration.additional.information" /></b></p></td></tr>

					<tr>
						<td><spring:message	code="ui.registration.email" /> <span style="color: red">*</span></td>
						<td><div class="portlet-form-field">
								<span class="kks-left"> <form:input class="defaultText"
										path="email" />
								</span>
								<spring:message var="uiEmailHelp" code="ui.registration.form.field.help.email" />
								<img class="koku-registration-help" title="${ uiEmailHelp }" src="${pageContext.request.contextPath}/css/images/help-button-small.png"/>
							</div></td>
					<td class="koku-registration-error"><form:errors path="email" cssClass="error"/></td>
					</tr>

					<tr>
						<td><spring:message	code="ui.registration.phonenumber" /> </td>
						<td><div class="portlet-form-field">
								<span class="kks-left"> <form:input class="defaultText"
										path="phonenumber" />
								</span>
								<spring:message var="uiPhonenumberHelp" code="ui.registration.form.field.help.phonenumber" />
								<img class="koku-registration-help" title="${ uiPhonenumberHelp }" src="${pageContext.request.contextPath}/css/images/help-button-small.png"/>
							</div></td>
							<td class="koku-registration-error"><form:errors path="phonenumber" cssClass="error"/></td>
					</tr>

					<tr>
						<td><spring:message	code="ui.registration.preferred.contact.method" /> <span style="color: red">*</span></td>
						<td><div class="portlet-form-field">
								<span class="kks-left"> 
								<form:radiobutton path="preferredContactMethod" value="email"/><spring:message	code="ui.registration.email" />
								<form:radiobutton path="preferredContactMethod" value="phone"/><spring:message	code="ui.registration.phone" />
								</span>
								<spring:message var="uiPreferredContactMethodHelp" code="ui.registration.form.field.help.preferredcontactmethod" />
								<img class="koku-registration-help" title="${ uiPreferredContactMethodHelp }" src="${pageContext.request.contextPath}/css/images/help-button-small.png"/>
							</div></td>
							<td class="koku-registration-error"><form:errors path="preferredContactMethod" cssClass="error"/></td>
					</tr>



					<tr>
						<td><spring:message	code="ui.registration.useraccount" /> <span style="color: red">*</span></td>
						<td><div class="portlet-form-field">
								<span class="kks-left"> <form:input class="defaultText"
										path="useraccount" />
								</span>
								<spring:message var="uiUseraccountHelp" code="ui.registration.form.field.help.useraccount" />
								<img class="koku-registration-help" title="${ uiUseraccountHelp }" src="${pageContext.request.contextPath}/css/images/help-button-small.png"/>
							</div></td>
							<td class="koku-registration-error"><form:errors path="useraccount" cssClass="error"/></td>
					</tr>

					<tr>
						<td><spring:message	code="ui.registration.password" /> <span style="color: red">*</span></td>
						<td><div class="portlet-form-field">
								<span class="kks-left"> 
								<form:password class="defaultText"	path="password" />
								</span>
								<spring:message var="uiPasswordHelp" code="ui.registration.form.field.help.password" />
								<img class="koku-registration-help" title="${ uiPasswordHelp }" src="${pageContext.request.contextPath}/css/images/help-button-small.png"/>
							</div></td>
							<td class="koku-registration-error"><form:errors path="password" cssClass="error"/></td>
					</tr>

					<tr>
						<td><spring:message	code="ui.registration.password.confirm" /> <span style="color: red">*</span></td>
						<td>
						<div class="portlet-form-field">
								<span class="kks-left"> 
								<form:password class="defaultText"	path="password2" />
								</span>
						</div></td>
						<td class="koku-registration-error"><form:errors path="password2" cssClass="error"/></td>
					</tr>

					<tr>
						<td>Olen lukenut ja hyväksyn <a href="#" onclick="javascript: toggleTermsOfUse()"><span class="koku-termsofuse">käyttöehdot</span></a> <span style="color: red">*</span></td>
						<td>
						<div class="portlet-form-field">
								<span class="kks-left"> 
									<form:checkbox path="acceptTermsOfUse" />
								</span>
								<spring:message var="uiTermsOfUseHelp" code="ui.registration.form.field.help.termsofuse" />
								<img class="koku-registration-help" title="${ uiTermsOfUseHelp }" src="${pageContext.request.contextPath}/css/images/help-button-small.png"/>
						</div>
						</td>
						<td></td>
					</tr>

					<tr>
						<td><a href="#" onclick="javascript: toggleInstructions()">Ohjeet rekisteröintiin</a></td>
						<td></td>
						<td></td>
					</tr>

					<tr>
						<td></td>
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

				<div id="koku-termsofuse" style="display: none">
				
				<h3>Käyttöehdot</h3>
				<p> &laquo; käyttöehdot tähän &raquo;
				</p>
				</div>

				<div id="koku-registration-instructions" style="display: none">
				
				<h3>Rekisteröintiohjeet</h3>
				<p> Palvelun käyttö vaatii rekisteröitymisen. Rekisteröitymislomakkeella on tunnistautumispalvelusta saadut esitiedot ja sen lisäksi lomakkeessa on annettava Kohti kumppanuutta -järjestelmän 
					käyttöön liittyviä lisätietoja. Lomakkeen täyttäminen:</p> 
					
					<ol>
						<li>Tarkista, että lomakkeen esitäytetyt tiedot ovat oikein</li>
						<li>Täytä lomakkeen pakolliset kentät. Pakolliset kentät on merkitty punaisella tähdellä. Tarkempia kenttäkohtaisia ohjeita näet viemällä hiiren kohdistimen <img title="infokuvake" src="${pageContext.request.contextPath}/css/images/help-button-small.png"/>-kuvakkeen päälle </li>
						<li>Paina Rekisteröidy -nappia</li>
					</ol>
				
				</div>



				<div class="koku-registration-reset-floating"></div>
			</div>

			<div class="koku-registration-reset-floating"></div>

		</form:form>

	</div>
</div>




<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.5.2.min.js"></script>
<script type="text/javascript">
	
	function toggleInstructions() {
		$("#koku-registration-instructions").toggle();
	}
	
	function toggleTermsOfUse() {
		$("#koku-termsofuse").toggle();
	}
	
</script>

