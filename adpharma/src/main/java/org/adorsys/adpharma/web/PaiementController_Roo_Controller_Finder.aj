// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.web;

import java.lang.String;
import java.util.Date;
import org.adorsys.adpharma.domain.Caisse;
import org.adorsys.adpharma.domain.Paiement;
import org.adorsys.adpharma.domain.TypePaiement;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

privileged aspect PaiementController_Roo_Controller_Finder {
    
    @RequestMapping(params = { "find=ByCaisse", "form" }, method = RequestMethod.GET)
    public String PaiementController.findPaiementsByCaisseForm(Model uiModel) {
        uiModel.addAttribute("caisses", Caisse.findAllCaisses());
        return "paiements/findPaiementsByCaisse";
    }
    
    @RequestMapping(params = "find=ByCaisse", method = RequestMethod.GET)
    public String PaiementController.findPaiementsByCaisse(@RequestParam("caisse") Caisse caisse, Model uiModel) {
        uiModel.addAttribute("paiements", Paiement.findPaiementsByCaisse(caisse).getResultList());
        return "paiements/list";
    }
    
    @RequestMapping(params = { "find=ByDateSaisieBetween", "form" }, method = RequestMethod.GET)
    public String PaiementController.findPaiementsByDateSaisieBetweenForm(Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
        return "paiements/findPaiementsByDateSaisieBetween";
    }
    
    @RequestMapping(params = "find=ByDateSaisieBetween", method = RequestMethod.GET)
    public String PaiementController.findPaiementsByDateSaisieBetween(@RequestParam("minDateSaisie") @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") Date minDateSaisie, @RequestParam("maxDateSaisie") @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") Date maxDateSaisie, Model uiModel) {
        uiModel.addAttribute("paiements", Paiement.findPaiementsByDateSaisieBetween(minDateSaisie, maxDateSaisie).getResultList());
        addDateTimeFormatPatterns(uiModel);
        return "paiements/list";
    }
    
    @RequestMapping(params = { "find=ByTypePaiementAndCaisse", "form" }, method = RequestMethod.GET)
    public String PaiementController.findPaiementsByTypePaiementAndCaisseForm(Model uiModel) {
        uiModel.addAttribute("typepaiements", java.util.Arrays.asList(TypePaiement.class.getEnumConstants()));
        uiModel.addAttribute("caisses", Caisse.findAllCaisses());
        return "paiements/findPaiementsByTypePaiementAndCaisse";
    }
    
    @RequestMapping(params = "find=ByTypePaiementAndCaisse", method = RequestMethod.GET)
    public String PaiementController.findPaiementsByTypePaiementAndCaisse(@RequestParam("typePaiement") TypePaiement typePaiement, @RequestParam("caisse") Caisse caisse, Model uiModel) {
        uiModel.addAttribute("paiements", Paiement.findPaiementsByTypePaiementAndCaisse(typePaiement, caisse).getResultList());
        return "paiements/list";
    }
    
}
