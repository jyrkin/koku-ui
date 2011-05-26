package com.ixonos.koku.kks;

import javax.portlet.ActionResponse;
import javax.portlet.RenderResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.ixonos.koku.kks.mock.Aktivointi;
import com.ixonos.koku.kks.mock.Henkilo;
import com.ixonos.koku.kks.mock.KKSService;
import com.ixonos.koku.kks.utils.KehitystietoTyyppiEditor;
import com.ixonos.koku.kks.utils.enums.KehitystietoTyyppi;

@Controller(value = "lapsiController")
@RequestMapping(value = "VIEW")
public class LapsiController {

  @Autowired
  @Qualifier("myKKSService")
  private KKSService service;

  private static Logger log = LoggerFactory.getLogger(LapsiController.class);

  @ActionMapping(params = "toiminto=lapsenTietoihin")
  public void lapsenTietoihin(@ModelAttribute(value = "lapsi") Henkilo lapsi,
      BindingResult bindingResult, ActionResponse response,
      SessionStatus sessionStatus) {
    log.debug("lapsenTietoihin");

    response.setRenderParameter("toiminto", "naytaLapsi");
    response.setRenderParameter("hetu", lapsi.getHetu());
    getCommandObject();
    sessionStatus.setComplete();
  }

  @RenderMapping(params = "toiminto=naytaLapsi")
  public String nayta(@ModelAttribute(value = "lapsi") Henkilo lapsi,
      @ModelAttribute(value = "aktivointi") Aktivointi aktivointi,
      RenderResponse response, Model model) {
    log.info("nayta lapsi");
    model.addAttribute("lapsi", lapsi);
    model.addAttribute("tiedot", lapsi.getKks().getKehitystiedot());
    aktivointi = getCommandObject();
    return "lapsi";
  }

  @ModelAttribute("lapsi")
  public Henkilo getLapsi(@RequestParam String hetu) {
    log.info("getLapsi with hetu=" + hetu);// #TODO# Remove hetu from log
    return service.getChild(hetu);
  }

  @InitBinder("aktivointi")
  public void initBinder(WebDataBinder binder) {
    log.debug("init binder  ");
    binder.registerCustomEditor(KehitystietoTyyppi.class,
        new KehitystietoTyyppiEditor(service));

  }

  @ModelAttribute("aktivointi")
  public Aktivointi getCommandObject() {
    log.debug("get aktivointi command object");
    return new Aktivointi();
  }

}
