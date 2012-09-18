package org.adorsys.adpharma.web;

import org.adorsys.adpharma.domain.LigneCmdClient;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RooWebScaffold(path = "lignecmdclients", formBackingObject = LigneCmdClient.class)
@RequestMapping("/lignecmdclients")
@Controller
public class LigneCmdClientController {
}
