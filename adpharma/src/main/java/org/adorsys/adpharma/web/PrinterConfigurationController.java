package org.adorsys.adpharma.web;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.adorsys.adpharma.domain.PrinterConfiguration;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RooWebScaffold(path = "printerconfigurations", formBackingObject = PrinterConfiguration.class)
@RequestMapping("/printerconfigurations")
@Controller
public class PrinterConfigurationController {
	
	
	 @RequestMapping(method = RequestMethod.POST)
	    public String create(@Valid PrinterConfiguration printerConfiguration, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
	       printerConfiguration.validate(bindingResult);
		 if (bindingResult.hasErrors()) {
	            uiModel.addAttribute("printerConfiguration", printerConfiguration);
	            return "printerconfigurations/create";
	        }
	        uiModel.asMap().clear();
	        printerConfiguration.persist();
	        return "redirect:/printerconfigurations/" + encodeUrlPathSegment(printerConfiguration.getId().toString(), httpServletRequest);
	    }
	 
	 @RequestMapping(method = RequestMethod.PUT)
	    public String  update(@Valid PrinterConfiguration printerConfiguration, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		 if (bindingResult.hasErrors()) {
	            uiModel.addAttribute("printerConfiguration", printerConfiguration);
	            return "printerconfigurations/update";
	        }
	        uiModel.asMap().clear();
	        printerConfiguration.merge();
	        return "redirect:/printerconfigurations/" + encodeUrlPathSegment(printerConfiguration.getId().toString(), httpServletRequest);
	    }
}
