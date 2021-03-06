<%@ include file="init.jsp" %>

<portlet:resourceURL var="ajaxURL" id="getTask">
</portlet:resourceURL>

<portlet:resourceURL var="suggestURL" id="getSuggestion">
</portlet:resourceURL>

<portlet:resourceURL var="archiveURL" id="archiveMessage">
</portlet:resourceURL>

<portlet:resourceURL var="archiveOldURL" id="archiveMessageOld">
</portlet:resourceURL>

<portlet:resourceURL var="deleteURL" id="deleteMessage">
</portlet:resourceURL>

<portlet:resourceURL var="revokeURL" id="revokeConsent">
</portlet:resourceURL>

<portlet:resourceURL var="revokeWarrantURL" id="revokeWarrants">
</portlet:resourceURL>

<portlet:resourceURL var="cancelURL" id="cancelAppointment">
</portlet:resourceURL>

<portlet:renderURL var="homeURL" windowState="<%= WindowState.NORMAL.toString() %>" >
	<portlet:param name="myaction" value="home" />
</portlet:renderURL>

<portlet:renderURL var="messageURL" windowState="<%= WindowState.NORMAL.toString() %>" >
	<portlet:param name="myaction" value="showMessage" />
</portlet:renderURL>

<portlet:renderURL var="requestURL" windowState="<%= WindowState.NORMAL.toString() %>" >
	<portlet:param name="myaction" value="showRequest" />
</portlet:renderURL>

<portlet:renderURL var="appointmentURL" windowState="<%= WindowState.NORMAL.toString() %>" >
	<portlet:param name="myaction" value="showAppointment" />
</portlet:renderURL>

<portlet:renderURL var="consentURL" windowState="<%= WindowState.NORMAL.toString() %>" >
	<portlet:param name="myaction" value="showConsent" />
</portlet:renderURL>

<portlet:renderURL var="citizenWarrantURL" windowState="<%= WindowState.NORMAL.toString() %>" >
	<portlet:param name="myaction" value="showWarrant" />
</portlet:renderURL>

<portlet:renderURL var="tipyURL" windowState="<%= WindowState.NORMAL.toString() %>" >
	<portlet:param name="myaction" value="showTipy" />
</portlet:renderURL>

<portlet:renderURL var="applicationKindergartenURL" windowState="<%= WindowState.NORMAL.toString() %>" >
	<portlet:param name="myaction" value="showApplicationKindergarten" />
</portlet:renderURL>


<portlet:resourceURL var="messageRenderURL" id="createMessageRenderUrl">
</portlet:resourceURL>

<portlet:resourceURL var="requestRenderURL" id="createRequestRenderUrl">
</portlet:resourceURL> 

<portlet:resourceURL var="responseRenderURL" id="createResponseRenderUrl">
</portlet:resourceURL> 

<portlet:resourceURL var="appointmentRenderURL" id="createAppointmentRenderUrl">
</portlet:resourceURL> 

<portlet:resourceURL var="consentRenderURL" id="createConsentRenderUrl">
</portlet:resourceURL>

<portlet:resourceURL var="warrantRenderURL" id="createWarrantRenderUrl">
</portlet:resourceURL>

<portlet:resourceURL var="tipyRenderURL" id="createTipyRenderUrl">
</portlet:resourceURL>

<portlet:resourceURL var="kindergartenRenderURL" id="createApplicationKindergartenRenderUrl">
</portlet:resourceURL>


<%-- Do not move navigation helper inside <script> tags --%>
<%@ include file="js_koku_navigation_helper.jspf" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.datepick.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.datepick-fi.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jgrowl_minimized.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.qtip.min.js"></script>

