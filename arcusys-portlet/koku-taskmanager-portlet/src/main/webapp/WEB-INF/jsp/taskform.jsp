<%@ include file="init.jsp"%>
<portlet:renderURL var="homeURL" windowState="<%= WindowState.NORMAL.toString() %>" >
	<portlet:param name="myaction" value="home" />
</portlet:renderURL>

<portlet:resourceURL var="sendWsRequestURL" id="sendWsRequest"></portlet:resourceURL>
<portlet:resourceURL var="serviceNamesURL" id="serviceNames"></portlet:resourceURL>


<%@ include file="js_koku_navigation_helper.jspf" %>
	<!-- taskform.jsp -->
	
<% final String tasklink = renderRequest.getParameter(Constants.ATTR_TASK_LINK); %>

<script type="text/javascript" src="<%= request.getContextPath() %>/js/taskManagerResizer.js"></script>
<script type="text/javascript"> 
	/* the times that iframe loads different srcs */
	var loadingTimes = 0;
	/* the source url that iframe loads at first time */
	var firstUrl = "";	
	/**
	 * Checks the operation of forms finished or not. Usually the form operation is
	 * finished if the source url forwards to another url such as empty.jsp
	 */
	function checkForm() {
		var url = jQuery('#taskform').attr("src");
		
		if(loadingTimes == 0) {
			firstUrl = url;
		}
		
		if(firstUrl != url) {
			returnMainPage();
		}
		
		loadingTimes += 1;
	}

	
	
	function setCompletedIframeSize() {
		function resizeIFrame(id, height) {
			if (height < minHeight) {
				height = minHeight;
			}		
			document.getElementById(id).style.height = height+'px';
		}
		resizeIFrame('taskform', 300);
	}
	
	function getKokuServicesEndpoints() {
		var url="/palvelut-portlet/Services";
		return jQuery.parseJSON(
			 jQuery.ajax( {
				url: url,  
				type: "POST", 
			    dataType: "html",
				async: false 
			}).responseText);
	}
	
	/* In EPP portlet won't expand fully because PORTLET-FRAGMENT has inline style width: 770px 
		Unfortunately there is no easy way to change this size using CSS. */
	function eppHack() {		
		jQuery('#task-manager-wrap').parent().css('width', '100%');		
	}
	
	function kokuIframePrint() {
		var iframeElement = document.getElementById('taskform');
		iframeElement.focus();
		iframeElement.contentWindow.print();
	}
	
	jQuery(document).ready(function() {
		<%-- var taskFormPath = decodeURIComponent('<c:out value="${tasklink}" />'); --%>
		var taskFormPath = decodeURIComponent('<%= tasklink %>');		
		jQuery('#taskform').attr('src', taskFormPath);		
		startResizer("taskform");
		eppHack();
	});
	
	
	<%-- WTF? --%>
	/* Simple function to send some example ajax data */
	function ajaxSampleData() {	
		var command = jQuery('#ajaxCommand').val();
		var data = jQuery('#ajaxData').val();		
		var result = sendKokuWS(command, data);
		jQuery(".test").append("<div><pre>"+result.toString()+"</pre></div>");
	}
	
	function doSomething(data) {
		var obj = JSON.parse(data);
		var json = obj.response;
		var test1 = json["result"];
		jQuery(".test").append("<div><pre>"+test1+"</pre></div>");
	}	

	/**
	 *  Returns Koku WS services
	 * 
	 *	@return list of services
	 */
	function getKokuWsServices() {
		var result = getKokuServices();
		jQuery(".test2").append("<div><pre>"+result+"</pre></div>");
	}
	
	/**
	 * Simple function to send some example ajax data 
	 * 
	 * @param service ServiceName (e.g AppoimentService)
	 * @param data XML-data
	 */
	function sendKokuWS(service, message) {	
		var url="<%= sendWsRequestURL %>";		
		var ajaxObject = {
				"service":service,
				"message":message
			};
		
		return jQuery.ajax( {
			url: url,  
			type: "POST", 
			data: ajaxObject, 
		    dataType: "html",
			async: false 
		}).responseText;
	}
	
	
	
	function getKokuServices() {
		var url="<%= serviceNamesURL %>";
		return jQuery.parseJSON(
			jQuery.ajax( {
				url: url,  
				type: "POST", 
			    dataType: "html",
				async: false 
			}).responseText);
	}
	
	<%-- End: WTF? --%>

	
</script>

 <div class="test" style="display: block;">
	<div class="testTextAreas">
		<textarea id="ajaxCommand" rows="3" cols="15" name="Command:"></textarea>
		<textarea id="ajaxData" rows="3" cols="50" name="Data:"></textarea>
	</div>	
	<button type="button" id="ajaxTest" name="Send data" onclick="ajaxSampleData()">Send data</button>
</div>
<div class="test2" style="display: block;">
	<button type="button" id="ajaxTest" name="Send data" onclick="getKokuWsServices()">Show services</button>
</div>

<div id="task-manager-wrap">
	<div id="task-manager-tasklist">
		<div id="intalioPrintingLink"><img title="Tulosta" src="${pageContext.request.contextPath}/images/print.png" /><a id="printLink" href="#" onclick="kokuIframePrint(); return false;">Tulosta</a></div>
		<iframe src="" id="taskform" name="taskform" style="width:100%; height:100%" frameborder="0" scrolling="no" ></iframe>
	</div>
	<div id="task-manager-operation" class="task-manager-operation-part">
		<input type="button" value="<spring:message code="task.return"/>" onclick="kokuNavigationHelper.returnMainPage()" />
	</div>
</div>

