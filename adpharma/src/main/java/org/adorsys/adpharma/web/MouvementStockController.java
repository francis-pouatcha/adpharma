package org.adorsys.adpharma.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.adorsys.adpharma.domain.Approvisionement;
import org.adorsys.adpharma.domain.CommandeClient;
import org.adorsys.adpharma.domain.Facture;
import org.adorsys.adpharma.domain.Fournisseur;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.MouvementStock;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.Rayon;
import org.adorsys.adpharma.domain.TypeMouvement;
import org.adorsys.adpharma.utils.ProcessHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
	public String Search(@RequestParam("cip") String cip, @RequestParam("cipM") String cipM,@RequestParam("typeMouvement") TypeMouvement typeMouvement ,@RequestParam("designation") String designation , 
			@RequestParam("minDateCreation") @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") Date minDateCreation, @RequestParam("maxDateCreation") @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") Date maxDateCreation,Model uiModel) {
		List<MouvementStock> search = MouvementStock.search(typeMouvement, cipM,cip, minDateCreation, maxDateCreation, designation);

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
	
	
	// Routeur pour la redirection vers des liens de visualisation des elements selon le type de mouvement de stock
	@RequestMapping(value="/redirectTo/{index}/", method=RequestMethod.GET)
	public String mvtStockToEntity(@PathVariable("index")String index, @RequestParam("typeMvt")String typeMvt, Model uiModel, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
		if(typeMvt.equals(TypeMouvement.APPROVISIONEMENT.toString())){
			LigneApprovisionement ligneApprovisionement = LigneApprovisionement.findLigneApprovisionementsByCipMaisonEquals(index).getResultList().iterator().next();
			Approvisionement approvisionement = ligneApprovisionement.getApprovisionement();
			uiModel.asMap().clear();
			return "redirect:/approvisionements/" + ProcessHelper.encodeUrlPathSegment(approvisionement.getId().toString(), httpServletRequest);
		}
		else if(typeMvt.equals(TypeMouvement.VENTE.toString())){
			Facture facture = Facture.findFacturesByFactureNumberEquals(index).getResultList().iterator().next();
			CommandeClient commandeClient = facture.getCommande();
			uiModel.asMap().clear();
			return "redirect:/commandeclients/"+ProcessHelper.encodeUrlPathSegment(commandeClient.getId().toString(), httpServletRequest);
		}
		else if(typeMvt.equals(TypeMouvement.SORTIE_PRODUIT.toString()) || typeMvt.equals(TypeMouvement.RETOUR_PRODUIT.toString())){
                Produit produit = Produit.findProduitsByCipEquals(index).getSingleResult(); 
                uiModel.asMap().clear();
			return "redirect:/produits/" +ProcessHelper.encodeUrlPathSegment(produit.getId().toString(), httpServletRequest);
		}
		else{
			uiModel.asMap().clear();
			return "redirect:/";
		}
	}
	
	
	

	@ModelAttribute("mouvementstocks")
	public Collection<MouvementStock> populateMouvementStocks() {
		return new ArrayList<MouvementStock>();	
	}
}
