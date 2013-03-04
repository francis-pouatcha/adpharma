package org.adorsys.adpharma.web;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;
import org.adorsys.adpharma.utils.DateConfig;
import org.adorsys.adpharma.utils.DateConfigPeriod;
import org.adorsys.adpharma.utils.PharmaDateUtil;

import javassist.expr.NewArray;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.adorsys.adpharma.beans.Decaissement;
import org.adorsys.adpharma.beans.process.ChiffreAffaireBean;
import org.adorsys.adpharma.beans.process.SessionBean;
import org.adorsys.adpharma.domain.AdPharmaBaseEntity;
import org.adorsys.adpharma.domain.AvoirClient;
import org.adorsys.adpharma.domain.Caisse;
import org.adorsys.adpharma.domain.CommandeClient;
import org.adorsys.adpharma.domain.Configuration;
import org.adorsys.adpharma.domain.Etat;
import org.adorsys.adpharma.domain.Filiale;
import org.adorsys.adpharma.domain.Genre;
import org.adorsys.adpharma.domain.Inventaire;
import org.adorsys.adpharma.domain.PharmaUser;
import org.adorsys.adpharma.domain.TypeCommande;
import org.adorsys.adpharma.domain.TypeDecaissement;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.services.DisbursementService;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.adorsys.adpharma.utils.ProcessHelper ;
import org.apache.log4j.spi.LoggerFactory;
@RooWebScaffold(path = "caisses", formBackingObject = Caisse.class)
@RequestMapping("/caisses")
@Controller
public class CaisseController {

	@Autowired
	private DisbursementService decaissementService;

	@RequestMapping(params = { "find=BySearch", "form" }, method = RequestMethod.GET)
	public String Search(Model uiModel) {
		uiModel.addAttribute("caisse", new Caisse());
		return "caisses/search";
		
	}
	
	// Formualire de decaissement
	@RequestMapping(value="/decaissement", params="form", method=RequestMethod.GET)
	public String decaissementForm(Model uiModel){
		uiModel.addAttribute("decaissement", new Decaissement());	
		return "caisses/decaissement";
	}
	
	
	// Action de decaissement
	@RequestMapping(value="/decaisser", method=RequestMethod.GET)
	public String decaissement(@Valid Decaissement decaissement, BindingResult bindingResult, Model uiModel){
		Caisse caisse=null;
		BigDecimal currentAmount=BigDecimal.ZERO;
		try {
			caisse = Decaissement.validateDisbursement(bindingResult, decaissement);
			currentAmount=caisse.getTotalCash();
		} catch (Exception e) {
			 System.out.println(e.getMessage());
		}
		
		if(bindingResult.hasErrors()){
			uiModel.addAttribute("errors", "erreurs!");
			return "caisses/decaissement";
		}
		
		decaissementService.makeDisbursement(caisse, decaissement.getMontantDecaissement(), decaissement.getNote());
		if(decaissement.getTypeDecaissement().equals(TypeDecaissement.CONSOMMATION_AVOIR.toString())){
			AvoirClient avoirClient = AvoirClient.findAvoirClientsByNumeroEquals(decaissement.getNumAvoir()).getSingleResult();
			avoirClient.setMontant(avoirClient.getMontant().subtract(decaissement.getMontantDecaissement()));
			avoirClient.setMontantUtilise(avoirClient.getMontantUtilise().add(decaissement.getMontantDecaissement()));
			avoirClient.merge();
		}
		uiModel.addAttribute("sucess", "Votre caisse avait un montant de: "+currentAmount+" ,vous avez decaisse la somme de: "+decaissement.getMontantDecaissement()+" ,le solde de votre caisse est de: "+caisse.getTotalCash());
		
		return "caisses/decaissement";
		
	}
	

	@RequestMapping(value = "/BySearch", method = RequestMethod.GET)
	public String Search(Caisse caisse , Model uiModel) {
		uiModel.addAttribute("results", Caisse.search(caisse.getCaisseNumber(), caisse.getCaissier(), caisse.getDateOuverture(), caisse.getDateFemeture(), caisse.getCaisseOuverte()).getResultList());
		addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("caisse", caisse);
		return "caisses/search";
	}

	@RequestMapping(params = { "find=chiffreAffaire", "form" }, method = RequestMethod.GET)
	public String chiffreAffaireForm(Model uiModel) {
		uiModel.addAttribute("filiales", ProcessHelper.populateFiliale());
		uiModel.addAttribute("chiffreAffaire", new ChiffreAffaireBean());

		return "caisses/chiffreaffaire";
	}

