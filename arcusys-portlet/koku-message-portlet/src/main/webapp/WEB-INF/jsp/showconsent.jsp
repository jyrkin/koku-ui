<%@ include file="init.jsp"%>

<portlet:actionURL var="homeURL">
	<portlet:param name="action" value="toHome" />
</portlet:actionURL>

<portlet:resourceURL var="ajaxURL" id="getTask">
</portlet:resourceURL>

<%
	/* Parses the parent path url from the portlet ajaxURL */
	final String defaultPath = portletPath;
%>

<%@ include file="js_koku_detail.jspf" %>
<%@ include file="js_koku_navigation_helper.jspf" %>
<%@ include file="js_koku_reset_view.jspf" %>

<c:choose> 
  <c:when test="${consent.responseStatus == 'FAIL'}" > 
  	<script type="text/javascript"> 
  			var kokuErrorMsg = kokuDetails.kokuErrorMsg + "<span class=\"failureUuid\"><c:out value="${consent.errorCode}" /></span></div>";
			jQuery.jGrowl(kokuErrorMsg, kokuDetails.kokuErrorMsgOptions);
	</script>
  </c:when> 
  <c:when test="${consent.responseStatus == 'OK'}" >	   

	<div id="task-manager-wrap" class="single">
		<div id="show-message" style="padding:12px">
		<c:if test="${not empty consent.model.targetPerson}">
		<span class="text-bold"><spring:message code="consent.requester" />: <c:out value="${consent.model.requesterUser.fullName}" /> </span><br />
		</c:if>
		<span class="text-bold"><spring:message code="consent.templateName" />:</span> <c:out value="${consent.model.templateName}" /><br />
		<c:if test="${not empty consent.model.templateTypeName}">
		<span class="text-bold"><spring:message code="consent.templateTypeName" />:</span> <c:out value="${consent.model.templateTypeName}" /><br />
		</c:if>
		<span class="text-bold"><spring:message code="consent.status"/>:</span> <c:out value="${consent.model.status}" /><br />
		<span class="text-bold"><spring:message code="consent.approvalStatus"/>:</span> <c:out value="${consent.model.approvalStatus}" /><br />
		<span class="text-bold"><spring:message code="consent.createType"/>:</span> <c:out value="${consent.model.createType}" /><br />
		<span class="text-bold"><spring:message code="consent.givenDate"/>:</span> <c:out value="${consent.model.assignedDate}" /><br />
		<span class="text-bold"><spring:message code="consent.validDate"/>:</span> <c:out value="${consent.model.validDate}" /><br />
		<span class="text-bold"><spring:message code="consent.secondApprover"/>:</span> <c:out value="${consent.model.anotherPermitterUser.fullName}" /><br />
		<span class="text-bold"><spring:message code="consent.recipients"/>:</span>
		<c:forEach var="recipientUsers" items="${consent.model.recipientUsers}" varStatus="loopStatus">
			<c:out value="${recipientUsers.fullName}" />${not loopStatus.last ? ', ' : ''}
		</c:forEach>
		<br />
		
		<c:if test="${not empty consent.model.targetPerson}">
		<span class="text-bold"><spring:message code="consent.targetPerson"/>:</span> <c:out value="${consent.model.targetPerson.fullName}" /><br />
		</c:if>
		
		<span class="text-bold"><spring:message code="consent.comment"/>:</span> <c:out value="${consent.model.comment}" /><br />
		
		<c:if test="${not empty consent.model.kksFormInstance}">
			<c:if test="${not empty consent.model.kksFormInstance.instanceName}">
			<span class="text-bold"><spring:message code="consent.kksFormName"/>:</span> <c:out value="${consent.model.kksFormInstance.instanceName}" /><br />
			</c:if>
			
			<c:if test="${not empty consent.model.kksFormInstance.fields}">
				<span class="text-bold"><spring:message code="consent.kksFieldName"/>:</span>
				<c:forEach var="formField" items="${consent.model.kksFormInstance.fields}" varStatus="loopStatus">
					<c:out value="${formField.fieldName}" />${not loopStatus.last ? ', ' : ''}
				</c:forEach>
				<br />
			</c:if>
		</c:if>
		
		<c:if test="${not empty consent.model.kksGivenTo}">
			<span class="text-bold"><spring:message code="consent.organization"/>:</span>
			<c:forEach var="organization" items="${consent.model.kksGivenTo}" varStatus="loopStatus">
				<c:out value="${organization.organizationName}" />${not loopStatus.last ? ', ' : ''}
			</c:forEach>
			<br />
		</c:if>
		
		<% if (naviPortalMode.equals(Constants.PORTAL_MODE_KUNPO)) { %>
		<span class="modifyConsentLink">
			<a href="<%= defaultPath %><%= NavigationPortletProperties.CONSENTS_NEW_CONSENT %>?FormID=<c:out value="${consent.model.consentId}"/>"><spring:message code="consent.modifyConsentLink"/></a>
		</span><br />
		<% } %>
		
		
	    <h3><spring:message code="consent.actionRequest"/></h3>
	    <table class="request-table">
	    	<tr>
	    		<td class="head"><spring:message code="consent.name"/></td>
	    		<td class="head"><spring:message code="consent.description"/></td>
	    		<td class="head"><spring:message code="consent.status"/></td>
	    	</tr>
	    	<c:forEach var="request" items="${consent.model.actionRequests}" varStatus="loopStatus">
	        <tr class="${loopStatus.index % 2 == 0 ? 'evenRow' : 'oddRow'}">
	          <td><c:out value="${request.name}" /></td>
	          <td><c:out value="${request.description}" /></td>
	          <td style="min-width:60px"><c:out value="${request.status}" /></td>    
	        </tr>
	      	</c:forEach>
	    </table>
	</c:when> 
</c:choose>


	</div>
	<div id="task-manager-operation" class="task-manager-operation-part">
		<input type="button" value="<spring:message code="page.return"/>" onclick="kokuNavigationHelper.returnMainPage()" />
	</div>
</div>