<script type="text/javascript">

	/* Global objects */
	var kokuConfig = null;
	var pageObj = null;
	var kokuAjax = null;
	var kokuSuggestionBox = null;
	var kokuTableNavigation = null;
	
	/**
	 *	URLs for ajaxCalls
	 */
	var ajaxUrls = {
	 	
	 	defaultUrl : "<%= portletPath %>",

		/* Actions or somethings? (portlet:resourceURL)*/
	 	ajaxTaskUrl : "<%= ajaxURL %>",
	 	homeUrl : "<%= homeURL %>",
		suggestUrl : "<%= suggestURL %>",
		archiveUrl : "<%= archiveURL %>",
		archiveOldUrl : "<%= archiveOldURL %>",
		deleteUrl : "<%= deleteURL %>", 
		revokeUrl : "<%=  revokeURL %>",
		revokeWarrantUrl : "<%= revokeWarrantURL %>", 
		cancelUrl : "<%= cancelURL %>", 
	 		 	
	 	/* Urls JBoss Loora  (portlet:resourceURL) */
	 	messageUrl : "<%= messageURL %>",
	 	requestUrl :"<%= requestURL %>",
	 	appointmentUrl : "<%= appointmentURL %>",
	 	responseRenderUrl :  "<%= responseRenderURL %>",
	 	consentUrl : "<%= consentURL %>",
		citizenWarrantUrl : "<%= citizenWarrantURL %>",
		tipyUrl : "<%= tipyURL %>",
		applicationKindergartenUrl : "<%= applicationKindergartenURL %>",
				
		/* RenderUrls GateIn (portlet:renderURL) */
	 	messageRenderUrl : "<%= messageRenderURL %>",
	 	requestRenderUrl : "<%= requestRenderURL %>",
	 	responseRenderUrl :  "<%= responseRenderURL %>",
	 	appointmentRenderUrl :  "<%= appointmentRenderURL %>",
	 	consentRenderUrl : 	"<%= consentRenderURL %>",
	 	warrantRenderUrl : "<%= warrantRenderURL %>",
		tipyRenderUrl : "<%= tipyRenderURL %>",
		kindergartenRenderUrl : "<%= kindergartenRenderURL %>"
	};
	
	<%-- Loading JS from separate jspf files. --%>
	<%-- Note that loading order in here is important! --%>
	<%@ include file="js_koku_config.jspf" %>
	<%@ include file="js_koku_utils.jspf" %>
	<%@ include file="js_koku_table.jspf" %>
	<%@ include file="employee/js_koku_table_employee.jspf" %>
	<%@ include file="js_koku_ajax.jspf" %>
	<%@ include file="employee/js_koku_ajax_employee.jspf" %>
	<%@ include file="js_koku_suggestion.jspf" %>
	<%@ include file="js_koku_table_navigation.jspf" %>
	<%@ include file="employee/js_koku_table_navigation_employee.jspf" %>
	<%@ include file="employee/js_koku_table_selector_employee.jspf" %>
	
	/* Quick fix */
	function kokuInit() {		
		kokuConfig = new Config("<%= refreshDuration %>", "<%= messageType %>");
		pageObj = new Paging();
		kokuAjax = new KokuAjaxEmployee(ajaxUrls);
		kokuSuggestionBox = new SuggestionBox(ajaxUrls);
		kokuTableNavigation = new KokuTableNavigationEmployee(kokuAjax);		
	}
</script>


<%@ include file="js_koku_main.jspf" %>

