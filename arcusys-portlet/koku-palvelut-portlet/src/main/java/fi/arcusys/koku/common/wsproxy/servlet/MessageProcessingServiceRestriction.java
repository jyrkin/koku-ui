package fi.arcusys.koku.common.wsproxy.servlet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.axiom.om.OMElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.arcusys.koku.common.util.KokuWebServicesJS;
import fi.arcusys.koku.common.util.Properties;

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

        if (methodName.equals("sendMessage"))
        {
            // Accessible for both Kunpo and Loora users
            String fromUid = WSCommonUtil.getTextOfChild(soapEnvelope, "fromUser");
            if (fromUid.equals(commonData.getCurrentUserUid())) {
                permitted = true;
            }
        }
        else if (methodName.equals("receiveMessage"))
        {
            // Because of the way the functions are separated from each other
            // it's not possible to check this function for anything without
            // refactoring the services functions completely which is not an
            // option at this point. Sadly this function will remain unchecked.
            permitted = true;
        }
        else if (methodName.equals("receiveNewMessage"))
        {
            // Accessible for both Kunpo and Loora users
            String fromUid = WSCommonUtil.getTextOfChild(soapEnvelope, "fromUser");
            if (fromUid.equals(commonData.getCurrentUserUid())) {
                permitted = true;
            }
        }
        else if (methodName.equals("getMessageById"))
        {
            // All checks done in responsePermitted
            permitted = true;
        }

        if (permitted) {
            logger.info("Permission granted for " + commonData.getCurrentUserName()
                    + " requesting " + methodName);
        } else {
            logger.warn("No permissions granted for " + commonData.getCurrentUserName()
                    + " requesting " + methodName + ". Request data: " + soapEnvelope.toString());
        }

        return permitted;
    }

    @Override
    public boolean responsePermitted(WSCommonData commonData, String methodName, OMElement soapEnvelope) {

        boolean permitted = false;

        if (methodName.equals("sendMessage"))
        {
            // All checks done in requestPermitted
            permitted = true;
        }
        else if (methodName.equals("receiveMessage"))
        {
            // Because of the way the functions are separated from each other
            // it's not possible to check this function for anything without
            // refactoring the services functions completely which is not an
            // option at this point. Sadly this function will remain unchecked.
            permitted = true;
        }
        else if (methodName.equals("receiveNewMessage"))
        {
            // All checks done in requestPermitted
            permitted = true;
        }
        else if (methodName.equals("getMessageById"))
        {
            soapEnvelope = soapEnvelope.getFirstElement();
            String senderUid = WSCommonUtil.getTextOfChild(soapEnvelope, "senderUserInfo", "uid");
            Set<String> receiverUids = WSCommonUtil.getTextOfChildrenOfParents(soapEnvelope, "recipientUserInfos", "uid");
            String fromRoleUid = WSCommonUtil.getTextOfChild(soapEnvelope, "fromRoleUid");
            String currentUid = commonData.getCurrentUserUid();
            Set<String> currentUserRoles = commonData.getCurrentUserRoles();

            // Accessible to people who are senders, receivers or who have the associated role
            if (currentUid.equals(senderUid) || receiverUids.contains(currentUid) || currentUserRoles.contains(fromRoleUid)) {
                permitted = true;
            }
        }

        if (permitted) {
            logger.info("Permission granted for " + commonData.getCurrentUserName()
                    + " accessing " + methodName + " request data.");
        } else {
            logger.warn("No permissions granted for " + commonData.getCurrentUserName()
                    + " accessing " + methodName + " request data. Request return data: " + soapEnvelope.toString());
        }

        return permitted;
    }

}

