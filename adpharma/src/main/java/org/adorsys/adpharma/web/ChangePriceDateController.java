package org.adorsys.adpharma.web;

import org.adorsys.adpharma.beans.ChangeDatePrice;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RooWebScaffold(path = "changedateprices", formBackingObject = ChangeDatePrice.class)
@RequestMapping("/changedateprices")
@Controller
public class ChangePriceDateController {
}