	@RequestMapping(value = "/chiffreAffaire", method = RequestMethod.GET)
	public String chiffreAffaire(ChiffreAffaireBean chiffreAffaire , Model uiModel) {
		uiModel.addAttribute("filiales", ProcessHelper.populateFiliale());
		uiModel.addAttribute("chiffreAffaire", chiffreAffaire);
		List<Caisse> caisses = Caisse.findCaissesByDateOuvertureBetween(chiffreAffaire.getDateDebut(), chiffreAffaire.getDateFin()).getResultList();
		if (caisses.isEmpty()) {
			uiModel.addAttribute("appMessage", "aucun etat trouve !");
			return "caisses/chiffreaffaire";
		}else {
			uiModel.addAttribute("caisses", caisses);
			uiModel.addAttribute("periode",PharmaDateUtil.format(chiffreAffaire.getDateDebut(), "dd-MM-yyyy HH:mm")+" Au "+ PharmaDateUtil.format(chiffreAffaire.getDateFin(), "dd-MM-yyyy HH:mm"));
			return "chiffreAffaireBean";
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	public String create(@Valid Caisse caisse, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		PharmaUser pharmaUser = SecurityUtil.getPharmaUser();
		if (bindingResult.hasErrors()) {
			uiModel.addAttribute("caisse", caisse);
			addDateTimeFormatPatterns(uiModel);
			return "caisses/create";
		}
		List<Caisse> list = Caisse.findCaissesByCaisseOuverteNotAndCaissier(Boolean.FALSE,pharmaUser ).getResultList();
		if (!list.isEmpty()) {
			uiModel.addAttribute("apMessage", "Vous avez deja une caisse ouverte veuillez la fermer avant . !  ");
			return "caisses/infos";
		}
		uiModel.asMap().clear();
		caisse.persist();
		return "redirect:/caisses/" + encodeUrlPathSegment(caisse.getId().toString(), httpServletRequest);
	}

	@RequestMapping(value = "/ouvrirCaisse", params = "form", method = RequestMethod.GET)
	public String createForm(Model uiModel) {
		PharmaUser pharmaUser = SecurityUtil.getPharmaUser();
		List<Caisse> list = Caisse.findCaissesByCaisseOuverteNotAndCaissier(Boolean.FALSE,pharmaUser ).getResultList();
		Caisse caisse = new Caisse() ;
		if (!list.isEmpty()) {
			uiModel.addAttribute("apMessage", "Vous avez deja une caisse ouverte veuillez la fermer avant . !  ");
			return "caisses/infos";

		}else {
			caisse = new Caisse(pharmaUser);
		}

		uiModel.addAttribute("caisse", caisse);
		addDateTimeFormatPatterns(uiModel);
		return "caisses/create";
	}

	@RequestMapping(value = "/fermerCaisse", method = RequestMethod.GET)
	public String fermerCaisse(Model uiModel, HttpServletRequest httpServletRequest) {
		List<Caisse> list = Caisse.findCaissesByCaisseOuverteAndCaissier(Boolean.TRUE, SecurityUtil.getPharmaUser()).getResultList();
		Caisse caisse = list.isEmpty() ?null :list.iterator().next();
		SessionBean sessionBean =	 (SessionBean) httpServletRequest.getSession().getAttribute("sessionBean") ;
		Configuration configuration = sessionBean.getConfiguration();
		List<Long> unpaySalesNumber = CommandeClient.findUnpayCloseSales(Etat.CLOS, false, false, TypeCommande.VENTE_PROFORMAT);
		if(caisse != null){
			if(configuration.getCloseCashUnpaySale()){
				closeCash(uiModel,caisse);
			}else {
				Long number = unpaySalesNumber.iterator().next();
				if(number >0){
					uiModel.addAttribute("apMessage", "Impossible de Fermer La Caisse Vous Avez : "+number +" Factures en attente d'encaissement ");
				}else {
					closeCash(uiModel,caisse);
				}

			}
		}else {
			uiModel.addAttribute("apMessage", "Vous n'avez aucune caisse ouverte veuillez en ouvrir une ! ");
		}

		return "caisses/infos";

	}

	public void closeCash(Model uiModel,Caisse caisse){
		caisse.setCaisseOuverte(Boolean.FALSE);
		caisse.setDateFemeture(new Date()) ;
		caisse.setFermerPar(SecurityUtil.getPharmaUser());
		Caisse merge = (Caisse) caisse.merge() ;
		addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("caisse",merge);
		uiModel.addAttribute("itemId", merge.getId());
		uiModel.addAttribute("apMessage", "caisse fermee avec succes ! ");
	}

	@RequestMapping(value = "/fermerCaisse/{caisseId}", method = RequestMethod.GET)
	public String fermerCaisseById(@PathVariable("caisseId")  Long caisseId , Model uiModel, HttpServletRequest httpServletRequest) {
		Caisse caisse = Caisse.findCaisse(caisseId);
		caisse.setCaisseOuverte(Boolean.FALSE);
		caisse.setDateFemeture(new Date()) ;
		caisse.setFermerPar(SecurityUtil.getPharmaUser());
		Caisse merge = (Caisse) caisse.merge() ;
		addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("caisse",merge);
		uiModel.addAttribute("itemId", merge.getId());
		uiModel.addAttribute("apMessage", "caisse fermee avec succes ! ");
		return "caisses/show";		

	}

	@RequestMapping(params = { "find=ByEtatCaisse", "form" }, method = RequestMethod.GET)
	public String ByEtatCaisseForm(Model uiModel) {
		ProcessHelper.addDateTimeFormatPatterns(uiModel);
		return "caisses/ByEtatCaisse";
	}

	@RequestMapping(params = "find=ByEtatCaisse", method = RequestMethod.GET)
	public String byEtatCaisse(@RequestParam("minDateOuverture") @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") Date minDateOuverture, @RequestParam("maxDateOuverture") @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") Date maxDateOuverture, Model uiModel) {
		List<Caisse> caisses = Caisse.findCaissesByDateOuvertureBetween(minDateOuverture, maxDateOuverture).getResultList();
		if (caisses.isEmpty()) {
			uiModel.addAttribute("appMessage", "aucun etat trouve !");
			ProcessHelper.addDateTimeFormatPatterns(uiModel);
			return "caisses/ByEtatCaisse";
		}else {
			uiModel.addAttribute("caisses", caisses);
			uiModel.addAttribute("periode",PharmaDateUtil.format(minDateOuverture, "dd-MM-yyyy HH:mm")+" Au "+ PharmaDateUtil.format(maxDateOuverture, "dd-MM-yyyy HH:mm"));
			return "bordereauCaissePdfDocView";
		}
	}
	
	
	@RequestMapping(params = "find=etatJournalier", method = RequestMethod.GET)
	public String etatJournalier(Model uiModel) {
		DateConfigPeriod period = DateConfig.getBegingEndOfDay(new Date());
		List<Caisse> caisses = Caisse.findCaissesByDateOuvertureBetween(period.getBegin(), period.getEnd()).getResultList();
		if (caisses.isEmpty()) {
			uiModel.addAttribute("apMessage", "aucun etat trouve  !");
			return "caisses/infos";
		}else {
			uiModel.addAttribute("caisses", caisses);
			uiModel.addAttribute("periode",PharmaDateUtil.format(period.getBegin(), "dd-MM-yyyy HH:mm")+" Au "+ PharmaDateUtil.format(period.getEnd(), "dd-MM-yyyy HH:mm"));
			return "bordereauCaissePdfDocView";
		}
	}



	// imprime les bordereaux de Caisse 
	@RequestMapping("/{caisseId}/print/{caisseNumber}.pdf")
	public String print(@PathVariable("caisseId")Long caisseId, Model uiModel){
		Caisse caisse = Caisse.findCaisse(caisseId);
		if (caisse!=null) {
			uiModel.addAttribute("caisse", caisse);
			return "bordereauCaissePdfDocView";
		}else {
			uiModel.addAttribute("apMessage", "un problem est survenu pendant l'operation veuillez contacter ADORSYS  ! ");
			return "caisses/infos";
		}


	}

	@ModelAttribute("pharmausers")
	public Collection<PharmaUser> populatePharmaUsers() {
		PharmaUser pharmaUser =	new PharmaUser();
		pharmaUser.setGender(Genre.Neutre);
		pharmaUser.setFirstName("ALL");
		pharmaUser.setLastName("USERS");
		pharmaUser.setId(new Long(0));
		Collection<PharmaUser> pharmaUsers =	new ArrayList<PharmaUser>();
		pharmaUsers.add(pharmaUser);
		pharmaUsers.addAll(PharmaUser.findAllPharmaUsers());
		return pharmaUsers;
	}

	@ModelAttribute("caisses")
	public Collection<Caisse> populateCaisses() {
		return new ArrayList<Caisse>();
	}
	
	@ModelAttribute("typesDecaissements")
	public Collection<TypeDecaissement> populateTypeDecaissement(){
		return Arrays.asList(TypeDecaissement.class.getEnumConstants());
	}
	
	
}
