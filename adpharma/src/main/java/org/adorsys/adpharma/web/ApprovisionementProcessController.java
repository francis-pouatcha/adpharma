package org.adorsys.adpharma.web;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.adorsys.adpharma.beans.ApprovisonementProcess;
import org.adorsys.adpharma.beans.PrintBareCodeBean;
import org.adorsys.adpharma.domain.Approvisionement;
import org.adorsys.adpharma.domain.CommandeFournisseur;
import org.adorsys.adpharma.domain.Devise;
import org.adorsys.adpharma.domain.Etat;
import org.adorsys.adpharma.domain.Filiale;
import org.adorsys.adpharma.domain.Fournisseur;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.Rayon;
import org.adorsys.adpharma.domain.Site;
import org.adorsys.adpharma.domain.TVA;
import org.adorsys.adpharma.domain.TauxMarge;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.adorsys.adpharma.utils.ProcessHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/approvisionementprocess")
@Controller
public class ApprovisionementProcessController {

	@RequestMapping(params = "form", method = RequestMethod.GET)
	public String createForm(Model uiModel) {
		uiModel.addAttribute("approvisionement", new Approvisionement());
		uiModel.addAttribute("filiales", ProcessHelper.populateAllFiliale());
		ProcessHelper.addDateTimeFormatPatterns(uiModel);
		return "approvisionementprocess/create";
	}
	
	@RequestMapping(value  = "/printBareCode", method = RequestMethod.GET)
	public String printBareCodeView(Model uiModel) {
		uiModel.addAttribute("printBareCodeBean", new PrintBareCodeBean());
		return "/approvisionementprocess/printBareCode";
	}
	
	@RequestMapping(value  = "/printBareCode.pdf", method = RequestMethod.GET)
	public String printBareCode(@Valid PrintBareCodeBean printBareCodeBean , BindingResult bindingResult,Model uiModel) {
		printBareCodeBean.validate(bindingResult);
		if (bindingResult.hasErrors()) {
			uiModel.addAttribute("printBareCodeBean", printBareCodeBean);
			return "/approvisionementprocess/printBareCode";
		}
		uiModel.addAttribute("printBareCodeBean", printBareCodeBean);
		return "printBarecodeDocView";
	}
	

	@RequestMapping(method = RequestMethod.POST)
	public String create(@Valid Approvisionement approvisionement, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		if (bindingResult.hasErrors()) {
			uiModel.addAttribute("approvisionement", approvisionement);
			ProcessHelper.addDateTimeFormatPatterns(uiModel);
			return "approvisionementprocess/create";
		}
		uiModel.asMap().clear();
		if (approvisionement.getFiliales()!=null)approvisionement.setFiliale(approvisionement.getFiliales().getFilialeNumber());
		approvisionement.persist();
		return "redirect:/approvisionementprocess/" + ProcessHelper.encodeUrlPathSegment(approvisionement.getId().toString(), httpServletRequest)+"/edit";
	}

	// redirect to edit approvisionement form
	@RequestMapping(value = "/{apId}/edit", method = RequestMethod.GET)
	public String editApprovisionnement(@PathVariable("apId") Long apId, Model uiModel,HttpSession session) {
		Approvisionement approvisionement = Approvisionement.findApprovisionement(apId);
		ApprovisonementProcess approvisonementProcess = new ApprovisonementProcess(apId);
		approvisonementProcess.setLigneApprovisionements(LigneApprovisionement.findLigneApprovisionementsByApprovisionement(approvisionement).getResultList());
		uiModel.addAttribute("approvisonementProcess",approvisonementProcess);
		initProcurementViewDependencies(uiModel);
		return "approvisionementprocess/edit";
	}
	
	@RequestMapping(value = "/{apId}/specialEdit", method = RequestMethod.GET)
	public String specialEdit(@PathVariable("apId") Long apId, Model uiModel,HttpSession session) {
		Approvisionement approvisionement = Approvisionement.findApprovisionement(apId);
		ApprovisonementProcess approvisonementProcess = new ApprovisonementProcess(apId);
		approvisonementProcess.setEtat(Etat.CLOS);
		approvisonementProcess.setLigneApprovisionements(LigneApprovisionement.findLigneApprovisionementsByApprovisionement(approvisionement).getResultList());
		uiModel.addAttribute("approvisonementProcess",approvisonementProcess);
		initProcurementViewDependencies(uiModel);
		return "approvisionementprocess/edit";
	}

