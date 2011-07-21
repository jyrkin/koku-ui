<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page contentType="text/html" isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<fmt:setBundle basename="com.ixonos.eservices.koku.bundle.KokuBundle" />


<portlet:defineObjects />

<portlet:renderURL var="kotiUrl">

	<c:if test="${ not sessionScope.ammattilainen }">
		<portlet:param name="toiminto" value="naytaLapset" />
	</c:if>
	<c:if test="${ sessionScope.ammattilainen }">
		<portlet:param name="toiminto" value="naytaTyontekija" />
		<portlet:param name="lapset" value="${lapsi.hetu}" />
	</c:if>

</portlet:renderURL>
<portlet:actionURL var="lisaaActionUrl">
	<portlet:param name="toiminto" value="lisaaTieto" />
	<portlet:param name="hetu" value="${lapsi.hetu}" />
</portlet:actionURL>
<portlet:actionURL var="aktivointiActionUrl">
	<portlet:param name="toiminto" value="aktivoiKokoelma" />
	<portlet:param name="hetu" value="${lapsi.hetu}" />
</portlet:actionURL>
<portlet:actionURL var="haeKirjausUrl">
	<portlet:param name="toiminto" value="haeKirjauksia" />
	<portlet:param name="hetu" value="${lapsi.hetu}" />
</portlet:actionURL>

<div>

	<div class="home">
		<a href="${kotiUrl}"><spring:message code="ui.takaisin" /> </a>
	</div>

</div>

