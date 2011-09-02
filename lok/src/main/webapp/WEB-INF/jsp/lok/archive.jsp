<%@ include file="imports.jsp" %>

<portlet:defineObjects />

<portlet:actionURL var="archiveActionURL">
	<portlet:param name="action" value="archiveLog" />
</portlet:actionURL>
 
<portlet:actionURL var="startArchiveActionURL">
	<portlet:param name="action" value="startArchiveLog" />
</portlet:actionURL>

<portlet:renderURL var="homeURL">
	<portlet:param name="action" value="choose" />
</portlet:renderURL>

<portlet:renderURL var="searchUserURL">
	<portlet:param name="action" value="searchUser" />
</portlet:renderURL>


<div class="koku-lok">
<div class="portlet-section-body">

	<div class="home">
		<a href="${homeURL}"><spring:message code="koku.common.back" />
		</a>
	</div>
	
		<h1 class="portlet-section-header">
			<spring:message code="koku.lok.header.archive" />
		</h1>
		
		<div class="portlet-menu">
			<ul>
				<li class="portlet-menu-item"><a href="${searchUserURL}"><spring:message code="koku.lok.menu.item.search"/></a></li>
				<li class="portlet-menu-item-selected"><spring:message code="koku.lok.menu.item.archive"/></li>
			</ul>
		</div>

		<c:if test="${empty archiveDate}">
		
			<div class="log-archive">
				<form:form name="logArchiveForm" commandName="logArchiveDate"
					method="post" action="${archiveActionURL}">

					<%-- TODO: Add error handling! Now the user can give any date and the
					parsing error is shown on the web page. --%> 
					<%-- TODO: Add a javascript date picker here? --%>
					<span class="form-field-label"><spring:message
							code="koku.common.archiveDate" /> </span>
							<form:input path="endDate"value="${defaultDate}" />
					<span class="errors"><form:errors path="endDate" />
					</span>
						<input type="submit"
						value="<spring:message code="koku.lok.button.archive"/>">

					<div class="clear"></div>
				</form:form>			
			</div>
		</c:if>

		<c:if test="${not empty archiveDate}">

<%-- NOTE! The archivedate is not passed back to the first page! If the user wants to 
change the date, the default date is shown.
This is because the archiveDate parameter is used in the logic of the page
(empty or not empty). --%>

	
		<p><spring:message code="koku.lok.archive.confirmation"/> <fmt:formatDate
									pattern="dd.MM.yyyy" value="${archiveDateDate}" />.</p> 

			<form:form name="changeArchiveDateForm"	commandName="logArchiveDate" 
				method="post" action="${archiveActionURL}">
				<input type="submit"
					value="<spring:message code="koku.common.changeDate"/>">
			</form:form>

			<form:form method="post" action="${startArchiveActionURL}">
			
			<input type="hidden" name="endDate" value="${archiveDate}"/>
			
				<input type="submit"				
					value="<spring:message code="koku.common.startArchive"/>">
			</form:form>
		</c:if>
	<br/>
	<br/>
	</div>

</div><!-- end of koku-lok-div -->