	@RequestMapping(value = "/{apId}/addLine", method = RequestMethod.POST)
	public String addLine(@PathVariable("apId") Long apId,@RequestParam Long pId,@RequestParam String qte,
			@RequestParam String pa,@RequestParam String pv,@RequestParam(required = false) String tvaj,@RequestParam String prm,Model uiModel,HttpSession session) {
		Approvisionement approvisionement = Approvisionement.findApprovisionement(apId);
		
		Produit produit = Produit.findProduit(pId);
		
		if (approvisionement.contientProduit(produit)) {
			uiModel.addAttribute("apMessage", "Ce produit est deja dans la liste ");
		}else if (approvisionement.CommandeContientProduit(produit)) {
			uiModel.addAttribute("apMessage", "Ce produit ne fait Pas partie de la commande recuperer !");
		}else{
			LigneApprovisionement ligneApprovisionement = new LigneApprovisionement();
			ligneApprovisionement.setAgentSaisie(SecurityUtil.getUserName());
			ligneApprovisionement.setApprovisionement(approvisionement);
			ligneApprovisionement.setQuantiteAprovisione(new BigInteger(qte.trim()));
			ligneApprovisionement.setPrixAchatUnitaire(new BigDecimal(pa.trim()));
			ligneApprovisionement.setDatePeremtion( PharmaDateUtil.parseToDate(prm, PharmaDateUtil.DATE_PATTERN_LONG2) );
			if (StringUtils.isNotBlank(pv)) {
				ligneApprovisionement.setPrixVenteUnitaire(new BigDecimal(pv.trim()));
			}
			ligneApprovisionement.setProduit(produit);
			ligneApprovisionement.persist();
			approvisionement.increaseMontant(ligneApprovisionement.getPrixAchatTotal());
			approvisionement.merge();
		}
		ApprovisonementProcess approvisonementProcess = new ApprovisonementProcess(apId);
		if (StringUtils.isNotBlank(tvaj)) {
			approvisonementProcess.setTaux(new BigDecimal(tvaj.trim()));
		}
		approvisonementProcess.setLigneApprovisionements(LigneApprovisionement.findLigneApprovisionementsByApprovisionement(approvisionement).getResultList());
		uiModel.addAttribute("approvisonementProcess",approvisonementProcess);
		initProcurementViewDependencies(uiModel);
		return "approvisionementprocess/edit";

	}
	@Transactional
	@RequestMapping(value = "/{apId}/specialaddLine", method = RequestMethod.POST)
	public String specialaddLine(@PathVariable("apId") Long apId,@RequestParam Long pId,@RequestParam String qte,
			@RequestParam String pa,@RequestParam String pv,@RequestParam(required = false) String tvaj,@RequestParam String prm,Model uiModel,HttpSession session) {
		Approvisionement approvisionement = Approvisionement.findApprovisionement(apId);
		
		Produit produit = Produit.findProduit(pId);
		
		if (approvisionement.contientProduit(produit)) {
			uiModel.addAttribute("apMessage", "Ce produit est deja dans la liste ");
		}else if (approvisionement.CommandeContientProduit(produit)) {
			uiModel.addAttribute("apMessage", "Ce produit ne fait Pas partie de la commande recuperer !");
		}else{
			LigneApprovisionement ligneApprovisionement = new LigneApprovisionement();
			ligneApprovisionement.setAgentSaisie(SecurityUtil.getUserName());
			ligneApprovisionement.setApprovisionement(approvisionement);
			ligneApprovisionement.setQuantiteAprovisione(new BigInteger(qte.trim()));
			ligneApprovisionement.setPrixAchatUnitaire(new BigDecimal(pa.trim()));
			ligneApprovisionement.setDatePeremtion( PharmaDateUtil.parseToDate(prm, PharmaDateUtil.DATE_PATTERN_LONG2) );
			if (!"".equals(pv)) {
				ligneApprovisionement.setPrixVenteUnitaire(new BigDecimal(pv.trim()));
			}
			produit.addproduct(ligneApprovisionement.getQuantiteAprovisione());
			ligneApprovisionement.setProduit(produit);
			ligneApprovisionement.persist();
			approvisionement.increaseMontant(ligneApprovisionement.getPrixAchatTotal());
			approvisionement.merge();
			produit.merge();
		}
		ApprovisonementProcess approvisonementProcess = new ApprovisonementProcess(apId);
		approvisonementProcess.setEtat(Etat.CLOS);
		if (StringUtils.isNotBlank(tvaj)) {
			approvisonementProcess.setTaux(new BigDecimal(tvaj.trim()));
		}
		approvisonementProcess.setLigneApprovisionements(LigneApprovisionement.findLigneApprovisionementsByApprovisionement(approvisionement).getResultList());
		uiModel.addAttribute("approvisonementProcess",approvisonementProcess);
		initProcurementViewDependencies(uiModel);
		return "approvisionementprocess/edit";

	}

