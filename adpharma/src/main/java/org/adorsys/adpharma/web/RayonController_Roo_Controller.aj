// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.web;

import java.io.UnsupportedEncodingException;
import java.lang.Integer;
import java.lang.Long;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.adorsys.adpharma.domain.Rayon;
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

privileged aspect RayonController_Roo_Controller {
    
    @RequestMapping(method = RequestMethod.POST)
    public String RayonController.create(@Valid Rayon rayon, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("rayon", rayon);
            return "rayons/create";
        }
        uiModel.asMap().clear();
        rayon.persist();
        return "redirect:/rayons/" + encodeUrlPathSegment(rayon.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String RayonController.createForm(Model uiModel) {
        uiModel.addAttribute("rayon", new Rayon());
        List dependencies = new ArrayList();
        if (Site.countSites() == 0) {
            dependencies.add(new String[]{"site", "sites"});
        }
        uiModel.addAttribute("dependencies", dependencies);
        return "rayons/create";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String RayonController.show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("rayon", Rayon.findRayon(id));
        uiModel.addAttribute("itemId", id);
        return "rayons/show";
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String RayonController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            uiModel.addAttribute("rayons", Rayon.findRayonEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) Rayon.countRayons() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("rayons", Rayon.findAllRayons());
        }
        return "rayons/list";
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public String RayonController.update(@Valid Rayon rayon, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("rayon", rayon);
            return "rayons/update";
        }
        uiModel.asMap().clear();
        rayon.merge();
        return "redirect:/rayons/" + encodeUrlPathSegment(rayon.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String RayonController.updateForm(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("rayon", Rayon.findRayon(id));
        return "rayons/update";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String RayonController.delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Rayon.findRayon(id).remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/rayons";
    }
    
    @ModelAttribute("rayons")
    public Collection<Rayon> RayonController.populateRayons() {
        return Rayon.findAllRayons();
    }
    
    @ModelAttribute("sites")
    public Collection<Site> RayonController.populateSites() {
        return Site.findAllSites();
    }
    
    String RayonController.encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
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
