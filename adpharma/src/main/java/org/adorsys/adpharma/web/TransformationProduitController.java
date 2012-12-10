package org.adorsys.adpharma.web;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.adorsys.adpharma.domain.AvoirClient;
import org.adorsys.adpharma.domain.DestinationMvt;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.MouvementStock;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.TransformationProduit;
import org.adorsys.adpharma.domain.TypeMouvement;
import org.adorsys.adpharma.security.SecurityUtil;
import org.apache.log4j.TTCCLayout;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RooWebScaffold(path = "transformationproduits", formBackingObject = TransformationProduit.class)
@RequestMapping("/transformationproduits")
@Controller
public class TransformationProduitController {


	@RequestMapping(method = RequestMethod.POST)
	public String create(@Valid TransformationProduit transformationProduit, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		transformationProduit.setProduitOrigine(Produit.findProduit(transformationProduit.getOrigineId()));
		transformationProduit.setProduitCible(Produit.findProduit(transformationProduit.getCibleId()));
		transformationProduit.validate(bindingResult);
		if (bindingResult.hasErrors()) {
			uiModel.addAttribute("transformationProduit", transformationProduit);
			return "transformationproduits/editTransformation";
		}
		uiModel.asMap().clear();
		if (transformationProduit.getId()!=null) {
			transformationProduit.merge();	        	
		}else {
			transformationProduit.persist();
		}
		return "redirect:/transformationproduits/" + encodeUrlPathSegment(transformationProduit.getId().toString(), httpServletRequest);

	}


	@RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
	public String updateForm(@PathVariable("id") Long id, Model uiModel) {
		uiModel.addAttribute("transformationProduit", TransformationProduit.findTransformationProduit(id).clone());
		return "transformationproduits/editTransformation";
	}

	@RequestMapping(value = "/editTransformation/{id}", method = RequestMethod.GET)
	public String editTransformation(@PathVariable("id") Long id,Model uiModel) {
		if (id.intValue() !=0) {
			TransformationProduit transformationProduit = TransformationProduit.findTransformationProduit(id);
			uiModel.addAttribute("transformationProduit",transformationProduit.clone() );
		}else {
			uiModel.addAttribute("transformationProduit",new TransformationProduit() );
		}

		return "transformationproduits/editTransformation";
	}

	@RequestMapping(params = { "find=ByProduitOrigine", "form" }, method = RequestMethod.GET)
	public String findTransformationProduitsByProduitOrigineForm(Model uiModel) {
		uiModel.addAttribute("transformationProduit", new TransformationProduit());
		return "transformationproduits/search";
	}

	@RequestMapping(value="/search" , method = RequestMethod.POST)
	public String search( TransformationProduit trans, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		if (trans.getOrigineId()!=null) {
			trans.setProduitOrigine(Produit.findProduit(trans.getOrigineId()));

		}
		if (trans.getCibleId()!=null) {
			trans.setProduitCible(Produit.findProduit(trans.getCibleId()));

		}
		uiModel.addAttribute("transformationProduit", new TransformationProduit());
		System.out.println(trans.getOrigineName());
		uiModel.addAttribute("transformationproduits", TransformationProduit.search(trans.getProduitOrigine(), trans.getProduitCible(),trans.getOrigineName(),trans.getCibleName(), trans.getActif()).getResultList());
		return "transformationproduits/search";

	}

	@RequestMapping(params = { "find=BySearch", "form" }, method = RequestMethod.GET)
	public String Search(Model uiModel) {
		uiModel.addAttribute("transformationProduit", new TransformationProduit());
		return "transformationproduits/search";
	}

	@RequestMapping(value = "/BySearch", method = RequestMethod.GET)
	public String Search(TransformationProduit transf , Model uiModel) {
		uiModel.addAttribute("results", TransformationProduit.search(null, null, transf.getOrigineName(), transf.getCibleName(), transf.getActif()).getResultList());
		uiModel.addAttribute("transformationProduit", transf);
		return "transformationproduits/search";
	}


	@RequestMapping(value="/livreeForm" ,params="form", method = RequestMethod.GET)
	public String transformForm( Model uiModel, HttpServletRequest httpServletRequest) {

		return "transformationproduits/livre" ;
	}


