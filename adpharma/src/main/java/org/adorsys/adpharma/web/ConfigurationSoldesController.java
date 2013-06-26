package org.adorsys.adpharma.web;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.adorsys.adpharma.domain.ConfigurationSoldes;
import org.adorsys.adpharma.domain.EtatSolde;
import org.hibernate.annotations.ForeignKey;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

@RequestMapping("/configurationsoldeses")
@Controller
public class ConfigurationSoldesController {
	
	 @RequestMapping(method = RequestMethod.POST)
	    public String create(@Valid ConfigurationSoldes configurationSoldes, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
	        if (bindingResult.hasErrors()) {
	            uiModel.addAttribute("configurationSoldes", configurationSoldes);
	            addDateTimeFormatPatterns(uiModel);
	            return "configurationsoldeses/create";
	        }
	        uiModel.asMap().clear();
	        return "redirect:/configurationsoldeses/";
	    }
	    
	    @RequestMapping(params = "form", method = RequestMethod.GET)
	    public String createForm(Model uiModel) {
	        uiModel.addAttribute("configurationSoldes", new ConfigurationSoldes());
	        addDateTimeFormatPatterns(uiModel);
	        return "configurationsoldeses/create";
	    }
	    
	    
	    
	    
	    
	    
	   
	    
	    @ModelAttribute("etatsoldes")
	    public Collection<EtatSolde> populateEtatSoldes() {
	        return Arrays.asList(EtatSolde.class.getEnumConstants());
	    }
	    
	    void addDateTimeFormatPatterns(Model uiModel) {
	        uiModel.addAttribute("configurationSoldes_debutsolde_date_format", "dd-MM-yyyy HH:mm");
	        uiModel.addAttribute("configurationSoldes_finsolde_date_format", "dd-MM-yyyy HH:mm");
	    }
	    
	    String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
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
