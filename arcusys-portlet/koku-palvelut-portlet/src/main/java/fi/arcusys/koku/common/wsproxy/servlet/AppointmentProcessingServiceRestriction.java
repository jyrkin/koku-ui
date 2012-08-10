package fi.arcusys.koku.common.wsproxy.servlet;

import org.apache.axiom.om.OMElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.arcusys.koku.common.util.KokuWebServicesJS;
import fi.arcusys.koku.common.util.Properties;

/**
 * Restrict KokuAppointmentProcessingService methods
 *
 * @author Mikhail Kapitonov (mikhail.kapitonov@arcusys.fi)
 *         Tapani Kiiskinen (tapani.kiiskinen@arcusys.fi)
 * Jul 27, 2012
 */
public class AppointmentProcessingServiceRestriction implements WSRestriction {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentProcessingServiceRestriction.class);

    @Override
    public KokuWebServicesJS getAssociatedEndpoint() {
        return KokuWebServicesJS.APPOINTMENT_PROCESSING_SERVICE;
    }

    @Override
    public boolean requestPermitted(WSCommonData commonData, String methodName, OMElement soapEnvelope) {

        boolean permitted = false;

        // Methods exposed to both Kunpo and Loora
        if (methodName.equals("getAppointmentForReply")) {

            String targetChildUid = WSCommonUtil.getTextOfChild(soapEnvelope, "targetUser");

            // Only people who have permissions to do decisions for targetChild can use this method
            if (commonData.getUserInfoAllowedUid().contains(targetChildUid)) {
                permitted = true;
            }
        }

        // Methods exposed to Loora
        if (Properties.IS_LOORA_PORTAL) {

            if (methodName.equals("getAppointment")) {
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

    private void permitMentionedParties(WSCommonData commonData, OMElement soapEnvelope) {
        final String sender = WSCommonUtil.getTextOfChild(soapEnvelope, "sender");
        final String senderUid = WSCommonUtil.getTextOfChild(soapEnvelope, "senderUserInfo", "uid");

        logger.info("Adding "+sender+" ("+senderUid+") to the permisson list");

        commonData.getUserInfoAllowedLoora().add(sender);
        commonData.getUserInfoAllowedUid().add(senderUid);
    }

    @Override
    public boolean responsePermitted(WSCommonData commonData, String methodName, OMElement soapEnvelope) {

        boolean permitted = false;

        if (methodName.equals("getAppointment")) {

            soapEnvelope = soapEnvelope.getFirstElement();
            String uid = WSCommonUtil.getTextOfChild(soapEnvelope, "senderUserInfo", "uid");
            String role = WSCommonUtil.getTextOfChild(soapEnvelope, "senderRole");

            // Only creator of appointment or person with right role can edit the appointment
            if (commonData.getCurrentUserUid().equals(uid) || commonData.getCurrentUserRoles().contains(role)) {

                String appointmentId = WSCommonUtil.getTextOfChild(soapEnvelope, "appointmentId");

                // Only valid appointmentIds are stored for future permissions and returned
                if (appointmentId != null) {
                    commonData.getAvAllowedMeetingIdSet().add(appointmentId);
                    permitted = true;
                }
            }

            // If the checks are successful, add mentioned users to permitted lists
            if (permitted) {
                permitMentionedParties(commonData, soapEnvelope);
            }
        }
        else if (methodName.equals("getAppointmentForReply")) {

            // All checks done in requestPermitted
            permitted = true;

            permitMentionedParties(commonData, soapEnvelope.getFirstElement());
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
