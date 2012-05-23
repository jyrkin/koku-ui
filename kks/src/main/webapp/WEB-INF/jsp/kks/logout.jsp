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
	<portlet:param name="action" value="choose" />
</portlet:renderURL>
<portlet:renderURL var="collectionUrl">
	<portlet:param name="action" value="showCollection" />
	<portlet:param name="pic" value="${pic}" />
	<portlet:param name="collection" value="${collection}" />
</portlet:renderURL>
<div class="koku-kks"> 
<div class="portlet-section-body">
<div>

	<div class="kks-home">
		<a href="${homeUrl}"><spring:message code="ui.kks.session.close" /> </a>
	</div>

</div>
<div>

<h1><spring:message code="ui.kks.session.ended.title"/></h1>

	<br/>
	<div class="kks-text">
		<spring:message code="ui.kks.session.ended"/>
	</div>
	
	<div class="kks-bottom-left">
		<a href="${collectionUrl}"><spring:message code="ui.kks.session.retry" /> </a>
	</div>
	
	<div class="kks-reset-floating">
	
	</div>
	
</div>


	<div class="kks-version">
		<%@ include file="../common/page-footer.jsp"%>
	</div>

</div>
