/**
 *
 */
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

        if (methodName.equals("getAppointment"))
        {
            // Only accessible to Loora users
            if (Properties.IS_LOORA_PORTAL) {
                permitted = true;
            }
        }
        else if (methodName.equals("getAppointmentForReply"))
        {
            // Accessible for both Kunpo and Loora users
            String targetChildUid = WSCommonUtil.getTextOfChild(soapEnvelope, "targetUser");
            // Only people who have permissions to do decisions for targetChild can use this method
            if (commonData.getUserInfoAllowedUid().contains(targetChildUid)) {
                permitted = true;
            }
        }
        else if (methodName.equals("storeAppointment"))
        {
            // Only accessible to Loora users
            if (Properties.IS_LOORA_PORTAL) {
                permitted = true;
            }
        }
        else if (methodName.equals("approveAppointment"))
        {
            // Only accessible to Kunpo users
            if (Properties.IS_KUNPO_PORTAL) {
                String userUid = WSCommonUtil.getTextOfChild(soapEnvelope, "user");
                // Only Kunpo user in question of appointment can decline or approve said appointment
                if (userUid.equals(commonData.getCurrentUserUid())) {
                    permitted = true;
                }
            }
        }
        else if (methodName.equals("declineAppointment"))
        {
            // Only accessible to Kunpo users
            if (Properties.IS_KUNPO_PORTAL) {
                String userUid = WSCommonUtil.getTextOfChild(soapEnvelope, "user");
                // Only Kunpo user in question of appointment can decline or approve said appointment
                if (userUid.equals(commonData.getCurrentUserUid())) {
                    permitted = true;
                }
            }
        }

        return permitted;
    }

    @Override
    public boolean responsePermitted(WSCommonData commonData, String methodName, OMElement soapEnvelope) {

        boolean permitted = false;

        if (methodName.equals("getAppointment"))
        {
            String uid = WSCommonUtil.getTextOfChild(soapEnvelope, "uid");
            // Only creator of appointment can edit the whole appointment
            if (uid.equals(commonData.getCurrentUserUid())) {
                String appointmentId = WSCommonUtil.getTextOfChild(soapEnvelope, "appointmentId");
                // Only valid appointmentIds are stored for future permissions and returned
                if (appointmentId != null) {
                    commonData.getAvAllowedMeetingIdSet().add(appointmentId);
                    permitted = true;
                }
            }
        }
        else if (methodName.equals("getAppointmentForReply"))
        {
            // Checks done in requestPermitted
            permitted = true;

        }
        else if (methodName.equals("storeAppointment"))
        {
            String appointmentId = WSCommonUtil.getTextOfChild(soapEnvelope, "return");
            // Only meetings that had been previously opened in this session can be stored
            if (commonData.getAvAllowedMeetingIdSet().contains(appointmentId)) {
                permitted = true;
            }
        }
        else if (methodName.equals("approveAppointment"))
        {
            // Checks done in requestPermitted
            permitted = true;
        }
        else if (methodName.equals("declineAppointment"))
        {
            // Checks done in requestPermitted
            permitted = true;
        }

        return permitted;
    }

}
