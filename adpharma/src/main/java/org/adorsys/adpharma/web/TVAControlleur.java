package org.adorsys.adpharma.web;

import org.adorsys.adpharma.domain.TVA;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RooWebScaffold(path = "tvas", formBackingObject = TVA.class)
@RequestMapping("/tvas")
@Controller
public class TVAControlleur {
}
