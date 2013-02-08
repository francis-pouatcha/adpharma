// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.web;

import java.io.UnsupportedEncodingException;
import java.lang.Integer;
import java.lang.Long;
import java.lang.String;
import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.adorsys.adpharma.domain.Caisse;
import org.adorsys.adpharma.domain.PharmaUser;
import org.adorsys.adpharma.domain.Site;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

privileged aspect CaisseController_Roo_Controller {
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String CaisseController.show(@PathVariable("id") Long id, Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("caisse", Caisse.findCaisse(id));
        uiModel.addAttribute("itemId", id);
        return "caisses/show";
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String CaisseController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            uiModel.addAttribute("caisses", Caisse.findCaisseEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) Caisse.countCaisses() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("caisses", Caisse.findAllCaisses());
        }
        addDateTimeFormatPatterns(uiModel);
        return "caisses/list";
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public String CaisseController.update(@Valid Caisse caisse, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("caisse", caisse);
            addDateTimeFormatPatterns(uiModel);
            return "caisses/update";
        }
        uiModel.asMap().clear();
        caisse.merge();
        return "redirect:/caisses/" + encodeUrlPathSegment(caisse.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String CaisseController.updateForm(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("caisse", Caisse.findCaisse(id));
        addDateTimeFormatPatterns(uiModel);
        return "caisses/update";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String CaisseController.delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Caisse.findCaisse(id).remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/caisses";
    }
    
    @ModelAttribute("sites")
    public Collection<Site> CaisseController.populateSites() {
        return Site.findAllSites();
    }
    
    void CaisseController.addDateTimeFormatPatterns(Model uiModel) {
        uiModel.addAttribute("caisse_dateouverture_date_format", "dd-MM-yyyy HH:mm");
        uiModel.addAttribute("caisse_datefemeture_date_format", "dd-MM-yyyy HH:mm");
    }
    
    String CaisseController.encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
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
