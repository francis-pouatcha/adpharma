package org.adorsys.adpharma.web;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.adorsys.adpharma.beans.InventaireProcess;
import org.adorsys.adpharma.domain.Etat;
import org.adorsys.adpharma.domain.Inventaire;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.LigneInventaire;
import org.adorsys.adpharma.domain.MouvementStock;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.utils.Contract;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.adorsys.adpharma.utils.ProcessHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/inventaireProcess")
@Controller
public class InventaireProcessController {

	@RequestMapping(value = "/{invId}/editInventaire", method = RequestMethod.GET)
	public String editCommand(@PathVariable("invId") Long invId, Model uiModel,HttpSession session) {
		Inventaire inventaire = Inventaire.findInventaire(invId);
		InventaireProcess inventaireProcess = new InventaireProcess(invId, LigneInventaire.findLigneInventairesByInventaire(inventaire).getResultList());
		uiModel.addAttribute("inventaireProcess",inventaireProcess);
		return "inventaireProcess/editInventaire";
	}

	@RequestMapping(value = "/{invId}/findProduct",params = "form", method = RequestMethod.GET)
	public String findProductForm(@PathVariable("invId") Long invId,Model uiModel,HttpSession session) {
		Inventaire inventaire = Inventaire.findInventaire(invId);
		InventaireProcess inventaireProcess = new InventaireProcess(invId);
		uiModel.addAttribute("inventaireProcess",inventaireProcess);
		return "inventaireProcess/findProduct";
	}


	@RequestMapping(value = "/{invId}/findProduct",params = "find=ByDesignationLike", method = RequestMethod.GET)
	public String findProduitsByDesignationLike(@PathVariable("invId") Long invId,@RequestParam("designation") String designation, Model uiModel,
			HttpServletRequest httpServletRequest ,HttpSession session) {
		Contract.notBlank("designation", designation);
		List<Produit> produits = Produit.findProduitsByDesignationLike(designation).getResultList();
		return	showPoduct(produits, uiModel, invId);
	}

	@RequestMapping(value = "/{invId}/selectProduct/{pId}", method = RequestMethod.GET)
	public String selectProduct(@PathVariable("invId") Long invId, @PathVariable("pId") Long pId  ,Model uiModel, HttpServletRequest httpServletRequest) {
		Produit produit = Produit.findProduit(pId);
		InventaireProcess inventaireProcess = new InventaireProcess(invId, LigneInventaire.findLigneInventairesByInventaire(Inventaire.findInventaire(invId)).getResultList());
		inventaireProcess.setProduit(produit);
		uiModel.addAttribute("inventaireProcess",inventaireProcess);
		return "inventaireProcess/editInventaire";
	}

	@RequestMapping(value = "/{invId}/addLine", method = RequestMethod.POST)
	public String addLine(@PathVariable("invId") Long invId,@RequestParam Long pId,@RequestParam BigInteger qte,Model uiModel,HttpSession session) {
		Inventaire inventaire = Inventaire.findInventaire(invId);
		Produit produit = Produit.findProduit(pId);
		produit.calculPrixTotalStock();
		if (inventaire.contientProduit(produit)) {
			uiModel.addAttribute("apMessage", "Ce produit est deja dans la liste ! ");
		}else{
			LigneInventaire ligneInventaire = new LigneInventaire();
			ligneInventaire.setInventaire(inventaire);
			ligneInventaire.setProduit(produit);
			ligneInventaire.setPrixTotal(produit.getPrixTotalStock());
			ligneInventaire.setQteEnStock(produit.getQuantiteEnStock());
			ligneInventaire.setQteReel(qte);
			ligneInventaire.calculerEcart();
			ligneInventaire.persist();


		}
		InventaireProcess inventaireProcess = new InventaireProcess(invId, LigneInventaire.findLigneInventairesByInventaire(inventaire).getResultList());
		uiModel.addAttribute("inventaireProcess",inventaireProcess);
		return "inventaireProcess/editInventaire";

	}

	@RequestMapping(value = "/{invId}/deleteLine/{lineId}",method = RequestMethod.GET)
	public String unselectLine(@PathVariable("invId") String invId, @PathVariable("lineId") Long lineId ,Model uiModel, HttpServletRequest httpServletRequest) {
		LigneInventaire ligneInventaire = LigneInventaire.findLigneInventaire(lineId);
		ligneInventaire.remove();
		return "redirect:/inventaireProcess/" + ProcessHelper.encodeUrlPathSegment(invId, httpServletRequest)+"/editInventaire";
	}


