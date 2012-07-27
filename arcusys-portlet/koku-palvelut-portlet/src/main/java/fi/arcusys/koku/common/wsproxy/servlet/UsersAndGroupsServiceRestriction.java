/**
 *
 */
package fi.arcusys.koku.common.wsproxy.servlet;

import org.apache.axiom.om.OMElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.arcusys.koku.common.util.KokuWebServicesJS;

/**
 * Restrict UsersAndGroupsService methods
 *
 * @author Mikhail Kapitonov (mikhail.kapitonov@arcusys.fi)
 * Jul 25, 2012
 */
public class UsersAndGroupsServiceRestriction implements WSRestriction {
    private static final Logger logger = LoggerFactory.getLogger(UsersAndGroupsServiceRestriction.class);

    @Override
    public KokuWebServicesJS getAssociatedEndpoint() {
        return KokuWebServicesJS.USERS_AND_GROUPS_SERVICE;
    }

    @Override
    public boolean requestPermitted(WSCommonData commonData, final String methodName, final OMElement soapEnvelope) {
        boolean permitted = false;

        // UsersAndGroupsService - getUserInfo
        if (methodName.equals("getUserInfo")) {
            final String userUid = WSCommonUtil.getTextOfChild(soapEnvelope, "userUid");

            logger.info("getUserInfo: userUid = " + userUid);

            if (userUid != null && commonData.getUserInfoAllowedUid().contains(userUid)) {
                permitted = true;
            }

        // UsersAndGroupsService - getUserUidByKunpoName
        } else if (methodName.equals("getUserUidByKunpoName")) {
            final String kunpoUsername = WSCommonUtil.getTextOfChild(soapEnvelope, "kunpoUsername");

            logger.info("getUserUidByKunpoName: kunpoUsername = " + kunpoUsername);

            if (kunpoUsername != null && commonData.getUserInfoAllowedKunpo().contains(kunpoUsername)) {
                permitted = true;
            }

        } else {

        }

        return permitted;
    }

    @Override
    public boolean responsePermitted(WSCommonData commonData, final String methodName, final OMElement soapEnvelope) {
        return true;
    }

}
