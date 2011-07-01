<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<portlet:defineObjects/>

<c:set var="guardian" value="${true}" scope="session"/>

<div>

<h1>Omat tiedot</h1> 

<p>
<c:if test="${not empty user}">
	${user.firstname} ${user.surname} <br/>
	s�hk�posti: ${user.email} <br/>
</c:if>
<br/>

Huollettavat lapset:<br/>

<c:if test="${not empty guardedChildren}">
	<c:forEach var="child" items="${guardedChildren}">
		${child.firstname} ${child.surname} ${child.ssn} <br/>
	</c:forEach>
</c:if> 

<br/>

Perheyhteis�n muut j�senet<br/>

ETUNIMI SUKUNIMI 311205-123C (rooli)<br/>

<br/>

</p>

<portlet:renderURL var="editFamilyInformation">
	<portlet:param name="action" value="editFamilyInformation"/>
</portlet:renderURL>

<div>
	<form:form name="editFamilyInformation" method="post" action="${editFamilyInformation}">
		<input type="submit" value="Muokkaa omia tietoja"/>
	</form:form>
</div>

<%--
<p>
*<span class="wait">ETUNIMI SUKUNIMI 121279-123A</span>
Uusi perheyhteis�tieto.
<input type="submit" value="Hyv�ksy">
<input type="submit" value="Hylk��">
</p>

<p>
*<span class="wait">K�ytt�j� ETUNIMI2 SUKUNIMI2 on lis�nnyt sinut perheyhteis�n muuksi j�seneksi.<br/>
Kaikkien opsapuolten on hyv�ksytt�v� uuden j�senen liitt�minen perheyhteis��n.</span>
</p>
--%>

</div>