	@RequestMapping(value = "/{invId}/updateLine/{lineId}", method = RequestMethod.GET)
	public String updateLineForm(@PathVariable("invId") Long invId, @PathVariable("lineId") Long lineId  ,Model uiModel, HttpServletRequest httpServletRequest) {
		Inventaire inventaire = Inventaire.findInventaire(invId);
		LigneInventaire ligneInventaire = LigneInventaire.findLigneInventaire(lineId);
		InventaireProcess inventaireProcess = new InventaireProcess(invId, LigneInventaire.findLigneInventairesByInventaire(inventaire).getResultList());
		inventaireProcess.setLineToUpdate(ligneInventaire);
		uiModel.addAttribute("inventaireProcess",inventaireProcess);
		return "inventaireProcess/editInventaire";
	}

	@RequestMapping(value = "/{invId}/updateLine/{lineId}", method = RequestMethod.POST)
	public String updateLine(@PathVariable("invId") String invId, @PathVariable("lineId") Long lineId , @RequestParam BigInteger qte, Model uiModel, HttpServletRequest httpServletRequest) {
		LigneInventaire ligneInventaire = LigneInventaire.findLigneInventaire(lineId);
		ligneInventaire.setQteReel(qte);
		ligneInventaire.calculerEcart();
		ligneInventaire.merge();
		return "redirect:/inventaireProcess/" + ProcessHelper.encodeUrlPathSegment(invId, httpServletRequest)+"/editInventaire";
	}
	@Transactional
	@RequestMapping(value = "/{invId}/enregistrerInv", method = RequestMethod.GET)
	public String enregistrer(@PathVariable("invId") Long invId, Model uiModel,HttpSession session) {
		Inventaire inventaire = Inventaire.findInventaire(invId);
		ProcessHelper.addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("inventaire", inventaire);
		uiModel.addAttribute("itemId",invId);
		inventaire.merge();
		return "inventaireProcess/show";
	}


	@Transactional
	@RequestMapping(value = "/{invId}/annulerInv", method = RequestMethod.GET)
	public String annuler(@PathVariable("invId") Long invId, Model uiModel,HttpSession session) {
		Inventaire.findInventaire(invId).remove();
		return "redirect:/menu/inventaire";
	}

	//@Transactional
	@RequestMapping(value = "/{invId}/closeInventaire", method = RequestMethod.GET)
	public String closeCommand(@PathVariable("invId") Long invId, Model uiModel,HttpSession session) {
		Inventaire inventaire = Inventaire.findInventaire(invId);
		List<LigneInventaire> resultList = LigneInventaire.findLigneInventairesByInventaire(inventaire).getResultList();
		if (resultList.isEmpty()) {
			uiModel.addAttribute("apMessage","imppossible de cloturer l'inventaire car ne contient aucune lignes ! ");
			return "caisses/infos";
		}
		if (inventaire.getEtat().equals(Etat.CLOS)) {
			uiModel.addAttribute("apMessage","cette Inventaire est deja clos ! ");
			return "caisses/infos";

		}

		inventaire.restoreStock() ;
		inventaire.setEtat(Etat.CLOS);
		Inventaire  merge = (Inventaire) inventaire.merge();
		uiModel.addAttribute("inventaire", merge);
		uiModel.addAttribute("itemId", merge.getId());
		ProcessHelper.addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("apMessage","inventaire  cloturer avec succes ");
		return "inventaireProcess/show";
	}


	// display the list of product to the client 

	public String showPoduct(List<Produit>  produits ,Model uiModel,Long invId){
		Inventaire inventaire = Inventaire.findInventaire(invId);
		InventaireProcess inventaireProcess = new InventaireProcess(invId);
		inventaireProcess.setProductResult(produits);
		uiModel.addAttribute("inventaireProcess",inventaireProcess);
		return "inventaireProcess/findProduct";
	}


	@RequestMapping(value = "/ficheInventaireParOrdreAlpha",params = {  "form" }, method = RequestMethod.GET)
	public String ficheSuivieStockForm(Model uiModel) {
		uiModel.addAttribute("inventaire", new Inventaire());
		uiModel.addAttribute("rayons", ProcessHelper.populateRayon() );
		uiModel.addAttribute("filiales", ProcessHelper.populateFiliale());
		uiModel.addAttribute("familleProduits", ProcessHelper.populateFamilleProduit());
		uiModel.addAttribute("sousFamilleProduits", ProcessHelper.populateSousFamilleProduit());
		return "inventaires/inventaireParOrdreAlpha";
	}

