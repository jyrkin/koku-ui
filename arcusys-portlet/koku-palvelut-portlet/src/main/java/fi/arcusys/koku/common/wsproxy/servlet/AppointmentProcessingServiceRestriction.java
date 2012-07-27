/**
 *
 */
package fi.arcusys.koku.common.wsproxy.servlet;

import org.apache.axiom.om.OMElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.arcusys.koku.common.util.KokuWebServicesJS;

/**
 * Restrict KokuAppointmentProcessingService methods
 *
 * @author Mikhail Kapitonov (mikhail.kapitonov@arcusys.fi)
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

        // KokuAppointmentProcessingService - getAppointmentForReply
        if (methodName.equals("getAppointmentForReply")) {
            permitted = true; // passthrough, will check the reply

        }

        return permitted;
    }

    @Override
    public boolean responsePermitted(WSCommonData commonData, String methodName, OMElement soapEnvelope) {
        boolean permitted = false;

        // KokuAppointmentProcessingService - getAppointmentForReply
        if (methodName.equals("getAppointmentForReply")) {
            permitted = true; // permitted, main check is in getter
        }

        return permitted;
    }

}
