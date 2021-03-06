<%--
 Copyright 2012 Ixonos Plc, Finland. All rights reserved.
  
 This file is part of Kohti kumppanuutta.
 
 This file is licensed under GNU LGPL version 3.
 Please see the 'license.txt' file in the root directory of the package you received.
 If you did not receive a license, please contact the copyright holder
 (kohtikumppanuutta@ixonos.com).
--%>
<%@ include file="imports.jsp" %>

<fmt:setBundle basename="com.ixonos.eservices.koku.bundle.KokuBundle" />

<portlet:defineObjects />

<portlet:renderURL var="homeUrl">
    <portlet:param name="action" value="showChild" />
    <portlet:param name="pic" value="${child.pic}" />
</portlet:renderURL>

<div class="koku-kks"> 
<div class="portlet-section-body">

<!-- Example page what kind of pegasos integration might be -->
    
<div class="kks-home">
    <div class="kks-right">
        <a href="${homeUrl}"><spring:message code="ui.kks.back" /> </a>
    </div>
</div>

<div class="kks-reset-floating"></div>

    <div class="kks-content">

		<h1 class="portlet-section-header">
		    ${child.name} <spring:message code="ui.kks.basic.healthcare.patient.information"/>
		</h1>
        <div class="kks-entry">
            <h2 class="portlet-section-subheader">Ruoka-aine allergiat ja ruokavaliot</h2>   
            
            <div class="portlet-section-text">                 
                 <span class="kks-read-only-text"> 20.06.2011 Heinänuha. Lievä heinänuha, joka on pahimmillaan touko-kesäkuussa </span>                 
            </div>
            
            <div class="portlet-section-text">                 
                 <span class="kks-read-only-text"> 10.06.2011 Vähähiilihydraattinen ruokavalio. Noudattaa II-tyypin diabeteksen vuoksi kokeilumielessä vähähiilihydraattista ruokavaliota</span>                 
            </div>                  
        </div>
        
        <div class="kks-entry">
            <h2 class="portlet-section-subheader">Sairaudet ja lääkitykset</h2> 
            
            
            <div class="portlet-section-text">                 
                 <span class="kks-read-only-text"> 02.06.2011 Aloitettu kolesteroli lääkitys</span>                 
            </div>
            
            <div class="portlet-section-text">                 
                 <span class="kks-read-only-text"> 01.06.2010 II-tyypin diabetes. Todettu lievä II-tyypin diabetes, jota hoidetaan toistaiseksi ruokavaliolla </span>                 
            </div>
                       
        </div>
        
        <div class="kks-entry">
            <h2 class="portlet-section-subheader">Tukitarpeet</h2>     
            
            <div class="portlet-section-text">                 
                 <span class="kks-read-only-text"> 01.07.2010 Tarvitsee edelleen tukea ja kuntoutusta II-tyypin diabeteksen aiheuttamaan lievää liikkumiskyvyn heikkenemiseen. Suunnitteilla kuntoutusjakso hoitolaitoksessa. </span>                 
            </div>
           
           <div class="portlet-section-text">                 
                 <span class="kks-read-only-text"> 01.06.2010 Tarvitsee tukea ja kuntoutusta II-tyypin diabeteksen aiheuttamaan lievää liikkumiskyvyn heikkenemiseen. Hoitona 10xkerran kuntoutuskäynnit kiropraktikolla. Saanut lainaan kävelytuen ja kävelykepin </span>                 
            </div>
        </div>
        
        
        
</div>
</div>
</div>
<br />
