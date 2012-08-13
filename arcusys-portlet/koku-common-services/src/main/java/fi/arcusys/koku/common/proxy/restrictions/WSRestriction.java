/**
 *
 */
package fi.arcusys.koku.common.proxy.restrictions;

import org.apache.axiom.om.OMElement;

import fi.arcusys.koku.common.proxy.util.WSCommonData;
import fi.arcusys.koku.common.util.KokuWebServicesJS;

/**
 * Base interface for WS restriction implementations
 *
 * @author Mikhail Kapitonov (mikhail.kapitonov@arcusys.fi)
 * Jul 24, 2012
 */
public interface WSRestriction {

    KokuWebServicesJS getAssociatedEndpoint();

    boolean requestPermitted(WSCommonData commonData, String methodName, OMElement soapEnvelope);

    boolean responsePermitted(WSCommonData commonData, String methodName, OMElement soapEnvelope);

}
