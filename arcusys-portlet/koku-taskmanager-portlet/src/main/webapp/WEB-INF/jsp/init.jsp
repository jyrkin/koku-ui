<%-- <%@ page import="fi.koku.taskmanager.util.Constants"%> --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="javax.portlet.PortletPreferences" %>
<%@ page import="javax.portlet.WindowState" %>
<%@ page import="fi.arcusys.koku.common.util.TaskUtil" %>
<%@ page import="fi.arcusys.koku.common.util.Constants" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
 

<portlet:defineObjects />

<%
final PortletPreferences preferences = renderRequest.getPreferences();
final String taskFilter = preferences.getValue("taskFilter", "");
final String notifFilter = preferences.getValue("notifFilter", "");
final String refreshDuration = preferences.getValue("refreshDuration", "180");
final String openForm = preferences.getValue("openForm", "1");
final String defaultTaskType = preferences.getValue("defaultTaskType", "task");
final String editable = "false";
final String excludeFilter = preferences.getValue("excludeFilter", "");
%>