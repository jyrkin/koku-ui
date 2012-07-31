/**
 *
 */
package fi.arcusys.koku.common.wsproxy.servlet;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jms.IllegalStateException;
import javax.print.attribute.ResolutionSyntax;
import javax.servlet.http.HttpSession;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final String GET_USER_UID_KUNPO =
            "<soa:getUserUidByKunpoName xmlns:soa=\"http://soa.common.koku.arcusys.fi/\"><kunpoUsername>%s</kunpoUsername></soa:getUserUidByKunpoName>";

    private static final String GET_USER_UID_LOORA =
            "<soa:getUserUidByLooraName xmlns:soa=\"http://soa.common.koku.arcusys.fi/\"><looraUsername>%s</looraUsername></soa:getUserUidByLooraName>";

    private static final String GET_USER_CHILDREN =
            "<soa:getUsersChildren xmlns:soa=\"http://soa.common.koku.arcusys.fi/\"><userUid>%s</userUid></soa:getUsersChildren>";

    private static class WSDataContainer implements Serializable {
        public String userName;
        public String userUid;
        public List<OMElement> children; // contains a list of <child> elements from getUsersChildren response (KunPo only)
        public Set<String> userInfoAllowedUid = new HashSet<String>();
        public Set<String> userInfoAllowedKunpo = new HashSet<String>();
        public Set<String> userInfoAllowedLoora = new HashSet<String>();
        public Set<String> avAllowedMeetingIdSet = new HashSet<String>();
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

        logger.info("Session ID is: "+session.getId());

        logger.info("There "+(session.getAttribute(SECURITY_DATA_CONTAINER) != null ? "IS" : "ISN'T")+" A DATA CONTAINER IN SESSION");

        // If user has been changed (or not set), remove the authentications
        if (currentUser == null || trackedUser == null || !currentUser.equals(trackedUser)) {
            session.removeAttribute(SECURITY_DATA_CONTAINER);
        }

        // Track current user name
        session.setAttribute(TRACKED_USER_NAME, currentUser);

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

    /**
     * This function fetches user's UID based on their KunPo user name
     * IT WILL NOT CHECK PERMISSIONS
     * */
    public String getUserUidByKunpoName(final String userName) throws AxisFault, XMLStreamException {
        OMElement response = WSCommonUtil.sendRequest(endpoints.get(KokuWebServicesJS.USERS_AND_GROUPS_SERVICE),
                AXIOMUtil.stringToOM(String.format(GET_USER_UID_KUNPO, userName)));

        return WSCommonUtil.getTextOfChild(response, "userUid");
    }

    /**
     * This function fetches user's UID based on their Loora user name
     * IT WILL NOT CHECK PERMISSIONS
     * */
    public String getUserUidByLooraName(final String userName) throws AxisFault, XMLStreamException {
        OMElement response = WSCommonUtil.sendRequest(endpoints.get(KokuWebServicesJS.USERS_AND_GROUPS_SERVICE),
                AXIOMUtil.stringToOM(String.format(GET_USER_UID_LOORA, userName)));

        return WSCommonUtil.getTextOfChild(response, "userUid");
    }

    private void fetchUserData() {
        try {
            final String currentUser = getCurrentUserName();
            String userUid = null;

            if (Properties.IS_KUNPO_PORTAL) {
                userUid = getUserUidByKunpoName(currentUser);
            } else if (Properties.IS_LOORA_PORTAL) {
                userUid = getUserUidByLooraName(currentUser);
            } else {
                throw new IllegalStateException("The portal type is undeterminable (should be KunPo or Loora)");
            }

            if (userUid == null)
                throw new Exception("Services are unavailable (bad response)");

            dataContainer.userName = currentUser;
            dataContainer.userUid = userUid;

            logger.info("Fetched user UID: "+dataContainer.userUid);
            logger.info("Fetched user name: "+dataContainer.userName);

            dataContainer.userInfoAllowedUid.add(userUid);

        } catch (Exception e) {
            logger.warn("Could not fetch user data from WS endpoint", e);
        }

    }

    @SuppressWarnings("unchecked")
    private void fetchChildrenPermissions() {
        try {
            OMElement response = WSCommonUtil.sendRequest(endpoints.get(KokuWebServicesJS.USERS_AND_GROUPS_SERVICE),
                    AXIOMUtil.stringToOM(String.format(GET_USER_CHILDREN, dataContainer.userUid)));

            logger.info("Children: " + response);

            final Iterator<OMElement> i = response.getChildrenWithLocalName("child");
            while (i.hasNext()) {
                final OMElement child = i.next();
                final String uid = WSCommonUtil.getTextOfChild(child, "uid");
                logger.info("Adding allowed UID: " + dataContainer.userUid);
                dataContainer.userInfoAllowedUid.add(uid);
            }

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

    public Set<String> getUserInfoAllowedUid() {
        return dataContainer.userInfoAllowedUid;
    }

    public Set<String> getUserInfoAllowedKunpo() {
        return dataContainer.userInfoAllowedKunpo;
    }

    public Set<String> getUserInfoAllowedLoora() {
        return dataContainer.userInfoAllowedLoora;
    }

    public Set<String> getAvAllowedMeetingIdSet() {
        return dataContainer.avAllowedMeetingIdSet;
    }

    public String getCurrentUserUid() {
        return dataContainer.userUid;
    }

}

