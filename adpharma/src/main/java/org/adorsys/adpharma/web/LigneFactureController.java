package org.adorsys.adpharma.web;

import org.adorsys.adpharma.domain.LigneFacture;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RooWebScaffold(path = "lignefactures", formBackingObject = LigneFacture.class)
@RequestMapping("/lignefactures")
@Controller
public class LigneFactureController {
}
