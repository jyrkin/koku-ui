package fi.arcusys.koku.common.wsproxy.servlet;

import java.util.Set;

import org.apache.axiom.om.OMElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.arcusys.koku.common.util.KokuWebServicesJS;

/**
 * Restrict KokuMessageProcessingService methods
 *
 * @author Tapani Kiiskinen (tapani.kiiskinen@arcusys.fi)
 *         Mikhail Kapitonov (mikhail.kapitonov@arcusys.fi)
 * Jul 27, 2012
 */
public class MessageProcessingServiceRestriction implements WSRestriction {
    private static final Logger logger = LoggerFactory.getLogger(MessageProcessingServiceRestriction.class);

    @Override
    public KokuWebServicesJS getAssociatedEndpoint() {
        return KokuWebServicesJS.KV_MESSAGE_SERVICE;
    }

    @Override
    public boolean requestPermitted(WSCommonData commonData, String methodName, OMElement soapEnvelope) {

        boolean permitted = false;

        // Methods exposed to both Kunpo and Loora
        if (methodName.equals("getMessageById")) {

            // All checks done in responsePermitted
            permitted = true;
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

        if (methodName.equals("getMessageById")) {

            soapEnvelope = soapEnvelope.getFirstElement();
            String senderUid = WSCommonUtil.getTextOfChild(soapEnvelope, "senderUserInfo", "uid");
            Set<String> receiverUids = WSCommonUtil.getTextOfChildrenOfParents(soapEnvelope, "recipientUserInfos", "uid");
            String fromRoleUid = WSCommonUtil.getTextOfChild(soapEnvelope, "fromRoleUid");

            String currentUid = commonData.getCurrentUserUid();
            Set<String> currentUserRoles = commonData.getCurrentUserRoles();

            // Accessible to people who are senders, receivers or who have the associated role
            if (currentUid.equals(senderUid) || receiverUids.contains(currentUid) || currentUserRoles.contains(fromRoleUid)) {

                permitted = true;
                if (senderUid != null) {
                    commonData.getUserInfoAllowedUid().add(senderUid);
                }
                commonData.getUserInfoAllowedUid().addAll(receiverUids);
            }
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

