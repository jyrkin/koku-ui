package fi.koku.kks.controller;

import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.portlet.bind.annotation.ActionMapping;

import fi.koku.kks.model.Creatable;
import fi.koku.kks.model.Creation;
import fi.koku.kks.model.KksService;
import fi.koku.kks.model.Person;
import fi.koku.kks.ui.common.State;
import fi.koku.kks.ui.common.utils.Utils;

/**
 * Controller for creating agreements and plans
 * 
 * @author tuomape
 */
@Controller(value = "createCollectionController")
@RequestMapping(value = "VIEW")
public class CreateCollectionController {

  @Autowired
  @Qualifier("kksService")
  private KksService kksService;

  private static final Logger LOG = LoggerFactory.getLogger(CreateCollectionController.class);

  @ActionMapping(params = "action=createNewVersion")
  public void createVersion(PortletSession session, @ModelAttribute(value = "child") Person child,
      @RequestParam String id, @RequestParam String name, @RequestParam(required = false) String clean,
      ActionResponse response, SessionStatus sessionStatus) {

    LOG.debug("create new version");

    String collection = kksService.createKksCollectionVersion(name, id, child.getPic(), Boolean.valueOf(clean),
        Utils.getPicFromSession(session));

    response.setRenderParameter("action", "showCollection");
    response.setRenderParameter("pic", child.getPic());
    response.setRenderParameter("collection", collection);

    sessionStatus.setComplete();
  }

  @ActionMapping(params = "action=createCollection")
  public void create(PortletSession session, @ModelAttribute(value = "child") Person child,
      @ModelAttribute(value = "creation") Creation creation, BindingResult bindingResult, ActionResponse response,
      SessionStatus sessionStatus) {

    LOG.debug("create collection");

    Creatable a = Creatable.create(creation.getField());
    String name = "".equals(creation.getName()) ? a.getName() : creation.getName();
    String collection = kksService.createKksCollection(name, a.getId(), child.getPic(),
        Utils.getPicFromSession(session));

    response.setRenderParameter("action", "showChild");
    response.setRenderParameter("pic", child.getPic());
    creation.setName("");
    creation.setField("");
    sessionStatus.setComplete();
  }

  @ModelAttribute("child")
  public Person getchild(PortletSession session, @RequestParam String pic) {
    LOG.info("getchild");
    return kksService.searchCustomer(pic, Utils.getPicFromSession(session));
  }

  @ModelAttribute("creation")
  public Creation getCommandObject() {
    LOG.debug("get creation command object");
    return new Creation();
  }

  @ActionMapping(params = "action=activate")
  public void activate(PortletSession session, @ModelAttribute(value = "child") Person child,
      @RequestParam(value = "collection") String collection, ActionResponse response, SessionStatus sessionStatus) {

    kksService.updateKksCollectionStatus(child.getPic(), collection, State.ACTIVE.toString(),
        Utils.getPicFromSession(session));
    response.setRenderParameter("action", "showChild");
    response.setRenderParameter("pic", child.getPic());
    sessionStatus.setComplete();
  }

  @ActionMapping(params = "action=lock")
  public void lock(PortletSession session, @ModelAttribute(value = "child") Person child,
      @RequestParam(value = "collection") String collection, ActionResponse response, SessionStatus sessionStatus) {

    kksService.updateKksCollectionStatus(child.getPic(), collection, State.LOCKED.toString(),
        Utils.getPicFromSession(session));

    response.setRenderParameter("action", "showChild");
    response.setRenderParameter("pic", child.getPic());
    sessionStatus.setComplete();
  }
}
