package org.adorsys.adpharma.web;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.adorsys.adpharma.domain.SalesConfiguration;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

@RooWebScaffold(path = "salesconfigurations", formBackingObject = SalesConfiguration.class)
@RequestMapping("/salesconfigurations")
@Controller
public class SalesConfigurationController {
	
	
	@RequestMapping(method = RequestMethod.POST)
    public String create(@Valid SalesConfiguration salesConfiguration, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		List<SalesConfiguration> configurations = SalesConfiguration.findSalesConfigurationsByTypeVente(salesConfiguration.getTypeVente()).getResultList();
		if(!configurations.isEmpty()){
			ObjectError error = new ObjectError("Erreur", "Impossible de creer cette configuration de vente, elle existe deja");
			bindingResult.addError(error);
		}
		SalesConfiguration.validateAll(bindingResult, salesConfiguration);
		if (bindingResult.hasErrors()) {
			SalesConfiguration configuration=null;
			if(!configurations.isEmpty()){
				configuration = configurations.iterator().next();
				uiModel.addAttribute("configuration", configuration.getId());
			}
            uiModel.addAttribute("salesConfiguration", salesConfiguration);
            return "salesconfigurations/create";
        }
        uiModel.asMap().clear();
        salesConfiguration.persist();
        return "redirect:/salesconfigurations/" + encodeUrlPathSegment(salesConfiguration.getId().toString(), httpServletRequest);
    }
	
	@RequestMapping(method = RequestMethod.PUT)
    public String update(@Valid SalesConfiguration salesConfiguration, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		SalesConfiguration.validateAll(bindingResult, salesConfiguration);
		if (bindingResult.hasErrors()) {
            uiModel.addAttribute("salesConfiguration", salesConfiguration);
            return "salesconfigurations/update";
        }
        uiModel.asMap().clear();
        salesConfiguration.merge();
        return "redirect:/salesconfigurations/" + encodeUrlPathSegment(salesConfiguration.getId().toString(), httpServletRequest);
    }
	
	
	
   public String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        }
        catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }
	
}
