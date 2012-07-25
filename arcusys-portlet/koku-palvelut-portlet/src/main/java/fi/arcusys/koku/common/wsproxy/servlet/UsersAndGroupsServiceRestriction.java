/**
 *
 */
package fi.arcusys.koku.common.wsproxy.servlet;

import org.apache.axiom.om.OMElement;

import fi.arcusys.koku.common.util.KokuWebServicesJS;

/**
 * Restrict UsersAndGroupsService methods
 *
 * @author Mikhail Kapitonov (mikhail.kapitonov@arcusys.fi)
 * Jul 25, 2012
 */
public class UsersAndGroupsServiceRestriction implements WSRestriction {

    @Override
    public KokuWebServicesJS getAssociatedEndpoint() {
        return KokuWebServicesJS.USERS_AND_GROUPS_SERVICE;
    }

    @Override
    public boolean requestPermitted(final String userName, final String methodName, final OMElement soapEnvelope) {
        return true;
    }

    @Override
    public boolean responsePermitted(final String userName, final String methodName, final OMElement soapEnvelope) {
        return true;
    }

}
