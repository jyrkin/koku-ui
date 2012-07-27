/**
 *
 */
package fi.arcusys.koku.common.wsproxy.servlet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jms.IllegalStateException;
import javax.servlet.http.HttpSession;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.arcusys.koku.common.exceptions.KokuServiceException;
import fi.arcusys.koku.common.util.KokuWebServicesJS;
import fi.arcusys.koku.common.util.Properties;

/**
 * Contains common data used by several WSRestriction implementations
 * The class is to be initialized PER REQUEST as there could be request-related state here
 *
 * @author Mikhail Kapitonov (mikhail.kapitonov@arcusys.fi)
 * Jul 26, 2012
 */
public class WSCommonData {
    private static final Logger logger = LoggerFactory.getLogger(WSCommonData.class);

    private static final String CURRENT_USER_NAME= "javax.portlet.identity.token";
    private static final String TRACKED_USER_NAME= "ws.security.username";
    private static final String SECURITY_DATA_CONTAINER = "ws.security.common.data";

    private static final String GET_USER_UID_KUNPO = "<soapenv:Envelope " +
            "xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
            "xmlns:soa=\"http://soa.common.koku.arcusys.fi/\">" +
            "<soapenv:Header/><soapenv:Body>" +
            "<soa:getUserUidByKunpoName><kunpoUsername>%s</kunpoUsername></soa:getUserUidByKunpoName>" +
            "</soapenv:Body></soapenv:Envelope>";

    private static final String GET_USER_UID_LOORA = "<soapenv:Envelope " +
            "xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
            "xmlns:soa=\"http://soa.common.koku.arcusys.fi/\">" +
            "<soapenv:Header/><soapenv:Body>" +
            "<soa:getUserUidByLooraName><looraUsername>%s</looraUsername></soa:getUserUidByLooraName>" +
            "</soapenv:Body></soapenv:Envelope>";

    private static final String GET_USER_CHILDREN = "<soapenv:Envelope " +
            "xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
            "xmlns:soa=\"http://soa.common.koku.arcusys.fi/\">" +
            "<soapenv:Header/><soapenv:Body>" +
            "<soa:getUsersChildren><userUid>%s</userUid></soa:getUsersChildren>" +
            "</soapenv:Body></soapenv:Envelope>";

    private class WSDataContainer {
        public String userName;
        public String userUid;
        public List<OMElement> children; // contains a list of <child> elements from getUsersCildren response (KunPo only)
        public Set<String> userInfoAllowedUid = new HashSet<String>();
        public Set<String> userInfoAllowedKunpo = new HashSet<String>();
        public Set<String> userInfoAllowedLoora = new HashSet<String>();
    }

    private HttpSession session;
    private WSDataContainer dataContainer;
    private Map<KokuWebServicesJS, String> endpoints;

    @SuppressWarnings("unchecked")
    public WSCommonData(final HttpSession session, final Map<KokuWebServicesJS, String> endpoints) {
        this.session = session;
        this.endpoints = endpoints;

        final String currentUser = getCurrentUserName();
        final String trackedUser = (String) session.getAttribute(TRACKED_USER_NAME);

        // If user has been changed (or not set), remove the authentications
        if (currentUser == null || trackedUser == null || !currentUser.equals(trackedUser)) {
            session.removeAttribute(SECURITY_DATA_CONTAINER);
        }

        dataContainer = (WSDataContainer) session.getAttribute(SECURITY_DATA_CONTAINER);
        if (dataContainer == null) {
            dataContainer = new WSDataContainer();
            session.setAttribute(SECURITY_DATA_CONTAINER, dataContainer);

            if (Properties.IS_KUNPO_PORTAL)
                getUserInfoAllowedKunpo().add(getCurrentUserName());

            if (Properties.IS_LOORA_PORTAL)
                getUserInfoAllowedLoora().add(getCurrentUserName());

            fetchUserData();

            if (Properties.IS_KUNPO_PORTAL)
                fetchChildrenPermissions();
        }

    }

    private void fetchUserData() {
        try {
            OMElement response = null;

            if (Properties.IS_KUNPO_PORTAL) {
                response = WSCommonUtil.sendRequest(endpoints.get(KokuWebServicesJS.USERS_AND_GROUPS_SERVICE),
                        AXIOMUtil.stringToOM(String.format(GET_USER_UID_KUNPO, getCurrentUserName())));
            }

            if (Properties.IS_LOORA_PORTAL) {
                response = WSCommonUtil.sendRequest(endpoints.get(KokuWebServicesJS.USERS_AND_GROUPS_SERVICE),
                        AXIOMUtil.stringToOM(String.format(GET_USER_UID_LOORA, getCurrentUserName())));
            }

            if (response == null)
                throw new IllegalStateException("The portal type is undeterminable (should be KunPo or Loora)");

            final String userUid = WSCommonUtil.getTextOfChild(response, "userUid");

            dataContainer.userName = getCurrentUserName();
            dataContainer.userUid = userUid;

            dataContainer.userInfoAllowedUid.add(userUid);

        } catch (Exception e) {
            logger.warn("Could not fetch user data from WS endpoint", e);
        }

    }

    private void fetchChildrenPermissions() {
        try {
            OMElement response;

            response = WSCommonUtil.sendRequest(endpoints.get(KokuWebServicesJS.USERS_AND_GROUPS_SERVICE),
                    AXIOMUtil.stringToOM(String.format(GET_USER_CHILDREN, "")));

            // TODO: Fetch user's children

        } catch (Exception e) {
            logger.warn("Could not fetch children list from WS endpoint", e);
        }
    }

    public HttpSession getSession() {
        return session;
    }

    public String getCurrentUserName() {
        return (String) session.getAttribute(CURRENT_USER_NAME);
    }

    @SuppressWarnings("unchecked")
    public Set<String> getUserInfoAllowedUid() {
        return dataContainer.userInfoAllowedUid;
    }

    @SuppressWarnings("unchecked")
    public Set<String> getUserInfoAllowedKunpo() {
        return dataContainer.userInfoAllowedKunpo;
    }

    @SuppressWarnings("unchecked")
    public Set<String> getUserInfoAllowedLoora() {
        return dataContainer.userInfoAllowedLoora;
    }


}

