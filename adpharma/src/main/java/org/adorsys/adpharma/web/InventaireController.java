package org.adorsys.adpharma.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.adorsys.adpharma.domain.Etat;
import org.adorsys.adpharma.domain.Filiale;
import org.adorsys.adpharma.domain.Inventaire;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.LigneInventaire;
import org.adorsys.adpharma.domain.PharmaUser;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.Rayon;
import org.adorsys.adpharma.domain.Site;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RooWebScaffold(path = "inventaires", formBackingObject = Inventaire.class)
@RequestMapping("/inventaires")
@Controller
public class InventaireController {
	@RequestMapping(params = "form", method = RequestMethod.GET)
	public String createForm(Model uiModel) {
		/*List<Inventaire> resultList = Inventaire.findInventairesByEtat(Etat.EN_COUR).getResultList();
		if (!resultList.isEmpty()) {
			uiModel.addAttribute("apMessage","Impossible d'effectuer cette Operation Un inventaire est deja en cour !");
			return "caisses/infos";

		}else {*/
			uiModel.addAttribute("inventaire", new Inventaire());
			addDateTimeFormatPatterns(uiModel);
			return "inventaires/create";

		//}
	}
	@RequestMapping(method = RequestMethod.POST)
	public String create(@Valid Inventaire inventaire, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		if (bindingResult.hasErrors()) {
			uiModel.addAttribute("inventaire", inventaire);
			addDateTimeFormatPatterns(uiModel);
			return "inventaires/create";
		}
		uiModel.asMap().clear();
		inventaire.persist();
		return "redirect:/inventaireProcess/" + encodeUrlPathSegment(inventaire.getId().toString(), httpServletRequest)+"/editInventaire";
	}
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String show(@PathVariable("id") Long id, Model uiModel) {
		addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("inventaire", Inventaire.findInventaire(id));
		uiModel.addAttribute("itemId", id);
		return "inventaireProcess/show";
	}

	

	@RequestMapping(value = "/inventaireEnCour",method = RequestMethod.GET)
	public String inventaireEnCour(Model uiModel,HttpServletRequest httpServletRequest ) {
		List<Inventaire> resultList = Inventaire.findInventairesByEtat(Etat.EN_COUR).getResultList();
		if (!resultList.iterator().hasNext()) {
			uiModel.addAttribute("apMessage","Aucun inventaire  en cour !");
			return "caisses/infos";

		}else {
			return "redirect:/inventaires/" + encodeUrlPathSegment(resultList.iterator().next().getId().toString(), httpServletRequest);

		}	
	}

	
	// imprime les inventaires 
	@RequestMapping("/{invId}/print/{invNumber}.pdf")
	public String print(@PathVariable("invId")Long invId, Model uiModel){
		Inventaire inventaire = Inventaire.findInventaire(invId);
		uiModel.addAttribute("inventaire", inventaire);
		return "inventairePdfDocView";

	}

	@RequestMapping("printFicheInventaireParRayon/{rayonId}.pdf")
	public String printficheRayon(@PathVariable("rayonId")Long rayonId, Model uiModel){
		uiModel.addAttribute("produits", Produit.findProduitsByRayon(Rayon.findRayon(rayonId)).getResultList());
		uiModel.addAttribute("rayonId", rayonId);

		return "ficheInventairePdfView";

	}
	@RequestMapping("printFicheInventaire/{debut}/{fin}.pdf")
	public String printfiche(@PathVariable("debut")String debut,@PathVariable("fin")String fin , Model uiModel){
		uiModel.addAttribute("produits", Produit.findProduitsByOrdreAlphabetique(debut, fin).getResultList());
		return "ficheInventairePdfView";

	}

	 @ModelAttribute("inventaires")
	   public Collection<Inventaire> populateInventaires() {
	     //   return Inventaire.findAllInventaires();
		 return null;
	    }
	    
	    @ModelAttribute("ligneinventaires")
	    public Collection<LigneInventaire> populateLigneInventaires() {
	       // return LigneInventaire.findAllLigneInventaires();
	    	return null;
	    }
	    
	    @ModelAttribute("etats")
	    public Collection<Etat> populateEtats() {
	        return Arrays.asList(Etat.class.getEnumConstants());
	    }
	    
	    @ModelAttribute("filiales")
	    public Collection<Filiale> populateFiliales() {
	        //return Filiale.findAllFiliales();
	    	return new ArrayList<Filiale>();
	    }
	    
	    @ModelAttribute("ligneapprovisionements")
	    public Collection<LigneApprovisionement> populateLigneApprovisionements() {
	      //  return LigneApprovisionement.findAllLigneApprovisionements();
	    	return new ArrayList<LigneApprovisionement>();
	    }
	    
	    @ModelAttribute("pharmausers")
	    public Collection<PharmaUser> populatePharmaUsers() {
	       // return PharmaUser.findAllPharmaUsers();
	    	return new ArrayList<PharmaUser>();
	    }
	    
	    @ModelAttribute("produits")
	    public Collection<Produit> populateProduits() {
	      //  return Produit.findAllProduits();
	    	return new ArrayList<Produit>();
	    }
	    
	    @ModelAttribute("rayons")
	    public Collection<Rayon> populateRayons() {
	       // return Rayon.findAllRayons();
	    	return new ArrayList<Rayon>();
	    }
	    
	    @ModelAttribute("sites")
	    public Collection<Site> populateSites() {
	        return Site.findAllSites();
	    }
	public Collection<String> populateLettress() {
		Collection<String> list = new ArrayList<String>();
		list.add("A");
		list.add("B");list.add("C");list.add("D");list.add("E");list.add("F");list.add("F");list.add("H");list.add("I");list.add("J");list.add("K");
		list.add("L");list.add("M");list.add("N");list.add("O");list.add("P");list.add("Q");list.add("R");list.add("S");list.add("T");list.add("U");
		list.add("V");list.add("W");list.add("X");list.add("Y");list.add("Z");
		return list ;
	}

}