<div id="task-manager-outer-wrap">
	<div id="task-manager-wrap">
		<div id="task-manager-tasklist">
			<table class="task-manager-table">
			</table>
		</div>
		
		<div class="taskmanager-operation-part-wrapper">
			<div id="task-manager-search" class="task-manager-operation-part">
			
				<div id="message-search" class="basic-search" style="display:none;">
					<form name="searchForm" onsubmit="kokuTableNavigation.searchTasks(); return false;">		
						<span class="text-bold" ><spring:message code="message.searchKeyword" /></span>
						<input type="text" name="keyword" id="keyword" style="width:160px;" />
						<input type="submit" value="<spring:message code="message.search"/>" />
						<input type="button" value="<spring:message code="message.searchReset"/>" onclick="kokuTableNavigation.resetSearch()" />
						<span id="search-fields" >
							<input type="checkbox" checked="checked" name="field" value="1" /><spring:message code="message.from" />
							<input type="checkbox" checked="checked" name="field" value="2" /><spring:message code="message.to" />
							<input type="checkbox" checked="checked" name="field" value="3" /><spring:message code="message.subject" />
							<input type="checkbox" checked="checked" name="field" value="4" /><spring:message code="message.content" />
						</span>	
					</form>
				</div>
				
				<div id="consent-search" class="basic-search" style="display:none; position:relative;">
					<form name="searchForm" onsubmit="kokuTableNavigation.searchConsents(); return false;">		
						<span class="text-bold" ><spring:message code="consent.recipients" /></span>
						<input type="text" name="recipient" id="recipient" style="width:100px;" />
						<span class="text-bold" ><spring:message code="consent.templateName" /></span>
						<input type="text" name="templateName" id="templateName" style="width:160px;" autocomplete="off" onkeydown="kokuSuggestionBox.beKeyDown(event)" onkeyup="kokuSuggestionBox.beKeyUp(event)" onclick="kokuSuggestionBox.createSuggestDiv('consent-search', 'templateName')" />
						<input type="submit" value="<spring:message code="message.search"/>" />
						<input type="button" value="<spring:message code="message.searchReset"/>" onclick="kokuTableNavigation.resetSearch()" />
					</form>
				</div>
				
				<!-- Employee appointments search -->
				<div id="employeeAppointment-search" class="basic-search" style="display:none; position:relative;">
					<form name="searchForm" onsubmit="kokuTableNavigation.searchAppointmentsByTargetPersonSsn(); return false;">		
						<span class="text-bold" ><spring:message code="appointment.targetPerson" /></span>
						<input type="text" name="appointmentTargetPerson" id="appointmentTargetPerson" style="width:100px;" />
						<input type="submit" value="<spring:message code="message.search"/>" />
						<input type="button" value="<spring:message code="message.searchReset"/>" onclick="kokuTableNavigation.resetSearch()" />
					</form>
				</div>
				
				<!-- TIVA-13 Selaa asiakkaan suostumuksia -->
				<div id="warrants-search-citizens" class="basic-search" style="display:none; position:relative;">
					<form name="searchForm" onsubmit="kokuTableNavigation.searchWarrantsByCitizen(); return false;">		
						<span class="text-bold" ><spring:message code="warrant.recievedWarrants" /></span>
						<input type="text" name="userIdRecieved" id="userIdRecieved" style="width:100px;" />
						<span class="text-bold" ><spring:message code="warrant.sendedWarrants" /></span>
						<input type="text" name="userIdSent" id="userIdSent" style="width:100px;" />
						<span class="text-bold" ><spring:message code="warrant.targetPerson" /></span>
						<input type="text" name="targetPersonUid" id="targetPersonUid" style="width:100px;" />
		<%-- 				<span class="text-bold" ><spring:message code="warrant.templateName" /></span> --%>
		<!-- 				<input type="text" name="warrantTemplateNameCitizen" id="warrantTemplateNameCitizen" style="width:160px;" autocomplete="off" onkeydown="createSuggestDiv.beKeyDown(event)" onkeyup="createSuggestDiv.beKeyUp(event)" onclick="createSuggestDiv.createSuggestDiv('warrants-search-citizens', 'warrantTemplateNameCitizen')" /> -->
						<input type="submit" value="<spring:message code="message.search"/>" />
						<input type="button" value="<spring:message code="message.searchReset"/>" onclick="kokuTableNavigation.resetSearch()" />
					</form>
				</div>
				
				<!-- TIVA-14 Selaa asian suostumuksia -->
				<div id="warrants-search-warrants" class="basic-search" style="display:none; position:relative;">
					<form name="searchForm" onsubmit="kokuTableNavigation.searchWarrantsByTemplate(); return false;">		
						<span class="text-bold" ><spring:message code="warrant.templateName" /></span>
						<input type="text" name="warrantTemplateName" id="warrantTemplateName" style="width:160px;" autocomplete="off" onkeydown="kokuSuggestionBox.beKeyDown(event)" onkeyup="kokuSuggestionBox.beKeyUp(event)" onclick="kokuSuggestionBox.createSuggestDiv('warrants-search-warrants', 'warrantTemplateName')" />
						
		<%-- 				<span style="display: hidden;" class="text-bold" ><spring:message code="warrant.groupFilter" /></span> --%>
		<%-- 				<input style="display: hidden;" type="text" name="warrantGroupFilter" id="warrantGroupFilter" style="width:100px;" /> --%>
						<input type="submit" value="<spring:message code="message.search"/>" />
						<input type="button" value="<spring:message code="message.searchReset"/>" onclick="kokuTableNavigation.resetSearch()" />
					</form>
				</div>
				
				<!-- TIVA-18 Selaa tietopyynt�j� -->
				<div id="infoRequestsSearch" class="basic-search" style="display:none; position:relative;">
					<form name="searchForm" onsubmit="kokuTableNavigation.searchInfoRequests(); return false;">		
					
						<p class="searchTimeRange">
							<span id="tipyCreateTime">
								<span class="text-bold searchTitle" ><spring:message code="tipy.search.timeRange" /></span>
								<input class="searchTime" type="text" name="tipyTimeRangeFrom" id="tipyTimeRangeFrom"  /> - 
								<input class="searchTime" type="text" name="tipyTimeRangeTo" id="tipyTimeRangeTo" />
							</span>
						</p>
						
						<p class="searchMisc">
							<span class="text-bold searchTitle"><spring:message code="tipy.search.targetPerson" /></span>
							<input type="text" name="tipyTargetPerson" id="tipyTargetPerson" style="width:200px;" />
							
							<span class="text-bold searchTitle"><spring:message code="tipy.search.requester" /></span>
							<input type="text" name="tipyRequester" id="tipyRequester" style="width:200px;" />
						</p>
		
						<p class="searchMisc">					
							<span class="text-bold searchTitle"><spring:message code="tipy.search.handOver" /></span>
							<input type="text" name="tipyHandOver" id="tipyHandOver" style="width:200px;" />
		
							<span class="text-bold searchTitle"><spring:message code="tipy.search.information" /></span>
							<input type="text" name="tipyInformation" id="tipyInformation" style="width:200px;" />
						</p>
						<p class="searchMisc">
							<span class="text-bold searchTitle"><spring:message code="tipy.search.freeTextSearch" /></span>
							<input type="text" name="tipyFreeTextSearch" id="tipyFreeTextSearch" style="width:200px;" />
						</p>
						<p class="searchMisc searchButtonArea">
							<input type="submit" value="<spring:message code="message.search"/>" />
							<input type="button" value="<spring:message code="message.searchReset"/>" onclick="kokuTableNavigation.resetSearch()" />
						</p>
					</form>
				</div>
				
				<!-- HAK-2 Selaa p�iv�kotihakemuksia -->
				<div id="applicationKindergartenBrowse" class="basic-search" style="display:none; position:relative;">
					<form name="searchForm" onsubmit="kokuTableNavigation.searchKindergartenApplications(); return false;">		
									
						<p class="searchMisc kindergartenSearch">
							<span class="text-bold searchTitle"><spring:message code="application.kindergarten.search.kindergarten" /></span>
							<input type="text" name="applicationKindergartenName" id="applicationKindergartenName" style="width:160px;"  autocomplete="off" onkeydown="kokuSuggestionBox.beKeyDown(event)" onkeyup="kokuSuggestionBox.beKeyUp(event)" onclick="kokuSuggestionBox.createSuggestDiv('applicationKindergartenBrowse', 'applicationKindergartenName')" />					
						</p>
		
						<p class="searchMisc kindergartenSelections">					
							<span class="text-bold searchTitle"><spring:message code="application.kindergarten.search.guardianAccpeted" /></span>
						  	<select class="yesNoSelect" name="applicationKindergartendGuardianAccepted" id="applicationKindergartendGuardianAccepted">
						  	  <option selected="selected"></option>
							  <option value="true">Kyll�</option>
							  <option value="false">Ei</option>
							</select>
							<span class="text-bold searchTitle"><spring:message code="application.kindergarten.search.placeAccepted" /></span>
							<select class="yesNoSelect" name="applicationKindergartendPlaceCancelled" id="applicationKindergartendPlaceCancelled" >
							  <option selected="selected"></option>
							  <option value="true">Kyll�</option>
							  <option value="false">Ei</option>
							</select>					
							<span class="text-bold searchTitle"><spring:message code="application.kindergarten.search.guardianAcceptedHighestIncome" /></span>
							<select class="yesNoSelect" name="applicationKindergartendHighestIncome" id="applicationKindergartendHighestIncome">
							  <option selected="selected"></option>
							  <option value="true">Kyll�</option>
							  <option value="false">Ei</option>
							</select>				
						</p>
						<p class="searchMisc searchButtonArea">
							<input type="submit" value="<spring:message code="message.search"/>" />
							<input type="button" value="<spring:message code="message.searchReset"/>" onclick="kokuTableNavigation.resetSearch()" />
						</p>
					</form>
				</div>
			</div>
		</div>
		<div class="taskmanager-operation-part-wrapper">		
			<div id="task-manager-operation" class="task-manager-operation-part">
				<div id="task-manager-operation-page"></div>
				<div id="task-manager-operation-loading"><spring:message code="page.loading"/></div>
			</div>
		</div>
	</div>
</div>

