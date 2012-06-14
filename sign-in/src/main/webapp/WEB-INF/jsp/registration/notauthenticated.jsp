<%--
 Copyright 2012 Ixonos Plc, Finland. All rights reserved.
  
 This file is part of Kohti kumppanuutta.
 
 This file is licensed under GNU LGPL version 3.
 Please see the 'license.txt' file in the root directory of the package you received.
 If you did not receive a license, please contact the copyright holder
 (kohtikumppanuutta@ixonos.com).
--%>

<%@ include file="imports.jsp" %>

<div class="koku-registration">
	<div class="portlet-section-body">
		
		<c:if test="${ alreadyregistered }">
		<p>
			<spring:message	code="ui.registration.already.registered" />
		</p>
		</c:if>
<%--#TODO#Removable code block?				
		<p>
		<spring:message	code="ui.registration.login.instructions" /> 
		</p>
 --%>		
		<c:if test="${ not alreadyregistered }">
		
		<%@ include file="instructions.jsp"%>
		
		<p>
			<spring:message	code="ui.registration.registration.instructions" /> <u><a href="${registrationURL}"><spring:message	code="ui.registration.click.to.register" /></a></u>
		</p>
		</c:if>
				
	</div>
</div>