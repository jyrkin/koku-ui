package com.ixonos.koku.kks;

import javax.portlet.RenderRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.ixonos.koku.kks.malli.DemoService;

@Controller(value = "EditController")
@RequestMapping(value = "EDIT")
public class EditController {

  @Autowired
  @Qualifier("demoKksService")
  private DemoService demoService;

  private static Logger log = LoggerFactory.getLogger(EditController.class);

  @RenderMapping
  public String render(RenderRequest req, @RequestParam(value = "viesti", required = false) String viesti, Model model) {

    if (viesti != null) {
      model.addAttribute("viesti", "Malli resetoitu");
    }
    return "edit";
  }

  @RenderMapping(params = "toiminto=resetoiMalli")
  public String naytaLuokittelu(RenderRequest req, @RequestParam(value = "viesti", required = false) String viesti,
      Model model) {
    demoService.luo("");

    if (viesti != null) {
      model.addAttribute("viesti", "Malli resetoitu");
    }
    return "edit";
  }
}
