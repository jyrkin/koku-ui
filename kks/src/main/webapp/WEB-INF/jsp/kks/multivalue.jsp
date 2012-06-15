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


<c:set var="free_text" value="<%=DataType.FREE_TEXT%>" />
<c:set var="text" value="<%=DataType.TEXT%>" />
<c:set var="multi_select" value="<%=DataType.MULTI_SELECT%>" />
<c:set var="select" value="<%=DataType.SELECT%>" />


<fmt:setBundle basename="com.ixonos.eservices.koku.bundle.KokuBundle" />


<portlet:defineObjects />

<portlet:renderURL var="homeUrl">
	<portlet:param name="action" value="showCollection" />
	<portlet:param name="pic" value="${child.pic}" />
	<portlet:param name="collection" value="${collection.id}" />	
	<c:if test="${not empty fromGroup}">
		<portlet:param name="fromGroup" value="true" />
	</c:if>
	<c:if test="${not empty selected}">
		<portlet:param name="selected" value="${selected}" />
	</c:if>		
</portlet:renderURL>
<portlet:actionURL var="addMultivalue">
	<portlet:param name="action" value="addMultivalue" />
	<portlet:param name="pic" value="${child.pic}" />
	<portlet:param name="collection" value="${collection.id}" />
	<portlet:param name="valueId" value="${valueId}" />
	
	<c:if test="${ not empty entryvalue }">
	   <portlet:param name="entryId" value="${entry.id}" />
	</c:if>
		<c:if test="${not empty fromGroup}">
		<portlet:param name="fromGroup" value="true" />
	</c:if>
	<c:if test="${not empty selected}">
		<portlet:param name="selected" value="${selected}" />
	</c:if>
</portlet:actionURL>
<portlet:actionURL var="removeMultivalue">
    <portlet:param name="action" value="removeMultivalue" />
    <portlet:param name="pic" value="${child.pic}" />
    <portlet:param name="collection" value="${collection.id}" />
    <portlet:param name="valueId" value="${valueId}" />
    <portlet:param name="value" value="${value}" />
    
    <c:if test="${ not empty entry }">
       <portlet:param name="entryId" value="${entry.id}" />
    </c:if>
    	<c:if test="${not empty fromGroup}">
		<portlet:param name="fromGroup" value="true" />
	</c:if>
	<c:if test="${not empty selected}">
		<portlet:param name="selected" value="${selected}" />
	</c:if>
</portlet:actionURL>
<portlet:actionURL var="cancelMultivalue">
    <portlet:param name="action" value="cancelMultivalue" />
    <portlet:param name="pic" value="${child.pic}" />
    <portlet:param name="collection" value="${collection.id}" />
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


<h1 class="portlet-section-header kks-print"><c:out value="${child.name}"/><c:out value=" "/><c:out value="${collection.name}"/></h1>

	<div class="kks-entry kks-print">

		<form:form name="addMultivalue" commandName="value"
			method="post" action="${addMultivalue}">
			<input type="hidden" name="entryType" value="${type.id }" />		
			         
				<span class="portlet-form-field-label">
				
				<c:out value="${type.name }"/>
				
				</span>
				<c:if test="${ not empty entry }">
				    <span style="padding-left: 5px"> <a href="${removeMultivalue}">(<spring:message code="ui.kks.remove" />)</a> </span>
				</c:if>
				
				<div class="portlet-form-field">
                	<form:textarea maxlength="2000" path="value" class="portlet-form-input-field" title="${type.description }" />
            	</div>

			<span > 			
			 <input type="submit" class="portlet-form-button"
				value="<spring:message code="ui.kks.contract.save"/>"> <span style="padding-left: 5px">
					<a href="${homeUrl}"><spring:message code="ui.kks.cancel" /> </a>  </span> 
			</span>

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
</script>
