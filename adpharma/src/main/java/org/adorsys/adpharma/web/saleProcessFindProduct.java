package org.adorsys.adpharma.web;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.adorsys.adpharma.beans.SaleProcess;
import org.adorsys.adpharma.domain.CommandeClient;
import org.adorsys.adpharma.domain.Etat;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.utils.Contract;
import org.adorsys.adpharma.utils.ProcessHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@RequestMapping("/saleprocess/{cmdId}/findProduct")
@Controller
public class saleProcessFindProduct {



	@RequestMapping( method = RequestMethod.GET)
	@ResponseBody
	public String findProductForm(@PathVariable("cmdId") Long cmdId,Model uiModel,HttpServletRequest httpServletRequest) {
		String designation = httpServletRequest.getParameter("designation") ;
		String rp = httpServletRequest.getParameter("rp") ;
		List <LigneApprovisionement> search = new ArrayList<LigneApprovisionement>();
		if ("on".equals(rp)) {
			search = LigneApprovisionement.searchAJAX(designation,null, Etat.CLOS).setMaxResults(200).getResultList();

		}else {
			search = LigneApprovisionement.searchAJAX(designation,BigInteger.ONE, Etat.CLOS).setMaxResults(200).getResultList();

		}
		ArrayList<LigneApprovisionement> arrayList = new ArrayList<LigneApprovisionement>();
		if (!search.isEmpty()) {
			for (LigneApprovisionement ligneApprovisionement : search) {
				arrayList.add(ligneApprovisionement.clone());

			}

		}
		return LigneApprovisionement.toJsonArray(arrayList);
	}

	@RequestMapping(params = "find=ByDesignationLike", method = RequestMethod.GET)
	public String findProduitsByDesignationLike(@PathVariable("cmdId") Long cmdId,@RequestParam("designation") String designation, Model uiModel,
			HttpServletRequest httpServletRequest ) {
		Contract.notBlank("designation", designation);
		List<LigneApprovisionement> produits = LigneApprovisionement.findLigneApprovisionementsByQuantieEnStockUpThanAndDesignationLike(BigInteger.ONE, designation,Etat.CLOS).getResultList();
		return	showPoduct(produits, uiModel, httpServletRequest, cmdId);
	}

	@RequestMapping(params = "find=ByCipEquals", method = RequestMethod.GET)
	public String findProduitsByCipEquals(@PathVariable("cmdId") Long cmdId,@RequestParam("cip") String cip, Model uiModel,
			HttpServletRequest httpServletRequest ) {
		Contract.notBlank("cip", cip);
		List<LigneApprovisionement> produits = LigneApprovisionement.findLigneApprovisionementsByQuantieEnStockUpThanAndCipEquals(BigInteger.ONE, cip).getResultList();
		return	showPoduct(produits, uiModel, httpServletRequest, cmdId);
	}

	// display the list of product to the client 
	public String showPoduct(List<LigneApprovisionement>  produits ,Model uiModel,HttpServletRequest httpServletRequest,Long cmdId){
		if (produits.isEmpty()) {
			httpServletRequest.setAttribute("appMessage","<b> Aucun Produit Trouves </b>");
			return "redirect:/saleprocess/" + ProcessHelper.encodeUrlPathSegment(cmdId.toString(), httpServletRequest)+"/findProduct?form";
		}else {
			SaleProcess saleProcess = new SaleProcess(CommandeClient.findCommandeClient(cmdId), null);
			saleProcess.setProductResult(produits);
			uiModel.addAttribute("saleProcess",saleProcess);
			return "saleprocess/findproduct";
		} 

	}
}

