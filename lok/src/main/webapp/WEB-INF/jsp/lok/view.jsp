<%@ include file="imports.jsp" %>

<portlet:defineObjects />

<portlet:actionURL var="viewActionURL">
	<portlet:param name="action" value="viewLog" />
	<portlet:param name="visited" value="---" />
	<portlet:param name="user" value="${user}" />
	 <portlet:param name="userRole" value="${userRole}" />
</portlet:actionURL>

<portlet:renderURL var="homeURL">
    <portlet:param name="action" value="home" />
    <portlet:param name="user" value="${user}" />
    <portlet:param name="userRole" value="${userRole}" />
</portlet:renderURL>

<div class="koku-lok">
	<div class="portlet-section-body">
<c:choose>
	<c:when test="${userRole == 'ROLE_LOG_ADMIN'}">
	
	<%-- 	<div class="home">
			<a href="${homeURL}"><spring:message code="koku.common.back" />
			</a>
		</div>
		--%>

<!--  		<h1 class="portlet-section-header">
			<spring:message code="koku.lok.header.view" />
		</h1>
-->

		<div class="portlet-form-field">
			<div class="portlet-form-field-label">
				<spring:message code="koku.lok.search.for.view" />
			</div>

			<p>
				<form:form name="logSearchForm" commandName="logSearchCriteria"
					method="post" action="${viewActionURL}">


					<!--  TODO: Javascript date picker will be added here! -->

					<span class="form-field-label"><spring:message
							code="koku.common.startTime" /> </span>
					<form:input path="from"
						value="${startDate}" maxlength="10" size="10" />
					<span class="errors"><form:errors path="from" />
					</span>

					<span class="form-field-label"><spring:message
							code="koku.common.endTime" /> </span>
					<form:input path="to" value="${endDate}" maxlength="10" size="10" />
					<span class="errors"><form:errors path="to" />
					</span>

					<input type="submit"
						value="<spring:message code="koku.lok.search"/>">

					<div class="clear"></div>
				</form:form>

<!--  TODO: Joka kerralla ei ehk� anneta kaikkia hakuparametrej�, se otettava huomioon -->

				<%-- th { text-align: center; font-weight: bold } --%>
				<c:if test="${not empty entries}">
					<c:if test="${not empty visited}">
					<h2 class="portlet-section-subheader">
						<spring:message code="koku.lok.view.results.header" />
						<fmt:formatDate pattern="dd.MM.yyyy" value="${searchParams.from}" />
						-
						<fmt:formatDate pattern="dd.MM.yyyy" value="${searchParams.to}" />
						:
					</h2>

					<%-- TODO: N�m� kent�t vastaukseen:
						aikaleima
						k�sittelij�n nimi
						tapahtumatyyppi
						k�sitelty tieto (tapahtumakuvaus ja kohde)
 					--%>
					<table class="portlet-table-body" width="100%" border="0">

						<tr>
							<%-- TODO! when using <th> the text won't align to left, that's why we use <td> and <b> here now.
								This should be changed! --%>

							<th width=15% scope="col"><b><spring:message code="ui.lok.timestamp" /></b>
							</th>
							<th width=15% scope="col"><b><spring:message code="ui.lok.koku.user" /></b>
							</th>
							<th width=15% scope="col"><b><spring:message code="ui.lok.operation" /></b>
							</th>
							<th width=55% scope="col"><b><spring:message code="ui.lok.data.item.type" /></b>
						
							
							</th>
						</tr>

						<c:forEach var="e" items="${entries}">
							<tr>
						<td width=15%><fmt:formatDate pattern="dd.MM.yyyy hh:mm:ss" value="${e.timestamp}"/></td>
							<%-- 	<td width=15%><c:out value="${e.timestamp}"/></td>--%>
								<td width=15%><c:out value="${e.user}"/></td>
								<td width=15%><c:out value="${e.operation}"/></td>
								<td width=55%><c:out value="${e.message}"/>
								<%-- 
								<c:out value="${e.dataItemType}"/>
												<c:out value="${e.clientSystemId}"/>
												<c:out value="${e.child}"/>
												<c:out value="${e.logId}"/>
												<%--<c:out value="${e.message}"/> --%>
												</td>
							</tr>
						</c:forEach>
					</table>
				</c:if>
				</c:if>
		</div>

		<c:if test="${empty entries}">
			<c:if test="${not empty visited}">
				<%-- do not show this on the first visit to this page --%>
				<p>
					<spring:message code="koku.lok.admin.noResults" />
				</p>
			</c:if>
		</c:if>

		<br />
</c:when>

<c:otherwise>
	<spring:message code="ui.lok.no.user.rights" />
</c:otherwise>
</c:choose>
	</div>
</div>
<!-- end of koku-lok-div -->