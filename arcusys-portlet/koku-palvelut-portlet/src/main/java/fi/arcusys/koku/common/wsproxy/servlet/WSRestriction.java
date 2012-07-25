/**
 *
 */
package fi.arcusys.koku.common.wsproxy.servlet;

import org.apache.axiom.om.OMElement;

import fi.arcusys.koku.common.util.KokuWebServicesJS;

/**
 * Base interface for WS restriction implementations
 *
 * @author Mikhail Kapitonov (mikhail.kapitonov@arcusys.fi)
 * Jul 24, 2012
 */
public interface WSRestriction {

    KokuWebServicesJS getAssociatedEndpoint();

    boolean requestPermitted(String userName, String methodName, OMElement soapEnvelope);

    boolean responsePermitted(String userName, String methodName, OMElement soapEnvelope);

}
