<%--
 Copyright 2012 Ixonos Plc, Finland. All rights reserved.
  
 This file is part of Kohti kumppanuutta.
 
 This file is licensed under GNU LGPL version 3.
 Please see the 'license.txt' file in the root directory of the package you received.
 If you did not receive a license, please contact the copyright holder
 (kohtikumppanuutta@ixonos.com).
--%>
<%@page import="fi.koku.kks.ui.common.DataType"%>
<%@page import="fi.koku.kks.ui.common.Accountable"%>
<%@ include file="imports.jsp"%>

<c:set var="free_text" value="<%=DataType.FREE_TEXT%>" />
<c:set var="text" value="<%=DataType.TEXT%>" />
<c:set var="multi_select" value="<%=DataType.MULTI_SELECT%>" />
<c:set var="select" value="<%=DataType.SELECT%>" />
<c:set var="guardian" value="<%=Accountable.GUARDIAN%>" />

<fmt:setBundle basename="Language-ext" />

<portlet:defineObjects />

<portlet:renderURL var="homeUrl" windowState="normal">
	<c:choose>
		<c:when test="${not empty fromGroup}">
			<portlet:param name="action" value="showGroup" />
			<portlet:param name="selected" value="${selected}" />
		</c:when>
		<c:otherwise>
			<c:if test="${empty print_mode}">
				<portlet:param name="action" value="showChild" />
				<portlet:param name="pic" value="${child.pic}" />
			</c:if>
			<c:if test="${not empty print_mode}">
				<portlet:param name="action" value="showCollection" />
				<portlet:param name="pic" value="${child.pic}" />
				<portlet:param name="collection" value="${collection.id}" />
			</c:if>
		</c:otherwise>
	</c:choose>
</portlet:renderURL>
<portlet:actionURL var="saveActionUrl">
	<portlet:param name="action" value="saveCollection" />
	<portlet:param name="pic" value="${child.pic}" />
	<portlet:param name="collection" value="${collection.id}" />
	<c:if test="${not empty fromGroup}">
		<portlet:param name="fromGroup" value="true" />
	</c:if>
	<c:if test="${not empty selected}">
		<portlet:param name="selected" value="${selected}" />
	</c:if>
</portlet:actionURL>
<portlet:actionURL var="addMultivalue">
	<portlet:param name="action" value="addMultivalue" />
	<portlet:param name="pic" value="${child.pic}" />
	<portlet:param name="collection" value="${collection.id}" />
	<c:if test="${not empty fromGroup}">
		<portlet:param name="fromGroup" value="true" />
	</c:if>
	<c:if test="${not empty selected}">
		<portlet:param name="selected" value="${selected}" />
	</c:if>
</portlet:actionURL>
<portlet:actionURL var="createVersionURL">
	<portlet:param name="action" value="createNewVersion" />
	<portlet:param name="pic" value="${child.pic}" />
	<c:if test="${not empty fromGroup}">
		<portlet:param name="fromGroup" value="true" />
	</c:if>
	<c:if test="${not empty selected}">
		<portlet:param name="selected" value="${selected}" />
	</c:if>
</portlet:actionURL>
<portlet:actionURL var="logoutURL">
	<portlet:param name="action" value="logoutKKS" />
	<portlet:param name="pic" value="${child.pic}" />
	<portlet:param name="collection" value="${collection.id}" />
	<c:if test="${not empty fromGroup}">
		<portlet:param name="fromGroup" value="true" />
	</c:if>
	<c:if test="${not empty selected}">
		<portlet:param name="selected" value="${selected}" />
	</c:if>
</portlet:actionURL>

