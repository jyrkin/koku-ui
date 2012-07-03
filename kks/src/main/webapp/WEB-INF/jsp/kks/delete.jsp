<%--
 Copyright 2012 Ixonos Plc, Finland. All rights reserved.
  
 This file is part of Kohti kumppanuutta.
 
 This file is licensed under GNU LGPL version 3.
 Please see the 'license.txt' file in the root directory of the package you received.
 If you did not receive a license, please contact the copyright holder
 (kohtikumppanuutta@ixonos.com).
--%>
<%@page import="fi.koku.kks.ui.common.DataType"%>
<%@ include file="imports.jsp"%>

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

<portlet:actionURL var="deleteCollection">
	<portlet:param name="action" value="deleteCollection" />
	<portlet:param name="pic" value="${child.pic}" />
	<portlet:param name="collection" value="${collection.id}" />
	<portlet:param name="collectionName" value="${collection.name}" />
	<portlet:param name="${csrf_token}" value="${csrf_token_value}" />
	<portlet:param name="collectionType"
		value="${collection.collectionClass.typeCode}" />
	<c:if test="${not empty fromGroup}">
		<portlet:param name="fromGroup" value="true" />
	</c:if>
	<c:if test="${not empty selected}">
		<portlet:param name="selected" value="${selected}" />
	</c:if>
</portlet:actionURL>
<portlet:actionURL var="cancelDeletion">
	<portlet:param name="action" value="showChild" />
	<portlet:param name="pic" value="${child.pic}" />
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

		<c:if test="${not empty error}">
			<div class="error">
				<spring:message code="${error}"></spring:message>
			</div>
		</c:if>

		<h1>
			<spring:message code="ui.kks.delete.title" />
		</h1>
		<h3>
			<c:out value="${child.name}" />
			<c:out value=" " />
			<c:out value="${collection.name}" />
		</h3>

		<div class="kks-entry kks-print">

			<form:form name="deleteCollection" commandName="deletable"
				method="post" action="${deleteCollection}">

				<div>
					<form:errors path="comment" cssClass="error" />
				</div>

				<span class="portlet-form-field-label"> <spring:message
						code="ui.kks.delete.reason" />
				</span>

				<div class="portlet-form-field">
					<fmt:message key="ui.kks.delete.description" var="titleLabel" />
					<form:textarea maxlength="500" path="comment"
						class="portlet-form-input-field" title="${titleLabel}" />
				</div>

				<div class="portlet-form-field">
					<fmt:message key="ui.kks.delete.send.message" var="messageLabel" />
					<fmt:message key="ui.kks.delete.send.description"
						var="messageTitleLabel" />
					<form:checkbox path="sendMessage" label="${messageLabel}"
						title="${messageTitleLabel}" />

				</div>
				<br />
				<div>

					<div class="kks-left">
						<input type="submit" class="portlet-form-button"
							value="<spring:message code="ui.kks.delete"/>">
					</div>

					<div class="kks-left">
						<input type="submit" class="portlet-form-button"
							onclick="addCancelToForm();"
							value="<spring:message code="ui.kks.cancel"/>">
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
	    $('#deletable')
	            .append(
	                    '<input name="cancel" type="hidden" value="true"/>');
	
	}
</script>
