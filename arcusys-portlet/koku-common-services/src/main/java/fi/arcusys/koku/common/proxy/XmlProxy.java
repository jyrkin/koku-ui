package fi.arcusys.koku.common.proxy;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.engine.ListenerManager;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.arcusys.koku.common.proxy.restrictions.AppointmentProcessingServiceRestriction;
import fi.arcusys.koku.common.proxy.restrictions.HakProcessingServiceRestriction;
import fi.arcusys.koku.common.proxy.restrictions.MessageProcessingServiceRestriction;
import fi.arcusys.koku.common.proxy.restrictions.RequestProcessingServiceRestriction;
import fi.arcusys.koku.common.proxy.restrictions.SuostumusProcessingServiceRestriction;
import fi.arcusys.koku.common.proxy.restrictions.TietopyyntoProcessingServiceRestriction;
import fi.arcusys.koku.common.proxy.restrictions.ValtakirjaProcessingServiceRestriction;
import fi.arcusys.koku.common.proxy.restrictions.WSRestriction;
import fi.arcusys.koku.common.proxy.util.WSCommonData;
import fi.arcusys.koku.common.util.KokuWebServicesJS;
import fi.koku.portlet.filter.userinfo.UserInfo;
import fi.koku.settings.KoKuPropertiesUtil;

/**
 * A secure proxy for WS calls to backend
 *
 * @author Toni Turunen
 * @author Mikhail Kapitonov
 *
 */
public class XmlProxy {

	private static final Logger LOG = LoggerFactory.getLogger(XmlProxy.class);

	private static final List<String> JS_ENDPOINT_NAMES;
	private static final Map<KokuWebServicesJS, String> JS_ENDPOINT_URI;
	private static final Map<KokuWebServicesJS, WSRestriction> JS_RESTRICTIONS;

	static {
		List<String> endpointNames = new ArrayList<String>();
		Map<KokuWebServicesJS, String> endpointURIs = new HashMap<KokuWebServicesJS, String>();
		Map<KokuWebServicesJS, WSRestriction> restrictions = new HashMap<KokuWebServicesJS, WSRestriction>();

		for (KokuWebServicesJS key : KokuWebServicesJS.values()) {
    		String value = KoKuPropertiesUtil.get(key.value());

    		if (value == null) {
    			throw new ExceptionInInitializerError("Couldn't find property '"+ key.value()+"'");
    		}

    		if (value.endsWith("?wsdl")) {
    			int end = value.indexOf("?wsdl");
    			value = value.substring(0, end);
    		}

    		endpointNames.add(key.value());
    		endpointURIs.put(key, value);

    		LOG.info("Added new endpoint to XmlProxy: "+value);
		}

		//restrictions.put(KokuWebServicesJS.USERS_AND_GROUPS_SERVICE, new UsersAndGroupsServiceRestriction());
		restrictions.put(KokuWebServicesJS.APPOINTMENT_PROCESSING_SERVICE, new AppointmentProcessingServiceRestriction());
		restrictions.put(KokuWebServicesJS.KV_MESSAGE_SERVICE, new MessageProcessingServiceRestriction());
		restrictions.put(KokuWebServicesJS.REQUEST_PROCESSING_SERVICE, new RequestProcessingServiceRestriction());
		restrictions.put(KokuWebServicesJS.SUOSTUMUS_PROCESSING_SERVICE, new SuostumusProcessingServiceRestriction());
		restrictions.put(KokuWebServicesJS.VALTAKIRJA_PROCESSING_SERVICE, new ValtakirjaProcessingServiceRestriction());
		restrictions.put(KokuWebServicesJS.TIETOPYYNTO_PROCESSING_SERVICE, new TietopyyntoProcessingServiceRestriction());
		restrictions.put(KokuWebServicesJS.HAK_PROCESSING_SERVICE, new HakProcessingServiceRestriction());

		JS_ENDPOINT_NAMES = Collections.unmodifiableList(endpointNames);
		JS_ENDPOINT_URI = Collections.unmodifiableMap(endpointURIs);
		JS_RESTRICTIONS = Collections.unmodifiableMap(restrictions);
	}

	private final String message;
	private final KokuWebServicesJS endpoint;
	private final String endpointName;
	private final String endpointURI;
	private final WSRestriction restriction;
	private final UserInfo userInfo;

