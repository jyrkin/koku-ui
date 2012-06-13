<%@ include file="init.jsp" %>

<portlet:resourceURL var="ajaxURL" id="update">
</portlet:resourceURL>

<portlet:resourceURL var="naviStatusURL" id="naviStatus">
</portlet:resourceURL>

<portlet:renderURL var="naviURL" >
	<portlet:param name="myaction" value="showNavi" />
</portlet:renderURL>

<portlet:resourceURL var="naviRenderURL" id="createNaviRenderUrl">
</portlet:resourceURL>

<%
	/* Parses the parent path url from the portlet renderURL */
	String currentPage = "";
	String actionParam = "";	
	
	if (portalInfo.contains("JBoss Portal")) {
		int pos1 = naviURL.lastIndexOf("/");
		actionParam = naviURL.substring(pos1);
		String currentPath = naviURL.substring(0, pos1);
		int pos2 = currentPath.lastIndexOf("/");
		currentPage = currentPath.substring(pos2+1);
	} else {
		/* EPP spesific */	
		int pos2 = naviURL.indexOf("?");
		String currentPath = naviURL.substring(0, pos2);
		int pos3 = currentPath.lastIndexOf("/");
		currentPage = currentPath.substring(pos3+1);
	}
	
	
// 	System.out.println("------------------------------------------------------------------------------------------------------");
// 	System.out.println("defaultPath: '"+defaultPath+"' currentPage: '"+currentPage+"' actionParam: '"+actionParam+"' \n NaviURL: '"+naviURL+"'");
// 	System.out.println("NAVI MODE: "+naviPortalMode);
// 	System.out.println("portalInfo: "+portalInfo);
// 	System.out.println("------------------------------------------------------------------------------------------------------");
			
%>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/jquery-1.7.2.min.js"></script>
<script type="text/javascript">
	<%@ include file="js_navi.jspf" %>
</script>


<div id="koku-navigation">
	<a href="#"><img src="<%= request.getContextPath() %>/images/kklogo.jpg" width="189" height="59" />
	</a>
	<ul class="main">
		
<%
	String kksPath = kksPref; 
	String lokPath = lokPref; 
	String pyhPath = pyhPref;
