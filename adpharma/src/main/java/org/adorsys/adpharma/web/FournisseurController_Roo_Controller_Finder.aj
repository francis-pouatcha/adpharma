// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.web;

import java.lang.String;
import org.adorsys.adpharma.domain.Fournisseur;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

privileged aspect FournisseurController_Roo_Controller_Finder {
    
    @RequestMapping(params = { "find=ByNameLike", "form" }, method = RequestMethod.GET)
    public String FournisseurController.findFournisseursByNameLikeForm(Model uiModel) {
        return "fournisseurs/findFournisseursByNameLike";
    }
    
    @RequestMapping(params = "find=ByNameLike", method = RequestMethod.GET)
    public String FournisseurController.findFournisseursByNameLike(@RequestParam("name") String name, Model uiModel) {
        uiModel.addAttribute("fournisseurs", Fournisseur.findFournisseursByNameLike(name).getResultList());
        return "fournisseurs/list";
    }
    
}