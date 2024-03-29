// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.web;

import java.io.UnsupportedEncodingException;
import java.lang.Integer;
import java.lang.Long;
import java.lang.String;
import java.util.Arrays;
import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import org.adorsys.adpharma.domain.Genre;
import org.adorsys.adpharma.domain.PharmaUser;
import org.adorsys.adpharma.domain.RoleName;
import org.adorsys.adpharma.domain.Site;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

privileged aspect PharmaUserController_Roo_Controller {
    
    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String PharmaUserController.createForm(Model uiModel) {
        uiModel.addAttribute("pharmaUser", new PharmaUser());
        addDateTimeFormatPatterns(uiModel);
        return "pharmausers/create";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String PharmaUserController.show(@PathVariable("id") Long id, Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("pharmauser", PharmaUser.findPharmaUser(id));
        uiModel.addAttribute("itemId", id);
        return "pharmausers/show";
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String PharmaUserController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            uiModel.addAttribute("pharmausers", PharmaUser.findPharmaUserEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) PharmaUser.countPharmaUsers() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("pharmausers", PharmaUser.findAllPharmaUsers());
        }
        addDateTimeFormatPatterns(uiModel);
        return "pharmausers/list";
    }
    
    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String PharmaUserController.updateForm(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("pharmaUser", PharmaUser.findPharmaUser(id));
        addDateTimeFormatPatterns(uiModel);
        return "pharmausers/update";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String PharmaUserController.delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        PharmaUser.findPharmaUser(id).remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/pharmausers";
    }
    
    @ModelAttribute("genres")
    public Collection<Genre> PharmaUserController.populateGenres() {
        return Arrays.asList(Genre.class.getEnumConstants());
    }
    
    @ModelAttribute("pharmausers")
    public Collection<PharmaUser> PharmaUserController.populatePharmaUsers() {
        return PharmaUser.findAllPharmaUsers();
    }
    
    @ModelAttribute("sites")
    public Collection<Site> PharmaUserController.populateSites() {
        return Site.findAllSites();
    }
    
    void PharmaUserController.addDateTimeFormatPatterns(Model uiModel) {
        uiModel.addAttribute("pharmaUser_accountexpiration_date_format", "dd-MM-yyyy HH:mm");
        uiModel.addAttribute("pharmaUser_credentialexpiration_date_format", "dd-MM-yyyy HH:mm");
    }
    
    String PharmaUserController.encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
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
