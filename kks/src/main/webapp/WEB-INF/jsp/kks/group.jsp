<%--
 Copyright 2012 Ixonos Plc, Finland. All rights reserved.
  
 This file is part of Kohti kumppanuutta.
 
 This file is licensed under GNU LGPL version 3.
 Please see the 'license.txt' file in the root directory of the package you received.
 If you did not receive a license, please contact the copyright holder
 (kohtikumppanuutta@ixonos.com).
--%>
<%@page import="fi.koku.kks.ui.common.DataType"%>
<%@ include file="imports.jsp"%>

<fmt:setBundle basename="Language-ext" />

<portlet:defineObjects />

<portlet:renderURL var="homeUrl">
	<portlet:param name="action" value="showEmployee" />
	<portlet:param name="selected" value="${selected}" />
</portlet:renderURL>

<portlet:actionURL var="createCollectionsURL">
	<portlet:param name="action" value="addCollectionsForGroup" />
	<portlet:param name="selected" value="${selected}" />
</portlet:actionURL>


<div class="koku-kks">
	<div class="portlet-section-body">
		<div>

			<div class="kks-home">
				<a href="${homeUrl}"><spring:message code="ui.kks.back" /> </a>
			</div>

		</div>

		<c:if test="${not empty error}">
			<div class="error">
				<spring:message code="${error}"></spring:message>
			</div>
		</c:if>

		<c:if test="${not empty message}">
			<div class="kks-read-only-text">
				<spring:message code="${message}"></spring:message>
			</div>
		</c:if>

		<h1>
			<spring:message code="ui.kks.group.create"></spring:message>
		</h1>
		<h3>${selected}</h3>

		<div class="kks-entry kks-print">

			<form:form name="createCollections" commandName="group" method="post"
				action="${createCollectionsURL}">

				<div class="portlet-form-field-label">
					<spring:message code="ui.kks.contract.type" />
				</div>
				<div>
					<form:errors path="type" cssClass="error" />
				</div>
				<div class="portlet-form-field">
					<form:select id="kks.select" path="type"
						onchange="insertSelection();" class="kks-width-50">

						<form:option value="" label="" class="portlet-form-input-field" />
						<c:forEach var="creatable" items="${creatables}">
							<form:option class="portlet-form-input-field"
								value="${creatable.asText}" label="${creatable.name}" />
						</c:forEach>
					</form:select>
				</div>

				<div class="portlet-form-field-label">
					<spring:message code="ui.kks.contract.name" />
				</div>
				<div>
					<form:errors path="name" cssClass="error" />
				</div>
				<div class="portlet-form-field">
					<form:input maxlength="250"
						class="portlet-form-input-field kks-width-50" id="kks.name"
						path="name" size="70" />
				</div>
				<div class="kks-small-top-margin">

					<div class="kks-left">
						<h3>
							<spring:message code="ui.kks.group.select"></spring:message>
						</h3>
					</div>
					<div class="kks-right kks-small-top-margin">
						<a class="show kks-small-top-margin"><spring:message
								code="ui.kks.group.show"></spring:message></a><a
							class="close kks-small-top-margin" style="display: none;"><spring:message
								code="ui.kks.group.hide"></spring:message></a>
					</div>

				</div>
				<div class="kks-reset-floating"></div>
				<div>
					<form:errors path="customers" cssClass="error" />
				</div>
				<div>
					<c:if test="${not empty groupChilds}">

						<div class="kks-choose-all">
							<div class="kks-left">
								<input type="checkbox" id="select_all" name="select_all"
									value="all" />
							</div>
							<div class="kks-left kks-checkbox-label">
								<b><spring:message code="ui.kks.group.select.all"></spring:message></b>
							</div>
							<div class="kks-reset-floating"></div>
						</div>
						<c:forEach var="groupChild" items="${groupChilds}">

							<div class="kks-small-top-margin">
								<div class="kks-left">
									<form:checkbox path="customers" value="${groupChild.pic}" />
								</div>
								<div class="kks-left kks-fake-link kks-checkbox-label">
									<strong> <a
										href="
						                        <portlet:actionURL>
						                            <portlet:param name="action" value="toChildInfo" />
						                            <portlet:param name="pic" value="${groupChild.pic}" />
						                            <portlet:param name="selected" value="${selected}" />
						                            <portlet:param name="fromGroup" value="true" />
						                        </portlet:actionURL>">
											<c:out value="${groupChild.customer.name}" />
									</a></strong>,&nbsp;<span>${groupChild.pic}</span>

								</div>
								<div class="kks-right single-collection kks-checkbox-label">
									<a href="#"><spring:message code="ui.kks.group.show.hide"></spring:message></a>
								</div>
								<div class="kks-reset-floating"></div>
								<div class="show-collection" style="display: none;">


									<table class="portlet-table-body kks-print kks-table">
										<tr>
											<th><spring:message code="ui.kks.collection" /></th>
											<th><spring:message code="ui.kks.last.entry" /></th>
											<th><spring:message code="ui.kks.entry.state" /></th>

										</tr>
										<c:if test="${not empty groupChild.collections}">
											<c:forEach var="collection" items="${groupChild.collections}">


												<tr>
													<td class="kks-width-50">
														<div>
															<a
																href="
														<portlet:renderURL>
															<portlet:param name="action" value="showCollection" />
															<portlet:param name="pic" value="${groupChild.pic}" />
															<portlet:param name="collection" value="${collection.id}" />
															<portlet:param name="selected" value="${selected}" />
															<portlet:param name="fromGroup" value="true" />
														</portlet:renderURL>">
																<c:out value="${ collection.name }" />
															</a>
														</div>
													</td>

													<td><c:out value="${collection.modifierFullName}" />
														<c:out value=" " />
														<fmt:formatDate pattern="dd.MM.yyyy"
															value="${collection.creationTime}" /></td>


													<td><c:choose>
															<c:when test="${collection.state.active}">
																<spring:message code="ui.kks.active" />
															</c:when>
															<c:otherwise>
																<c:if test="${ not collection.versioned }">
																	<spring:message code="ui.kks.locked" />
																</c:if>
																<c:if test="${ collection.versioned }">
																	<spring:message code="ui.kks.versioned" />
																</c:if>
															</c:otherwise>
														</c:choose></td>
												</tr>
											</c:forEach>
										</c:if>

									</table>

								</div>
							</div>
						</c:forEach>

					</c:if>
				</div>


				<div class="kks-reset-floating"></div>
				<br></br>

				<div class="kks-left">
					<input type="submit" class="portlet-form-button"
						value="<spring:message code="ui.kks.contract.create"/>">
				</div>

				<div class="kks-left">
					<input type="submit" class="portlet-form-button"
						onclick="addCancelToForm();"
						value="<spring:message code="ui.kks.group.close"/>">
				</div>
				<div class="kks-reset-floating"></div>
			</form:form>
		</div>

		<div class="kks-reset-floating"></div>

	</div>

	<div class="kks-version">
		<%@ include file="../common/page-footer.jsp"%>
	</div>

