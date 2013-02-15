package org.adorsys.adpharma.web;

import org.adorsys.adpharma.domain.Fournisseur;
import org.adorsys.adpharma.domain.Rayon;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RooWebScaffold(path = "fournisseurs", formBackingObject = Fournisseur.class)
@RequestMapping("/fournisseurs")
@Controller
public class FournisseurController {
	
	
	@RequestMapping(value="/search")
	public String searchLigneApp( @RequestParam("name") String name, Model uiModel) {
		
		if("".equals(name)){
			Integer page = 1;
			Integer size = 50;
			int sizeNo = size == null ? 10 : size.intValue();
            uiModel.addAttribute("fournisseurs", Fournisseur.findFournisseurEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) Fournisseur.countFournisseurs() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
		}else{
				
				uiModel.addAttribute("fournisseurs", Fournisseur.findFournisseursByNameLike(name).
						setMaxResults(50).getResultList());
		}
		return "fournisseurs/list";
	}
}