	@RequestMapping(value = "/{apId}/updateLine", method = RequestMethod.POST)
	public String updatedLine(@PathVariable("apId") Long apId,@RequestParam Long lineId,@RequestParam BigInteger qte,
			@RequestParam BigDecimal pa,@RequestParam String pv, @RequestParam(required = false) String tvaj,@RequestParam String prm,Model uiModel,HttpSession session) {
		LigneApprovisionement line = LigneApprovisionement.findLigneApprovisionement(lineId);
		if (!line.getApprovisionement().getEtat().equals(Etat.CLOS)) {
			line.setQuantiteAprovisione(qte);
			line.setPrixAchatUnitaire(pa);
			if (!"".equals(pv)) {
				line.setPrixVenteUnitaire(new BigDecimal(pv.trim()));
			}else {
				line.setPrixVenteUnitaire(BigDecimal.ZERO);
				line.CalculerPrixVente();
			}
			line.setDatePeremtion( PharmaDateUtil.parseToDate(prm, PharmaDateUtil.DATE_PATTERN_LONG2) );
			line.CalculePaTotal();
			line.merge();
		}else{
			uiModel.addAttribute("apMessage", "impposible d effectuer une mis a jour <b> Approvisionement CLOS </b>");
		}
		Approvisionement approvisionement = Approvisionement.findApprovisionement(apId);
		approvisionement.calculateMontant();
		approvisionement.merge();
		ApprovisonementProcess approvisonementProcess = new ApprovisonementProcess(apId);
		if (StringUtils.isNotBlank(tvaj)) {
			approvisonementProcess.setTaux(new BigDecimal(tvaj.trim()));
		}
		approvisonementProcess.setLigneApprovisionements(LigneApprovisionement.findLigneApprovisionementsByApprovisionement(approvisionement).getResultList());
		uiModel.addAttribute("approvisonementProcess",approvisonementProcess);
		initProcurementViewDependencies(uiModel);
		return "approvisionementprocess/edit";

	}




	@RequestMapping(value = "/{apId}/showProduct/{pId}", method = RequestMethod.GET)
	public String showProduct(@PathVariable("apId") Long apId,@PathVariable("pId") Long pId, Model uiModel,HttpSession session) {
		ProcessHelper.addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("produit", Produit.findProduit(pId));
		uiModel.addAttribute("itemId", pId);
		uiModel.addAttribute("apId", apId);

		return "approvisionementprocess/showProduct";

	}
	@RequestMapping(value = "/{apId}/showCmd/{cmdId}", method = RequestMethod.GET)
	public String showCmd(@PathVariable("apId") Long apId,@PathVariable("cmdId") Long cmdId, Model uiModel,HttpSession session) {
		ProcessHelper.addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("commandefournisseur", CommandeFournisseur.findCommandeFournisseur(cmdId));
		uiModel.addAttribute("itemId", cmdId);
		uiModel.addAttribute("apId", apId);
		return "commandprocesses/show";

	}

