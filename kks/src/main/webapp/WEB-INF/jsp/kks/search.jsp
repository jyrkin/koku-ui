<%--
 Copyright 2012 Ixonos Plc, Finland. All rights reserved.
  
 This file is part of Kohti kumppanuutta.
 
 This file is licensed under GNU LGPL version 3.
 Please see the 'license.txt' file in the root directory of the package you received.
 If you did not receive a license, please contact the copyright holder
 (kohtikumppanuutta@ixonos.com).
--%>
<%@ include file="imports.jsp"%>


<portlet:renderURL var="homeUrl">
	<portlet:param name="action" value="choose" />
</portlet:renderURL>
<portlet:actionURL var="searchActionUrl">
	<portlet:param name="action" value="searchChild" />
</portlet:actionURL>
<portlet:actionURL var="toGroupActionUrl">
	<portlet:param name="action" value="toGroupActions" />
</portlet:actionURL>

<c:set var="municipal_employee" value="${true}" scope="session" />

<div class="koku-kks">
	<div class="portlet-section-body">

		<div class="kks-home">
			<a href="${homeUrl}"><spring:message code="ui.kks.back" /></a>
		</div>

		<div class="kks-reset-floating"></div>

		<c:if test="${not empty error}">
			<div class="error">
				<spring:message code="${error}"></spring:message>
			</div>
		</c:if>

		<div class="kks-content">
			<h1 class="portlet-section-header">
				<spring:message code="ui.kks.title" />
			</h1>
			<div class="search">
				<form:form name="searchChildForm" commandName="child" method="post"
					action="${searchActionUrl}">

					<h3 class="portlet-section-subheader">
						<spring:message code="ui.kks.search.child.info" />
					</h3>

					<div>
						<h3 class="portlet-section-subheader"> <spring:message
								code="ui.kks.form.pic" />
						</h3> 
						
						<div class="portlet-form-field"> <span class="kks-left"><form:input
								class="defaultText" path="pic" /> </span><span class="kks-left"><input type="submit"
							class="portlet-form-button"
							value="<spring:message code="ui.kks.search.info"/>"></span>
						</div>
						<div class="kks-reset-floating"></div>
					</div>

					<div class="kks-reset-floating"></div>

				</form:form>
			</div>
			<br/>
			<div class="kks-collection">
				<c:if test="${not empty childs}">
					<c:forEach var="child" items="${childs}">
						<div class="kks-link">
							<a
								href="
                        <portlet:actionURL>
                            <portlet:param name="action" value="toChildInfo" />
                            <portlet:param name="pic" value="${child.pic}" />
                        </portlet:actionURL>">
								<c:out value="${child.lastName }" />, <c:out
									value="${child.firstName}" />
							</a> <span>${child.pic}</span>
						</div>
					</c:forEach>
				</c:if>
				<c:if test="${empty childs && not empty search}">
					<div class="kks-search-result">
						<strong><spring:message code="ui.kks.no.search.results" /></strong>
					</div>
				</c:if>
			</div>

			<div class="kks-reset-floating"></div>

			<h3 class="portlet-section-subheader"><spring:message code="ui.kks.groups" /></h3>
			
			<c:if test="${empty groups}">
				<div>
					<spring:message code="ui.kks.no.groups" />
				</div>
			</c:if>
			
			<c:forEach var="group" items="${groups}">
				<div>
					<c:choose>

						<c:when test="${ selected eq group }">
							<div class="kks-header">&nbsp
								<div class="kks-left">		
								<strong><c:out value="${group}" /></strong>
								</div>
								
								<div class="kks-right kks-medium-left-margin kks-uppercase">	
		                        	<a href="
			                        	<portlet:actionURL>
											<portlet:param name="action" value="toGroupActions" />
											<portlet:param name="selected" value="${group}" />
										</portlet:actionURL>">
										<spring:message code="ui.kks.group.collections"/>	
									</a>						
								</div>
							</div>

							<div class="kks-reset-floating"></div>
							
							<div class="kks-inner">
								<c:forEach var="groupChild" items="${groupChilds}">
									<div class="kks-link">		
									
									<c:choose>
											<c:when test="${selectedPic eq groupChild.pic}">
											
												<div class="kks-left kks-pic-box">${groupChild.pic}</div>
												<div class="kks-left"><strong><a href="
								                        <portlet:actionURL>
								                            <portlet:param name="action" value="toChildInfo" />
								                            <portlet:param name="pic" value="${groupChild.pic}" />
								                            <portlet:param name="selected" value="${group}" />
								                        </portlet:actionURL>">
														<c:out value="${groupChild.sname }" />, <c:out
															value="${groupChild.fname}" />
												</a></strong></div>												
												<div class="kks-reset-floating"></div>
											</c:when>
										<c:otherwise>
										    <div class="kks-left kks-pic-box">${groupChild.pic}</div>
											<div class="kks-left"><a href="
						                        <portlet:actionURL>
						                            <portlet:param name="action" value="toChildInfo" />
						                            <portlet:param name="pic" value="${groupChild.pic}" />
						                            <portlet:param name="selected" value="${group}" />
						                        </portlet:actionURL>">
												<c:out value="${groupChild.sname }" />, <c:out
													value="${groupChild.fname}" />
											</a> </div>
												
												<div class="kks-reset-floating"></div>										
										</c:otherwise>
									</c:choose>								
										
									</div>
								</c:forEach>
							</div>
						</c:when>
						<c:otherwise>
							<a href="
								<portlet:actionURL>
									<portlet:param name="action" value="searchGroup" />
									<portlet:param name="pic" value="${child.pic}" />
									<portlet:param name="selected" value="${group}" />
								</portlet:actionURL>">
									<c:out value="${group}" />
							</a>
						</c:otherwise>
					</c:choose>
				</div>
			</c:forEach>
		</div>

		<div class="kks-spacer">
			<br></br>
		</div>

		<div class="kks-version">
			<%@ include file="../common/page-footer.jsp"%>
		</div>
	</div>
</div>

