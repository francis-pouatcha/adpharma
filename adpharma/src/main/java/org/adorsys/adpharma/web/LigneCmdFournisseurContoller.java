package org.adorsys.adpharma.web;

import org.adorsys.adpharma.domain.LigneCmdFournisseur;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RooWebScaffold(path = "lignecmdfournisseurs", formBackingObject = LigneCmdFournisseur.class)
@RequestMapping("/lignecmdfournisseurs")
@Controller
public class LigneCmdFournisseurContoller {
}
