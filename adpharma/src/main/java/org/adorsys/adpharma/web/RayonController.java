package org.adorsys.adpharma.web;

import org.adorsys.adpharma.domain.Rayon;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RooWebScaffold(path = "rayons", formBackingObject = Rayon.class)
@RequestMapping("/rayons")
@Controller
public class RayonController {
}
