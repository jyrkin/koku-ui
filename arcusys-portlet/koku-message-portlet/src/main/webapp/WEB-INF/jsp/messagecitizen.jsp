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

<portlet:actionURL var="homeURL">
	<portlet:param name="action" value="toHome" />
</portlet:actionURL>

<portlet:actionURL var="messageActionURL">
	<portlet:param name="action" value="toMessage" />
</portlet:actionURL>

<portlet:actionURL var="responseActionURL">
	<portlet:param name="action" value="toResponse" />
</portlet:actionURL>

<portlet:actionURL var="consentActionURL">
	<portlet:param name="action" value="toConsent" />
</portlet:actionURL>

<portlet:actionURL var="appointmentActionURL">
	<portlet:param name="action" value="toAppointment" />
</portlet:actionURL>

<portlet:actionURL var="warrantActionURL">
	<portlet:param name="action" value="toWarrant" />
</portlet:actionURL>


<%-- Do not move navigation helper inside <script> tags --%>
<%@ include file="js_koku_navigation_helper.jspf" %>

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

	 	ajaxTaskUrl : "<%= ajaxURL %>",
	 	homeUrl : "<%= homeURL %>",
		suggestUrl : "<%= suggestURL %>",
		archiveUrl : "<%= archiveURL %>",
		archiveOldUrl : "<%= archiveOldURL %>",
		deleteUrl : "<%= deleteURL %>", 
		revokeUrl : "<%=  revokeURL %>",
		revokeWarrantUrl : "<%= revokeWarrantURL %>", 
		cancelUrl : "<%= cancelURL %>", 
	 	
	 	messageUrl : "<%= messageActionURL %>",
	 	responseUrl :  "<%= responseActionURL %>",
	 	consentUrl : 	"<%= consentActionURL %>",
	 	appointmentUrl :  "<%= appointmentActionURL %>",
	 	warrantUrl : "<%= warrantActionURL %>"
	};

	<%-- Loading JS from separate jspf files instead of .js files. --%>
	<%-- Note that loading order in here is important! --%>
	<%@ include file="js_koku_config.jspf" %>
	<%@ include file="js_koku_utils.jspf" %>
	<%@ include file="js_koku_table.jspf" %>
	<%@ include file="citizen/js_koku_table_citizen.jspf" %>
	<%@ include file="js_koku_ajax.jspf" %>
	<%@ include file="citizen/js_koku_ajax_citizen.jspf" %> 
	<%@ include file="js_koku_suggestion.jspf" %>
	<%@ include file="js_koku_table_navigation.jspf" %>
	<%@ include file="citizen/js_koku_table_navigation_citizen.jspf" %>
	<%@ include file="citizen/js_koku_table_selector_citizen.jspf" %>

	/* Quick fix */
	function kokuInit() {		
		kokuConfig = new Config("<%= refreshDuration %>", "<%= messageType %>");
		pageObj = new Paging();
		kokuAjax = new KokuAjaxCitizen(ajaxUrls);
		kokuSuggestionBox = new SuggestionBox(ajaxUrls);
		kokuTableNavigation = new KokuTableNavigationCitizen(kokuAjax);		
	}
	
	
</script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.datepick.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.datepick-fi.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jgrowl_minimized.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.qtip.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-ui-1.8.20.custom.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.inputhints.min.js"></script>

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
						<input type="button" value="<spring:message code="message.searchReset"/>" onclick="resetSearch()" />
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
						<input type="text" title="123456-789A" name="recipient" id="recipient" style="width:100px;" />
						<span class="text-bold" ><spring:message code="consent.templateName" /></span>
						<input type="text" name="templateName" id="templateName" style="width:160px;" autocomplete="off" onkeydown="beKeyDown(event)" onkeyup="beKeyUp(event)" onclick="createSuggestDiv('consent-search', 'templateName')" />
						<input type="submit" value="<spring:message code="message.search"/>" />
						<input type="button" value="<spring:message code="message.searchReset"/>" onclick="resetSearch()" />
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

<div id="cancelAppointment" title="<spring:message code="appointmnet.dialog.caption"/>" style="display: none">
	<div class="cancelMessage"><spring:message code="appointmnet.dialog.description"/></div>
	<textarea id="kokuCancelMessage"></textarea>
</div>
