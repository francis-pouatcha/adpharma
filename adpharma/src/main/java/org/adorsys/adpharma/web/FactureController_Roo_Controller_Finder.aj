// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.web;

import java.lang.Boolean;
import java.lang.String;
import java.util.Date;
import org.adorsys.adpharma.domain.Caisse;
import org.adorsys.adpharma.domain.Client;
import org.adorsys.adpharma.domain.CommandeClient;
import org.adorsys.adpharma.domain.Facture;
import org.adorsys.adpharma.domain.PharmaUser;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

privileged aspect FactureController_Roo_Controller_Finder {
    
    @RequestMapping(params = { "find=ByCaisseAndEncaisserNot", "form" }, method = RequestMethod.GET)
    public String FactureController.findFacturesByCaisseAndEncaisserNotForm(Model uiModel) {
        uiModel.addAttribute("caisses", Caisse.findAllCaisses());
        return "factures/findFacturesByCaisseAndEncaisserNot";
    }
    
    @RequestMapping(params = "find=ByCaisseAndEncaisserNot", method = RequestMethod.GET)
    public String FactureController.findFacturesByCaisseAndEncaisserNot(@RequestParam("caisse") Caisse caisse, @RequestParam(value = "encaisser", required = false) Boolean encaisser, Model uiModel) {
        uiModel.addAttribute("factures", Facture.findFacturesByCaisseAndEncaisserNot(caisse, encaisser == null ? new Boolean(false) : encaisser).getResultList());
        return "factures/list";
    }
    
    @RequestMapping(params = { "find=ByClientAndDateCreationBetween", "form" }, method = RequestMethod.GET)
    public String FactureController.findFacturesByClientAndDateCreationBetweenForm(Model uiModel) {
        uiModel.addAttribute("clients", Client.findAllClients());
        addDateTimeFormatPatterns(uiModel);
        return "factures/findFacturesByClientAndDateCreationBetween";
    }
    
    @RequestMapping(params = "find=ByClientAndDateCreationBetween", method = RequestMethod.GET)
    public String FactureController.findFacturesByClientAndDateCreationBetween(@RequestParam("client") Client client, @RequestParam("minDateCreation") @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") Date minDateCreation, @RequestParam("maxDateCreation") @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") Date maxDateCreation, Model uiModel) {
        uiModel.addAttribute("factures", Facture.findFacturesByClientAndDateCreationBetween(client, minDateCreation, maxDateCreation).getResultList());
        addDateTimeFormatPatterns(uiModel);
        return "factures/list";
    }
    
    @RequestMapping(params = { "find=ByCommande", "form" }, method = RequestMethod.GET)
    public String FactureController.findFacturesByCommandeForm(Model uiModel) {
        uiModel.addAttribute("commandeclients", CommandeClient.findAllCommandeClients());
        return "factures/findFacturesByCommande";
    }
    
    @RequestMapping(params = "find=ByCommande", method = RequestMethod.GET)
    public String FactureController.findFacturesByCommande(@RequestParam("commande") CommandeClient commande, Model uiModel) {
        uiModel.addAttribute("factures", Facture.findFacturesByCommande(commande).getResultList());
        return "factures/list";
    }
    
    @RequestMapping(params = { "find=ByDateCreationBetween", "form" }, method = RequestMethod.GET)
    public String FactureController.findFacturesByDateCreationBetweenForm(Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
        return "factures/findFacturesByDateCreationBetween";
    }
    
    @RequestMapping(params = "find=ByDateCreationBetween", method = RequestMethod.GET)
    public String FactureController.findFacturesByDateCreationBetween(@RequestParam("minDateCreation") @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") Date minDateCreation, @RequestParam("maxDateCreation") @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") Date maxDateCreation, Model uiModel) {
        uiModel.addAttribute("factures", Facture.findFacturesByDateCreationBetween(minDateCreation, maxDateCreation).getResultList());
        addDateTimeFormatPatterns(uiModel);
        return "factures/list";
    }
    
    @RequestMapping(params = { "find=ByFactureNumberEquals", "form" }, method = RequestMethod.GET)
    public String FactureController.findFacturesByFactureNumberEqualsForm(Model uiModel) {
        return "factures/findFacturesByFactureNumberEquals";
    }
    
    @RequestMapping(params = "find=ByFactureNumberEquals", method = RequestMethod.GET)
    public String FactureController.findFacturesByFactureNumberEquals(@RequestParam("factureNumber") String factureNumber, Model uiModel) {
        uiModel.addAttribute("factures", Facture.findFacturesByFactureNumberEquals(factureNumber).getResultList());
        return "factures/list";
    }
    
    @RequestMapping(params = { "find=BySolderNot", "form" }, method = RequestMethod.GET)
    public String FactureController.findFacturesBySolderNotForm(Model uiModel) {
        return "factures/findFacturesBySolderNot";
    }
    
    @RequestMapping(params = "find=BySolderNot", method = RequestMethod.GET)
    public String FactureController.findFacturesBySolderNot(@RequestParam(value = "solder", required = false) Boolean solder, Model uiModel) {
        uiModel.addAttribute("factures", Facture.findFacturesBySolderNot(solder == null ? new Boolean(false) : solder).getResultList());
        return "factures/list";
    }
    
    @RequestMapping(params = { "find=ByVendeurAndDateCreationBetween", "form" }, method = RequestMethod.GET)
    public String FactureController.findFacturesByVendeurAndDateCreationBetweenForm(Model uiModel) {
        uiModel.addAttribute("pharmausers", PharmaUser.findAllPharmaUsers());
        addDateTimeFormatPatterns(uiModel);
        return "factures/findFacturesByVendeurAndDateCreationBetween";
    }
    
    @RequestMapping(params = "find=ByVendeurAndDateCreationBetween", method = RequestMethod.GET)
    public String FactureController.findFacturesByVendeurAndDateCreationBetween(@RequestParam("vendeur") PharmaUser vendeur, @RequestParam("minDateCreation") @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") Date minDateCreation, @RequestParam("maxDateCreation") @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") Date maxDateCreation, Model uiModel) {
        uiModel.addAttribute("factures", Facture.findFacturesByVendeurAndDateCreationBetween(vendeur, minDateCreation, maxDateCreation).getResultList());
        addDateTimeFormatPatterns(uiModel);
        return "factures/list";
    }
    
}
