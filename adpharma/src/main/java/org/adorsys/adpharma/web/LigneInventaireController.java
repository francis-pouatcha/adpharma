package org.adorsys.adpharma.web;

import org.adorsys.adpharma.domain.LigneInventaire;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RooWebScaffold(path = "ligneinventaires", formBackingObject = LigneInventaire.class)
@RequestMapping("/ligneinventaires")
@Controller
public class LigneInventaireController {
}
