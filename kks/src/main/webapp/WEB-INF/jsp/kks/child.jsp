<%--
 Copyright 2012 Ixonos Plc, Finland. All rights reserved.
  
 This file is part of Kohti kumppanuutta.
 
 This file is licensed under GNU LGPL version 3.
 Please see the 'license.txt' file in the root directory of the package you received.
 If you did not receive a license, please contact the copyright holder
 (kohtikumppanuutta@ixonos.com).
--%>
<%@ include file="imports.jsp" %>


<portlet:renderURL var="homeUrl">
<c:choose>
	<c:when test="${not empty fromGroup}">
		<portlet:param name="action" value="showGroup" />
		<portlet:param name="selected" value="${selected}" />
	</c:when>
	<c:otherwise>
	<c:if test="${ not sessionScope.municipal_employee }">
		<portlet:param name="action" value="showChildrens" />
	</c:if>
	<c:if test="${ sessionScope.municipal_employee }">
		<portlet:param name="action" value="showEmployee" />
		<portlet:param name="childs" value="${child.pic}" />
		
		<c:if test="${ not empty selected }">
		   <portlet:param name="selected" value="${selected}" />
		</c:if>
	</c:if>
	</c:otherwise>
</c:choose>
</portlet:renderURL>
<portlet:actionURL var="creationActionUrl">
	<portlet:param name="action" value="createCollection" />
	<portlet:param name="pic" value="${child.pic}" />
	<c:if test="${not empty fromGroup}">
		<portlet:param name="fromGroup" value="true" />
	</c:if>
	<c:if test="${not empty selected}">
		<portlet:param name="selected" value="${selected}" />
	</c:if>	
</portlet:actionURL>
<portlet:actionURL var="searchUrl">
	<portlet:param name="action" value="searchEntries" />
	<portlet:param name="pic" value="${child.pic}" />
	<c:if test="${not empty fromGroup}">
		<portlet:param name="fromGroup" value="true" />
	</c:if>
	<c:if test="${not empty selected}">
		<portlet:param name="selected" value="${selected}" />
	</c:if>		
</portlet:actionURL>
<portlet:actionURL var="sendConsentURL">
	<portlet:param name="action" value="sendConsentRequest" />
	<portlet:param name="pic" value="${child.pic}" />
	<c:if test="${not empty fromGroup}">
		<portlet:param name="fromGroup" value="true" />
	</c:if>
	<c:if test="${not empty selected}">
		<portlet:param name="selected" value="${selected}" />
	</c:if>	
</portlet:actionURL>
<portlet:actionURL var="deleteCollectionURL">
	<portlet:param name="action" value="toDeleteConfirmation" />
    <portlet:param name="pic" value="${child.pic}" />
	<c:if test="${not empty fromGroup}">
		<portlet:param name="fromGroup" value="true" />
	</c:if>
	<c:if test="${not empty selected}">
		<portlet:param name="selected" value="${selected}" />
	</c:if>	
</portlet:actionURL>
<portlet:actionURL var="sendMailURL">
	<portlet:param name="action" value="toMessage" />
    <portlet:param name="pic" value="${child.pic}" />
    <portlet:param name="childName" value="${child.name}" />
	<c:if test="${not empty fromGroup}">
		<portlet:param name="fromGroup" value="true" />
	</c:if>
	<c:if test="${not empty selected}">
		<portlet:param name="selected" value="${selected}" />
	</c:if>	
</portlet:actionURL>

<div class="koku-kks"> 
<div class="portlet-section-body">

	<div class="kks-home">
		<a href="${homeUrl}"><spring:message code="ui.kks.back" /> </a>
	</div>

    <div class="kks-reset-floating"></div>
