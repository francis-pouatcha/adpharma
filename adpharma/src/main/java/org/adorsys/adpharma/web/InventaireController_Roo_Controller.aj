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
import javax.validation.Valid;
import org.adorsys.adpharma.domain.Etat;
import org.adorsys.adpharma.domain.FamilleProduit;
import org.adorsys.adpharma.domain.Filiale;
import org.adorsys.adpharma.domain.Inventaire;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.LigneInventaire;
import org.adorsys.adpharma.domain.PharmaUser;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.Rayon;
import org.adorsys.adpharma.domain.Site;
import org.adorsys.adpharma.domain.SousFamilleProduit;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

privileged aspect InventaireController_Roo_Controller {
    
    @RequestMapping(method = RequestMethod.GET)
    public String InventaireController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            uiModel.addAttribute("inventaires", Inventaire.findInventaireEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) Inventaire.countInventaires() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("inventaires", Inventaire.findAllInventaires());
        }
        addDateTimeFormatPatterns(uiModel);
        return "inventaires/list";
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public String InventaireController.update(@Valid Inventaire inventaire, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("inventaire", inventaire);
            addDateTimeFormatPatterns(uiModel);
            return "inventaires/update";
        }
        uiModel.asMap().clear();
        inventaire.merge();
        return "redirect:/inventaires/" + encodeUrlPathSegment(inventaire.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String InventaireController.updateForm(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("inventaire", Inventaire.findInventaire(id));
        addDateTimeFormatPatterns(uiModel);
        return "inventaires/update";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String InventaireController.delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Inventaire.findInventaire(id).remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/inventaires";
    }
    
    @ModelAttribute("familleproduits")
    public Collection<FamilleProduit> InventaireController.populateFamilleProduits() {
        return FamilleProduit.findAllFamilleProduits();
    }
    
    @ModelAttribute("sousfamilleproduits")
    public Collection<SousFamilleProduit> InventaireController.populateSousFamilleProduits() {
        return SousFamilleProduit.findAllSousFamilleProduits();
    }
    
    void InventaireController.addDateTimeFormatPatterns(Model uiModel) {
        uiModel.addAttribute("inventaire_daterupture_date_format", "dd-MM-yyyy HH:mm");
        uiModel.addAttribute("inventaire_datedebut_date_format", "dd-MM-yyyy HH:mm");
        uiModel.addAttribute("inventaire_datefin_date_format", "dd-MM-yyyy HH:mm");
        uiModel.addAttribute("inventaire_dateinventaire_date_format", "dd-MM-yyyy HH:mm");
    }
    
    String InventaireController.encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
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
