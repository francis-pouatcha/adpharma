package org.adorsys.adpharma.web;

import org.adorsys.adpharma.domain.SortieProduit;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RooWebScaffold(path = "sortieproduits", formBackingObject = SortieProduit.class)
@RequestMapping("/sortieproduits")
@Controller
public class SortieProduitController {
}
