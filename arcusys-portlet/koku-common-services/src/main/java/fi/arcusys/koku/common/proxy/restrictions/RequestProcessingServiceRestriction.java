package fi.arcusys.koku.common.proxy.restrictions;

import org.apache.axiom.om.OMElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.arcusys.koku.common.proxy.util.WSCommonData;
import fi.arcusys.koku.common.proxy.util.WSCommonUtil;
import fi.arcusys.koku.common.util.KokuWebServicesJS;
import fi.arcusys.koku.common.util.Properties;

/**
 * Restrict KokuRequestProcessingService methods
 *
 * @author Tapani Kiiskinen (tapani.kiiskinen@arcusys.fi)
 * Aug, 2012
 */
public class RequestProcessingServiceRestriction implements WSRestriction {
    private static final Logger logger = LoggerFactory.getLogger(MessageProcessingServiceRestriction.class);

    @Override
    public KokuWebServicesJS getAssociatedEndpoint() {
        return KokuWebServicesJS.REQUEST_PROCESSING_SERVICE;
    }

    @Override
    public boolean requestPermitted(WSCommonData commonData, String methodName, OMElement soapEnvelope) {

        boolean permitted = false;

        // Methods exposed to Loora
        if (Properties.IS_LOORA_PORTAL) {

            if (methodName.equals("getRequestTemplateSummary")) {

                String uid = WSCommonUtil.getTextOfChild(soapEnvelope, "user");

                // Only users themselves can ask for their templates.
                if (commonData.getCurrentUserUid().equals(uid)) {
                    permitted = true;
                }
            }
            else if (methodName.equals("getRequestTemplateById")) {
                permitted = true;
            }
            else if (methodName.equals("isRequestTemplateExist")) {
                permitted = true;
            }
        }

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

        if (methodName.equals("getRequestTemplateSummary")) {
            // All checks done in requestPermitted
            permitted = true;
        }
        else if (methodName.equals("getRequestTemplateById")) {
            // All checks done in requestPermitted
            permitted = true;
        }
        else if (methodName.equals("isRequestTemplateExist")) {
            permitted = true;
        }


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

