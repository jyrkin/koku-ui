<%@ page import="fi.arcusys.koku.common.services.appointments.model.EmployeeAppointment" %>
<%@ page import="fi.arcusys.koku.common.services.users.KokuUser" %>
<%@ page import="fi.arcusys.koku.web.util.ModelWrapper"%>
<%@ page import="fi.arcusys.koku.common.util.Constants" %>
<%@ page import="javax.portlet.PortletRequest" %>

<%@ include file="init.jsp"%>

<portlet:actionURL var="homeURL">
	<portlet:param name="action" value="toHome" />
</portlet:actionURL>

<portlet:resourceURL var="cancelURL" id="cancelAppointment"></portlet:resourceURL>
<portlet:resourceURL var="disableSlotURL" id="disableAppointmentSlot"></portlet:resourceURL>

<portlet:resourceURL var="appointmentRenderURL" id="createAppointmentRenderUrl">
</portlet:resourceURL>

<%@ include file="js_koku_detail.jspf" %>
<%@ include file="js_koku_navigation_helper.jspf" %>
<%@ include file="js_koku_reset_view.jspf" %>

<%
	ModelWrapper<EmployeeAppointment> messageModel = (ModelWrapper<EmployeeAppointment>) request.getAttribute("appointment");
	
	EmployeeAppointment appointment = messageModel.getModel();
	
	String appointmentId = String.valueOf(appointment.getAppointmentId());
	
	String currentUserId = (String) renderRequest.getPortletSession().getAttribute(Constants.ATTR_USER_ID);
	String senderUserId = appointment.getSenderUser().getUid();
	
%>

<style type="text/css">
	<%@include file="../../../css/jquery.jgrowl.koku.css" %>
</style>

<script type="text/javascript">
	<%@ include file="js_koku_ajax.jspf" %>
	<%@ include file="js_koku_utils.jspf" %>
	<%@ include file="js_koku_appointment_details.jspf" %>
	
	var kokuAppointmentDetails = new KokuAppointmentDetails(); 

</script>

