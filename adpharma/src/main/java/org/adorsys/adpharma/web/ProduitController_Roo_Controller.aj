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
import org.adorsys.adpharma.domain.FamilleProduit;
import org.adorsys.adpharma.domain.Filiale;
import org.adorsys.adpharma.domain.ModeConditionement;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.Rayon;
import org.adorsys.adpharma.domain.SousFamilleProduit;
import org.adorsys.adpharma.domain.TVA;
import org.adorsys.adpharma.domain.TauxMarge;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

privileged aspect ProduitController_Roo_Controller {
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String ProduitController.show(@PathVariable("id") Long id, Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("produit", Produit.findProduit(id));
        uiModel.addAttribute("itemId", id);
        return "produits/show";
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String ProduitController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            uiModel.addAttribute("produits", Produit.findProduitEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) Produit.countProduits() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("produits", Produit.findAllProduits());
        }
        addDateTimeFormatPatterns(uiModel);
        return "produits/list";
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public String ProduitController.update(@Valid Produit produit, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("produit", produit);
            addDateTimeFormatPatterns(uiModel);
            return "produits/update";
        }
        uiModel.asMap().clear();
        produit.merge();
        return "redirect:/produits/" + encodeUrlPathSegment(produit.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String ProduitController.updateForm(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("produit", Produit.findProduit(id));
        addDateTimeFormatPatterns(uiModel);
        return "produits/update";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String ProduitController.delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Produit.findProduit(id).remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/produits";
    }
    
    @ModelAttribute("familleproduits")
    public Collection<FamilleProduit> ProduitController.populateFamilleProduits() {
        return FamilleProduit.findAllFamilleProduits();
    }
    
    @ModelAttribute("filiales")
    public Collection<Filiale> ProduitController.populateFiliales() {
        return Filiale.findAllFiliales();
    }
    
    @ModelAttribute("modeconditionements")
    public Collection<ModeConditionement> ProduitController.populateModeConditionements() {
        return ModeConditionement.findAllModeConditionements();
    }
    
    @ModelAttribute("rayons")
    public Collection<Rayon> ProduitController.populateRayons() {
        return Rayon.findAllRayons();
    }
    
    @ModelAttribute("sousfamilleproduits")
    public Collection<SousFamilleProduit> ProduitController.populateSousFamilleProduits() {
        return SousFamilleProduit.findAllSousFamilleProduits();
    }
    
    @ModelAttribute("tvas")
    public Collection<TVA> ProduitController.populateTVAS() {
        return TVA.findAllTVAS();
    }
    
    @ModelAttribute("tauxmarges")
    public Collection<TauxMarge> ProduitController.populateTauxMarges() {
        return TauxMarge.findAllTauxMarges();
    }
    
    void ProduitController.addDateTimeFormatPatterns(Model uiModel) {
        uiModel.addAttribute("produit_datederniereentre_date_format", "dd-MM-yyyy HH:mm");
        uiModel.addAttribute("produit_datedernieresortie_date_format", "dd-MM-yyyy HH:mm");
        uiModel.addAttribute("produit_datederniererupture_date_format", "dd-MM-yyyy HH:mm");
    }
    
    String ProduitController.encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
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
