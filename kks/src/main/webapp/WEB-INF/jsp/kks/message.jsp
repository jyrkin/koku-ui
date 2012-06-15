<%--
 Copyright 2012 Ixonos Plc, Finland. All rights reserved.
  
 This file is part of Kohti kumppanuutta.
 
 This file is licensed under GNU LGPL version 3.
 Please see the 'license.txt' file in the root directory of the package you received.
 If you did not receive a license, please contact the copyright holder
 (kohtikumppanuutta@ixonos.com).
--%>
<%@page import="fi.koku.kks.ui.common.DataType"%>
<%@ include file="imports.jsp" %>

<fmt:setBundle basename="Language-ext" />

<portlet:defineObjects />

<portlet:renderURL var="homeUrl">
		<portlet:param name="action" value="showChild" />
		<portlet:param name="pic" value="${child.pic}" />

		<c:if test="${not empty fromGroup}">
			<portlet:param name="fromGroup" value="true" />
		</c:if>
		<c:if test="${not empty selected}">
			<portlet:param name="selected" value="${selected}" />
		</c:if>
				
</portlet:renderURL>

<portlet:actionURL var="sendMessageURL">
    <portlet:param name="action" value="sendMessage" />
    <portlet:param name="pic" value="${child.pic}" />
    <portlet:param name="collectionName" value="${collectionName}" />
    <portlet:param name="childName" value="${childName}" />
   
   	<c:if test="${not empty fromGroup}">
		<portlet:param name="fromGroup" value="true" />
	</c:if>
	<c:if test="${not empty selected}">
		<portlet:param name="selected" value="${selected}" />
	</c:if>
</portlet:actionURL>


<div class="koku-kks"> 
<div class="portlet-section-body">
<div>

	<div class="kks-home">
		<a href="${homeUrl}"><spring:message code="ui.kks.back" /> </a>
	</div>

</div>

<c:if test="${not empty error}"><div class="error"><spring:message code="${error}"></spring:message> </div></c:if>

<h1><spring:message code="ui.kks.send.title"/></h1>

	<div class="kks-entry kks-print">

		<form:form name="sendMessage" commandName="kks_message" method="post" action="${sendMessageURL}">				
			<br/>
			        			    
				<div class="portlet-form-field-label">				
					<spring:message code="ui.kks.send.message.title"/>				
				</div>
				<div>  
			    	<form:errors path="title" cssClass="error" />
			    </div>	
			    <div class="portlet-form-field">
					<form:input maxlength="100" path="title" size="100"/>
				</div>
				
				<div class="portlet-form-field-label">				
					<spring:message code="ui.kks.send.message"/>				
				</div>
				<div>  
			    	<form:errors path="message" cssClass="error" />
			    </div>
				<div class="portlet-form-field">
                	<form:textarea maxlength="500" path="message" class="portlet-form-input-field"/>
            	</div>
			<br/>
			<div>
			
			<div class="kks-left">
				<input type="submit" class="portlet-form-button" value="<spring:message code="ui.kks.send.button"/>"> 
			</div>
			
			<div class="kks-left">			
				<input type="submit" class="portlet-form-button" onclick="addCancelToForm();"  value="<spring:message code="ui.kks.cancel"/>">
			</div>
			<div class="kks-reset-floating"></div> 
			
			</div>

		</form:form>
	</div>
	
	<div class="kks-reset-floating"></div>

	</div>

	<div class="kks-version">
		<%@ include file="../common/page-footer.jsp"%>
	</div>

</div>

<script type="text/javascript">

window.onload = function() { 
	  var txts = document.getElementsByTagName('TEXTAREA') 

	  for(var i = 0, l = txts.length; i < l; i++) {
	    if(/^[0-9]+$/.test(txts[i].getAttribute("maxlength"))) { 
	      var func = function() { 
	        var len = parseInt(this.getAttribute("maxlength"), 10); 

	        if(this.value.length > len) { 
	          this.value = this.value.substr(0, len); 
	          return false; 
	        } 
	      }

	      txts[i].onkeyup = func;
	      txts[i].onblur = func;
	    } 
	  } 
	}
		
	function addCancelToForm() {
	    $('#kks_message')
	            .append(
	                    '<input name="cancel" type="hidden" value="true"/>');
	
	}
</script>