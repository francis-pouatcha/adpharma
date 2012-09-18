package org.adorsys.adpharma.web;

import org.adorsys.adpharma.domain.Devise;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RooWebScaffold(path = "devises", formBackingObject = Devise.class)
@RequestMapping("/devises")
@Controller
public class DeviseController {
}
