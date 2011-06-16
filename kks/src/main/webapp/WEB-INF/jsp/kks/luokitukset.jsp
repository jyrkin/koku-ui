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

</portlet:renderURL>


<div>

	<div class="home">
		<a href="${kotiUrl}"><spring:message code="ui.takaisin" /> </a>
	</div>

</div>

<div>
	<h1>OLEMASSAOLEVAT LUOKITUKSET</h1>

        
        <div style="float: left;padding: 2px">
                <h3>Luokitukset</h3>                
                <span>${luokitukset}</span>
        </div>
        
        <div style="float: left; padding: 2px">
                <h3>Kehitysasialajit</h3>                
                <span>${kehitysAsialajit}</span>
        </div>

        
        <div style="clear:both"></div>
        
            <h1>KOKOELMIEN LUOKITUKSET</h1>
		<div>
			<c:forEach var="tyyppi" items="${kokoelmaTyypit}">

				<h2>${ tyyppi.nimi }</h2>

				<table width="100%" border="0">
					<tr>
						<th align="left"><h3>Kent�n nimi</h3></th>
						<th align="left"><h3>Tietotyyppi</h3></th>
						<th align="left"><h3>Kehitysasialajit</h3></th>
						<th align="left"><h3>Luokitukset</h3></th>
						
						<th align="left"><h3>Rekisteri</h3></th>
						<th align="left"><h3>Arvojoukko</h3></th>

					</tr>

					<c:forEach var="kirjaus" items="${tyyppi.kirjausTyypit}">


						<tr style="padding:5px">
							<td style="padding:2px"><strong>${ kirjaus.nimi }</strong></td>
							<td style="padding:2px">${ kirjaus.tietoTyyppi }</td>
							<td style="padding:2px">${ kirjaus.kehitysasiaTyypitTekstina }</td>
							<td style="padding:2px"><strong>${ kirjaus.luokituksetTekstina }</strong></td>
							
							<td style="padding:2px">${ kirjaus.rekisteri }</td>
							<td style="padding:2px">${ kirjaus.arvotTekstina }</td>
						</tr>
					</c:forEach>


				</table>
			</c:forEach>
		</div>
</div>
