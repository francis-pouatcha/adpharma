// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.web;

import java.io.UnsupportedEncodingException;
import java.lang.Integer;
import java.lang.Long;
import java.lang.String;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.adorsys.adpharma.domain.Client;
import org.adorsys.adpharma.domain.DetteClient;
import org.adorsys.adpharma.domain.EtatCredits;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

privileged aspect EtatCreditsController_Roo_Controller {
    
    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String EtatCreditsController.createForm(Model uiModel) {
        uiModel.addAttribute("etatCredits", new EtatCredits());
        addDateTimeFormatPatterns(uiModel);
        return "etatcreditses/create";
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String EtatCreditsController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            uiModel.addAttribute("etatcreditses", EtatCredits.findEtatCreditsEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) EtatCredits.countEtatCreditses() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("etatcreditses", EtatCredits.findAllEtatCreditses());
        }
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("search",true);
        return "etatcreditses/list";
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public String EtatCreditsController.update(@Valid EtatCredits etatCredits, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("etatCredits", etatCredits);
            addDateTimeFormatPatterns(uiModel);
            return "etatcreditses/update";
        }
        uiModel.asMap().clear();
        etatCredits.merge();
        return "redirect:/etatcreditses/" + encodeUrlPathSegment(etatCredits.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String EtatCreditsController.updateForm(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("etatCredits", EtatCredits.findEtatCredits(id));
        addDateTimeFormatPatterns(uiModel);
        return "etatcreditses/update";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String EtatCreditsController.delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        EtatCredits.findEtatCredits(id).remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/etatcreditses";
    }
    
    void EtatCreditsController.addDateTimeFormatPatterns(Model uiModel) {
        uiModel.addAttribute("etatCredits_mindatedette_date_format", "dd-MM-yyyy HH:mm");
        uiModel.addAttribute("etatCredits_maxdatedette_date_format", "dd-MM-yyyy HH:mm");
        uiModel.addAttribute("etatCredits_dateedition_date_format", "dd-MM-yyyy HH:mm");
        uiModel.addAttribute("etatCredits_datepaiement_date_format", "dd-MM-yyyy HH:mm");
    }
    
    String EtatCreditsController.encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
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
