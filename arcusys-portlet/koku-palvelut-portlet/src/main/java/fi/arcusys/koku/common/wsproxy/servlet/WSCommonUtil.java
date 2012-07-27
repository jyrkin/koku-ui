/**
 *
 */
package fi.arcusys.koku.common.wsproxy.servlet;

import java.util.Iterator;

import javax.swing.text.html.parser.Element;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;

/**
 * Utility class for WS security
 *
 * @author Mikhail Kapitonov (mikhail.kapitonov@arcusys.fi)
 * Jul 26, 2012
 */
public class WSCommonUtil {

    /**
     * Gets the text of the child element with specified tag name
     * @param parent Parent element
     * @param childName Child's tag name
     * @return Text or null if the child is not found
     *
     * */
    @SuppressWarnings("unchecked")
    public static String getTextOfChild(OMElement parent, String childName) {
        OMElement child = null;

        final Iterator<OMElement> elements = parent.getChildrenWithLocalName(childName);
        if (elements.hasNext())
            child = elements.next();

        return child != null ? child.getText() : null;
    }

    /**
     * Sends a SOAP request and returns the result
     * @param endpointURI The URI to the target endpoint
     * @param soapRequest The SOAP request envelope
     * @return The SOAP response envelope
     *
     * */
    public static OMElement sendRequest(final String endpointURI, OMElement soapRequest)
            throws AxisFault {

        Options options = new Options();
        options.setTo(new EndpointReference(endpointURI));

        ServiceClient client = new ServiceClient();
        client.setOptions(options);

        return client.sendReceive(soapRequest);
    }
}