	public XmlProxy(String endpointName, String message, UserInfo userInfo) {

		if (endpointName == null || message == null || userInfo == null) {
			LOG.error("One or more given constructor parameters was null");
			throw new IllegalStateException();
		}

		this.endpointName = endpointName;
		this.message = message;
		this.userInfo = userInfo;

		this.endpoint = KokuWebServicesJS.fromValue(endpointName);
		this.endpointURI = JS_ENDPOINT_URI.get(endpoint);
		this.restriction = JS_RESTRICTIONS.get(endpoint);

        // Copied from fi.arcusys.koku.palvelut.bean.Configuration [palvelut-portlet]
        // TODO: Refactor fi.arcusys.koku.palvelut.bean.Configuration to reuse the code
        ConfigurationContext configurationContext = ListenerManager.defaultConfigurationContext;
        if (configurationContext == null) {
            try {
                configurationContext = ConfigurationContextFactory
                        .createConfigurationContextFromFileSystem(null, null);
            } catch (final AxisFault e) {
                throw new RuntimeException(e);
            }
            ListenerManager.defaultConfigurationContext = configurationContext;
        }
        final HttpClient cachedClient = (HttpClient) configurationContext
                .getProperty(HTTPConstants.CACHED_HTTP_CLIENT);
        if (cachedClient != null) {
            cachedClient
                    .getHttpConnectionManager()
                    .getParams()
                    .setDefaultMaxConnectionsPerHost(
                            MultiThreadedHttpConnectionManager.DEFAULT_MAX_TOTAL_CONNECTIONS);
        } else {
            MultiThreadedHttpConnectionManager multiThreadedHttpConnectionManager = new MultiThreadedHttpConnectionManager();

            HttpConnectionManagerParams params = new HttpConnectionManagerParams();
            params.setDefaultMaxConnectionsPerHost(20);
            multiThreadedHttpConnectionManager.setParams(params);
            HttpClient httpClient = new HttpClient(
                    multiThreadedHttpConnectionManager);
            configurationContext.setProperty(HTTPConstants.CACHED_HTTP_CLIENT,
                    httpClient);
        }
	}

	public String send(PortletRequest request) throws XMLStreamException {
        LOG.info("Will send an envelope to endpoint: " + endpointURI);

        OMElement soapRequest;
        String methodName;

        try {
            soapRequest = parseRequest(message);

            LOG.info("Will send: " + soapRequest.toString());

            methodName = soapRequest.getLocalName();

            LOG.info("Method name: " + methodName);
        } catch (Exception e) {
            return generateErrorResponse("Malformed message: " + e.getMessage());
        }

        final PortletSession session = request.getPortletSession();
        final WSCommonData commonData = new WSCommonData(session, userInfo, JS_ENDPOINT_URI);

        if (restriction != null) {
            if (!restriction.requestPermitted(commonData, methodName, soapRequest)) {
                return generateErrorResponse("User is not permitted to perform this request");
            }

        } else {
            LOG.warn("In-depth security checks are not defined for "+endpoint.value());
        }

        LOG.info("ServiceClient.class was loaded from: " + ServiceClient.class.getProtectionDomain().getCodeSource().getLocation().getPath());

        Options options = new Options();
        options.setTo(new EndpointReference(endpointURI));

        OMElement soapResponse = null;

        try {
            ServiceClient client = new ServiceClient();
            client.setOptions(options);

            soapResponse = client.sendReceive(soapRequest);
            soapResponse.build();

            client.cleanupTransport();

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
        LOG.info("Starting to parse request...");

        StringReader stringreader = new StringReader(message);
        XMLStreamReader xmlstreamreader = XMLInputFactory.newInstance()
                .createXMLStreamReader(stringreader);

        StAXOMBuilder staxombuilder = new StAXOMBuilder(xmlstreamreader);
        OMElement document = staxombuilder.getDocumentElement();
        OMElement element = null;

        for (Iterator<OMElement> children = document.getChildElements(); children.hasNext(); ) {
            element = children.next();

            LOG.info("Looking at element: qname="+element.getQName());
            LOG.info("Looking at element: text="+element.getText());

            if (element.getText().toLowerCase().contains("body"))
                break;
        }

        LOG.info("Completed parsing request.");

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
            LOG.info("Adding payload: " + payload.toString());

            OMElement omPayload = factory.createOMElement("payload", null);
            omPayload.addChild(payload);
            response.addChild(omPayload);
        }

        LOG.info("Test output:" + response.toString());
        return response.toString();
    }

    protected OMElement createTextElement(OMFactory omfactory, String elementName, String text) {
        OMElement omelement = omfactory.createOMElement(elementName, null);
        OMText omtext = omfactory.createOMText(omelement, text);
        omelement.addChild(omtext);
        return omelement;
    }

    public static List<String> getServiceNames() {
		return JS_ENDPOINT_NAMES;
	}

}
