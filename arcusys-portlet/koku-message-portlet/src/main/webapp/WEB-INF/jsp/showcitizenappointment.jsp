<%@ page import="fi.arcusys.koku.common.services.messages.model.KokuMessage" %>
<%@ page import="fi.arcusys.koku.common.services.appointments.model.CitizenAppointment" %>
<%@ page import="fi.arcusys.koku.common.services.users.KokuUser" %>
<%@ page import="fi.arcusys.koku.common.util.Constants" %>
<%@ page import="fi.arcusys.koku.web.util.ModelWrapper"%>


<%@ include file="init.jsp"%>

<portlet:actionURL var="homeURL">
	<portlet:param name="action" value="toHome" />
</portlet:actionURL>

<portlet:resourceURL var="cancelURL" id="cancelAppointment"></portlet:resourceURL>

<portlet:resourceURL var="appointmentRenderURL" id="createAppointmentRenderUrl">
</portlet:resourceURL>

<%@ include file="js_koku_detail.jspf" %>
<%@ include file="js_koku_navigation_helper.jspf" %>
<%@ include file="js_koku_reset_view.jspf" %>


<%
	ModelWrapper<CitizenAppointment> messageModel = (ModelWrapper<CitizenAppointment>) request.getAttribute("appointment");
	
	CitizenAppointment appointment = messageModel.getModel();
	
	String appointmentId = String.valueOf(appointment.getAppointmentId());
	String targetPerson = appointment.getTargetPersonUser().getUid();
	
%>

<!-- <style type="text/css"> -->
<%-- 	<%@include file="../../../css/jquery.jgrowl.koku.css" %> --%>
<!-- </style> -->

<script type="text/javascript">
	<%@ include file="js_koku_ajax.jspf" %>
	<%@ include file="js_koku_utils.jspf" %>
	<%@ include file="js_koku_appointment_details.jspf" %>

	KokuAppointmentDetailsCitizen = function() {};
	KokuAppointmentDetailsCitizen.prototype = new KokuAppointmentDetails();
	KokuAppointmentDetailsCitizen.prototype.editAppointment = function () {
		window.location = "<%= portletPath %><%= NavigationPortletProperties.CONSENTS_ANSWER_TO_CONSENT %>?FormID=<%= appointmentId %>&arg1=<%= targetPerson %>";				
	}
	var kokuAppointmentDetails = new KokuAppointmentDetailsCitizen();
	
</script>

<c:choose> 
  <c:when test="${appointment.responseStatus == 'FAIL'}" > 
  	<script type="text/javascript"> 
  			kokuErrorMsg += "<span class=\"failureUuid\"><c:out value="${appointment.errorCode}" /></span></div>";
			jQuery.jGrowl(kokuErrorMsg, kokuErrorMsgOptions);
	</script>
  </c:when>
  
  <c:when test="${appointment.responseStatus == 'OK'}" >

	</script>
	
<div id="cancelAppointment" title="<spring:message code="appointmnet.dialog.caption"/>" style="display: none">
	<div class="cancelMessage"><spring:message code="appointmnet.dialog.description"/></div>
	<textarea id="kokuCancelMessage"></textarea>
</div>

	<div id="task-manager-wrap" class="single">
		<div id="show-message" style="padding:12px">
		<c:if test="${appointment.model.senderUser != null}">
		<span class="request-c-1"><spring:message code="message.from"/>: <c:out value="${appointment.model.senderUser.fullName}" /> </span><br />
		</c:if>		
		<span class="request-c-1"><spring:message code="message.subject"/>:</span> <c:out value="${appointment.model.subject}" /><br />
		<span class="request-c-1"><spring:message code="message.description"/>:</span> <c:out value="${appointment.model.description}" /><br />
		<span class="request-c-1"><spring:message code="message.status"/>:</span> <c:out value="${appointment.model.status}" /><br />
		<c:if test="${appointment.model.cancellationComment  != null}">
		<span class="request-c-1"><spring:message code="appointment.cancellationComment"/>:</span> <c:out value="${appointment.model.cancellationComment}" /><br />
		</c:if>
		<c:if test="${appointment.model.targetPersonUser != null}">
		<span class="request-c-1"><spring:message code="appointment.targetPerson"/>:</span> <c:out value="${appointment.model.targetPersonUser.fullName}" /><br />
		</c:if>
		<span class="request-c-1"><spring:message code="appointment.replier"/>:</span> <c:out value="${appointment.model.replierUser.fullName}" /><br />
		<span class="request-c-1"><spring:message code="appointment.replierComment"/>:</span> <c:out value="${appointment.model.replierComment}" /><br />
		
		<c:if test="${appointment.model.slot != null}" >
	    <h3><spring:message code="appointment.approvedSlots"/></h3>   
	    <table class="request-table citizen">
	    	<tr>
	    		<td class="head date"><spring:message code="appointment.date"/></td>
	    		<td class="head startTime"><spring:message code="appointment.start"/></td>
	    		<td class="head endTime"><spring:message code="appointment.end"/></td>
	    		<td class="head location"><spring:message code="appointment.location"/></td>
	    		<td class="head comment"><spring:message code="appointment.comment"/></td>
	    	</tr>
	        <tr class="evenRow">
	          <td class="date"><c:out value="${appointment.model.slot.appointmentDate}" /></td>
	          <td class="startTime"><c:out value="${appointment.model.slot.startTime}" /></td>
	          <td class="endTime"><c:out value="${appointment.model.slot.endTime}" /></td>
	          <td class="location"><c:out value="${appointment.model.slot.location}" /></td>
	          <td class="comment"><c:out value="${appointment.model.slot.comment}" /></td>
	        </tr>
	    </table>  
		</c:if>		
	</c:when>
</c:choose>
	</div>
	<div id="task-manager-operation" class="task-manager-operation-part">
		<input type="button" value="<spring:message code="page.return"/>" onclick="kokuNavigationHelper.returnMainPage()" />
		<c:if test="${appointment.model.status != 'Peruutettu'}">
			<input type="button" id="cancelButton" value="<spring:message code="appointment.cancel.button"/>" onclick="kokuAppointmentDetails.cancelAppointment('<%= appointmentId %>', '<%= targetPerson %>')" />
			<input type="button" id="editButton" value="<spring:message code="appointment.edit.button"/>" onclick="kokuAppointmentDetails.editAppointment()" />			
		</c:if>
	</div>
</div>