	@RequestMapping(value = "/ficheSuivieStock", method = RequestMethod.GET)
	public String ficheSuivieStock(@Valid Inventaire inp, BindingResult bindingResult,HttpServletRequest request , Model uiModel) {
		if (inp.isCipm()) {
			inp.setLigneApprovisionements(LigneApprovisionement.search(inp.getFamilleProduit(),inp.getSousFamilleProduit(),inp.getDesignation(), null, inp.getRayon(), inp.getBeginBy(), inp.getEndBy(), inp.getFiliale(), null, null,null,null).getResultList());

		}else {
			List<Produit> resultList = Produit.search(inp.getFamilleProduit(),inp.getSousFamilleProduit(),null, inp.getDesignation(), inp.getBeginBy(), inp.getEndBy(), inp.getRayon(), inp.getFiliale(),inp.getDateRupture());
			if (!resultList.isEmpty()) {
				for (Produit produit : resultList) {
					produit.calculTransientPrice();
				}
				inp.setProduits(resultList);

			}
		}


		uiModel.addAttribute("inventaire", inp);
		uiModel.addAttribute("headTexte", "Fiche De Suivie de Stock Du "+PharmaDateUtil.format(inp.getDateDebut(), PharmaDateUtil.DATETIME_PATTERN_LONG)+" AU :" +
				"" +PharmaDateUtil.format(inp.getDateFin(), PharmaDateUtil.DATETIME_PATTERN_LONG));


		return "inventaires/ficheSuivieStock";
	}



	@RequestMapping(value = "/ficheSuivieQte",params = {"form" }, method = RequestMethod.GET)
	public String inventaireFicheQteForm(Model uiModel) {
		uiModel.addAttribute("inventaire", new Inventaire());
		uiModel.addAttribute("rayons", ProcessHelper.populateRayon() );
		uiModel.addAttribute("filiales", ProcessHelper.populateFiliale() );
		return "inventaires/inventaireFicheQte";

	}

	@RequestMapping(value = "/ficheSuivieQte.pdf", method = RequestMethod.GET)
	public String inventaireFicheQte(@Valid Inventaire inp, BindingResult bindingResult,HttpServletRequest request , Model uiModel) {
		List<Produit> result = new ArrayList<Produit>();
		if (inp.isFindall()) {
			List<Object[]> etatVente = MouvementStock.getQteVenduByCip(inp.getCipProduct(), inp.getDesignation(), inp.getBeginBy(), inp.getEndBy(),inp.getDateDebut(), inp.getDateFin());
			if (!etatVente.isEmpty()) {
				for (Object[] obj : etatVente) {
					List<Produit> resultList = Produit.findProduitsByCipEquals((String)obj[0]).getResultList();
					if (!resultList.isEmpty()) {
						Produit produit = resultList.iterator().next();
						produit.setQtevendu((BigInteger)obj[2]);
						result.add(produit);
					}
				}

			}
		}else {
			List<Produit> search = Produit.search(null ,null,inp.getCipProduct(), inp.getDesignation(), inp.getBeginBy(), inp.getEndBy(), inp.getRayon(), inp.getFiliale(),inp.getDateRupture());
			if (!search.isEmpty()) {
				for (Produit produit : search) {
					produit.calculQteVendue(inp.getDateDebut(), inp.getDateFin());
					if (produit.getQtevendu().intValue()!=0) result.add(produit);

				}
			}

			inp.setProduits(result);
		}

		uiModel.addAttribute("listeProduit", result);
		uiModel.addAttribute("headTexte", "Fiche De Suivie de Quantitee Du "+PharmaDateUtil.format(inp.getDateDebut(), PharmaDateUtil.DATETIME_PATTERN_LONG)+" AU :" +
				"" +PharmaDateUtil.format(inp.getDateFin(), PharmaDateUtil.DATETIME_PATTERN_LONG));
		return "ficheSuivieQtePdf";
	}
	@RequestMapping(params = { "find=BySearch", "form" }, method = RequestMethod.GET)
	public String Search(Model uiModel) {
		uiModel.addAttribute("rayon", ProcessHelper.populateRayon());
		uiModel.addAttribute("filiale", ProcessHelper.populateFiliale());
		uiModel.addAttribute("produit",new Produit() );

		return "produits/search";
	}

	@RequestMapping(value="/search", method = RequestMethod.GET)
	public String Search(Produit prd , Model uiModel) {
		if (StringUtils.isBlank(prd.getDesignation())&& StringUtils.isBlank(prd.getCip()) && prd.getFiliale()==null && prd.getRayon() ==null ) {
			return "redirect:/produits?page=1" ;

		}
		uiModel.addAttribute("results", Produit.search(prd.getFamilleProduit(),prd.getSousfamilleProduit(),prd.getCip(), prd.getDesignation(),null,null, prd.getRayon(), prd.getFiliale() ,prd.getDateDerniereRupture() ));
		uiModel.addAttribute("rayon", ProcessHelper.populateRayon());
		uiModel.addAttribute("filiale", ProcessHelper.populateFiliale());
		return "produits/search";
	}


}