<c:choose> 
  <c:when test="${appointment.responseStatus == 'FAIL'}" > 
  	<script type="text/javascript"> 
  			kokuErrorMsg += "<span class=\"failureUuid\"><c:out value="${appointment.errorCode}" /></span></div>";
			jQuery.jGrowl(kokuErrorMsg, kokuErrorMsgOptions);
	</script>
  </c:when>
  
  <c:when test="${appointment.responseStatus == 'OK'}" >
  
  	<div id="cancelAppointment" title="Peruuta tapaaminen" style="display: none">
		<div class="cancelMessage">Peruuta tapaaminen kirjoittamalla vapaaehtoinen saateteksti ja valitsemalla 'L�het� peruutus'. Jos et halua peruuttaa tapaamista, paina 'Sulje dialogi' painiketta.</div>
		<textarea id="kokuCancelMessage"></textarea>
	</div>
  	<div id="cancelAppointmentSlotDialog" title="Haluatko varmasti poistaa t�m�n tapaamisen?" style="display: none">
		<div class="cancelMessage">Tapaamisaikaa ei voi uudelleenk�ytt�� tekem�tt� uutta tapaamislomaketta. </div>
	</div>	

	<div id="task-manager-wrap" class="single">
		<div id="show-message" style="padding:12px">
		<c:if test="${appointment.model.senderUser != null}">
		<span class="request-c-1"><spring:message code="message.from"/>: <c:out value="${appointment.model.senderUser.fullName}" /> </span><br />
		</c:if>
		<c:if test="${appointment.model.senderRole != null}">
			<span class="request-c-1"><spring:message code="appointment.senderRole"/>:</span> <c:out value="${appointment.model.senderRole}" /><br />
		</c:if>
		<span class="request-c-1"><spring:message code="message.subject"/>:</span> <c:out value="${appointment.model.subject}" /><br />
		<span class="request-c-1"><spring:message code="message.description"/>:</span> <c:out value="${appointment.model.description}" /><br />
		<span class="request-c-1"><spring:message code="message.status"/>:</span> <c:out value="${appointment.model.status}" /><br />
		<c:if test="${appointment.model.cancellationComment != null}">
		<span class="request-c-1"><spring:message code="appointment.cancellationComment"/>:</span> <c:out value="${appointment.model.cancellationComment}" /><br />
		</c:if>
		
		<c:if test="${fn:length(appointment.model.approvedSlots) > 0}">
	    <h3><spring:message code="appointment.approvedSlots"/></h3>   
	    <table class="request-table employee">
	    	<tr>
	    		<td class="head date"><spring:message code="appointment.date"/></td>
	    		<td class="head startTime"><spring:message code="appointment.start"/></td>
	    		<td class="head endTime"><spring:message code="appointment.end"/></td>
	    		<td class="head location"><spring:message code="appointment.location"/></td>
	    		<td class="head comment"><spring:message code="appointment.comment"/></td>
	    		<td class="head targetPersonName"><spring:message code="appointment.targetPerson"/></td>
	    		<td class="head recipients"><spring:message code="appointment.recipients"/></td>
				<c:if test="<%= !appointment.getStatus().equals("Peruutettu") && currentUserId != null && currentUserId.equals(senderUserId)%>">
					<td class="head remove"><spring:message code="appointment.deleteTimeSlot"/></td>
				</c:if>
	    	</tr>
	    	<c:forEach var="slot" items="${appointment.model.approvedSlots}" varStatus="loopStatus">
	        <tr class="<c:out value="${loopStatus.index % 2 == 0 ? 'evenRow' : 'oddRow'}" />">
	          <td class="date"><c:out value="${slot.appointmentDate}" /></td>
	          <td class="startTime"><c:out value="${slot.startTime}" /></td>
	          <td class="endTime"><c:out value="${slot.endTime}" /></td> 
	          <td class="location"><c:out value="${slot.location}" /></td>
	          <td class="comment"><c:out value="${slot.comment}" /></td> 
	          <td class="targetPersonName"><c:out value="${slot.targetPersonUser.fullName}" /></td>
	          <td class="recipients">
	          <c:forEach var="recipient" items="${slot.recipientUsers}" varStatus="loopStatus">
	          	<c:out value="${recipient.fullName}" />,
	          </c:forEach>
				<c:if test="<%= !appointment.getStatus().equals("Peruutettu") && currentUserId != null && currentUserId.equals(senderUserId)%>">
					<td class="remove">
						<c:choose>
							<c:when test="${!slot.disabled}">
								<input id="${"slotCancelButton" + slot.slotNumber}" type="button" value="<spring:message code="delete"/>" onclick="kokuAppointmentDetails.disableAppointmentSlot(<%= appointmentId %>, <c:out value="${slot.slotNumber}" />)" />
							</c:when>
							<c:otherwise>
								<spring:message code="deleted" />
							</c:otherwise>
						</c:choose>
					</td>
				</c:if>
	          </td>
	        </tr>
	      	</c:forEach>
	    </table>  
		</c:if>
		
		<c:if test="${fn:length(appointment.model.unapprovedSlots) > 0}">
	    <h3><spring:message code="appointment.unapprovedSlots"/></h3>   
	    <table class="request-table employee">
	    	<tr>
	    		<td class="head date"><spring:message code="appointment.date"/></td>
	    		<td class="head startTime"><spring:message code="appointment.start"/></td>
	    		<td class="head endTime"><spring:message code="appointment.end"/></td>
	    		<td class="head location"><spring:message code="appointment.location"/></td>
	    		<td class="head comment"><spring:message code="appointment.comment"/></td>
				<c:if test="<%= !appointment.getStatus().equals("Peruutettu") && currentUserId != null && currentUserId.equals(senderUserId)%>">
					<td class="head remove"><spring:message code="appointment.deleteTimeSlot"/></td>
				</c:if>
	    	</tr>
	    	<c:forEach var="slot" items="${appointment.model.unapprovedSlots}" varStatus="loopStatus">
		        <tr class="<c:out value="${loopStatus.index % 2 == 0 ? 'evenRow' : 'oddRow'}" />">
			        <td class="date"><c:out value="${slot.appointmentDate}" /></td>
			        <td class="startTime"><c:out value="${slot.startTime}" /></td>
			        <td class="endTime"><c:out value="${slot.endTime}" /></td>
			        <td class="location"><c:out value="${slot.location}" /></td>
			        <td class="comment"><c:out value="${slot.comment}" /></td>
					<c:if test="<%= !appointment.getStatus().equals("Peruutettu") && currentUserId != null && currentUserId.equals(senderUserId)%>">
			        	<td class="remove">
							<c:choose>
								<c:when test="${!slot.disabled}">
									<input id="slotCancelButton${slot.slotNumber}" type="button" value="<spring:message code="delete"/>" onclick="kokuAppointmentDetails.disableAppointmentSlot(<%= appointmentId %>, <c:out value="${slot.slotNumber}" />)" />
								</c:when>
								<c:otherwise>
									<spring:message code="deleted" />
								</c:otherwise>
							</c:choose>
						</td>
			        </c:if>
		        </tr>
	      	</c:forEach>
	    </table>  
		</c:if>
		
		<h3><spring:message code="appointment.unrepliedUsers"/></h3>
		<c:if test="${fn:length(appointment.model.unrespondedUsers) == 0}">
			<spring:message code="appointment.none"/>
		</c:if>
		<c:if test="${fn:length(appointment.model.unrespondedUsers) > 0}">
		<table class="request-table employee">
	    	<tr>
	    		<td class="head"><spring:message code="appointment.targetPerson"/></td>
	    		<td class="head"><spring:message code="appointment.recipients"/></td>
	
	    	</tr>
	    	<c:forEach var="user" items="${appointment.model.unrespondedUsers}" varStatus="loopStatus">
	        <tr class="<c:out value="${loopStatus.index % 2 == 0 ? 'evenRow' : 'oddRow'}" />">
	          <td><c:out value="${user.targetPersonUser.fullName}" /></td>
	          <td>
	           	<c:forEach var="recipientUser" items="${user.recipientUsers}" varStatus="loopStatus">
		          <c:out value="${recipientUser.fullName}" />, 
	          	</c:forEach>
	          </td>  
	        </tr>
	      	</c:forEach>
	    </table>  
	    </c:if>
		<h3><spring:message code="appointment.rejectedUsers"/></h3>
		<c:if test="${fn:length(appointment.model.rejectedUsers) == 0}">
			<spring:message code="appointment.none"/>
		</c:if>
		<c:if test="${fn:length(appointment.model.rejectedUsers) > 0}">
		<table class="request-table employee">
	    	<tr>
	    		<td class="head"><spring:message code="appointment.targetPerson"/></td>
	    		<td class="head"><spring:message code="appointment.recipients"/></td>
	    	</tr>
	    	<c:forEach var="rejectedUser" items="${appointment.model.rejectedUsers}" varStatus="loopStatus">
	        <tr class="<c:out value="${loopStatus.index % 2 == 0 ? 'evenRow' : 'oddRow'}" />">
	          <td><c:out value="${rejectedUser.targetPersonUser.fullName}" /></td>
	            <td>
	           	<c:forEach var="rejectedUserInfo" items="${rejectedUser.recipientUsers}" varStatus="loopStatus">
		          <c:out value="${rejectedUserInfo.fullName}" />, 
	          	</c:forEach>
	          </td>  
	        </tr>
	      	</c:forEach>
	    </table>  
		</c:if>
		
		<%-- Users who cancelled their appointment --%>
		<c:if test="${fn:length(appointment.model.usersRejected) > 0}">
		<h3><spring:message code="appointment.userCancellationHeader"/></h3>
		<table class="request-table employee">
	    	<tr>
	    		<td class="head"><spring:message code="appointment.targetPerson"/></td>
	    		<td class="head"><spring:message code="appointment.replierComment"/></td>
	    	</tr>
	    	<c:forEach var="reject" items="${appointment.model.usersRejected}" varStatus="loopStatus">
	        <tr class="<c:out value="${loopStatus.index % 2 == 0 ? 'evenRow' : 'oddRow'}" />">
	          <td><c:out value="${reject.user.fullName}" /></td>
	          <td><c:out value="${reject.rejectComment}" /></td>  
	        </tr>
	      	</c:forEach>
	    </table>  
		</c:if>
	</c:when> 
</c:choose>
	
	</div>
	<div id="task-manager-operation" class="task-manager-operation-part">
		<input type="button" value="<spring:message code="page.return"/>" onclick="kokuNavigationHelper.returnMainPage()" />
		<c:if test="<%= !appointment.getStatus().equals("Peruutettu") && currentUserId != null && currentUserId.equals(senderUserId)%>">
			<input type="button" id="cancelButton" value="<spring:message code="appointment.cancel.button"/>" onclick="kokuAppointmentDetails.cancelAppointment('<%= appointmentId %>')" />
		</c:if>
	</div>
</div>