</div>

<c:set var="hasChild" value="${not empty group.customers}"></c:set>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/js/jquery-1.5.2.min.js"></script>
<script type="text/javascript">
	
	var selectAll = '${hasChild}';
	

	$(document).ready(function() {
		
		$('div.show-collection').hide();  
		$('a.show').click(function() {
			$('div.show-collection').show('fast');
			$('a.show').hide();
			$('a.close').show();
		  });
		$('a.close').click(function() {
			$('div.show-collection').hide('fast');
			$('a.close').hide();
			$('a.show').show();
		  });
		$('div.single-collection').click(function() {
		    $(this).next().next().slideToggle('fast');
		  });
		
		if ( selectAll == 'true'  ) {
			$('#select_all').attr('checked','checked');
		}
		$('#select_all').click(function() {
			if ( $('#select_all').is(':checked') ) {
				$(':checkbox:not(:checked)').attr('checked', 'checked');
			} else {
				$(':checkbox:checked').removeAttr('checked');
			}
		 });
	 });
	 
    function setInitialState() {  
        var i = 0;    
        <c:forEach items="${Cgroup.customers}" var="current" varStatus="status">  
            alert("CUR" + current );     
            i++;  
        </c:forEach>  
    }  
    
	window.onload = function() {
		var txts = document.getElementsByTagName('TEXTAREA')

		for ( var i = 0, l = txts.length; i < l; i++) {
			if (/^[0-9]+$/.test(txts[i].getAttribute("maxlength"))) {
				var func = function() {
					var len = parseInt(this.getAttribute("maxlength"), 10);

					if (this.value.length > len) {
						this.value = this.value.substr(0, len);
						return false;
					}
				}

				txts[i].onkeyup = func;
				txts[i].onblur = func;
			}
		}
	}

	function addCancelToForm() {
		$('#group').append(
				'<input name="cancel" type="hidden" value="true"/>');

	}
	
	function updateCheckBoxes() {
		
	}

	function insertSelection() {
		var str = document.getElementById("kks.select").value;

		if (str == "") {
			document.getElementById("kks.name").value = "";
		} else {
			document.getElementById("kks.name").value = str.split("#", 10)[2];
		}
	}
</script>