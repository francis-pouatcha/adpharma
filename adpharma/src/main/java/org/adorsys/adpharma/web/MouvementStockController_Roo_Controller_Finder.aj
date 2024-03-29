// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.web;

import java.lang.String;
import java.util.Date;
import org.adorsys.adpharma.domain.MouvementStock;
import org.adorsys.adpharma.domain.TypeMouvement;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

privileged aspect MouvementStockController_Roo_Controller_Finder {
    
    @RequestMapping(params = { "find=ByCipMEquals", "form" }, method = RequestMethod.GET)
    public String MouvementStockController.findMouvementStocksByCipMEqualsForm(Model uiModel) {
        return "mouvementstocks/findMouvementStocksByCipMEquals";
    }
    
    @RequestMapping(params = "find=ByCipMEquals", method = RequestMethod.GET)
    public String MouvementStockController.findMouvementStocksByCipMEquals(@RequestParam("cipM") String cipM, Model uiModel) {
        uiModel.addAttribute("mouvementstocks", MouvementStock.findMouvementStocksByCipMEquals(cipM).getResultList());
        return "mouvementstocks/list";
    }
    
    @RequestMapping(params = { "find=ByDateCreationBetween", "form" }, method = RequestMethod.GET)
    public String MouvementStockController.findMouvementStocksByDateCreationBetweenForm(Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
        return "mouvementstocks/findMouvementStocksByDateCreationBetween";
    }
    
    @RequestMapping(params = "find=ByDateCreationBetween", method = RequestMethod.GET)
    public String MouvementStockController.findMouvementStocksByDateCreationBetween(@RequestParam("minDateCreation") @DateTimeFormat(pattern = "dd-MM-yyyy hh:mm") Date minDateCreation, @RequestParam("maxDateCreation") @DateTimeFormat(pattern = "dd-MM-yyyy hh:mm") Date maxDateCreation, Model uiModel) {
        uiModel.addAttribute("mouvementstocks", MouvementStock.findMouvementStocksByDateCreationBetween(minDateCreation, maxDateCreation).getResultList());
        addDateTimeFormatPatterns(uiModel);
        return "mouvementstocks/list";
    }
    
    @RequestMapping(params = { "find=ByDateCreationBetweenAndAgentCreateurEquals", "form" }, method = RequestMethod.GET)
    public String MouvementStockController.findMouvementStocksByDateCreationBetweenAndAgentCreateurEqualsForm(Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
        return "mouvementstocks/findMouvementStocksByDateCreationBetweenAndAgentCreateurEquals";
    }
    
    @RequestMapping(params = "find=ByDateCreationBetweenAndAgentCreateurEquals", method = RequestMethod.GET)
    public String MouvementStockController.findMouvementStocksByDateCreationBetweenAndAgentCreateurEquals(@RequestParam("minDateCreation") @DateTimeFormat(pattern = "dd-MM-yyyy hh:mm") Date minDateCreation, @RequestParam("maxDateCreation") @DateTimeFormat(pattern = "dd-MM-yyyy hh:mm") Date maxDateCreation, @RequestParam("agentCreateur") String agentCreateur, Model uiModel) {
        uiModel.addAttribute("mouvementstocks", MouvementStock.findMouvementStocksByDateCreationBetweenAndAgentCreateurEquals(minDateCreation, maxDateCreation, agentCreateur).getResultList());
        addDateTimeFormatPatterns(uiModel);
        return "mouvementstocks/list";
    }
    
    @RequestMapping(params = { "find=ByDateCreationBetweenAndAgentCreateurLike", "form" }, method = RequestMethod.GET)
    public String MouvementStockController.findMouvementStocksByDateCreationBetweenAndAgentCreateurLikeForm(Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
        return "mouvementstocks/findMouvementStocksByDateCreationBetweenAndAgentCreateurLike";
    }
    
    @RequestMapping(params = "find=ByDateCreationBetweenAndAgentCreateurLike", method = RequestMethod.GET)
    public String MouvementStockController.findMouvementStocksByDateCreationBetweenAndAgentCreateurLike(@RequestParam("minDateCreation") @DateTimeFormat(pattern = "dd-MM-yyyy hh:mm") Date minDateCreation, @RequestParam("maxDateCreation") @DateTimeFormat(pattern = "dd-MM-yyyy hh:mm") Date maxDateCreation, @RequestParam("agentCreateur") String agentCreateur, Model uiModel) {
        uiModel.addAttribute("mouvementstocks", MouvementStock.findMouvementStocksByDateCreationBetweenAndAgentCreateurLike(minDateCreation, maxDateCreation, agentCreateur).getResultList());
        addDateTimeFormatPatterns(uiModel);
        return "mouvementstocks/list";
    }
    
    @RequestMapping(params = { "find=ByDesignationEqualsAndDateCreationBetween", "form" }, method = RequestMethod.GET)
    public String MouvementStockController.findMouvementStocksByDesignationEqualsAndDateCreationBetweenForm(Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
        return "mouvementstocks/findMouvementStocksByDesignationEqualsAndDateCreationBetween";
    }
    
    @RequestMapping(params = "find=ByDesignationEqualsAndDateCreationBetween", method = RequestMethod.GET)
    public String MouvementStockController.findMouvementStocksByDesignationEqualsAndDateCreationBetween(@RequestParam("designation") String designation, @RequestParam("minDateCreation") @DateTimeFormat(pattern = "dd-MM-yyyy hh:mm") Date minDateCreation, @RequestParam("maxDateCreation") @DateTimeFormat(pattern = "dd-MM-yyyy hh:mm") Date maxDateCreation, Model uiModel) {
        uiModel.addAttribute("mouvementstocks", MouvementStock.findMouvementStocksByDesignationEqualsAndDateCreationBetween(designation, minDateCreation, maxDateCreation).getResultList());
        addDateTimeFormatPatterns(uiModel);
        return "mouvementstocks/list";
    }
    
    @RequestMapping(params = { "find=ByDesignationLike", "form" }, method = RequestMethod.GET)
    public String MouvementStockController.findMouvementStocksByDesignationLikeForm(Model uiModel) {
        return "mouvementstocks/findMouvementStocksByDesignationLike";
    }
    
    @RequestMapping(params = "find=ByDesignationLike", method = RequestMethod.GET)
    public String MouvementStockController.findMouvementStocksByDesignationLike(@RequestParam("designation") String designation, Model uiModel) {
        uiModel.addAttribute("mouvementstocks", MouvementStock.findMouvementStocksByDesignationLike(designation).getResultList());
        return "mouvementstocks/list";
    }
    
    @RequestMapping(params = { "find=ByTypeMouvementAndDateCreationBetween", "form" }, method = RequestMethod.GET)
    public String MouvementStockController.findMouvementStocksByTypeMouvementAndDateCreationBetweenForm(Model uiModel) {
        uiModel.addAttribute("typemouvements", java.util.Arrays.asList(TypeMouvement.class.getEnumConstants()));
        addDateTimeFormatPatterns(uiModel);
        return "mouvementstocks/findMouvementStocksByTypeMouvementAndDateCreationBetween";
    }
    
    @RequestMapping(params = "find=ByTypeMouvementAndDateCreationBetween", method = RequestMethod.GET)
    public String MouvementStockController.findMouvementStocksByTypeMouvementAndDateCreationBetween(@RequestParam("typeMouvement") TypeMouvement typeMouvement, @RequestParam("minDateCreation") @DateTimeFormat(pattern = "dd-MM-yyyy hh:mm") Date minDateCreation, @RequestParam("maxDateCreation") @DateTimeFormat(pattern = "dd-MM-yyyy hh:mm") Date maxDateCreation, Model uiModel) {
        uiModel.addAttribute("mouvementstocks", MouvementStock.findMouvementStocksByTypeMouvementAndDateCreationBetween(typeMouvement, minDateCreation, maxDateCreation).getResultList());
        addDateTimeFormatPatterns(uiModel);
        return "mouvementstocks/list";
    }
    
}
