package org.adorsys.adpharma.web;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.adorsys.adpharma.domain.FamilleProduit;
import org.adorsys.adpharma.domain.Filiale;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.Rayon;
import org.adorsys.adpharma.domain.SousFamilleProduit;
import org.adorsys.adpharma.domain.TVA;
import org.adorsys.adpharma.domain.TauxMarge;
import org.adorsys.adpharma.utils.ProcessHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RooWebScaffold(path = "produits", formBackingObject = Produit.class)
@RequestMapping("/produits")
@Controller
public class ProduitController {

	//a redefinir
	@RequestMapping(value="/findByCipAjax/{cip}", method = RequestMethod.GET)
	@ResponseBody
	public String findProductByCip(@PathVariable("cip") String cip,Model uiModel) {
		List<Produit> produits = Produit.findProduitsByCipEquals(cip).setMaxResults(100).getResultList();
		Produit prd = null ;
		String reponse = "Aucun produit Trouve" ;
		if (!produits.isEmpty()) {
			prd = produits.iterator().next();
			prd.calculPrixTotalStock();
			reponse= ""+prd.getCip()+","+prd.getDesignation()+","+prd.getTauxDeMarge().getMargeValue()+","+prd.getId()+","+prd.getQuantiteEnStock()+","+prd.getSeuilComande()+","+prd.getPrixTotalStock();
		}

		return reponse;
	}

	@RequestMapping(value="/attributionFamile", params = "form", method = RequestMethod.GET)
	public String attributionFamile(Model uiModel) {
		uiModel.addAttribute("produit", new Produit());
		return "produits/attributionfsf";
	}

	@RequestMapping(value="/setFamilleAndSousFamille",method = RequestMethod.GET)
	public String setFamilleAndSousFamille(@RequestParam("rem") BigDecimal rem ,@RequestParam("lineId") Long lineId,@RequestParam(value="famille" ,required = false) Long familleId ,@RequestParam(value="sfamille" ,required = false) Long sfamilleId, Model uiModel, HttpServletRequest httpServletRequest) {
		Produit produit = Produit.findProduit(lineId);
		if (familleId != null)  produit.setFamilleProduit(FamilleProduit.findFamilleProduit(familleId)); 
		if (sfamilleId != null)  produit.setSousfamilleProduit(SousFamilleProduit.findSousFamilleProduit(sfamilleId)); 	
		if (rem != null)  produit.setTauxRemiseMax(rem); 	
		Produit merge = (Produit) produit.merge();
		ArrayList<Produit> arrayList = new ArrayList<Produit>();
		arrayList.add(produit);
		uiModel.addAttribute("produits", arrayList);
		return "produits/attributionfsf";
	}

	@RequestMapping(value="/findProductApByCipAjax/{cip}", method = RequestMethod.GET)
	@ResponseBody
	public String findProductApByCipAjax(@PathVariable("cip") String cip,Model uiModel) {
		List<Produit> produits = Produit.findProduitsByCipEqualsAndVenteAutorise(cip,Boolean.TRUE).setMaxResults(200).getResultList();
		Produit prd = new Produit() ;
		if (!produits.isEmpty()) {
			prd = produits.iterator().next();
			//prd.calculPrixTotalStock();
		}

		return prd.toJson();
	}

	//a redefinir
	@RequestMapping(value="/findProductByCipAjax", method = RequestMethod.GET)
	@ResponseBody
	public String findProductByCipAjax(Model uiModel ,  HttpServletRequest httpServletRequest) {
		String des = httpServletRequest.getParameter("designation");
		List<Produit> resultList = Produit.findProduitsByDesignationLike(des).setMaxResults(200).getResultList();
		System.out.println("in");
		return Produit.toJsonArray(resultList);
	}

	@RequestMapping(value="/findProductByIdAjax/{id}", method = RequestMethod.GET)
	@ResponseBody
	public String findProductByIdAjax(@PathVariable("id") Long id,Model uiModel ,  HttpServletRequest httpServletRequest) {
		Produit produit = Produit.findProduit(id);
		produit.calculPrixTotalStock();

		return produit.toJson();
	}


	//retourne les information apres un ajax request
	@RequestMapping(value="/findByCipmAjax/{cipm}", method = RequestMethod.GET)
	@ResponseBody
	public String findByCipmAjax(@PathVariable("cipm") String cipm,Model uiModel) {
		String	cipMaison = cipm;
		//cipMaison = StringUtils.removeStart(cipMaison, "0");
		List<LigneApprovisionement> lines = LigneApprovisionement.findLigneApprovisionementsByCipMaisonEquals(cipMaison).setMaxResults(100).getResultList();
		if (!lines.isEmpty()) {
			return lines.iterator().next().clone().toJson();
		}else {
			return new LigneApprovisionement().toJson();
		}	

	}


	@RequestMapping(value="/create/{cip}",method = RequestMethod.POST)
	public String create(@PathVariable("cip") boolean cip, @Valid Produit produit, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		produit.validate(bindingResult);
		if (bindingResult.hasErrors()) {
			uiModel.addAttribute("produit", produit);
			uiModel.addAttribute("cip", cip);
			addDateTimeFormatPatterns(uiModel);
			return "produits/create";
		}
		uiModel.asMap().clear();
		produit.persist();
		return "redirect:/produits/" + encodeUrlPathSegment(produit.getId().toString(), httpServletRequest);
	}
	
	@ResponseBody
	@RequestMapping(value="/createByAjax",method = RequestMethod.GET)
	public String create( @Valid Produit produit, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		produit.validate(bindingResult);
		if (bindingResult.hasErrors()) {
			return null;
		}
		uiModel.asMap().clear();
		produit.persist();
		return produit.toJson() ;
	}

	@RequestMapping(value="/ProduitRuptureStock",method = RequestMethod.GET)
	public String ProduitRuptureStock(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
		if (page != null || size != null) {
			int sizeNo = size == null ? 10 : size.intValue();
			uiModel.addAttribute("produits", Produit.findProduitRuptureStockEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
			float nrOfPages = (float) Produit.countOutProduits() / sizeNo;
			uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
		} else {
			uiModel.addAttribute("produits", Produit.findProduitRuptureStockEntries(0, 1));
		}
		addDateTimeFormatPatterns(uiModel);
		return "produits/list";
	}

	@RequestMapping(value="/ProduitAlerteStock",method = RequestMethod.GET)
	public String ProduitAlerteStock(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
		if (page != null || size != null) {
			int sizeNo = size == null ? 10 : size.intValue();
			uiModel.addAttribute("produits", Produit.findProduitAlerteStockEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
			float nrOfPages = (float) Produit.countProduits() / sizeNo;
			uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
		} else {
			uiModel.addAttribute("produits", Produit.findProduitAlerteStockEntries(1, 10));
		}
		addDateTimeFormatPatterns(uiModel);
		return "produits/list";
	}


	@RequestMapping(value="/create/{cip}", params = "form", method = RequestMethod.GET)
	public String createForm(@PathVariable("cip") Boolean cip,Model uiModel) {
		uiModel.addAttribute("produit", new Produit());
		uiModel.addAttribute("cip", cip);
		ProcessHelper.addDateTimeFormatPatterns(uiModel);
		return "produits/create";
	}


	


	@ModelAttribute("produits")
	public Collection<Produit> populateProduits() {
		return new ArrayList<Produit>();
	}	 


}
