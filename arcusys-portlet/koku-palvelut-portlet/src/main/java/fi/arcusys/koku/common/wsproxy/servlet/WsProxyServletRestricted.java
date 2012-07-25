package fi.arcusys.koku.common.wsproxy.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

import fi.arcusys.koku.common.util.KokuWebServicesJS;
import fi.koku.settings.KoKuPropertiesUtil;

/**
 * Servlet for proxying and securing WS requests
 */
public class WsProxyServletRestricted extends HttpServlet implements Servlet {
    private static final long serialVersionUID = 1L;

    private static final Set<String> RESTRICTED_ENDPOINTS = new HashSet<String>();
	private static final Logger logger = LoggerFactory.getLogger(WsProxyServletRestricted.class);

    static {
    	for (KokuWebServicesJS service : KokuWebServicesJS.values()) {

    	    String endpoint = KoKuPropertiesUtil.get(service.value());

    	    if (endpoint == null) {
    			throw new ExceptionInInitializerError("Coulnd't find endpoint definintion property '"+ service.value()+"'");
    		}

    	    // Trim ".wsdl"
    	    if (endpoint.endsWith("?wsdl"))
    			endpoint = endpoint.substring(0, endpoint.indexOf("?wsdl"));

    	    RESTRICTED_ENDPOINTS.add(endpoint);

    		logger.info("Added new permitted endpoint to WsProxyServlet: "+endpoint);
    	}
    }

//    /**
//     * @param config
//     * @throws ServletException
//     */
//    @Override
//    public void init(ServletConfig config) throws ServletException {
//        super.init(config);
//        if (config.getInitParameter("endpoints") != null) {
//            for (final String parameter : config.getInitParameter("endpoints").split(",")) {
//                final String endpoint = parameter.trim();
//                if (!endpoint.isEmpty()) {
//                    restrictedEndpoints.add(endpoint);
//                }
//            }
//        }
//
//    }

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

        final String endpoint = request.getParameter("endpoint").trim();

        // Limit the endpoint proxying to specific list
        if (!RESTRICTED_ENDPOINTS.contains(endpoint))
            return generateErrorResponse("Endpoint '" + endpoint + "' is not allowed for proxying.");

        logger.info("Will send an envelope to endpoint: "+endpoint);

        OMElement soapRequest;
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
        } catch (Exception e) {
            return generateErrorResponse("Malformed message: " + e.getMessage());
        }

        final String userName = request.getRemoteUser();
        logger.info("Current user: " + userName);
        logger.info("Current user (session 2): " + request.getSession().getAttribute("koku.userinfo"));

        Options options = new Options();
        options.setTo(new EndpointReference(endpoint));

        String action = request.getParameter("action");

        if (action == null)
        	action = request.getHeader("soapaction");

        options.setAction(action);

        return callEndpoint(soapRequest, options);
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

    protected String callEndpoint(OMElement soapRequest, Options options) {
        String response = null;

        logger.info("Calling the endpoint...");

        try {
            ServiceClient client = new ServiceClient();
            client.setOptions(options);

            OMElement soapResponse = client.sendReceive(soapRequest);
            response = generateSuccessResponse(soapResponse);

            logger.info("Returned response: " + soapResponse.toString());
        } catch (Exception exception) {
            response = generateErrorResponse("Error sending request to WS backend. Exception: "
                    + exception);
        }

        logger.info("Will return: " + response);
        logger.info("Endpoint call completed.");

        return response;
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
