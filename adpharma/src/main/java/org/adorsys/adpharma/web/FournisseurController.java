package org.adorsys.adpharma.web;

import org.adorsys.adpharma.domain.Fournisseur;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RooWebScaffold(path = "fournisseurs", formBackingObject = Fournisseur.class)
@RequestMapping("/fournisseurs")
@Controller
public class FournisseurController {
}