<div class="koku-kks">
	<div class="portlet-section-body">

		<div class="kks-left">
			<div class="kks-collection">
				<div class="kks-error-bindings">

					<c:if test="${not empty error}">
						<div class="error">
							<spring:message code="${error}"></spring:message>
						</div>
					</c:if>

					<spring:hasBindErrors name="version">
						<spring:bind path="version.*">
							<c:forEach var="error" items="${status.errorMessages}">
								<div class="error">
									<c:out value="${error}" />
								</div>
							</c:forEach>
						</spring:bind>
					</spring:hasBindErrors>
					<spring:hasBindErrors name="entry">
						<spring:bind path="entry.*">
							<c:forEach var="error" items="${status.errorMessages}">
								<div class="error">
									<c:out value="${error}" />
								</div>
							</c:forEach>
						</spring:bind>
					</spring:hasBindErrors>
					<spring:hasBindErrors name="value">
						<spring:bind path="value.*">
							<c:forEach var="error" items="${status.errorMessages}">
								<div class="error">
									<c:out value="${error}" />
								</div>
							</c:forEach>
						</spring:bind>
					</spring:hasBindErrors>
				</div>
				<c:if
					test="${ sessionScope.municipal_employee && !collection.versioned && empty print_mode}">
					<a class="create"> <spring:message code="ui.kks.new.version" /><span
						class="kks-close"><spring:message code="ui.kks.hide" /> </span>
					</a>
					<div class="kks-fields" style="display: none;">

						<fmt:message key="ui.kks.clear.fields" var="checkBoxLabel" />
						<form:form name="newVersionForm" commandName="version"
							method="post" action="${createVersionURL}">
							<input type="hidden" id="id" name="id" value="${ collection.id }" />
							<div class="portlet-form-field-label">
								<spring:message code="ui.kks.contract.name" />
							</div>
							<div class="portlet-form-field">
								<form:input path="name" maxlength="250" size="70"
									class="portlet-form-input-field" />
							</div>
							<div class="portlet-form-field">
								<form:checkbox label="${checkBoxLabel}" path="clear"
									class="portlet-form-input-field" />
							</div>
							<div>

								<input type="submit" class="portlet-form-button"
									value="<spring:message code="ui.kks.contract.create"/>">

							</div>
						</form:form>

					</div>
				</c:if>
			</div>
		</div>

		<div class="kks-reset-floating"></div>
		<div class="kks-right">
			<div class="kks-left">
				<div>
					<strong><a href="${homeUrl}"><spring:message
								code="ui.kks.close.collection" /> </a></strong>

				</div>
				<br />
				<c:if test="${empty print_mode}">
					<div>
						<a
							href="
						<portlet:actionURL>
							<portlet:param name="action" value="printCollection" />
							<portlet:param name="pic" value="${child.pic}" />
							<portlet:param name="collection" value="${collection.id}" />
						</portlet:actionURL>">
							<spring:message code="ui.kks.printable" />
						</a>
					</div>
				</c:if>
				<c:if
					test="${ not empty collection.prevVersion && empty print_mode }">

					<div>
						<a
							href="
                        <portlet:renderURL>
                            <portlet:param name="action" value="showCollection" />
                            <portlet:param name="pic" value="${child.pic}" />
                            <portlet:param name="collection" value="${collection.prevVersion}" />                            
                            <c:if test="${not empty fromGroup}">
								<portlet:param name="fromGroup" value="true" />
							</c:if>
							<c:if test="${not empty selected}">
								<portlet:param name="selected" value="selected" />
							</c:if>	
                        </portlet:renderURL>">
							<spring:message code="ui.kks.prev.version" />
						</a>
					</div>
				</c:if>
				<c:if
					test="${not empty collection.nextVersion && empty print_mode }">
					<div>
						<a
							href="
                        <portlet:renderURL>
                            <portlet:param name="action" value="showCollection" />
                            <portlet:param name="pic" value="${child.pic}" />
                            <portlet:param name="collection" value="${collection.nextVersion}" />
                            <c:if test="${not empty fromGroup}">
								<portlet:param name="fromGroup" value="true" />
							</c:if>
							<c:if test="${not empty selected}">
								<portlet:param name="selected" value="selected" />
							</c:if>	
                        </portlet:renderURL>">
							<spring:message code="ui.kks.next.version" />
						</a>
					</div>
				</c:if>
			</div>

		</div>



		<div class="kks-left">
			<h1 class="portlet-section-header kks-print">
				<c:out value="${child.name}" />
				<c:out value=" " />
				<c:out value="${collection.name}" />
				<c:if test="${not collection.state.active}">
        (<spring:message code="ui.kks.locked" />)
    </c:if>

			</h1>
			<div class="kks-reset-floating"></div>
			<div id="top-status" class="kks-status-text"></div>

		</div>

		<div class="kks-reset-floating"></div>

		<div class="kks-content kks-print">

			<c:if test="${ empty_collection }">
				<spring:message code="ui.kks.no.authorization" />
			</c:if>
			
			<c:if test="${ not empty deleted }">
				<spring:message code="ui.kks.collection.deleted" />
			</c:if>

			<c:if test="${not empty collection.collectionClass && empty deleted}">

				<form:form class="form-wrapper" name="entryForm"
					commandName="collectionForm" method="post"
					action="${saveActionUrl}">

					<c:forEach var="group"
						items="${collection.collectionClass.kksGroups.kksGroup }">
						<c:if test="${not empty authorized[group.register] || master }">
							<c:if test="${not empty group.name}">
								<h2 class="portlet-section-subheader">
									<c:out value="${group.name}" />
									<span class="kks-right kks-no-print"> <c:if
											test="${ not empty_collection && can_save && collection.state.active && empty print_mode}">
											<input name=save-button type="submit"
												class="portlet-form-button"
												value="<spring:message code="ui.kks.save"/>">
											<input name=save-button type="submit"
												class="portlet-form-button"
												value="<spring:message code="ui.kks.save.close"/>"
												onclick="addCloseToForm();">
										</c:if>
									</span>
								</h2>
							</c:if>
							<c:if test="${not empty group.description}">
								<div class="portlet-section-text">
									<span class="kks-read-only-description"><c:out
											value="${group.description}" /></span>
								</div>
							</c:if>

							<c:forEach var="childGroup" items='${group.subGroups.kksGroup}'>
								<c:if
									test="${not empty authorized[childGroup.register] || master }">
									<c:if test="${not empty childGroup.name}">
										<h3 class="portlet-section-subheader">
											<c:out value="${childGroup.name}" />
										</h3>
									</c:if>

									<c:if test="${not empty childGroup.description}">
										<div class="portlet-section-text">
											<span class="kks-read-only-description">${childGroup.description}</span>
										</div>
									</c:if>
									<c:set var="block_guardian"
										value="${ parent && not (guardian.name eq childGroup.accountable) }" />
									<c:forEach var="type"
										items='${ childGroup.kksEntryClasses.kksEntryClass  }'>
										<div class="kks-entry">
											<span class="portlet-form-field-label">${type.name} <c:if
													test="${not block_guardian && type.multiValue && collection.state.active && empty print_mode }">
													<span class="kks-no-print"> <a
														href="javascript:void(0)"
														onclick="doSubmitNewMulti('${type.id}');"> (<spring:message
																code="ui.kks.add.multivalue" />)
													</a>
													</span>
												</c:if>

											</span>

											<c:if test="${ collection.state.active && empty print_mode }">

												<c:if test="${type.multiValue}">
													<div class="kks-free-text">

														<div class="kks-multi-value-entries">

															<c:if test="${empty collection.entries[type.id]}">
																<div class="portlet-section-text">
																	<spring:message code="ui.kks.no.entries" />
																</div>
															</c:if>

															<c:forEach var="multivalue"
																items='${ collection.entries[type.id].entryValues }'>

																<div class="kks-comment">
																	<p class="kks-entry-value">${multivalue.value}</p>

																	<c:if test="${not block_guardian && empty print_mode}">
																		<span class="kks-right kks-no-print"> <a
																			href="javascript:void(0)"
																			onclick="doSubmitForm('${type.id}', '${collection.entries[type.id].id}', '${multivalue.id}' );">
																				<spring:message code="ui.kks.modify" />
																		</a>
																		</span>
																	</c:if>
																	<div class="portlet-section-text">
																		<span class="kks-commenter">${multivalue.modifierFullName}
																			<fmt:formatDate type="both"
																				pattern="dd.MM.yyyy HH:mm:ss"
																				value="${multivalue.modified}" />
																		</span>
																	</div>
																</div>
															</c:forEach>
														</div>
													</div>
												</c:if>
												<c:if test="${not type.multiValue}">
													<c:choose>
														<c:when test="${ block_guardian }">
															<span class="kks-read-only-text"><c:out
																	value="${collection.entries[type.id].firstValue.value}"></c:out>
															</span>
														</c:when>
														<c:when test="${ type.dataType eq multi_select.name }">
															<div class="portlet-form-field">
																<c:forEach
																	items="${collection.entries[type.id].valueChoices}"
																	var="value">

																	<form:checkbox class="portlet-form-input-field"
																		title="${type.description }"
																		path="entries['${type.id}'].firstValue.values"
																		value="${value}" label="${value}" />
																</c:forEach>
															</div>
														</c:when>
														<c:when test="${ type.dataType eq select.name }">
															<div class="portlet-form-field">
																<c:forEach items="${type.valueSpaces.valueSpace}"
																	var="value">
																	<form:radiobutton class="portlet-form-input-field"
																		title="${type.description }"
																		path="entries['${type.id}'].firstValue.values"
																		value="${ value }" label="${value}" />
																</c:forEach>
															</div>
														</c:when>
														<c:when test="${ type.dataType eq text.name }">
															<div class="portlet-form-field">
																<form:input maxlength="2000"
																	title="${type.description }"
																	class="portlet-form-input-field"
																	path="entries['${type.id}'].firstValue.value" />
															</div>
														</c:when>
														<c:otherwise>
															<div class="portlet-form-field">
																<form:textarea maxlength="2000"
																	class="portlet-form-input-field"
																	title="${type.description}"
																	path="entries['${type.id}'].firstValue.value" />
															</div>
														</c:otherwise>
													</c:choose>
												</c:if>
											</c:if>

											<c:if
												test="${ not collection.state.active || not empty print_mode }">

												<div class="portlet-section-text">

													<c:choose>
														<c:when
															test="${ empty collection.entries[type.id] || empty collection.entries[type.id].firstValue.value }">
															<span class="kks-read-only-text">-</span>
														</c:when>
														<c:otherwise>
															<c:if test="${ type.multiValue }">
																<c:forEach var="multivalue"
																	items='${ collection.entries[type.id].entryValues }'>
																	<span class="kks-read-only-text"><c:out
																			value="${multivalue.value}" />
																		<c:out value=" (${multivalue.modifierFullName}" /> <fmt:formatDate
																			type="both" pattern="dd.MM.yyyy hh:mm"
																			value="${multivalue.modified}" />)</span>
																</c:forEach>
															</c:if>
															<c:if test="${ not type.multiValue }">
																<p class="kks-read-only-text">
																	<c:out
																		value="${collection.entries[type.id].firstValue.value}"></c:out>
																</p>
															</c:if>
														</c:otherwise>
													</c:choose>
												</div>

											</c:if>
										</div>

									</c:forEach>
								</c:if>
							</c:forEach>

						</c:if>
					</c:forEach>

					<div class="kks-bottom-left kks-no-print">
						<c:if
							test="${ not empty_collection && can_save && collection.state.active && empty print_mode && empty deleted}">
							<input name="save-button" type="submit"
								class="portlet-form-button"
								value="<spring:message code="ui.kks.save"/>">
							<input name="save-button" type="submit"
								class="portlet-form-button"
								value="<spring:message code="ui.kks.save.close"/>"
								onclick="addCloseToForm();">
						</c:if>
						<div id="bottom-status" class="kks-status-text"></div>
					</div>
					<div class="kks-bottom-right kks-no-print">
						<c:if test="${ not empty_collection }">
							<div class="kks-home">
								<div class="kks-right">
									<strong><a href="${homeUrl}"><spring:message
												code="ui.kks.close.collection" /> </a></strong>
								</div>
							</div>
						</c:if>
					</div>
					<div class="kks-reset-floating"></div>


				</form:form>

			</c:if>
		</div>
	</div>

	<div id="sessionTimeoutWarning" style="display: none"></div>
	<div id="savingDialog" style="display: none"></div>
	<br />

	<fmt:message key="ui.kks.session.expire" var="sessionExpire" />
	<fmt:message key="ui.kks.session.expire.title" var="sessionTitle" />
	<fmt:message key="ui.kks.save" var="sessionSave" />
	<fmt:message key="ui.kks.session.end" var="sessionEnd" />
	<fmt:message key="ui.kks.session.no.save" var="sessionEndNoSave" />
	<fmt:message key="ui.kks.session.saving.title" var="sessionSaveTitle" />
	<fmt:message key="ui.kks.session.saving" var="sessionSaveDesc" />
	<fmt:message key="ui.kks.session.read.only" var="sessionStatus" />
	<fmt:message key="ui.kks.minutes" var="minutes" />
	<fmt:message key="ui.kks.seconds" var="seconds" />

	<div class="kks-version">
		<%@ include file="../common/page-footer.jsp"%>
	</div>