	@RequestMapping(params = "form", method = RequestMethod.GET)
	public String  createForm(Model uiModel) {
		uiModel.addAttribute("transformationProduit", new TransformationProduit());
		return "redirect:/transformationproduits/editTransformation/0";
	}

	@Transactional
	@RequestMapping(value="/livreeForm" , method = RequestMethod.POST)
	public String transform(@RequestParam("qte")BigInteger qte ,@RequestParam("lineId") Long lineId, Model uiModel, HttpServletRequest httpServletRequest) {
		LigneApprovisionement ligneApprovisionement = LigneApprovisionement.findLigneApprovisionement(lineId);
		if(qte.intValue() > ligneApprovisionement.getQuantieEnStock().intValue()) {
			uiModel.addAttribute("apMessage", "imppossible d effectuer la tranformation !  la qte est superieur a la quantite en stock");
		}else if (!ligneApprovisionement.getApprovisionement().getCloturer()) {
			uiModel.addAttribute("apMessage", "imppossible d effectuer la tranformation ! veuillez  cloturer l'aprovisionnement avant");
		}else {
			LigneApprovisionement transforme = ligneApprovisionement.transforme(qte,uiModel);
			System.out.println(transforme);
			if (transforme!=null) {
				transforme.persist();
				Produit produit = transforme.getProduit();
				Produit produit2 = ligneApprovisionement.getProduit();
				produit2.setDateDerniereSortie(new Date());
				produit.setDateDerniereEntre(new Date());
				MouvementStock mvt1 = new MouvementStock();
				MouvementStock mvt2 = new MouvementStock();
				mvt1.setCip(transforme.getCip());
				mvt1.setQteInitiale(produit.getQuantiteEnStock());
				produit.addproduct(transforme.getQuantiteAprovisione());
				mvt1.setQteFinale(produit.getQuantiteEnStock());
				mvt1.setCipM(transforme.getCipMaison());
				mvt1.setDesignation(transforme.getDesignation());
				mvt1.setDestination(DestinationMvt.MAGASIN);
				mvt1.setNumeroBordereau(transforme.getApprovisionement().getBordereauNumber());
				mvt1.setOrigine(DestinationMvt.MAGASIN);
				mvt1.setQteDeplace(transforme.getQuantiteAprovisione());
				mvt1.setSite(transforme.getApprovisionement().getMagasin());
				mvt1.setTypeMouvement(TypeMouvement.ENTREE_TRANSFOMATION);
				mvt1.setAgentCreateur(SecurityUtil.getUserName());
				mvt1.setFiliale(transforme.getProduit().getFiliale().toString());
				mvt1.setPAchatTotal(transforme.getPrixAchatTotal().toBigInteger());
				mvt2.setCip(ligneApprovisionement.getCip());
				mvt2.setCipM(ligneApprovisionement.getCipMaison());
				mvt2.setQteInitiale(produit2.getQuantiteEnStock());
				produit2.removeProduct(qte);
				mvt2.setQteFinale(produit2.getQuantiteEnStock());
				mvt2.setDesignation(ligneApprovisionement.getDesignation());
				mvt2.setDestination(DestinationMvt.MAGASIN);
				mvt2.setNumeroBordereau(ligneApprovisionement.getApprovisionement().getBordereauNumber());
				mvt2.setOrigine(DestinationMvt.MAGASIN);
				mvt2.setQteDeplace(qte);
				mvt2.setFiliale(produit2.getFiliale().toString());
				mvt2.setSite(ligneApprovisionement.getApprovisionement().getMagasin());
				mvt2.setTypeMouvement(TypeMouvement.SORTIE_TRANSFOMATION);
				mvt2.setAgentCreateur(SecurityUtil.getUserName());
				mvt1.persist();
				mvt2.persist();
				produit.merge();
				produit2.merge();
				uiModel.addAttribute("transforme", transforme);
				uiModel.addAttribute("ligne", ligneApprovisionement);	
			}

		}


		return "transformationproduits/livre" ;
	}

	@ModelAttribute("produits")
	public Collection<Produit> populateProduits() {
		//return Produit.findAllProduits();
		return new ArrayList<Produit>();
	}

	@ModelAttribute("transformationproduits")
	public Collection<TransformationProduit> populateTransformationProduits() {
		// return TransformationProduit.findAllTransformationProduits();
		return new ArrayList<TransformationProduit>();

	}	 

}
