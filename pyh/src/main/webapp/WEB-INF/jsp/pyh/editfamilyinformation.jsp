<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<h1>Perhetiedot</h1>

<portlet:renderURL var="backURL">
	<portlet:param name="action" value="guardianFamilyInformation"/>
</portlet:renderURL>
<a class="takaisin" href="${backURL}">&lt;&lt; Takaisin</a>

<p>
<c:if test="${not empty user}">
	${user.firstname} ${user.surname} <br/>
	S�hk�posti: ${user.email} <br/>
	<br/>
</c:if>

<c:if test="${not empty guardedChildren}">
	Huollettavat lapset: <br/>
	<c:forEach var="child" items="${guardedChildren}">
		
		<%-- this feature is not used, do not remove the commented code before we are sure we don't need this
		
		<portlet:actionURL var="removeDependant">
			<portlet:param name="action" value="removeDependant"/>
			<portlet:param name="dependantSSN" value="${child.ssn}"/>
		</portlet:actionURL>
		${child.firstname} ${child.surname} ${child.ssn} <a href="${removeDependant}">poista huoltajuus</a> <br/>
		--%>
		
		${child.firstname} ${child.surname} ${child.ssn}
		<c:choose>
			<c:when test="${child.memberOfUserFamily}">
				lis�tty perheenj�seneksi
			</c:when>
			<c:otherwise>
				<portlet:actionURL var="addDependantAsFamilyMember">
					<portlet:param name="action" value="addDependantAsFamilyMember"/>
					<portlet:param name="dependantSSN" value="${child.ssn}"/>
				</portlet:actionURL>
				<a href="${addDependantAsFamilyMember}">lis�� perheenj�seneksi</a> <br/>
			</c:otherwise>
		</c:choose>
		<br/>
		
	</c:forEach>
</c:if>
</p>

<p>
+ LIS�� UUSI LAPSI <br/>

<portlet:actionURL var="addNewChild">
	<portlet:param name="action" value="addNewChild"/>
</portlet:actionURL>

<form:form name="addNewChildForm" commandName="newChild" method="post" action="${addNewChild}">
	
	<span class="pvm">
	Etunimi: <form:input path="firstname"/> 
	Toinen nimi: <form:input path="middlename"/>
	Sukunimi: <form:input path="surname"/>
	</span>
	
	<%-- TODO: how to map three select values (birth_dd, birth_mm, birth_yyyy) into one String (Person.birthDate)? --%>
	<%-- one solution is to use a separate AddChildCommandObject class and create the Person (Child) object in the controller --%>
	
	<span class="pvm">
	Syntym�aika: 
	<select name="birth_dd" class="syntmaika">
	
	<option>01</option>
	<option>02</option>
	<option>03</option>
	<option>04</option>
	<option>05</option>
	<option>06</option>
	<option>07</option>
	<option>08</option>
	<option>09</option>
	<option>10</option>
	<option>11</option>
	<option>12</option>
	<option>13</option>
	<option>14</option>
	<option>15</option>
	<option>16</option>
	<option>17</option>
	<option>18</option>
	<option>19</option>
	<option>20</option>
	<option>21</option>
	<option>22</option>
	<option>23</option>
	<option>24</option>
	<option>25</option>
	<option>26</option>
	<option>27</option>
	<option>28</option>
	<option>29</option>
	<option>30</option>
	<option>31</option>
	</select>
	
	<select name="birth_mm" class="syntmaika">
	<option>01</option>
	<option>02</option>
	<option>03</option>
	<option>04</option>
	<option>05</option>
	<option>06</option>
	<option>07</option>
	<option>08</option>
	<option>09</option>
	<option>10</option>
	<option>11</option>
	<option>12</option>
	</select>
	
	<select name="birth_yyyy" class="syntmaika">
	<option>2000</option>
	<option>2001</option>
	<option>2002</option>
	<option>2003</option>
	<option>2004</option>
	<option>2005</option>
	<option>2006</option>
	<option>2007</option>
	<option>2008</option>
	<option>2009</option>
	<option>2010</option>
	<option>2011</option>
	</select>
	
	HETU: <form:input path="ssn"/>
	
	<input type="submit" value="Lis�� &gt;"/>
	
	</span>
</form:form>
<br/>

<c:if test="${not empty familyMembers}">
	Perheyhteis�n muut j�senet <br/>
	<c:forEach var="familyMember" items="${familyMembers}">
		
		<portlet:actionURL var="removeFamilyMember">
			<portlet:param name="action" value="removeFamilyMember"/>
			<portlet:param name="familyMemberSSN" value="${familyMember.ssn}"/>
		</portlet:actionURL>
		
		<%-- TODO: lis�� perheenj�senen rooli --%>
		
		${familyMember.firstname} ${familyMember.surname} ${familyMember.ssn} (rooli) 
		<a href="${removeFamilyMember}">poista j�senyys</a> <br/>
		
	</c:forEach>
<br/>
</c:if>
</p>

<p>
<span class="pvm">
LIS�� K�YTT�JI� PERHEYHTEIS��SI <br/>
<br/>

<portlet:actionURL var="searchUsers">
	<portlet:param name="action" value="searchUsers"/>
</portlet:actionURL>

<form:form name="searchUsersForm" method="post" action="${searchUsers}">
	Etunimi: <input name="searchFirstname"/>
	Sukunimi: <input name="searchSurname"/>
	SOTU: <input name="searchSSN"/>
	<input type="submit" value="HAE &gt;"/>
</form:form>
</span>
</p>

<%-- TODO: show table (and headers) only when we have search results --%>

<table width="100%" border="0">

<tr>
<td width="38%">NIMI</td>
<td width="26%">SOTU</td>
<td width="10%">LIS��</td>
<td width="26%"> VALITSE LIS�TT�V�N ROOLI</td>
</tr>

<c:if test="${not empty searchedUsers}">
<c:forEach var="user" items="${searchedUsers}">
	<tr>
	<td>${user.firstname} ${user.surname}</td>
	<td>${user.ssn}</td>
	
	<%-- TODO: checkboxille pit�� generoida joku nimi tai tieto, jolla osataan valita oikean rivin tiedot lis�tt�v�ksi --%>
	<td><input name="" type="checkbox"/></td>
	
	<td>
	<select name="family_member_role" class="syntmaika">
	<option>VALITSE ROOLI</option>
	<option>�iti</option>
	<option>Is�</option>
	<option>Lapsi</option>
	</select>
	</td>
	</tr>
</c:forEach>
</c:if>

</table>

<p>&nbsp;</p>

<%-- TODO: tietojen l�hett�minen eli formin submit --%>

<input type="submit" value="Tallenna tiedot" class="tallenna"/>

</div>