</div>


<script type="text/javascript"
	src="${pageContext.request.contextPath}/js/jquery-1.5.2.min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/js/jquery-ui-1.8.20.custom.min.js"></script>
<script type="text/javascript">

var idleTime = ${idleTime}; // number of miliseconds until the user is considered idle
var initialSessionTimeoutMessage = '${sessionExpire}';
var initialSaveMessage = '${sessionSaveDesc}';
var initialStatusMessage= '${sessionStatus}';
var sessionTimeoutCountdownId = 'sessionTimeoutCountdown';
var minutesMessage= '${minutes}';
var secondsMessage= '${seconds}';
var initialStatusMessage= '${sessionStatus}';
var redirectAfter = ${redirectTime}; // number of seconds to wait before redirecting the user
var redirectTo = '${logoutURL}'; // URL to relocate the user to once they have timed out
var keepAliveURL = 'keepAlive.php'; // URL to call to keep the session alive
var expiredMessage = '${sessionEnd}'; // message to show user when the countdown reaches 0
var running = false; // var to check if the countdown is running
var timer; // reference to the setInterval timer so it can be stopped
var dialogTimer;
var timerNeeded= ${!empty_collection && can_save && collection.state.active && !can_print};

$(document).ready(function() {
	
	$("a.create").click(function() {
		$(this).toggleClass("active").next().slideToggle("fast");
	});

	if ( timerNeeded ) {
		// create the warning window and set autoOpen to false
		var savingDialog = $("#savingDialog");
		$(savingDialog).html(initialSaveMessage);
		$(savingDialog).dialog({
			title: '${sessionSaveTitle}',
			autoOpen: false,	// set this to false so we can manually open it
			closeOnEscape: false,
			draggable: false,
			width: 460,
			minHeight: 50, 
			modal: true,
			
			resizable: false,
			open: function() {
				// scrollbar fix for IE
				$('body').css('overflow','hidden');
			},
			close: function() {
				// reset overflow
				$('body').css('overflow','auto');
			}
		}); // end of dialog
		
		var b = '${sessionSave}';
		var c = '${sessionEndNoSave}';
		var buttonOpts = {};
		buttonOpts[b] = function () {			
			$('#collectionForm').submit();
		    $(this).dialog("close");
		    $(savingDialog).dialog('open');		    
		};
		
		buttonOpts[c] = function () {	
			var topStatus = $("#top-status");
			$(topStatus).html(initialStatusMessage);
			
			var bottomStatus = $("#bottom-status");
			$(bottomStatus).html(initialStatusMessage);
			
		    $(this).dialog("close");
		    clearInterval(dialogTimer);			    
		};
	

	
		
		
		// create the warning window and set autoOpen to false
		var sessionTimeoutWarningDialog = $("#sessionTimeoutWarning");
		$(sessionTimeoutWarningDialog).html(initialSessionTimeoutMessage);
		$(sessionTimeoutWarningDialog).dialog({
			title: '${sessionTitle}',
			autoOpen: false,	// set this to false so we can manually open it
			closeOnEscape: false,
			draggable: false,
			width: 460,
			minHeight: 50, 
			modal: true,
			buttons: buttonOpts,
			beforeclose: function() { // bind to beforeclose so if the user clicks on the "X" or escape to close the dialog, it will work too
				// stop the timer
				clearInterval(timer);
					
				// stop countdown
				running = false;
				
				$("input").each(function(){
			        $(this).attr("disabled","disabled");
			  	});
				
				$("input[name=save-button]").each(function(){
			        $(this).hide();
			  	});
			    
			    $("textarea").each(function(){
			        $(this).attr("disabled","disabled");
			  	});

			    $("a.create").hide();		    
			    
			},
			resizable: false,
			open: function() {
				// scrollbar fix for IE
				$('body').css('overflow','hidden');
			},
			close: function() {
				// reset overflow
				$('body').css('overflow','auto');
			}
		}); // end of dialog
	
		
		// create a timer that runs every second
		dialogTimer = setInterval(function(){
			idleTime -= 1;
			
			// if the counter is 0, redirect the user
			if(idleTime === 0) {
				clearInterval(dialogTimer);
				var counter = redirectAfter;
				var minVar = Math.floor(counter/60);  // The minutes
				var secVar = counter % 60;
				
				running = true;
				
				// intialisze timer
				var tmp = minVar + minutesMessage + " " + secVar + secondsMessage;
				$('#'+sessionTimeoutCountdownId).html(tmp);
				// open dialog
				$(sessionTimeoutWarningDialog).dialog('open');
				
				// create a timer that runs every second
				timer = setInterval(function(){
					counter -= 1;
					
					// if the counter is 0, redirect the user
					if(counter === 0) {
						$(sessionTimeoutWarningDialog).html(expiredMessage);
						$(sessionTimeoutWarningDialog).dialog('disable');
						window.location = redirectTo;
					} else {
						
						minVar = Math.floor(counter/60);  // The minutes
						secVar = counter % 60; 
						var tmp = minVar + minutesMessage + " " + secVar + secondsMessage;
						$('#'+sessionTimeoutCountdownId).html(tmp);
						
					};
				}, 1000);
			}
		}, 1000);
	
	}

});