<div id="main" class="wide">
	<h1>
		${lapsi.nimi}
		<spring:message code="ui.kks.otsikko" />
	</h1>

	<div>
		<table width="100%" border="0">
			<tr>
				<th align="left"><spring:message code="ui.tietokokoelma" />
				</th>
				<th align="left"><spring:message code="ui.viimeisin.kirjaus" />
				</th>

				<c:if test="${ sessionScope.ammattilainen }">
					<th align="left"><spring:message code="ui.kirjausten.tila" />
					</th>
				</c:if>
			</tr>

			<c:if test="${not empty kokoelmat}">


				<c:forEach var="tieto" items="${kokoelmat}">

					<c:if
						test="${ sessionScope.ammattilainen || tieto.tila.aktiivinen }">
						<tr>
							<td><span class="kokoelma"> <a
									href="
						<portlet:renderURL>
							<portlet:param name="toiminto" value="naytaKokoelma" />
							<portlet:param name="hetu" value="${lapsi.hetu}" />
							<portlet:param name="kokoelma" value="${tieto.id}" />
						</portlet:renderURL>">
										<strong>${ tieto.nimi }</strong> </a> </span>
							</td>
							<td>${ tieto.muokkaaja } <fmt:formatDate
									pattern="dd/MM/yyyy" value="${ tieto.luontiAika }" /></td>

							<c:if test="${ sessionScope.ammattilainen }">
								<td><c:choose>
										<c:when test="${tieto.tila.aktiivinen}">
											<spring:message code="ui.aktiivinen" />
											<span class="linkki"> <a
												href="
							                        <portlet:actionURL>
							                            <portlet:param name="toiminto" value="lukitse" />
							                            <portlet:param name="hetu" value="${lapsi.hetu}" />
							                            <portlet:param name="kokoelma" value="${tieto.id}" />
							                        </portlet:actionURL>">
													<strong><spring:message code="ui.lukitse" />
												</strong> </a> </span>
										</c:when>
										<c:otherwise>
											<spring:message code="ui.lukittu" />
											
											<span class="linkki"> <a
                                                href="
                                                    <portlet:actionURL>
                                                        <portlet:param name="toiminto" value="aktivoi" />
                                                        <portlet:param name="hetu" value="${lapsi.hetu}" />
                                                        <portlet:param name="kokoelma" value="${tieto.id}" />
                                                    </portlet:actionURL>">
                                                    <strong><spring:message code="ui.aktivoi" />
                                                </strong> </a> </span>
											
										</c:otherwise>
									</c:choose>
								</td>
							</c:if>
						</tr>
					</c:if>
				</c:forEach>
			</c:if>

		</table>
		<br />
		<div class="kokoelma">
			<span class="linkki"> <a
				href="
                        <portlet:actionURL>
                            <portlet:param name="toiminto" value="haeKirjauksia" />
                            <portlet:param name="hetu" value="${lapsi.hetu}" />
                            <portlet:param name="luokitus" value="terveydentila" />
                            <portlet:param name="kuvaus" value="Terveydentila" />
                        </portlet:actionURL>">
					<strong><spring:message code="ui.terveydentila" />
				</strong> </a> </span><br /> 
				
			   
            <span class="linkki"> <a
                href="
                        <portlet:actionURL>
                            <portlet:param name="toiminto" value="haeKirjauksia" />
                            <portlet:param name="hetu" value="${lapsi.hetu}" />
                            <portlet:param name="luokitus" value="mittaus" />
                            <portlet:param name="kuvaus" value="Mittaukset" />
                        </portlet:actionURL>">
                    <strong><spring:message code="ui.mittaus" />
                </strong> </a> </span><br /> 
                	
			<span class="linkki"> <a
				href="
                        <portlet:actionURL>
                            <portlet:param name="toiminto" value="haeKirjauksia" />
                            <portlet:param name="hetu" value="${lapsi.hetu}" />
                            <portlet:param name="luokitus" value="koti" />
                            <portlet:param name="kuvaus" value="Kasvatusta ohjaavat tiedot" />
                        </portlet:actionURL>">
					<strong><spring:message code="ui.lapsen.kasvatus" />
				</strong> </a> </span><br /> <span class="linkki"> <a
				href="
                        <portlet:actionURL>
                            <portlet:param name="toiminto" value="haeKirjauksia" />
                            <portlet:param name="hetu" value="${lapsi.hetu}" />
                            <portlet:param name="luokitus" value="tuen_tarve, huolenaiheet" />
                            <portlet:param name="kuvaus" value="Tuen tarve" />
                        </portlet:actionURL>">
					<strong><spring:message code="ui.tuen.tarpeet" />
				</strong> </a> </span> <br />

			<c:if test="${ sessionScope.ammattilainen }">

				<span class="linkki"> <a
					href="
                        <portlet:actionURL>
                            <portlet:param name="toiminto" value="haeKirjauksia" />
                            <portlet:param name="hetu" value="${lapsi.hetu}" />
                            <portlet:param name="luokitus" value="palaute" />
                            <portlet:param name="kuvaus" value="Palautteet" />
                        </portlet:actionURL>">
						<strong><spring:message code="ui.palautteet" />
					</strong> </a> </span>
				<br />


				<span class="linkki"> <a
					href="
                        <portlet:actionURL>
                            <portlet:param name="toiminto" value="haeKirjauksia" />
                            <portlet:param name="hetu" value="${lapsi.hetu}" />
                            <portlet:param name="luokitus" value="toive" />
                            <portlet:param name="kuvaus" value="Toiveet" />
                        </portlet:actionURL>">
						<strong><spring:message code="ui.toiveet" />
					</strong> </a> </span>
				<br />


			</c:if>
		</div>

		<br />


		<div class="aktivoi.kokoelma">

			<div class="kokoelma">
				<c:if test="${ sessionScope.ammattilainen }">
					<a class="tieto"> <spring:message code="ui.sopimus.uusi" /><span
						class="sulje"><spring:message code="ui.piilota" /> </span> </a>
					<div class="tietokentta ">

						<form:form name="aktivointiForm" commandName="aktivointi"
							method="post" action="${aktivointiActionUrl}">

							<div>
								<spring:message code="ui.sopimus.nimi" />
								<span class="pvm"><form:input path="nimi" />
								</span>
							</div>
							<div>
								<br>
								<spring:message code="ui.sopimus.tyyppi" />
								<span class="pvm"> <form:select path="aktivoitavaKentta"
										class="kokoelmavalinta">

										<form:option value="" label="" />
										<c:forEach var="kokoelma" items="${aktivoitavat}">
											<form:option value="${kokoelma}" label="${ kokoelma }" />
										</c:forEach>
									</form:select> </span>

							</div>
							<br />


							<span> <input type="submit"
								value="<spring:message code="ui.sopimus.tallenna"/>"> </span>
						</form:form>

					</div>
				</c:if>
			</div>
		</div>


	</div>
</div>



<div></div>

<script type="text/javascript"
	src="http://code.jquery.com/jquery-1.4.4.min.js"></script>
<script type="text/javascript"
	src="http://gsgd.co.uk/sandbox/jquery/easing/jquery.easing.1.4.js"></script>
<script type="text/javascript">
	$(document).ready(function() {

		$(".tietokentta").hide();

		$("a.tieto").click(function() {
			$(this).toggleClass("active").next().slideToggle("fast");
		});

	});
</script>