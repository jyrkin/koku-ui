<%@page import="fi.koku.settings.KoKuPropertiesUtil"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="javax.portlet.PortletPreferences" %>
<%@ page import="javax.portlet.PortletSession" %>
<%@ page import="javax.portlet.WindowState" %>
<%@ page import="fi.arcusys.koku.util.Constants" %>
<%@ page import="fi.arcusys.koku.util.NavigationPortletProperties" %>
<%@ page import="fi.arcusys.koku.util.Properties" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<portlet:defineObjects />

<%
	PortletPreferences preferences = renderRequest.getPreferences();

	final String defaultPath = NavigationPortletProperties.NAVIGATION_PORTLET_PATH;
	final String frontPagePath = NavigationPortletProperties.FRONTPAGE_LINK;
	final String kksPref = NavigationPortletProperties.KKS_PORTLET_LINK;
	final String lokPref = NavigationPortletProperties.LOK_PORTLET_LINK;
	final String pyhPref = NavigationPortletProperties.PYH_PORTLET_LINK;
	final String helpLinkPath = NavigationPortletProperties.HELP_LINK;

	final String defaultPage = defaultPath.substring(defaultPath.lastIndexOf('/')+1, defaultPath.length());
	final String naviPortalMode = Properties.PORTAL_MODE;
	request.setAttribute("naviPortalMode", naviPortalMode);
	final String portalInfo = renderRequest.getPortalContext().getPortalInfo();	
	final String navigationState = (String) renderRequest.getPortletSession().getAttribute(Constants.ATTR_NAVI_STATE, PortletSession.APPLICATION_SCOPE);
	final String navigationPosition = (String) renderRequest.getPortletSession().getAttribute(Constants.ATTR_NAVI_POSITION, PortletSession.APPLICATION_SCOPE);
%>