<div >
	<h1 class="portlet-section-header kks-print">
		<c:out value="${child.name}"/><c:out value=" "/><spring:message code="ui.kks.title" />
	</h1>
	
	<div class="kks-error-bindings">    		
    	<c:if test="${not empty error}"><div class="error"><spring:message code="${error}"></spring:message> </div></c:if>
		<spring:hasBindErrors name="creation">
	     <spring:bind path="creation.*">
	       <c:forEach var="error" items="${status.errorMessages}">
	         <div class="error"><c:out value="${error}"/></div>
	       </c:forEach>
	     </spring:bind>
		</spring:hasBindErrors>
	</div>
	<c:if test="${not empty message}">
		<div class="kks-read-only-text"><spring:message code="${message}"></spring:message> 
		</div>
	</c:if>
	<div class="kks-table">	

		<table class="portlet-table-body kks-print" width="100%" border="0">
			<tr>
				<th><spring:message code="ui.kks.collection" /></th>
				<c:if test="${ sessionScope.municipal_employee }">
					<th>
						<spring:message code="ui.kks.messages" />
					</th>
					<th>
						<spring:message code="ui.kks.actions" />
					</th>
				</c:if>
				<th><spring:message code="ui.kks.last.entry" />
				</th>

				<c:if test="${ sessionScope.municipal_employee }">
					<th><spring:message code="ui.kks.entry.state" />
					</th>
					<!-- <th><spring:message code="ui.kks.consents" />
					</th> -->
				</c:if>
			</tr>

			<c:if test="${not empty collections}">
				<c:forEach var="collection" items="${collections}">

					<c:if
						test="${ sessionScope.municipal_employee || not collection.versioned }">
						<tr>
									<td>
										<div>
											<strong><a href="
														<portlet:renderURL>
															<portlet:param name="action" value="showCollection" />
															<portlet:param name="pic" value="${child.pic}" />
															<portlet:param name="collection" value="${collection.id}" />
															<c:if test="${not empty fromGroup}">
																<portlet:param name="fromGroup" value="true" />
															</c:if>
															<c:if test="${not empty selected}">
																<portlet:param name="selected" value="${selected}" />
															</c:if>	
														</portlet:renderURL>">
														<c:out value="${ collection.name }" /></a> 
											</strong> 											
										</div>
									</td>
									<c:if test="${ sessionScope.municipal_employee }">
										<td>
										
										<form:form name="sendForm-${collection.id}" method="post"
												action="${sendMailURL}">											
											    <input type="hidden" id="collectionName" name="collectionName" value="${ collection.name }"/>
												<input type="submit" class="portlet-form-button" value="<spring:message code="ui.kks.send.button"/>">
										</form:form>
										</td>
										<td>
										<form:form name="deleteForm-${collection.id}" method="post"
												action="${deleteCollectionURL}">
											<c:if test="${ collection.creator eq kksUser }">
											    <input type="hidden" id="collection" name="collection" value="${ collection.id }"/>
												<input type="submit" class="portlet-form-button" value="<spring:message code="ui.kks.delete"/>">
											</c:if>
										</form:form>										
										</td>
									</c:if>
									<td><c:out value="${collection.modifierFullName}"/><c:out value=" "/><fmt:formatDate
									pattern="dd.MM.yyyy" value="${collection.creationTime}" />
							</td>

							<c:if test="${ sessionScope.municipal_employee }">
								<td><c:choose>
										<c:when test="${collection.state.active}">
											<spring:message code="ui.kks.active" />
											<span class="kks-link"> <a
												href="
							                        <portlet:actionURL>
							                            <portlet:param name="action" value="lock" />
							                            <portlet:param name="pic" value="${child.pic}" />
							                            <portlet:param name="collection" value="${collection.id}" />
							                            <c:if test="${not empty fromGroup}">
															<portlet:param name="fromGroup" value="true" />
														</c:if>
														<c:if test="${not empty selected}">
															<portlet:param name="selected" value="${selected}" />
														</c:if>	
							                        </portlet:actionURL>">
													<spring:message code="ui.kks.lock" />  </a> </span>
										</c:when>
										<c:otherwise>
											<c:if test="${ not collection.versioned }">
												<spring:message code="ui.kks.locked" />
												<span class="kks-link"> <a
													href="
	                                                    <portlet:actionURL>
	                                                        <portlet:param name="action" value="activate" />
	                                                        <portlet:param name="pic" value="${child.pic}" />
	                                                        <portlet:param name="collection" value="${collection.id}" />
	                                                        <c:if test="${not empty fromGroup}">
																<portlet:param name="fromGroup" value="true" />
															</c:if>
															<c:if test="${not empty selected}">
																<portlet:param name="selected" value="${selected}" />
															</c:if>	
	                                                    </portlet:actionURL>">
														<spring:message code="ui.kks.activate" /> </a> </span>
											</c:if>
											<c:if test="${ collection.versioned }">
												<spring:message code="ui.kks.versioned" />
											</c:if>
										</c:otherwise>
									</c:choose></td>
									<!-- <td>
										<c:if test="${not collection.consentRequested && not empty collection.collectionClass.consentType }">
					                        <form:form name="sendForm-${collection.id}"  method="post" action="${sendConsentURL}">
					                                <input type="hidden" id="collectionId" name="collectionId" value="${ collection.id }"/>
					                                <input type="hidden" id="consent" name="consent" value="${ collection.collectionClass.consentType }"/>
					                                
					                                <span>
					                                	<input type="submit" class="portlet-form-button" value="<spring:message code="ui.kks.consent.send"/>">
					                                </span>
					
					                        </form:form>
				                        </c:if>
									</td> -->
							</c:if>
						</tr>
					</c:if>
				</c:forEach>
			</c:if>

		</table>
		<br />
		<div class="kks-collection">
			<!-- <div class="kks-link"> <a
				href="
                        <portlet:renderURL>
                            <portlet:param name="action" value="showPegasos" />
                            <portlet:param name="pic" value="${child.pic}" />
                        </portlet:renderURL>">
					<spring:message code="ui.kks.patient.healthcare" /> </a> </div>  -->

            <div class="kks-link"> <a
                href="
                        <portlet:actionURL>
                            <portlet:param name="action" value="searchEntries" />
                            <portlet:param name="pic" value="${child.pic}" />
                            <portlet:param name="classification" value="terveydentila" />
                            <portlet:param name="description" value="ui.kks.terveydentila.query" />
                            <c:if test="${not empty fromGroup}">
								<portlet:param name="fromGroup" value="true" />
							</c:if>
							<c:if test="${not empty selected}">
								<portlet:param name="selected" value="${selected}" />
							</c:if>	
                        </portlet:actionURL>">
                    <spring:message code="ui.kks.healthcare" />  </a> </div>
                    
			<div class="kks-link"> <a
				href="
                        <portlet:actionURL>
                            <portlet:param name="action" value="searchEntries" />
                            <portlet:param name="pic" value="${child.pic}" />
                            <portlet:param name="classification" value="mittaus" />
                            <portlet:param name="description" value="ui.kks.mittaukset.query" />
                            <c:if test="${not empty fromGroup}">
								<portlet:param name="fromGroup" value="true" />
							</c:if>
							<c:if test="${not empty selected}">
								<portlet:param name="selected" value="${selected}" />
							</c:if>
                        </portlet:actionURL>">
					<spring:message code="ui.kks.measurement" /> </a> </div>
			 <div
				class="kks-link"> <a
				href="
                        <portlet:actionURL>
                            <portlet:param name="action" value="searchEntries" />
                            <portlet:param name="pic" value="${child.pic}" />
                            <portlet:param name="classification" value="koti" />
                            <portlet:param name="description" value="ui.kks.kasvatusta.ohjaavat.tiedot.query" />
                            <c:if test="${not empty fromGroup}">
								<portlet:param name="fromGroup" value="true" />
							</c:if>
							<c:if test="${not empty selected}">
								<portlet:param name="selected" value="${selected}" />
							</c:if>
                        </portlet:actionURL>">
					<spring:message code="ui.kks.child.rase" /> </a> </div>
			<div class="kks-link"> <a
				href="
                        <portlet:actionURL>
                            <portlet:param name="action" value="searchEntries" />
                            <portlet:param name="pic" value="${child.pic}" />
                            <portlet:param name="classification" value="tuen_tarve, huolenaiheet" />
                            <portlet:param name="description" value="ui.kks.tuen.tarve.query" />
                            <c:if test="${not empty fromGroup}">
								<portlet:param name="fromGroup" value="true" />
							</c:if>
							<c:if test="${not empty selected}">
								<portlet:param name="selected" value="${selected}" />
							</c:if>
                        </portlet:actionURL>">
					<spring:message code="ui.kks.support.needs" /> </a> </div> 

			<c:if test="${ sessionScope.municipal_employee }">

				<span class="kks-link"> <a
					href="
                        <portlet:actionURL>
                            <portlet:param name="action" value="searchEntries" />
                            <portlet:param name="pic" value="${child.pic}" />
                            <portlet:param name="classification" value="palaute" />
                            <portlet:param name="description" value="ui.kks.palautteet.query" />
                            <c:if test="${not empty fromGroup}">
								<portlet:param name="fromGroup" value="true" />
							</c:if>
							<c:if test="${not empty selected}">
								<portlet:param name="selected" value="${selected}" />
							</c:if>
                        </portlet:actionURL>">
						<spring:message code="ui.kks.feedback" />  </a> </span>
				<br />


				<span class="kks-link"> <a
					href="
                        <portlet:actionURL>
                            <portlet:param name="action" value="searchEntries" />
                            <portlet:param name="pic" value="${child.pic}" />
                            <portlet:param name="classification" value="toive" />
                            <portlet:param name="description" value="ui.kks.toiveet.query" />
                            <c:if test="${not empty fromGroup}">
								<portlet:param name="fromGroup" value="true" />
							</c:if>
							<c:if test="${not empty selected}">
								<portlet:param name="selected" value="${selected}" />
							</c:if>
                        </portlet:actionURL>">
						<spring:message code="ui.kks.wishes" />  </a> </span>
				<br />


			</c:if>
		</div>

		<br />


		<div class="activate.collection">

			<div class="collection">
				<c:if test="${ sessionScope.municipal_employee }">
					<a class="create"> <spring:message code="ui.kks.new.contract" /><span
						class="kks-close"><spring:message code="ui.kks.hide" /> </span> </a>
					<div class="kks-fields" style="display: none;">

						<form:form name="creationForm" commandName="creation"
							method="post" action="${creationActionUrl}">
   
								<div class="portlet-form-field-label"><spring:message code="ui.kks.contract.type" />
								</div>

    
								<span class="portlet-form-field"> <form:select id="kks.select"
										path="field" onchange="insertSelection();"
										 >

										<form:option class="portlet-form-input-field" value="" label="" />
										<c:forEach var="creatable" items="${creatables}">
											<c:if test="${creatable.needsVersioning}">
												<form:option value="${creatable.asText}"
													label="${creatable.name}" />
											</c:if>
											<c:if test="${not creatable.needsVersioning}">
                                            <form:option class="portlet-form-input-field" value="${creatable.asText}" label="${creatable.name}" />
                                            </c:if>
										</c:forEach>
									</form:select> </span>
								<div class="portlet-form-field-label"><spring:message code="ui.kks.contract.name" />
								</div>
								<span class="portlet-form-field"><form:input maxlength="250" id="kks.name" path="name" size="70" />
								</span>
							

							<span class="kks-right"> <input type="submit" class="portlet-form-button"
								value="<spring:message code="ui.kks.contract.save"/>"> </span>
						</form:form>

					</div>
				</c:if>
			</div>
		</div>


	</div>
</div>

<div></div>
</div>

<div class="kks-version">
		<%@ include file="../common/page-footer.jsp"%>
	</div>

</div>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.5.2.min.js"></script>
<script type="text/javascript">


	$(document).ready(function() {
		$("a.create").click(function() {
			$(this).toggleClass("active").next().slideToggle("fast");
		});
	});
	
	function insertSelection() {
		var str = document.getElementById("kks.select").value;

		if (str == "") {
			document.getElementById("kks.name").value = "";
		} else {
			document.getElementById("kks.name").value = str.split("#", 10)[2];
		}
	}
</script>
