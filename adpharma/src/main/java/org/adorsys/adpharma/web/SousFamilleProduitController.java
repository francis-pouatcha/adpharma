package org.adorsys.adpharma.web;

import org.adorsys.adpharma.domain.SousFamilleProduit;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RooWebScaffold(path = "sousfamilleproduits", formBackingObject = SousFamilleProduit.class)
@RequestMapping("/sousfamilleproduits")
@Controller
public class SousFamilleProduitController {
}
