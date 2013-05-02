package org.adorsys.adpharma.web;

import java.math.BigInteger;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.adorsys.adpharma.beans.ApprovisonementProcess;
import org.adorsys.adpharma.domain.Approvisionement;
import org.adorsys.adpharma.domain.Fournisseur;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.utils.Contract;
import org.adorsys.adpharma.utils.ProcessHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@RequestMapping("/approvisionementprocess/{apId}/findProduct")
@Controller
public class ApprovisionementProcessFindProductController {

	@RequestMapping(params = "form", method = RequestMethod.GET)
	public String findProductForm(@PathVariable("apId") Long apId,Model uiModel,HttpSession session) {
		Approvisionement approvisionement = Approvisionement.findApprovisionement(apId);
		ApprovisonementProcess approvisonementProcess = new ApprovisonementProcess(apId);
		uiModel.addAttribute("approvisonementProcess",approvisonementProcess);
		uiModel.addAttribute("apId", apId);
		return "approvisionementprocess/findproduct";
	}

	@RequestMapping(value="/{cip}", method = RequestMethod.GET)
	@ResponseBody
	public String findProductApByCipAjax(@PathVariable("cip") String cip,@PathVariable("apId") Long apId , Model uiModel) {
		List<LigneApprovisionement> lastlineAp = LigneApprovisionement.findLastLigneApprovisionementsByCip(cip).setMaxResults(1).getResultList();
		Produit prd = new Produit() ;
		if (!lastlineAp.isEmpty()) {
			LigneApprovisionement next = lastlineAp.iterator().next();
			 prd = next.getProduit();
			 prd.setPrixAchatSTock(next.getPrixAchatUnitaire());
			 prd.setPrixVenteStock(next.getPrixVenteUnitaire());
		}else {
			List<Produit> produits = Produit.findProduitsByCipEquals(cip).setMaxResults(1).getResultList();
            if (!produits.isEmpty()) {
				 prd = produits.iterator().next() ;
				 prd.initPrice();
			}
		}

		return prd.toJson();
	}


	@RequestMapping(params = "find=ByDesignationLike", method = RequestMethod.GET)
	public String findProduitsByDesignationLike(@PathVariable("apId") Long apId,@RequestParam("designation") String designation, Model uiModel,
			HttpServletRequest httpServletRequest ,HttpSession session) {
		Contract.notBlank("designation", designation);
		List<Produit> produits = Produit.findProduitsByDesignationLike(designation).getResultList();
		return	showPoduct(produits, uiModel, session, httpServletRequest, apId);
	}
	@RequestMapping(params = "find=ByQuantiteEnStock", method = RequestMethod.GET)
	public String findProduitsByQuantiteEnStock(@PathVariable("apId") Long apId,@RequestParam("quantiteEnStock") BigInteger quantiteEnStock,
			Model uiModel,HttpServletRequest httpServletRequest ,HttpSession session) {
		Contract.notNull("quantiteEnStock", quantiteEnStock) ;
		List<Produit> produits = Produit.findProduitsByQuantiteEnStock(quantiteEnStock).getResultList();
		return	showPoduct(produits, uiModel, session, httpServletRequest, apId);

	}


	// display the list of product to the client 
	public String showPoduct(List<Produit>  produits ,Model uiModel, HttpSession session,HttpServletRequest httpServletRequest,Long apId){
		if (produits.isEmpty()) {
			uiModel.addAttribute("appMessage", "<b> Aucun Produit Trouves </b>");
			return "forward:/approvisionementprocess/" + ProcessHelper.encodeUrlPathSegment(apId.toString(), httpServletRequest)+"/findProduct?form";
		}else {
			Approvisionement approvisionement = Approvisionement.findApprovisionement(apId);
			ApprovisonementProcess process = new ApprovisonementProcess(apId);
			process.setProductResult(produits);
			uiModel.addAttribute("approvisonementProcess",process);
			uiModel.addAttribute("apId", apId);
			return "approvisionementprocess/findproduct";
		} 

	}
}
