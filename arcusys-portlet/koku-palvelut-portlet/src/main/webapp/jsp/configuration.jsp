<%@ include file="/jsp/init.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"	isELIgnored="false"%>
<%
	PortletPreferences prefs = (PortletPreferences)request.getAttribute(Constants.ATTR_PREFERENCES);
	String portalInfo = (String)request.getAttribute(Constants.ATTR_PORTAL_ID);
%>

<%@page import="javax.portlet.PortletPreferences"%>

<div class="configuration">	
	<c:set value="<%= request.getAttribute(Constants.ATTR_PORTAL_ID) %>" var="portalVersion"></c:set>
	<c:choose>
		<c:when test="<%= portalInfo.equals(Constants.PORTAL_JBOSS) %>">		
		<!-- JBoss	  -->
			 <c:set value="<%=prefs.getValue(Constants.PREF_SHOW_ONLY_FORM_BY_ID, null) %>" var="formId"></c:set>
			 <c:set value="<%=prefs.getValue(Constants.PREF_SHOW_ONLY_FORM_BY_DESCRIPTION, null) %>" var="formDescription"></c:set>
		 	 <c:set value="<%=prefs.getValue(Constants.PREF_SHOW_TASKS_BY_ID, null) %>" var="selectFormById"></c:set>
			 <div class="CurrentForm">T‰ll‰hetkell‰ valittu lomake:
			 <% Boolean isTaskIdEnabled = Boolean.valueOf(prefs.getValue(Constants.PREF_SHOW_TASKS_BY_ID, null)); %>
			 <c:choose>	 
			 	<c:when test="<%= (isTaskIdEnabled) %>">
					'${formDescription}' - <b>'${formId}'</b>
			 	</c:when>
				<c:otherwise>
			  		<b>'${formDescription}'</b> - '${formId}'
				</c:otherwise>
			 </c:choose>
			 </div>	 
					<div class="FormValuesHeader">Lomakkeet:</div> 
					<table class="FormValues">
						<tbody>
						<tr class="headerRow">
							<th class="description rowHeader">Lomake:</th>
							<th class="value rowHeader">ID:</th>
						</tr>
						<c:forEach var="form" items="${formList}">
							<tr class="row">
								<td class="description">${form.task.description}</td>
								<td class="value">${form.task.id}</td>
							</tr>
						</c:forEach>
						</tbody>
					</table>
					<div class="FormValuesHelp">Lomakkeen sis‰ltˆ‰ ei voi muuttaa t‰‰lt‰. Muutokset t‰h‰n portletti instanssiin 
					t‰ytyy tehd‰ portaalin hallintapaneelin kautta (Admin -> Portlet instances).</div>
					
	 	</c:when>
		<c:otherwise>
	
		<!--  GateIn -->
		<form action="<portlet:actionURL portletMode="edit"><portlet:param name="action" value="config" /></portlet:actionURL>" method="post">	
				<span>N√§ytett√§v√§ lomake</span>
				<select name="showOnlyForm">
					<c:forEach var="form" items="${formList}">
						<c:set value="<%=prefs.getValue(Constants.PREF_SHOW_ONLY_FORM_BY_ID, null) %>" var="formId"></c:set>
						<c:choose>
							<c:when test="${formId eq form.task.id}">
								<option value="${form.task.id}" selected="selected">${form.task.description}</option>
							</c:when>
							<c:otherwise>
								<option value="${form.task.id}">${form.task.description}</option>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</select>
				<input type="submit" value="Tallenna">  
			</form>	
		</c:otherwise>
	</c:choose>
</div>


