package fi.arcusys.koku.common.wsproxy.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletRequest;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.openjpa.lib.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import serp.bytecode.NewArrayInstruction;

import fi.arcusys.koku.common.util.KokuWebServicesJS;
import fi.arcusys.koku.common.util.Properties;
import fi.koku.portlet.filter.userinfo.UserInfo;
import fi.koku.settings.KoKuPropertiesUtil;

/**
 * Servlet for proxying and securing WS requests
 */
public class WsProxyServletRestricted extends HttpServlet implements Servlet {
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(WsProxyServletRestricted.class);
    private static final Map<String, KokuWebServicesJS> endpoints = new HashMap<String, KokuWebServicesJS>();
    private static final Map<KokuWebServicesJS, WSRestriction> restrictions = new HashMap<KokuWebServicesJS, WSRestriction>();

    static {
        for (KokuWebServicesJS service : KokuWebServicesJS.values()) {

            String endpoint = KoKuPropertiesUtil.get(service.value());

            if (endpoint == null) {
                throw new ExceptionInInitializerError("Coulnd't find endpoint definintion property '"+ service.value()+"'");
            }

            // Trim ".wsdl"
            if (endpoint.endsWith("?wsdl"))
                endpoint = endpoint.substring(0, endpoint.indexOf("?wsdl"));

            endpoints.put(endpoint, service);

            logger.info("Added new permitted endpoint to WsProxyServlet: "+endpoint);
        }

        //restrictions.put(KokuWebServicesJS.USERS_AND_GROUPS_SERVICE, new UsersAndGroupsServiceRestriction());
        restrictions.put(KokuWebServicesJS.APPOINTMENT_PROCESSING_SERVICE, new AppointmentProcessingServiceRestriction());
        restrictions.put(KokuWebServicesJS.KV_MESSAGE_SERVICE, new MessageProcessingServiceRestriction());
    }

    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        logger.info("Start proxying request...");

        response.setContentType("text/xml; charset=UTF-8");

        String content = processRequest(request);

        PrintWriter writer = response.getWriter();
        writer.write(content);
        writer.close();

        logger.info("Completed proxying request.");
    }

    protected String processRequest(HttpServletRequest request) {
        if (request.getParameter("endpoint") == null) {
            return generateErrorResponse("Missing parameter: endpoint");
        }

        final String endpointURI = request.getParameter("endpoint").trim();

        // Limit the endpoint proxying to specific list
        if (!endpoints.keySet().contains(endpointURI))
            return generateErrorResponse("Endpoint '" + endpointURI + "' is not allowed for proxying.");

        logger.info("Will send an envelope to endpoint: "+endpointURI);

        OMElement soapRequest;
        String methodName;

        try {
            String message = request.getParameter("message");

            // Read the message from the body of the request if the parameter is not available
            if (message == null) {
                BufferedReader reader = request.getReader();
                StringBuilder sb = new StringBuilder();

                do sb.append(reader.readLine());
                while (reader.ready());

                message = sb.toString();
            }

            soapRequest = parseRequest(message);

            logger.info("Will send: " + soapRequest.toString());

            methodName = soapRequest.getLocalName();

            logger.info("Method name: " + methodName);
        } catch (Exception e) {
            return generateErrorResponse("Malformed message: " + e.getMessage());
        }

        final Map<KokuWebServicesJS, String> wsMap = new HashMap<KokuWebServicesJS, String>();
        for (String uri : endpoints.keySet()) {
            wsMap.put(endpoints.get(uri), uri);
        }

        final HttpSession session = request.getSession();
        final WSCommonData commonData = new WSCommonData(session, wsMap);

        // Check if user is authenticated
        if (commonData.getCurrentUserName() == null)
            return generateErrorResponse("User is not authenticated");

        final KokuWebServicesJS endpoint = endpoints.get(endpointURI);

        final WSRestriction restriction = restrictions.get(endpoint);

        if (restriction != null) {
            if (!restriction.requestPermitted(commonData, methodName, soapRequest)) {
                return generateErrorResponse("User is not permitted to perform this request");
            }

        } else {
            logger.warn("In-depth security checks are not defined for "+endpoint.value());
        }

        Options options = new Options();
        options.setTo(new EndpointReference(endpointURI));

        String action = request.getParameter("action");

        if (action == null)
            action = request.getHeader("soapaction");

        options.setAction(action);

        OMElement soapResponse = null;

        try {
            ServiceClient client = new ServiceClient();
            client.setOptions(options);

            soapResponse = client.sendReceive(soapRequest);

        } catch (Exception exception) {
            return generateErrorResponse("Error sending request to WS backend. Exception: " + exception);
        }

        if (soapResponse == null) {
            return generateErrorResponse("Error: Received empty result");
        }

        if (restriction != null && !restriction.responsePermitted(commonData, methodName, soapResponse)) {
            return generateErrorResponse("User is not permitted to receive this response");
        }

        return generateSuccessResponse(soapResponse);
    }

    @SuppressWarnings("unchecked")
    protected OMElement parseRequest(String message) throws Exception {
        logger.info("Starting to parse request...");

        StringReader stringreader = new StringReader(message);
        XMLStreamReader xmlstreamreader = XMLInputFactory.newInstance()
                .createXMLStreamReader(stringreader);

        StAXOMBuilder staxombuilder = new StAXOMBuilder(xmlstreamreader);
        OMElement document = staxombuilder.getDocumentElement();
        OMElement element = null;

        for (Iterator<OMElement> children = document.getChildElements(); children.hasNext(); ) {
            element = (OMElement) children.next();

            logger.info("Looking at element: qname="+element.getQName());
            logger.info("Looking at element: text="+element.getText());

            if (element.getText().toLowerCase().contains("body"))
                break;
        }

        logger.info("Completed parsing request.");

        return element != null ? element.getFirstElement() : null;
    }

    protected String generateErrorResponse(String message) {
        return generateResponse("error", message, null);
    }

    protected String generateSuccessResponse(OMElement soapEnvelope) {
        return generateResponse("success", null, soapEnvelope);
    }

    protected String generateResponse(String status, String message, OMElement payload) {
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMElement response = factory.createOMElement("response", null);
        OMElement omStatus = createTextElement(factory, "status", status);

        response.addChild(omStatus);

        if (message != null) {
            OMElement omMessage = createTextElement(factory, "message", message);
            response.addChild(omMessage);
        }

        if (payload != null) {
            logger.info("Adding payload: " + payload.toString());

            OMElement omPayload = factory.createOMElement("payload", null);
            omPayload.addChild(payload);
            response.addChild(omPayload);
        }

        logger.info("Test output:" + response.toString());
        return response.toString();
    }

    protected OMElement createTextElement(OMFactory omfactory, String elementName, String text) {
        OMElement omelement = omfactory.createOMElement(elementName, null);
        OMText omtext = omfactory.createOMText(omelement, text);
        omelement.addChild(omtext);
        return omelement;
    }

}
