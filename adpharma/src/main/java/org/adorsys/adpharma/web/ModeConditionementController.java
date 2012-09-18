package org.adorsys.adpharma.web;

import org.adorsys.adpharma.domain.ModeConditionement;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RooWebScaffold(path = "modeconditionements", formBackingObject = ModeConditionement.class)
@RequestMapping("/modeconditionements")
@Controller
public class ModeConditionementController {
}
