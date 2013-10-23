// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.web;

import java.lang.String;
import java.util.Date;
import org.adorsys.adpharma.domain.CommandeClient;
import org.adorsys.adpharma.domain.Etat;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

privileged aspect CommandeClientController_Roo_Controller_Finder {
    
    @RequestMapping(params = { "find=ByCmdNumberEquals", "form" }, method = RequestMethod.GET)
    public String CommandeClientController.findCommandeClientsByCmdNumberEqualsForm(Model uiModel) {
        return "commandeclients/findCommandeClientsByCmdNumberEquals";
    }
    
    @RequestMapping(params = "find=ByCmdNumberEquals", method = RequestMethod.GET)
    public String CommandeClientController.findCommandeClientsByCmdNumberEquals(@RequestParam("cmdNumber") String cmdNumber, Model uiModel) {
        uiModel.addAttribute("commandeclients", CommandeClient.findCommandeClientsByCmdNumberEquals(cmdNumber).getResultList());
        return "commandeclients/list";
    }
    
    @RequestMapping(params = { "find=ByDateCreationBetween", "form" }, method = RequestMethod.GET)
    public String CommandeClientController.findCommandeClientsByDateCreationBetweenForm(Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
        return "commandeclients/findCommandeClientsByDateCreationBetween";
    }
    
    @RequestMapping(params = "find=ByDateCreationBetween", method = RequestMethod.GET)
    public String CommandeClientController.findCommandeClientsByDateCreationBetween(@RequestParam("minDateCreation") @DateTimeFormat(pattern = "dd-MM-yyyy hh:mm") Date minDateCreation, @RequestParam("maxDateCreation") @DateTimeFormat(pattern = "dd-MM-yyyy hh:mm") Date maxDateCreation, Model uiModel) {
        uiModel.addAttribute("commandeclients", CommandeClient.findCommandeClientsByDateCreationBetween(minDateCreation, maxDateCreation).getResultList());
        addDateTimeFormatPatterns(uiModel);
        return "commandeclients/list";
    }
    
    @RequestMapping(params = { "find=ByStatusAndDateCreationBetween", "form" }, method = RequestMethod.GET)
    public String CommandeClientController.findCommandeClientsByStatusAndDateCreationBetweenForm(Model uiModel) {
        uiModel.addAttribute("etats", java.util.Arrays.asList(Etat.class.getEnumConstants()));
        addDateTimeFormatPatterns(uiModel);
        return "commandeclients/findCommandeClientsByStatusAndDateCreationBetween";
    }
    
    @RequestMapping(params = "find=ByStatusAndDateCreationBetween", method = RequestMethod.GET)
    public String CommandeClientController.findCommandeClientsByStatusAndDateCreationBetween(@RequestParam("status") Etat status, @RequestParam("minDateCreation") @DateTimeFormat(pattern = "dd-MM-yyyy hh:mm") Date minDateCreation, @RequestParam("maxDateCreation") @DateTimeFormat(pattern = "dd-MM-yyyy hh:mm") Date maxDateCreation, Model uiModel) {
        uiModel.addAttribute("commandeclients", CommandeClient.findCommandeClientsByStatusAndDateCreationBetween(status, minDateCreation, maxDateCreation).getResultList());
        addDateTimeFormatPatterns(uiModel);
        return "commandeclients/list";
    }
    
}
