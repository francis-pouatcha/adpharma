package org.adorsys.adpharma.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.adorsys.adpharma.domain.Fournisseur;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.MouvementStock;
import org.adorsys.adpharma.domain.Rayon;
import org.adorsys.adpharma.domain.TypeMouvement;
import org.adorsys.adpharma.utils.ProcessHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RooWebScaffold(path = "mouvementstocks", formBackingObject = MouvementStock.class)
@RequestMapping("/mouvementstocks")
@Controller
public class MouvementStockController {
	
	
	
	@RequestMapping(params = { "find=BySearch", "form" }, method = RequestMethod.GET)
	public String Search(Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
		return "mouvementstocks/search";
	}

	@RequestMapping(params = "find=BySearch", method = RequestMethod.GET)
	public String Search( @RequestParam("cipM") String cipM,@RequestParam("typeMouvement") TypeMouvement typeMouvement ,@RequestParam("designation") String designation , 
    		@RequestParam("minDateCreation") @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") Date minDateCreation, @RequestParam("maxDateCreation") @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") Date maxDateCreation,Model uiModel) {
		List<MouvementStock> search = MouvementStock.search(typeMouvement, cipM, minDateCreation, maxDateCreation, designation);
		
			if (search.isEmpty()) {
			uiModel.addAttribute("apMessage",  "Aucun Mouvements trouves !");

		}else {
			uiModel.addAttribute("results", search );

		}
		addDateTimeFormatPatterns(uiModel);
		return "mouvementstocks/search";
	}
	
	@RequestMapping(value="/search")
	public String searchLigneApp( @RequestParam("name") String name, Model uiModel) {
		
		if("".equals(name)){
			Integer page = 1;
			Integer size = 50;
			int sizeNo = size == null ? 10 : size.intValue();
            uiModel.addAttribute("mouvementstocks", MouvementStock.findMouvementStockEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) MouvementStock.countMouvementStocks() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
		}else{
				
				uiModel.addAttribute("mouvementstocks", MouvementStock.findMouvementStocksByDesignationLike(name).
						setMaxResults(50).getResultList());
		}
		return "mouvementstocks/list";
	}
	 
    @ModelAttribute("mouvementstocks")
    public Collection<MouvementStock> populateMouvementStocks() {
    	return new ArrayList<MouvementStock>();	
    }
}
