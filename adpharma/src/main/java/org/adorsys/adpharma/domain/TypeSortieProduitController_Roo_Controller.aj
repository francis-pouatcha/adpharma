// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.domain;

import java.io.UnsupportedEncodingException;
import java.lang.Integer;
import java.lang.Long;
import java.lang.String;
import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.adorsys.adpharma.domain.TypeSortieProduit;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

privileged aspect TypeSortieProduitController_Roo_Controller {
    
    @RequestMapping(method = RequestMethod.POST)
    public String TypeSortieProduitController.create(@Valid TypeSortieProduit typeSortieProduit, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("typeSortieProduit", typeSortieProduit);
            return "typesortieproduits/create";
        }
        uiModel.asMap().clear();
        typeSortieProduit.persist();
        return "redirect:/typesortieproduits/" + encodeUrlPathSegment(typeSortieProduit.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String TypeSortieProduitController.createForm(Model uiModel) {
        uiModel.addAttribute("typeSortieProduit", new TypeSortieProduit());
        return "typesortieproduits/create";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String TypeSortieProduitController.show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("typesortieproduit", TypeSortieProduit.findTypeSortieProduit(id));
        uiModel.addAttribute("itemId", id);
        return "typesortieproduits/show";
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String TypeSortieProduitController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            uiModel.addAttribute("typesortieproduits", TypeSortieProduit.findTypeSortieProduitEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) TypeSortieProduit.countTypeSortieProduits() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("typesortieproduits", TypeSortieProduit.findAllTypeSortieProduits());
        }
        return "typesortieproduits/list";
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public String TypeSortieProduitController.update(@Valid TypeSortieProduit typeSortieProduit, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("typeSortieProduit", typeSortieProduit);
            return "typesortieproduits/update";
        }
        uiModel.asMap().clear();
        typeSortieProduit.merge();
        return "redirect:/typesortieproduits/" + encodeUrlPathSegment(typeSortieProduit.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String TypeSortieProduitController.updateForm(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("typeSortieProduit", TypeSortieProduit.findTypeSortieProduit(id));
        return "typesortieproduits/update";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String TypeSortieProduitController.delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        TypeSortieProduit.findTypeSortieProduit(id).remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/typesortieproduits";
    }
    
    @ModelAttribute("typesortieproduits")
    public Collection<TypeSortieProduit> TypeSortieProduitController.populateTypeSortieProduits() {
        return TypeSortieProduit.findAllTypeSortieProduits();
    }
    
    String TypeSortieProduitController.encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
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