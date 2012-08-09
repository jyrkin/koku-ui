package fi.arcusys.koku.common.wsproxy.servlet;

import org.apache.axiom.om.OMElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.arcusys.koku.common.util.KokuWebServicesJS;

/**
 * Restrict KokuHakProcessingService methods
 *
 * @author Tapani Kiiskinen (tapani.kiiskinen@arcusys.fi)
 * Aug, 2012
 */
public class HakProcessingServiceRestriction implements WSRestriction {
    private static final Logger logger = LoggerFactory.getLogger(MessageProcessingServiceRestriction.class);

    @Override
    public KokuWebServicesJS getAssociatedEndpoint() {
        return KokuWebServicesJS.HAK_PROCESSING_SERVICE;
    }

    @Override
    public boolean requestPermitted(WSCommonData commonData, String methodName, OMElement soapEnvelope) {

        boolean permitted = false;

        // No methods currently exposed through proxy

        if (permitted) {
            logger.info("Permission granted for " + commonData.getCurrentUserName()
                    + " requesting " + methodName);
        }
        else {
            logger.warn("No permissions granted for " + commonData.getCurrentUserName()
                    + " requesting " + methodName + ". Request data: " + soapEnvelope.toString());
        }

        return permitted;
    }

    @Override
    public boolean responsePermitted(WSCommonData commonData, String methodName, OMElement soapEnvelope) {

        boolean permitted = false;

        // No methods currently exposed through proxy

        if (permitted) {
            logger.info("Permission granted for " + commonData.getCurrentUserName()
                    + " accessing " + methodName + " request data.");
        }
        else {
            logger.warn("No permissions granted for " + commonData.getCurrentUserName()
                    + " accessing " + methodName + " request data. Request return data: " + soapEnvelope.toString());
        }

        return permitted;
    }

}

