<%@ page import="fi.arcusys.koku.kv.model.KokuMessage" %>
<%@ page import="fi.arcusys.koku.av.CitizenAppointment" %>
<%@ page import="fi.arcusys.koku.users.KokuUser" %>
<%@ page import="fi.arcusys.koku.util.Constants" %>
<%@ page import="fi.arcusys.koku.web.util.ModelWrapper"%>


<%@ include file="init.jsp"%>

<portlet:renderURL var="homeURL" windowState="<%= WindowState.NORMAL.toString() %>" >
	<portlet:param name="myaction" value="home" />
</portlet:renderURL>

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

<script type="text/javascript">
	<%@ include file="js_koku_ajax.jspf" %>

	var ajaxUrls = {
			cancelUrl : "<%= cancelURL %>",
			appointmentRenderUrl :  "<%= appointmentRenderURL %>"
	};
	
	var kokuAjax = new KokuAjax(ajaxUrls);
	
	function notify_appointment_cancelled() {
		jQuery.jGrowl("<spring:message code="notification.canceled.appointment"/>");
	}
	
	function callback(result) {			
		if (result == 'OK') {
			jQuery.jGrowl("<spring:message code="notification.canceled.appointment"/>");
			setTimeout("kokuNavigationHelper.returnMainPage();", 5000);
		} else if (result == 'FAIL') {
			jQuery.jGrowl("<spring:message code="notification.canceled.appointment.failed"/>");
		} else {
			KokuUtil.errorMsg.showErrorMessage("<spring:message code="error.unLogin" />");
		}
	}
	
	function cancelAppointment() {
		var appointments = [], targetPersons = [], comment, taskType;
		appointments[0] = "<%= appointmentId %>";
		targetPersons[0] = "<%= targetPerson %>";
		comment = prompt('<spring:message code="appointment.cancel"/>',"");
		if(comment == null)	{
			return;
		}		

		taskType = "<%= Constants.TASK_TYPE_APPOINTMENT_INBOX_CITIZEN %>";
		kokuAjax.cancelAppointments(appointments, targetPersons, comment, taskType, callback);
	}
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
	          <td class="date"><c:out value="${appointment.model.slot.date}" /></td>
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
	</div>
	<c:if test="${appointment.model.status} != 'check'">
		<div id="task-manager-operation" class="task-manager-operation-part">
			<input type="button" value="Peruuta" onclick="cancelAppointment()" />
		</div>
	</c:if>
</div>

