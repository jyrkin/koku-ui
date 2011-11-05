<%--
 Copyright 2011 Ixonos Plc, Finland. All rights reserved.

 You should have received a copy of the license text along with this program.
 If not, please contact the copyright holder (http://www.ixonos.com/).
--%>
<%@ include file="imports.jsp" %>



<portlet:renderURL var="pekka">
	<portlet:param name="action" value="userSelection"/>
	<portlet:param name="pic" value="010101-1010"/>
</portlet:renderURL>

<portlet:renderURL var="irina">
    <portlet:param name="action" value="userSelection"/>
    <portlet:param name="pic" value="020202-2020"/>
</portlet:renderURL>

<portlet:renderURL var="tytti">
    <portlet:param name="action" value="userSelection"/>
    <portlet:param name="pic" value="020304-2345"/>
</portlet:renderURL>

<div class="koku-pyh">
	<div>
		<form:form name="guardian" method="post" action="${pekka}">
			<input type="submit" value="Pekka Peltola"/>
		</form:form>
	</div>
	
	<div>
		<form:form name="non-guardian" method="post" action="${irina}">
			<input type="submit" value="Piritta Peltola"/>
		</form:form>
	</div>
	
	   <div>
        <form:form name="non-guardian" method="post" action="${tytti}">
            <input type="submit" value="Tytti J�rvinen (Mummu)"/>
        </form:form>
    </div>
	
</div>
