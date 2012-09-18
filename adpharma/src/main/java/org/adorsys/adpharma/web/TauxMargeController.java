package org.adorsys.adpharma.web;

import org.adorsys.adpharma.domain.TauxMarge;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RooWebScaffold(path = "tauxmarges", formBackingObject = TauxMarge.class)
@RequestMapping("/tauxmarges")
@Controller
public class TauxMargeController {
}
