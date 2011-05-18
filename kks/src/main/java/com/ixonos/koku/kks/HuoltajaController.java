package com.ixonos.koku.kks;

import java.util.List;

import javax.portlet.RenderResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.ixonos.koku.kks.mock.Henkilo;
import com.ixonos.koku.kks.mock.KKSService;

@Controller(value = "huoltajaController")
@RequestMapping(value = "VIEW")
public class HuoltajaController {

  @Autowired
  @Qualifier("myKKSService")
  private KKSService service;

  private static Logger log = LoggerFactory.getLogger(HuoltajaController.class);

  @RenderMapping(params = "toiminto=naytaLapset")
  public String naytaLapset(RenderResponse response, Model model) {
    log.info("naytaLapset");
    model.addAttribute("lapset", getLapset());
    return "lapset";
  }

  @ModelAttribute("lapset")
  public List<Henkilo> getLapset() {
    log.info("getLapset");
    return service.getChilds(null);
  }

}