window.onload = function() { 
	  var txts = document.getElementsByTagName('TEXTAREA') 

	  for(var i = 0, l = txts.length; i < l; i++) {
	    if(/^[0-9]+$/.test(txts[i].getAttribute("maxlength"))) { 
	      var func = function() { 
	        var len = parseInt(this.getAttribute("maxlength"), 10); 

	        if(this.value.length > len) { 
	          this.value = this.value.substr(0, len); 
	          return false; 
	        } 
	      }

	      txts[i].onkeyup = func;
	      txts[i].onblur = func;
	    } 
	  } 
	}



       function addMultivalueIdToForm( multiId ) {
           $('#collectionForm')
           .append(
                   '<input name="multiValueId" type="hidden" value="' + multiId + '"/>');
       }
       
	   function addValueIdToForm(valueId) {
	        $('#collectionForm')
	                .append(
	                        '<input name="valueId" type="hidden" value="' + valueId + '"/>');

	    }
	   
       
	   function addTypeToForm(type) {
	        $('#collectionForm')
	                .append(
	                        '<input name="type" type="hidden" value="' + type + '"/>');

	    }
	   
	   function addCloseToForm() {
	        $('#collectionForm')
	                .append(
	                        '<input name="close" type="hidden" value="true"/>');

	    }

	    function doSubmitNewMulti( type ) {
	        addTypeToForm(type);
	        $('#collectionForm').submit();

	    }

	       function doSubmitForm( type, multiId, valueId ) {
	    	   	addValueIdToForm(valueId);
	            addTypeToForm(type);
	            addMultivalueIdToForm(multiId);
	            $('#collectionForm').submit();

	        }
	       
</script>