	@RequestMapping(value = "/{apId}/updateLine/{lnId}",  method = RequestMethod.GET)
	public String updateLineForm(@PathVariable("lnId") Long lnId,@PathVariable("apId") Long apId, Model uiModel) {
		Approvisionement approvisionement = Approvisionement.findApprovisionement(apId);
		ApprovisonementProcess approvisonementProcess = new ApprovisonementProcess(apId);
		approvisonementProcess.setLigneApprovisionements(LigneApprovisionement.findLigneApprovisionementsByApprovisionement(approvisionement).getResultList());
		approvisonementProcess.setLineToUpdate(LigneApprovisionement.findLigneApprovisionement(lnId));
		uiModel.addAttribute("approvisonementProcess",approvisonementProcess);
		initProcurementViewDependencies(uiModel);
		return "approvisionementprocess/edit";
	}
	//assure la convertion des lignes de commande en ligne d'approvisionement 
	
	@RequestMapping(value = "/{apId}/recupererCmd/{cmId}",  method = RequestMethod.GET)
	public String recupererCmd(@PathVariable("cmId") Long cmId,@PathVariable("apId") Long apId, Model uiModel) {
		Approvisionement approvisionement = Approvisionement.findApprovisionement(apId);
		CommandeFournisseur.findCommandeFournisseur(cmId).convertToLineAprov(approvisionement);
		approvisionement.calculateMontant();
		approvisionement.merge();
		ApprovisonementProcess approvisonementProcess = new ApprovisonementProcess(apId);
		approvisonementProcess.setLigneApprovisionements(LigneApprovisionement.findLigneApprovisionementsByApprovisionement(approvisionement).getResultList());
		uiModel.addAttribute("approvisonementProcess",approvisonementProcess);
		return "approvisionementprocess/edit";
	}

	@RequestMapping(value = "/{apId}/deleteLine/{lnId}", method = RequestMethod.GET)
	public String deleteLine(@PathVariable("apId") Long apId,@PathVariable("lnId") Long lnId, HttpServletRequest httpServletRequest) {
		Approvisionement approvisionement = Approvisionement.findApprovisionement(apId);
		LigneApprovisionement ligneApprovisionement = LigneApprovisionement.findLigneApprovisionement(lnId);
		approvisionement.decreaseMontant(ligneApprovisionement.getPrixAchatTotal());
		approvisionement.merge();
		ligneApprovisionement.remove();

		return "redirect:/approvisionementprocess/" + ProcessHelper.encodeUrlPathSegment(apId.toString(), httpServletRequest)+"/edit";


	}
	@RequestMapping(value = "/{apId}/specialdeleteLine/{lnId}", method = RequestMethod.GET)
	public String specialDeleteLine(@PathVariable("apId") Long apId,@PathVariable("lnId") Long lnId, HttpServletRequest httpServletRequest) {
		Approvisionement approvisionement = Approvisionement.findApprovisionement(apId);
		LigneApprovisionement ligneApprovisionement = LigneApprovisionement.findLigneApprovisionement(lnId);
		approvisionement.decreaseMontant(ligneApprovisionement.getPrixAchatTotal());
		approvisionement.merge();
		ApprovisonementProcess.specialDeleteLine(ligneApprovisionement);
		return "redirect:/approvisionementprocess/" + ProcessHelper.encodeUrlPathSegment(apId.toString(), httpServletRequest)+"/specialEdit";


	}
	@RequestMapping(value = "/{apId}/selectProduct/{pId}",  method = RequestMethod.GET)
	public String selectProduct(@PathVariable("pId") Long pId,@PathVariable("apId") Long apId, Model uiModel) {
		Approvisionement approvisionement = Approvisionement.findApprovisionement(apId);
		Produit produit = Produit.findProduit(pId);
		ApprovisonementProcess approvisonementProcess = new ApprovisonementProcess(apId);
		approvisonementProcess.setLigneApprovisionements(LigneApprovisionement.findLigneApprovisionementsByApprovisionement(approvisionement).getResultList());
		approvisonementProcess.setProduit(produit);
		uiModel.addAttribute("approvisonementProcess",approvisonementProcess);
		initProcurementViewDependencies(uiModel);
		return "approvisionementprocess/edit";
	}
	@RequestMapping(value = "/{apId}/enregistrer", method = RequestMethod.GET)
	public String enregistrer(@PathVariable("apId") Long apId, Model uiModel) {
		Approvisionement approvisionement = Approvisionement.findApprovisionement(apId);
		ProcessHelper.addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("approvisionement", approvisionement);
		uiModel.addAttribute("itemId",apId);
		return "approvisionementprocess/show";
	}

