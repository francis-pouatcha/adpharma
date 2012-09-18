package org.adorsys.adpharma.web;

import org.adorsys.adpharma.domain.FamilleProduit;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RooWebScaffold(path = "familleproduits", formBackingObject = FamilleProduit.class)
@RequestMapping("/familleproduits")
@Controller
public class FamilleProduitController {
}
