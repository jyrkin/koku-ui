<%@ include file="init.jsp"%>

<portlet:actionURL var="homeURL">
	<portlet:param name="action" value="toHome" />
</portlet:actionURL>

<%@ include file="js_koku_detail.jspf" %>
<%@ include file="js_koku_navigation_helper.jspf" %>
<%@ include file="js_koku_reset_view.jspf" %>


<c:choose> 
  <c:when test="${tipy.responseStatus == 'FAIL'}" > 
  	<script type="text/javascript"> 
  			var kokuErrorMsg = kokuDetails.kokuErrorMsg +  "<span class=\"failureUuid\"><c:out value="${tipy.errorCode}" /></span></div>";
			jQuery.jGrowl(kokuErrorMsg, kokuDetails.kokuErrorMsgOptions);
	</script>
  </c:when> 
  
  <c:when test="${tipy.responseStatus == 'OK'}" >	   
	<div id="task-manager-wrap" class="single">
		<div id="show-message" style="padding:12px">
			<span class="text-bold"><spring:message code="tipy.details.reciever" />:</span> <c:out value="${tipy.model.recieverUser.fullName}" /><br />
			<span class="text-bold"><spring:message code="tipy.details.sender" />:</span> <c:out value="${tipy.model.senderUser.fullName}" /><br />
			<span class="text-bold"><spring:message code="tipy.details.requestStatus" />:</span> <c:out value="${tipy.model.localizedStatus}" /><br />
			<span class="text-bold"><spring:message code="tipy.details.targetPerson" />:</span> <c:out value="${tipy.model.targetPersonUser.fullName}" /><br />
			<span class="text-bold"><spring:message code="tipy.details.title" />:</span> <c:out value="${tipy.model.title}" /><br />
			<span class="text-bold"><spring:message code="tipy.details.legislationinfo" />:</span> <c:out value="${tipy.model.legislationInfo}" /><br />
			<span class="text-bold"><spring:message code="tipy.details.requestPurpose"/>:</span> <c:out value="${tipy.model.requestPurpose}" /><br />
			<span class="text-bold"><spring:message code="tipy.details.description"/>:</span> <c:out value="${tipy.model.description}" /><br />	
			<span class="text-bold"><spring:message code="tipy.details.additionalInfo"/>:</span> <c:out value="${empty tipy.model.additionalInfo ? '-' : tipy.model.additionalInfo}" /><br />
			<span class="text-bold"><spring:message code="tipy.details.attachmentURL"/>:</span> <c:out value="${empty tipy.model.attachmentURL ? '-' : tipy.model.attachmentURL}" /><br />
			<span class="text-bold"><spring:message code="tipy.details.createdDate"/>:</span> <c:out value="${tipy.model.createdDate}" /><br />
			<span class="text-bold"><spring:message code="tipy.details.validTill"/>:</span> <c:out value="${empty tipy.model.validTill ? 'Toistaiseksi' : tipy.model.validTill}" /><br />
			<span class="text-bold"><spring:message code="tipy.details.replyDescription" />:</span> <c:out value="${empty tipy.model.replyDescription ? '-' : tipy.model.replyDescription}" /><br />
			<span class="text-bold"><spring:message code="tipy.details.informationDetails"/>:</span> <c:out value="${empty tipy.model.informationDetails ? '-' : tipy.model.informationDetails}" /><br />
			<span class="text-bold"><spring:message code="tipy.details.additionalReplyInfo"/>:</span> <c:out value="${empty tipy.model.additionalReplyInfo ? '-' : tipy.model.additionalReplyInfo}" /><br />
			<c:if test="${not empty tipy.model.localizedAccessType}">
			<span class="text-bold"><spring:message code="tipy.details.accessType"/>:</span> <c:out value="${tipy.model.localizedAccessType}" /><br />
			</c:if>
			
	    <h3><spring:message code="tipy.details.categoryHeader"/></h3>
	    <table class="request-table">
	    	<tr>
	    		<td class="head"><spring:message code="tipy.details.categoryTableHeader"/></td>
	    	</tr>
	    	<c:forEach var="cat" items="${tipy.model.categories}" varStatus="loopStatus">
		        <tr class="<c:out value="${loopStatus.index % 2 == 0 ? 'evenRow' : 'oddRow'}"/>">
		          <td><c:out value="${cat}"/></td>
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