	@RequestMapping(value = "/{apId}/annuler", method = RequestMethod.GET)
	public String annuler(@PathVariable("apId") Long apId, Model uiModel) {
		Approvisionement approvisionement = Approvisionement.findApprovisionement(apId);
		if (approvisionement.getEtat().equals(Etat.EN_COUR)) {
			approvisionement.remove();
		}
		return "redirect:/";
	}

	
	//@Transactional
	@RequestMapping(value = "/{apId}/cloturer", method = RequestMethod.GET)
	public String cloturer(@PathVariable("apId") Long apId, Model uiModel,HttpServletRequest httpServletRequest) {
		Approvisionement apr = Approvisionement.findApprovisionement(apId);
		if (!apr.isValide(uiModel)) {
			ProcessHelper.addDateTimeFormatPatterns(uiModel);
			uiModel.addAttribute("approvisionement", apr);
			uiModel.addAttribute("itemId",apId);
			return "approvisionementprocess/show";
		}
		new ApprovisonementProcess(apId).closeApprovisionement();
		Approvisionement approvisionement = Approvisionement.findApprovisionement(apId);
		ProcessHelper.addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("approvisionement", approvisionement);
		uiModel.addAttribute("itemId",apId);
		uiModel.addAttribute("appMessages",Arrays.asList("Approvisionement cloturer avec succes ! "));
		return "approvisionementprocess/show";
	}

	// imprime la fiche d 'approvisionenment
	@RequestMapping("/{apId}/printFicheAp/{ficheApId}.pdf")
	public String printFicheApprov( @PathVariable("apId")Long apId, @PathVariable("ficheApId")String ficheApId, Model uiModel){
		Approvisionement approvisionement = Approvisionement.findApprovisionement(apId);

		uiModel.addAttribute("approvisionement", approvisionement);
		return "ficheApprovisionementPdfDocView";

	}


	// imprime la fiche de code bare
	@RequestMapping("/{apId}/printFicheCodeBare/{ficheCodebarId}.pdf")
	public String printFicheCodeBar( @PathVariable("apId")Long apId, @PathVariable("ficheCodebarId")String ficheCodebarId, Model uiModel){
		Approvisionement approvisionement = Approvisionement.findApprovisionement(apId);
		List<LigneApprovisionement> ligneApprivisionement = LigneApprovisionement.findLigneApprovisionementsByApprovisionement(approvisionement).getResultList();
		uiModel.addAttribute("ligneApprivisionement", ligneApprivisionement);
		uiModel.addAttribute("apNumber", approvisionement.getApprovisionementNumber());
		return "ficheCodeBarePdfDocView";

	}



	/*
	 @ModelAttribute("produits")
	    public Collection<Produit> populateProduits() {
	        return Produit.findAllProduits();
	    }*/

	@ModelAttribute("fournisseurs")
	public Collection<Fournisseur> populateFournisseurs() {
		return Fournisseur.findAllFournisseurs();
	}

	@ModelAttribute("sites")
	public Collection<Site> populateSites() {
		return Site.findAllSites();
	}

	@ModelAttribute("devises")
	public Collection<Devise> populateDevises() {
		return Devise.findAllDevises();
	}
	
	public void initProcurementViewDependencies(Model uiModel){
		uiModel.addAttribute("produit", new Produit());
		uiModel.addAttribute("rayons", Rayon.findAllRayons());
		uiModel.addAttribute("filiales", Filiale.findAllFiliales());
		uiModel.addAttribute("tauxmarges", TauxMarge.findAllTauxMarges());
		uiModel.addAttribute("tvas", TVA.findAllTVAS());
		
		
	}

}
