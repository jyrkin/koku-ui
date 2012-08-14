<%@ include file="/jsp/init.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>

<portlet:resourceURL var="sendWsRequestURL" id="sendWsRequest"></portlet:resourceURL>
<portlet:resourceURL var="serviceNamesURL" id="serviceNames"></portlet:resourceURL>
<portlet:resourceURL var="attachmentURL" id="attachment"></portlet:resourceURL>

<portlet:renderURL var="homeURL" windowState="<%= WindowState.NORMAL.toString() %>" >
	<portlet:param name="myaction" value="home" />
</portlet:renderURL>



<%-- Load jQuery --%>
<%-- This just temporary fix, because jQuery should be made available from theme or by portal not in portlet. --%>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.7.2.min.js"></script>
<%@ include file="js_koku_navigation_helper.jspf" %>
<script type="text/javascript">

	var timerOn = 0;
	var iFrameContentPreviousHeight = 0;
	var minHeight = 700;
	var iFrameId = '<portlet:namespace />xforms_iframe';

	function startResizer() {
		if (!timerOn) {
			timerOn = 1;
			resizeTimer();
		}
	}
	
	/* Meansure IFrame size */
	function resizeTimer() {
		var iFrameContentHeight = getIFrameBodyHeight(iFrameId);
		if (iFrameContentPreviousHeight != iFrameContentHeight) {
			var newHeight = iFrameContentHeight + 20;
			resizeIFrame(iFrameId, newHeight);
			/* 	We have to ask height again, because Chorme/Safari will change content height on some 
			 *	cases after resizing.
			 */
			iFrameContentPreviousHeight = getIFrameBodyHeight(iFrameId);
		}		
		setTimeout("resizeTimer()", 500 );
	}
	
	function setCompletedIframeSize() {
		resizeIFrame('<portlet:namespace />xforms_iframe', 300);
	}
			
	function resizeIFrame(id, height) {
		if (height < minHeight) {
			height = minHeight;
		}		
		document.getElementById(id).style.height = height+'px';
	}
	
	function getIFrameBodyHeight(id) {
		var iFrame =  document.getElementById(id);
		if (iFrame == null) {
			return minHeight;
		}
		
		var body;
		 
		if (!isIE()) { 
			 /* Firefox, Safari, Chrome etc. */
			  body = iFrame.contentDocument.getElementsByTagName('body')[0];
		} else { 
			 /*  InternetExplorer */
			 body = iFrame.contentWindow.document.getElementsByTagName('body')[0];
		}
		
		/* IE doesn't seem to be working correctly */
		if (isIE()) {
			if (body == undefined) {
				return iFrameContentPreviousHeight;
			}
			
			/* IE specific fix (for somereason IE can't set CSS styles by itself) */
			var jsx3id = '#_jsx_0_3';
			var jsx2id = '#_jsx_0_2';
			var jsx1id = '#_jsx_0_1';
			
			var jsx3 = iFrame.contentWindow.document.getElementById('_jsx_0_3');

			if (jsx3 != undefined || jsx3 != null ) {
				jsx3.style.display = 'block';
				jsx3.style.height = '100%';
				jsx3.style.overflow = 'visible';
				jsx3.style.overflowX = 'visible';
				jsx3.style.overflowY = 'visible';
				jsx3.style.position = 'absolute';
				jsx3.style.width = '100%';
			} 
			
			var jsx2 = iFrame.contentWindow.document.getElementById('_jsx_0_2');
			if (jsx2 != undefined || jsx2 != null ) {
				jsx2.style.height = '100%';
			}
			
			var jsx1 = iFrame.contentWindow.document.getElementById('_jsx_0_1');
			if (jsx1 != undefined || jsx1 != null ) {				
				jsx1.style.display = 'block';
				jsx1.style.height = '100%';
				jsx1.style.overflow = 'visible';
				jsx1.style.position = 'absolute';
				jsx1.style.width = '100%';
			}
			
			jQuery('._bmcss3flagsyntax').css('box-sizing', 'border-box');
							
			return body.scrollHeight + (body.offsetHeight - body.clientHeight);
		} else {
			return body.scrollHeight;		
		}
		
	}
	
	function isIE() {
		if (navigator.appName == 'Microsoft Internet Explorer') {
			return true;
		} else {
			return false;
		}
	}
	
	function getAttachment(path) {
		var url = formatUrl("<%= attachmentURL %>");		
		url = formatUrl(url);
		window.location = url + "&path=" +path;
	}
	
	
	function formatUrl(url) {
		var newUrl;
		newUrl = url.replace(/&quot;/g,'"');
		newUrl = newUrl.replace(/&amp;/g,"&");
		newUrl = newUrl.replace(/&lt;/g,"<");
		newUrl =  newUrl.replace(/&gt;/g,">");
		return newUrl;
	}

	/**
	 * KokuWS namespace
	 */
	var KokuWS = {}
				
	/**
	 * Simple function to send some example ajax data
	 *
	 * @param service ServiceName (e.g AppoimentService)
	 * @param data XML-data
	 */
	KokuWS.send = function(service, message) {
		var url=formatUrl("<%= sendWsRequestURL %>");
		var ajaxObject = {
				"service":service,
				"message":message
			};
		
		var result = jQuery.ajax( {
			url: url,
			type: "POST",
			data: ajaxObject,
			dataType: "html",
			async: false
		}).responseText;
		return jQuery.parseJSON(result);

	};
	
	/**
	 *  Returns Koku WS services
	 *
	 *  @return list of services
	 */
	 KokuWS.getKokuServices = function() {
		var url=formatUrl("<%= serviceNamesURL %>");
		var result = jQuery.ajax( {
			url: url,
			type: "POST",
			dataType: "html",
			async: false
		}).responseText;
		return jQuery.parseJSON(result);
	}

	/**
	 * Invokes a send request and handles the responses
	 *
	 * @return response string
	 */
	KokuWS.handleSend = function(serviceName, soapMessage) {

		var wsReply = KokuWS.send(serviceName, soapMessage);

		if (wsReply == null) {
			alert("Error completing proxy request. See browser console for request message.");
			console.log("Service name: " + serviceName + "; request: " + soapMessage);
		}
		else if (wsReply.result != "OK") {
			alert("Error completing proxy request. See browser console for request message and response.");
			console.log("Service name: " + serviceName + "; Request: " + soapMessage + "; Response: " + wsReply.wsMessage);
		}
		else {
			return wsReply.wsMessage;
		}
	};

	/**
	 * Invokes a send request and handles the responses
	 *
	 * @return response xml
	 */
	KokuWS.handleSendXML = function(serviceName, soapMessage) {
            var xmlString = KokuWS.handleSend(SERVICE_NAME, SOAP_MESSAGE);
            var xmlDoc = new jsx3.xml.Document();
            xmlDoc.loadXML(xmlString);

            return xmlDoc;
	};

	function getKokuServicesEndpoints() {
		var url="/palvelut-portlet/Services";
		var result = jQuery.ajax( {
				url: url,
				type: "POST",
				dataType: "html",
				async: false
			}).responseText;
		return jQuery.parseJSON(result);
	}
	 
	/* add the formId to the intalio form for editting appointment form */
	window.onload = function() {
		var global_url = document.URL;

		 if (global_url.indexOf("FormID=") > 0) {
			var newUrl = "${formholder.url}";
			var temp = global_url.split("FormID=");
			var param = temp[1];
			newUrl += "&FormID=" + param;			
			jQuery('#<portlet:namespace />xforms_iframe').attr('src', newUrl);
			// jQuery('a#printLink').attr('href',newUrl);
		} else {
			jQuery('#<portlet:namespace />xforms_iframe').attr('src', "${formholder.url}");
			// var link = jQuery('a#printLink');
			// link.attr('href', "${formholder.url}");
		}
		
		 /* Make sure that IFrame height is correct. We do not want to 
		  *	see any extra scrollbars. */
		resizeIFrame(iFrameId, minHeight);
	}

        function scrollToTop() {
// 		var iframe = document.getElementById('hiddenIframe');
//                 iframe.src = 'https://intalio:8443/xssEnabler.html?action=' + 'scrollToTop';
        };

	function modifyButton() {
		jQuery('#<portlet:namespace />xforms_iframe').contents().find('span[label=IntalioInternal_StartButton]').click(function() { scrollToTop(); });
	}

	jQuery(document).ready( function() {window.setInterval(modifyButton, 5000);
});
	
	function kokuIframePrint() {
		var iframeElement = document.getElementById('<portlet:namespace />xforms_iframe');
		iframeElement.focus();
		iframeElement.contentWindow.print();
	}
	
</script>

<portlet:renderURL var="viewURL" windowState="<%= WindowState.MAXIMIZED.toString() %>">
	<portlet:param name="action" value="view"/>
</portlet:renderURL>


<div id="intalioPrintingLink"><img title="Tulosta" src="${pageContext.request.contextPath}/images/print.png" /><a id="printLink" href="#" onclick="kokuIframePrint(); return false;">Tulosta</a></div>
<div id="form_wrap" style="margin:5px; position:relative; min-width: 720px;">
	<iframe src="" id="<portlet:namespace />xforms_iframe" class="xforms_container_iframe" frameborder="0" scrolling="no" horizontalscrolling="no" style="height: 700px; width:100%; overflow-x: hidden;"></iframe>
	<div id="task-manager-operation" class="task-manager-operation-part">
		<input type="button" value="<spring:message code="return"/>" onclick="history.back()" />
	</div>
</div>

<script type="text/javascript">
	startResizer();
</script>

