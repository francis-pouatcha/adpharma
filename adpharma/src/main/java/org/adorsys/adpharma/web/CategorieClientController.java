package org.adorsys.adpharma.web;

import org.adorsys.adpharma.domain.CategorieClient;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RooWebScaffold(path = "categorieclients", formBackingObject = CategorieClient.class)
@RequestMapping("/categorieclients")
@Controller
public class CategorieClientController {
}
