/**
 *
 */
package fi.arcusys.koku.common.wsproxy.servlet;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
     * Gets the first child element with specified tag name
     * @param parent Parent element
     * @param childName Child's tag name
     * @return OMElement or null if the child is not found
     *
     * */
    @SuppressWarnings("unchecked")
    public static OMElement getFirstOMElement(OMElement parent, String childName) {
        OMElement child = null;

        final Iterator<OMElement> elements = parent.getChildrenWithLocalName(childName);
        if (elements.hasNext())
        {
            child = elements.next();
        }

        return child;
    }

    /**
     * Gets the text of the first child element with specified tag name
     * @param parent Parent element
     * @param childName Child's tag name
     * @return Text or null if the child is not found
     *
     * */
    public static String getTextOfChild(OMElement parent, String childName) {
        OMElement child = getFirstOMElement(parent, childName);
        return child != null ? child.getText() : null;
    }

    /**
     * Gets the text of the first child element with specified tag name and
     * parent name
     * @param grandParentElement Grandparent element
     * @param parentName parent element name
     * @param childName child element name
     * @return Text or null if the parent or child is not found
     *
     * */
    public static String getTextOfChild(OMElement grandParentElement, String parentName, String childName) {
        OMElement parent = getFirstOMElement(grandParentElement, parentName);
        return parent != null ? getTextOfChild(parent, childName) : null;
    }

    /**
     * Returns a set of all childName element texts inside this type of structure:
     *
     *                     parentName - childName
     *                    /
     * grandParentElement
     *                    \
     *                     parentName - childName
     *
     * @param grandParentElement Grandparent element
     * @param parentName parent element name
     * @param childName child element name
     * @return Set of strings or empty set if no parents or children are found
     *
     * */
    @SuppressWarnings("unchecked")
    public static Set<String> getTextOfChildrenOfParents(OMElement grandParentElement, String parentName, String childName) {
        Set<String> children = new HashSet<String>();
        for (Iterator<OMElement> parents = grandParentElement.getChildrenWithLocalName(parentName); parents.hasNext(); ) {
            String childText = getTextOfChild(parents.next(), childName);
            if (childText != null) {
                children.add(childText);
            }
        }

        return children;
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