%>		
		
		<li><a href="<%= frontPagePath %>">Etusivu</a></li>
		<li id="kks"><a href="<%= kksPath %>">Sopimukset ja suunnitelmat</a>
		<li id="lok"><a href="<%= lokPath %>">Lokihallinta</a></li>
		
		<!--  VIESTIT -->
		<li>
			<span class="naviLinkHeaderNonLink" onclick="toggleKokuNavi('#kokuNaviMessages', '#kokuNaviTreeNodeIndicatorMessage');">
				<span class="kokuNaviTreeNodeIndicator" id="kokuNaviTreeNodeIndicatorMessage">+</span>Viestit<span id="inbox_num_super" class="message_num"></span>
			</span>
			<ul id="kokuNaviMessages" class="child" style="display:none;">
				<!-- Show "New message" only for employee in Jboss portal -->
				<li id="msg_new"><a href="<%= defaultPath %><%= NavigationPortletProperties.MESSAGES_NEW_MESSAGE %>">Uusi viesti</a> </li>
				<li id="msg_inbox"><a href="javascript:void(0)" onclick="navigateToPage('msg_inbox')">Saapuneet</a><span id="inbox_num" class="message_num"></span></li>
				<li id="msg_outbox"><a href="javascript:void(0)" onclick="navigateToPage('msg_outbox')">L�hetetyt</a></li>
				<li>
					<span class="naviLinkHeaderNonLink" onclick="toggleKokuNavi('#archive-part', '#kokuNaviTreeNodeIndicatorMessageArchived');">
						<span class="kokuNaviTreeNodeIndicator" id="kokuNaviTreeNodeIndicatorMessageArchived">+</span>Arkistoidut<span id="archive_inbox_num_super" class="message_num"></span>
					</span>
					<ul id="archive-part" class="child" style="display:none;">
						<li id="msg_archive_inbox"><a href="javascript:void(0)" onclick="navigateToPage('msg_archive_inbox')">Saapuneet</span></a><span id="archive_inbox_num" class="message_num"></span></li>
						<li id="msg_archive_outbox"><a href="javascript:void(0)" onclick="navigateToPage('msg_archive_outbox')">L�hetetyt</a></li>
					</ul>
				</li>
			</ul>
		</li>
			
		<!--  PYYNN�T -->
		<li>
			<span class="naviLinkHeaderNonLink" onclick="toggleKokuNavi('#kokuNaviRequests', '#kokuNaviTreeNodeIndicatorConstants');">
				<span class="kokuNaviTreeNodeIndicator" id="kokuNaviTreeNodeIndicatorConstants">+</span>Pyynn�t<span id="requests_num_super_super" class="message_num"></span>
			</span>
			<ul id="kokuNaviRequests" class="child" style="display:none;">
				<li id="req_new"><a href="<%= defaultPath %><%= NavigationPortletProperties.REQUESTS_NEW_REQUEST %>">Uusi pyynt�</a></li>
				<li id="luopohja"><a href="<%= defaultPath %><%= NavigationPortletProperties.REQUESTS_NEW_TEMPLATE%>">Luo uusi pohja</a></li>
				<li>
					<span class="naviLinkHeaderNonLink" onclick="toggleKokuNavi('#kokuNaviRequestsRecevied', '#kokuNaviTreeNodeIndicatorRequestsRecevied');">
						<span class="kokuNaviTreeNodeIndicator" id="kokuNaviTreeNodeIndicatorRequestsRecevied">+</span>Saapuneet<span id="requests_num_super" class="message_num"></span>
					</span>
					<ul id="kokuNaviRequestsRecevied" class="child"  style="display:none;">
						<li id="req_valid_request"><a href="javascript:void(0)" onclick="navigateToTaskMgrPage('<%= NavigationPortletProperties.REQUESTS_RECIEVED_REQUESTS %>')">Saapuneet</a><span id="requests_num" class="message_num"></span></li>
						<li id="<%=Constants.TASK_TYPE_REQUEST_REPLIED %>"><a href="javascript:void(0)" onclick="navigateToPage('<%=Constants.TASK_TYPE_REQUEST_REPLIED %>')">Vastatut</a></li>
						<li id="<%=Constants.TASK_TYPE_REQUEST_OLD %>"><a href="javascript:void(0)" onclick="navigateToPage('<%=Constants.TASK_TYPE_REQUEST_OLD %>')">Vanhat</a></li>
					</ul>
				</li>
				<li>
					<span class="naviLinkHeaderNonLink" onclick="toggleKokuNavi('#kokuNaviRequestsSent', '#kokuNaviTreeNodeIndicatorRequestsSent');">
						<span class="kokuNaviTreeNodeIndicator" id="kokuNaviTreeNodeIndicatorRequestsSent">+</span>L�hetetyt
					</span>
					<ul id="kokuNaviRequestsSent" class="child">
						<li id="<%=Constants.TASK_TYPE_REQUEST_VALID_EMPLOYEE %>"><a href="javascript:void(0)" onclick="navigateToPage('<%=Constants.TASK_TYPE_REQUEST_VALID_EMPLOYEE %>')">Avoimet</a></li>
						<li id="<%=Constants.TASK_TYPE_REQUEST_DONE_EMPLOYEE %>"><a  href="javascript:void(0)" onclick="navigateToPage('<%=Constants.TASK_TYPE_REQUEST_DONE_EMPLOYEE %>')">Valmiit</a></li>
					</ul>
				</li>
			</ul>
		</li>
		
		<!-- TAPAAMISET -->
		<li>
			<span class="naviLinkHeaderNonLink" onclick="toggleKokuNavi('#kokuNaviAppointment', '#kokuNaviTreeNodeIndicatorApp');">
				<span class="kokuNaviTreeNodeIndicator" id="kokuNaviTreeNodeIndicatorApp">+</span>Tapaamiset
			</span>
			<ul id="kokuNaviAppointment" class="child" style="display:none;">
				<li id="app_new"><a href="<%= defaultPath %><%= NavigationPortletProperties.APPOINTMENTS_NEW_APPOINTMENT %>" >Uusi tapaaminen</a></li>
				<li id="app_inbox_employee"><a href="javascript:void(0)" onclick="navigateToPage('app_inbox_employee')">Avoimet</a></li>
				<li id="app_response_employee"><a href="javascript:void(0)" onclick="navigateToPage('app_response_employee')">Valmiit</a></li>
			</ul>
		</li>
		
		<!--  SUOSTUMUKSET -->
		<li>
			<span class="naviLinkHeaderNonLink" onclick="toggleKokuNavi('#kokuNaviConsents', '#kokuNaviTreeNodeIndicatorConsents');">
				<span class="kokuNaviTreeNodeIndicator" id="kokuNaviTreeNodeIndicatorConsents">+</span>Suostumukset
			</span>
			<ul id="kokuNaviConsents" class="child" style="display:none;">
				<li id="cst_new"><a href="<%= defaultPath %><%= NavigationPortletProperties.CONSENTS_NEW_CONSENT_TEMPLATE %>">Uusi suostumuspohja</a></li>
				<li id="sendconsent"><a href="<%= defaultPath %><%= NavigationPortletProperties.CONSENTS_NEW_CONSENT %>">Uusi suostumuspyynt�</a></li>
				<li id="fillconsent"><a href="<%= defaultPath %><%= NavigationPortletProperties.CONSENTS_CUSTOMER_CONSENT %>">Kirjaa asiakkaan suostumus</a></li>				
				<li id="<%= Constants.TASK_TYPE_CONSENT_EMPLOYEE_CONSENTS%>"><a href="javascript:void(0)" onclick="navigateToPage('<%= Constants.TASK_TYPE_CONSENT_EMPLOYEE_CONSENTS%>')">L�hetetyt suostumuspyynn�t</a></li>
			</ul>
		</li>
		
		<!--  VALTAKIRJAT -->
		<li>
			<span class="naviLinkHeaderNonLink"  onclick="toggleKokuNavi('#kokuNaviWarrants', '#kokuNaviTreeNodeIndicatorWarrants')">
				<span class="kokuNaviTreeNodeIndicator" id="kokuNaviTreeNodeIndicatorWarrants">+</span>Valtakirjat
			</span>
			<ul id="kokuNaviWarrants" class="child" style="display:none;">
				<li id="<%= Constants.TASK_TYPE_WARRANT_LIST_CITIZEN_CONSENTS%>"><a href="javascript:void(0)" onclick="navigateToPage('<%= Constants.TASK_TYPE_WARRANT_LIST_CITIZEN_CONSENTS%>')">Asiakkaan valtakirjat </a></li> 
				<li id="<%= Constants.TASK_TYPE_WARRANT_LIST_SUBJECT_CONSENTS%>"><a href="javascript:void(0)" onclick="navigateToPage('<%= Constants.TASK_TYPE_WARRANT_LIST_SUBJECT_CONSENTS%>')">Asian valtakirjat</a></li>
			</ul>
		</li>
			
		<!--  TIETOPYYNN�T -->
		<li>
			<span class="naviLinkHeaderNonLink" onclick="toggleKokuNavi('#kokuNaviInfoRequests', '#kokuNaviTreeNodeIndicatorInfoRequests');">
				<span class="kokuNaviTreeNodeIndicator" id="kokuNaviTreeNodeIndicatorInfoRequests">+</span>Tietopyynn�t<span id="infoRequests_num_super" class="message_num"></span>
			</span>
			<ul id="kokuNaviInfoRequests" class="child" style="display:none;">
				<li id="newinformation"><a href="<%= defaultPath %><%= NavigationPortletProperties.INFO_REQ_NEW_INFORMATION_REQ %>">Uusi tietopyynt�</a></li>
				<li id="informationbox"><a href="javascript:void(0)" onclick="navigateToTaskMgrPage('<%= NavigationPortletProperties.INFO_REQ_RECIEVED_INFO_REQS %>')">Saapuneet</a><span id="infoRequests_num" class="message_num"></span></li>
				<li>
					<span class="naviLinkHeaderNonLink" onclick="toggleKokuNavi('#kokuNaviInfoRequestsSent', '#kokuNaviTreeNodeIndicatorInfoRequestsSent');">
						<span class="kokuNaviTreeNodeIndicator" id="kokuNaviTreeNodeIndicatorInfoRequestsSent">+</span>L�hetetyt
					</span>
					<ul id="kokuNaviInfoRequestsSent" class="child" style="display:none;">
						<li id="<%= Constants.TASK_TYPE_INFO_REQUEST_BROWSE_SENT%>"><a href="javascript:void(0)" onclick="navigateToPage('<%= Constants.TASK_TYPE_INFO_REQUEST_BROWSE_SENT%>')">L�hetetyt</a></li>
						<li id="<%= Constants.TASK_TYPE_INFO_REQUEST_BROWSE_REPLIED %>"><a href="javascript:void(0)" onclick="navigateToPage('<%= Constants.TASK_TYPE_INFO_REQUEST_BROWSE_REPLIED%>')">Vastatut</a></li>
					</ul>
				</li>
				<li id="<%= Constants.TASK_TYPE_INFO_REQUEST_BROWSE%>"><a href="javascript:void(0)" onclick="navigateToPage('<%= Constants.TASK_TYPE_INFO_REQUEST_BROWSE%>')">Selaa tietopyynt�j�</a></li>				
			</ul>
		</li>
		
		<!--  ASIOINTIPALVELUT -->
		<li>
			<span class="naviLinkHeaderNonLink" onclick="toggleKokuNavi('#kokuNaviApplications', '#kokuNaviTreeNodeIndicatorApplications');">
				<span class="kokuNaviTreeNodeIndicator" id="kokuNaviTreeNodeIndicatorApplications">+</span>Asiointipalvelut
			</span>
			<ul id="kokuNaviApplications" class="child" style="display:none;">
				<li>
					<span class="naviLinkHeaderNonLink" onclick="toggleKokuNavi('#kokuNavikokurApplicationsService', '#kokuNaviTreeNodeIndicatorApplicationsService');">
						<span class="kokuNaviTreeNodeIndicator" id="kokuNaviTreeNodeIndicatorApplicationsService">+</span>Palveluhakemukset
					</span>
					<ul id="kokuNavikokurApplicationsService" class="child">
					<li id="<%= Constants.TASK_TYPE_APPLICATION_PROSESSED_BROWSE%>"><a href="javascript:void(0)" onclick="navigateToTaskMgrPage('<%= NavigationPortletProperties.APPLICATIONS_PROCESSED_LIST %>')">K�sitellyt hakemukset</a></li>	
						<li id="<%= Constants.TASK_TYPE_APPLICATION_KINDERGARTEN_BROWSE%>"><a href="javascript:void(0)" onclick="navigateToPage('<%= Constants.TASK_TYPE_APPLICATION_KINDERGARTEN_BROWSE%>')">P�iv�hoitohakemukset</a></li>
						<li id="<%= Constants.TASK_TYPE_APPLICATION_DAYCARE_PAYMENT_BROWSE%>"><a href="javascript:void(0)" onclick="navigateToTaskMgrPage('<%= NavigationPortletProperties.APPLICATIONS_DAYCARE_PAYMENT_BROWSE %>')">P�iv�hoidon asiakasmaksulomakkeet</a></li>
						<li id="<%= Constants.TASK_TYPE_APPLICATION_DAYCARE_PAYMENT_MODIFY_BROWSE%>"><a href="javascript:void(0)" onclick="navigateToTaskMgrPage('<%= NavigationPortletProperties.APPLICATIONS_DAYCARE_PAYMENT_MODIFY_BROWSE %>')">P�iv�hoidon asiakasmaksun muutoslomakkeet</a></li>
						<li id="<%= Constants.TASK_TYPE_APPLICATION_DAYCARE_PAYMENT_DISCOUNT_BROWSE%>"><a href="javascript:void(0)" onclick="navigateToTaskMgrPage('<%= NavigationPortletProperties.APPLICATIONS_DAYCARE_PAYMENT_DISCOUNT_BROWSE %>')">P�iv�hoidon asiakasmaksunalentamiset</a></li>						
						<li id="<%= Constants.TASK_TYPE_APPLICATION_DAYCARE_TERMINATION_BROWSE%>"><a href="javascript:void(0)" onclick="navigateToTaskMgrPage('<%= NavigationPortletProperties.APPLICATIONS_DAYCARE_TERMINATION_BROWSE %>')">Irtisanoutumiset p�iv�hoitopaikasta</a></li>
						
						<span class="naviLinkHeaderNonLink" onclick="toggleKokuNavi('#kokuNavikokurApplicationsDaycare', '#kokuNaviTreeNodeIndicatorDaycare');">
							<span class="kokuNaviTreeNodeIndicator" id="kokuNaviTreeNodeIndicatorDaycare">+</span>Loma-aikojen hoitotarvekysely
						</span>
						<ul id="kokuNavikokurApplicationsDaycare" class="child">
							<li id="daycareHolidays"><a href="<%= defaultPath %><%= NavigationPortletProperties.APPLICATIONS_DAYCARE_HOLIDAYS_FORM %>">Uusi kysely</a></li>
							<li id="<%= Constants.TASK_TYPE_APPLICATION_DAYCARE_HOLIDAYS_ANSWERED_BROWSE%>"><a href="javascript:void(0)" onclick="navigateToTaskMgrPage('<%= NavigationPortletProperties.APPLICATIONS_DAYCARE_HOLIDAYS_ANSWERED_BROWSE %>')">K�sitellyt hakemukset</a></li>
							
					</ul>
				</li>
			</ul>
		</li>
		<li><a href="<%= helpLinkPath %>">Ohjeet</a></li>
	</ul>
</div>
