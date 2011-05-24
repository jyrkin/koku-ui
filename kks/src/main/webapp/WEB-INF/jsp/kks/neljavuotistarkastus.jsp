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
	<portlet:param name="toiminto" value="naytaLapsi" />
	<portlet:param name="hetu" value="${lapsi.hetu}" />
</portlet:renderURL>
<!--
<portlet:actionURL var="lisaaActionUrl">
	<portlet:param name="toiminto" value="lisaaTukitarve" />
	<portlet:param name="hetu" value="${lapsi.hetu}" />
</portlet:actionURL>
-->
<div>

	<div class="home">
		<a href="${kotiUrl}"><spring:message code="ui.takaisin" /> </a>
	</div>

</div>

<div class="wide" id="main">
	<h1>${lapsi.nimi} : 4-vuotistarkastuksen tietokokoelma</h1>
	<h2>
		P�iv�hoidossa t�ytett�v�t kent�t<span class="kirjaaja">Viimeisin
			kirjaus 15.4.2011 <a href="#">Etunimi Sukunimi</a>
		</span>
	</h2>
	<div class="kirjaus">Huoltaja on suostunut luovuttamaan n�m�
		tiedot neuvolan ty�ntekij�n n�ht�v�ksi.</div>

	<div class="kirjaus">
		P�IVITT�ISTOIMET
		<table width="100%" border="0">
			<tbody>
				<tr>
					<td width="42%">Pukee ja riisuu itse:</td>
					<td width="58%"><input type="checkbox"> Kyll� <input
						type="checkbox"> Opettelee <input type="checkbox">
						Ei viel�</td>
				</tr>
				<tr>
					<td>Sy� siististi:</td>
					<td><input type="checkbox"> Kyll� <input
						type="checkbox"> Opettelee <input type="checkbox">
						Ei viel�</td>
				</tr>
				<tr>
					<td>P�iv�lepo:</td>
					<td><input type="checkbox"> Nukkuu p�ivitt�in <input
						type="checkbox"> Satunnaisesti</td>
				</tr>
			</tbody>
		</table>
		<span class="kirjaaja"></span>
	</div>
	<div class="kirjaus">
		<h3>LIIKKUMINEN JA HAHMOTTAMINEN</h3>
		<table width="100%" border="0">
			<tbody>
				<tr>
					<td width="42%">Juoksee sujuvasti:</td>
					<td width="58%"><input type="checkbox"> Kyll� <input
						type="checkbox"> Vaihtelevasti <input type="checkbox">
						Ei viel�</td>
				</tr>
				<tr>
					<td>Hypp�� tasajalkahyppyj�:</td>
					<td>
						<input type="checkbox"> Kyll� 
						<input type="checkbox"> Vaihtelevasti 
						<input type="checkbox">	Ei viel�
					</td>
				</tr>
				<tr>
					<td>K�tisyys:</td>
					<td>
						<input type="checkbox"> Oikea 
						<input type="checkbox"> Vasen 
						<input type="checkbox">	Vaihtelee
					</td>
				</tr>
			</tbody>
		</table>
		<span class="kirjaaja"></span>
	</div>
	<div class="kirjaus">
		<h3>KIELI JA KOMMUNIKAATIO</h3>
		<table width="100%" border="0">
			<tbody>
				<tr>
					<td width="42%">Ottaa ja s�ilytt�� katsekontaktin
						vuorovaikutuksessa:</td>
					<td width="58%">
						<input type="checkbox"> Yleens� 
						<input type="checkbox" checked=""> Vaihtelevasti 
						<input type="checkbox"> Ei viel�
					</td>
				</tr>
				<tr>
					<td>Osaa kuunnella vastavuoroisesti:</td>
					<td>
						<input type="checkbox" checked=""> Yleens� 
						<input type="checkbox"> Vaihtelevasti 
						<input type="checkbox">	Ei viel�
					</td>
				</tr>
				<tr>
					<td>Toimii ohjeiden mukaan:</td>
					<td>
						<input type="checkbox"> Yleens� 
						<input type="checkbox" checked=""> Vaihtelevasti 
						<input type="checkbox"> Ei viel�
					</td>
				</tr>
			</tbody>
		</table>
	</div>


	<div class="kirjaus">
		VAHVUUDET JA HEIKKOUDET LEIKEISS�
		<textarea class="add" wrap="soft"></textarea>
		<span class="kirjaaja"></span>
	</div>


	<div class="kirjaus">
		VAHVUUDET JA HEIKKOUDET SOSIAALISISSA TAIDOISSA
		<textarea class="add" wrap="soft">T�ss� n�kyy tallennettu kirjaus, jota voidaan p�ivitt�� kokoelman ollessa aktiivinen.
		Alakulmaan tallentuu automaattisesti p�iv�m��r� ja kirjaajan nimi.</textarea>
	</div>
	<div class="kirjaus">
		TERVEISET NEUVOLAAN
		<textarea class="add" wrap="soft">T�ss� n�kyy tallennettu kirjaus, jota voidaan p�ivitt�� kokoelman ollessa aktiivinen.
Alakulmaan tallentuu automaattisesti p�iv�m��r� ja kirjaajan nimi.</textarea>
	</div>
	<div class="kirjaus">
		<input type="submit" class="kokolema" value="Tallenna tieto">
	</div>
	<h2>
		Huoltajan t�ytett�v�t kent�t<span class="kirjaaja">Viimeisin
			kirjaus 15.4.2011 <a href="#">Etunimi Sukunimi</a>
		</span>
	</h2>
	<div class="kirjaus">
		<span class="kirjaaja"></span>
	</div>
	<div class="kirjaus">
		<h3>LIIKKUMINEN JA HAHMOTTAMINEN</h3>
		<table width="100%" border="0">
			<tbody>
				<tr>
					<td>Esimerkki huoltajlle tarkoitetusta kirjauksesta:</td>
					<td>
						<span class="clear"> Kyll�</span> 
						<span class="chosen">Vaihtelevasti</span> 
						<span class="clear"> Ei viel� </span>
					</td>
				</tr>
				<tr>
					<td>Esimerkki huoltajlle tarkoitetusta kirjauksesta:</td>
					<td>
						<span class="clear"> Kyll�</span> 
						<span class="chosen">Vaihtelevasti</span> 
						<span class="clear"> Ei viel� </span>
					</td>
				</tr>
				<tr>
					<td>Esimerkki huoltajlle tarkoitetusta kirjauksesta:</td>
					<td>
						<span class="clear"> Oikea</span> 
						<span class="chosen">Vasen</span>
					</td>
				</tr>
			</tbody>
		</table>
		<span class="kirjaaja"></span>
	</div>


	<div class="kirjaus">
		VAHVUUDET JA HEIKKOUDET LEIKEISS�
		<div class="add">...</div>
		<span class="kirjaaja"></span>
	</div>


	<div class="kirjaus">
		VAHVUUDET JA HEIKKOUDET SOSIAALISISSA TAIDOISSA
		<div class="add">T�ss� n�kyy tallennettu kirjaus, jota voidaan
			p�ivitt�� kokoelman ollessa aktiivinen. Alakulmaan tallentuu
			automaattisesti p�iv�m��r� ja kirjaajan nimi.</div>
	</div>
	<div class="kirjaus">
		KODIN HUOMIOITA
		<div class="add">T�ss� n�kyy tallennettu kirjaus, jota voidaan
			p�ivitt�� kokoelman ollessa aktiivinen. Alakulmaan tallentuu
			automaattisesti p�iv�m��r� ja kirjaajan nimi.</div>
	</div>

</div>

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