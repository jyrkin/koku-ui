package fi.arcusys.koku.common.proxy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.arcusys.koku.common.util.KokuWebServicesJS;
import fi.koku.portlet.filter.userinfo.UserInfo;
import fi.koku.settings.KoKuPropertiesUtil;


/**
 * Replace XMLProxy with WSProxy implementation...
 *
 *
 * @author Toni Turunen
 *
 */
public class WsProxy {

	private static final Logger LOG = LoggerFactory.getLogger(WsProxy.class);

	private static final Map<String, String> JS_ENDPOINTS;
	private static final List<String> JS_ENDPOINT_NAMES;

	static {
		Map<String, String> endpoints = new HashMap<String, String>();
		List<String> endpointNames = new ArrayList<String>();

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
    		endpoints.put(key.value(), value);
    		LOG.info("Added new endpoint to WsProxyServlet: "+value);
		}
		JS_ENDPOINTS = Collections.unmodifiableMap(endpoints);
		JS_ENDPOINT_NAMES = Collections.unmodifiableList(endpointNames);
	}


	private final XmlProxy proxy;
	private final UserInfo userinfo;

	public WsProxy(String service, String message, UserInfo userinfo) throws IllegalOperationCall {

		this.userinfo = userinfo;
//		OperationsValidator validator = new OperationsValidatorImpl();
		OperationsValidatorImpl validator = null;
		String endpointUrl = JS_ENDPOINTS.get(service);
		if (endpointUrl == null) {
			proxy = null;
			throw new IllegalOperationCall("Couldn't create XMLProxy. No service found by given service name: '"+service+"'");
		} else {
			proxy = new XmlProxy("", endpointUrl, message, validator);
		}
	}

	public String send() throws IllegalOperationCall, XMLStreamException {
		return proxy.send();
	}

	public static List<String> getServiceNames() {
		return JS_ENDPOINT_NAMES;
	}

	public static Map<String,String> getServices() {
		return JS_ENDPOINTS;
	}

}
