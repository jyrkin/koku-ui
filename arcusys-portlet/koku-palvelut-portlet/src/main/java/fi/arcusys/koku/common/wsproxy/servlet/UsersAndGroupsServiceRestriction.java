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

        // UsersAndGroupsService - loginByKunpoNameAndSsn
        if (methodName.equalsIgnoreCase("loginByKunpoNameAndSsn")) {
            // TODO: permit if necessary

        // UsersAndGroupsService - loginByLooraNameAndSsn
        } else if (methodName.equalsIgnoreCase("loginByLooraNameAndSsn")) {
            // TODO: permit if necessary

        // UsersAndGroupsService - getUserUidByKunpoName
        } else if (methodName.equalsIgnoreCase("getUserUidByKunpoName")) {
            final String kunpoUsername = WSCommonUtil.getTextOfChild(soapEnvelope, "kunpoUsername");

            logger.info("getUserUidByKunpoName: kunpoUsername = " + kunpoUsername);

            if (kunpoUsername != null && commonData.getUserInfoAllowedKunpo().contains(kunpoUsername)) {
                permitted = true;
            } else {
                logger.warn("User is not permitted to get the information about other non-relevant users");
            }

        // UsersAndGroupsService - getKunpoNameByUserUid
        } else if (methodName.equalsIgnoreCase("getKunpoNameByUserUid")) {
            // TODO: permit if necessary

        // UsersAndGroupsService - getUserUidByLooraName
        } else if (methodName.equalsIgnoreCase("getUserUidByLooraName")) {
            final String looraUsername = WSCommonUtil.getTextOfChild(soapEnvelope, "looraUsername");

            logger.info("getUserUidByLooraName: looraUsername = " + looraUsername);

            if (looraUsername != null && commonData.getUserInfoAllowedLoora().contains(looraUsername)) {
                permitted = true;
            } else {
                logger.warn("User is not permitted to get the information about other non-relevant users");
            }

        // UsersAndGroupsService - getLooraNameByUserUid
        } else if (methodName.equalsIgnoreCase("getLooraNameByUserUid")) {
            // TODO: permit if necessary

        // UsersAndGroupsService - searchUsers
        } else if (methodName.equalsIgnoreCase("searchUsers")) {
            // Permitted for Loora users
            if (Properties.IS_LOORA_PORTAL)
                permitted = true;

        // UsersAndGroupsService - searchEmployees
        } else if (methodName.equalsIgnoreCase("searchEmployees")) {
            // Permitted for Loora users
            if (Properties.IS_LOORA_PORTAL)
                permitted = true;

        // UsersAndGroupsService - searchGroups
        } else if (methodName.equalsIgnoreCase("searchGroups")) {
            // Permitted for Loora users
            if (Properties.IS_LOORA_PORTAL)
                permitted = true;

        // UsersAndGroupsService - getUsersByGroupUid
        } else if (methodName.equalsIgnoreCase("getUsersByGroupUid")) {
            // TODO: permit if necessary

        // UsersAndGroupsService - searchChildren
        } else if (methodName.equalsIgnoreCase("searchChildren")) {
            // Permitted for Loora users
            if (Properties.IS_LOORA_PORTAL)
                permitted = true;

        // UsersAndGroupsService - getUsersChildren
        } else if (methodName.equalsIgnoreCase("getUsersChildren")) {
            final String userUid = WSCommonUtil.getTextOfChild(soapEnvelope, "userUid");

            if (userUid != null && commonData.getUserInfoAllowedUid().contains(userUid)) {
                permitted = true;
            } else {
                logger.warn("User is not permitted to get the information about other non-relevant users");
            }

        // UsersAndGroupsService - getChildInfo
        } else if (methodName.equalsIgnoreCase("getChildInfo")) {
            // TODO: permit if necessary

        // UsersAndGroupsService - getUserInfo
        } else if (methodName.equalsIgnoreCase("getUserInfo")) {
            final String userUid = WSCommonUtil.getTextOfChild(soapEnvelope, "userUid");

            logger.info("getUserInfo: userUid = " + userUid);

            if (userUid != null && commonData.getUserInfoAllowedUid().contains(userUid)) {
                permitted = true;
            } else {
                logger.warn("User is not permitted to get the information about other non-relevant users");
            }

        // UsersAndGroupsService - getSsnByLdapName
        } else if (methodName.equalsIgnoreCase("getSsnByLdapName")) {
            // TODO: permit if necessary

        // UsersAndGroupsService - getSsnByLooraName
        } else if (methodName.equalsIgnoreCase("getSsnByLooraName")) {
            final String user = WSCommonUtil.getTextOfChild(soapEnvelope, "arg0");

            logger.info("getSsnByLooraName: String = " + user);

            if (user != null && commonData.getUserInfoAllowedLoora().contains(user)) {
                permitted = true;
            } else {
                logger.warn("User is not permitted to get the information about other non-relevant users");
            }

        // UsersAndGroupsService - getSsnByKunpoName
        } else if (methodName.equalsIgnoreCase("getSsnByKunpoName")) {
            final String user = WSCommonUtil.getTextOfChild(soapEnvelope, "arg0");

            logger.info("getSsnByKunpoName: String = " + user);

            if (user != null && commonData.getUserInfoAllowedKunpo().contains(user)) {
                permitted = true;
            } else {
                logger.warn("User is not permitted to get the information about other non-relevant users");
            }

        // UsersAndGroupsService - getUserUidByKunpoSsn
        } else if (methodName.equalsIgnoreCase("getUserUidByKunpoSsn")) {
            // TODO: permit if necessary

        // UsersAndGroupsService - getUserUidByEmployeeSsn
        } else if (methodName.equalsIgnoreCase("getUserUidByEmployeeSsn")) {
            // TODO: permit if necessary

        // UsersAndGroupsService - getUserRoles
        } else if (methodName.equalsIgnoreCase("getUserRoles")) {
            // Permitted for Loora users
            if (Properties.IS_LOORA_PORTAL)
                permitted = true;

        // UsersAndGroupsService - searchRoles
        } else if (methodName.equalsIgnoreCase("searchRoles")) {
            // Permitted for Loora users
            if (Properties.IS_LOORA_PORTAL)
                permitted = true;

        // UsersAndGroupsService - getUsernamesInRole
        } else if (methodName.equalsIgnoreCase("getUsernamesInRole")) {
            // Permitted for Loora users
            if (Properties.IS_LOORA_PORTAL)
                permitted = true;

        // UsersAndGroupsService - getUserOrganizations
        } else if (methodName.equalsIgnoreCase("getUserOrganizations")) {
            // Permitted for Loora users
            if (Properties.IS_LOORA_PORTAL)
                permitted = true;

        // UsersAndGroupsService - getUserOrganizationsByLooraName
        } else if (methodName.equalsIgnoreCase("getUserOrganizationsByLooraName")) {
            // Permitted for Loora users
            if (Properties.IS_LOORA_PORTAL)
                permitted = true;

        }

        logger.info("User is "+(permitted ? "" : "not ")+"permitted to make this request ("+methodName+") on " +
                (Properties.IS_KUNPO_PORTAL ? "KunPo" : "Loora"));

        return permitted;
    }

    @Override
    public boolean responsePermitted(WSCommonData commonData, final String methodName, final OMElement soapEnvelope) {
        return true;
    }

}
