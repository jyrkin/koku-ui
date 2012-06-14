<%@ include file="init.jsp"%>

<portlet:actionURL var="homeURL">
	<portlet:param name="action" value="toHome" />
</portlet:actionURL>

<%@ include file="js_koku_detail.jspf" %>
<%@ include file="js_koku_navigation_helper.jspf" %>
<%@ include file="js_koku_reset_view.jspf" %>


 
<div id="task-manager-wrap" class="single">
	<div><h1>Show here intalio form!</h1></div>
	<div id="task-manager-operation" class="task-manager-operation-part">
		<input type="button" value="<spring:message code="page.return"/>" onclick="kokuNavigationHelper.returnMainPage()" />
	</div>
</div>

