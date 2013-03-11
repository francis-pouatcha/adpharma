package org.adorsys.adpharma.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.adorsys.adpharma.beans.process.EtatCreditFinder;
import org.adorsys.adpharma.domain.AdPharmaBaseEntity;
import org.adorsys.adpharma.domain.Client;
import org.adorsys.adpharma.domain.CommandeClient;
import org.adorsys.adpharma.domain.DetteClient;
import org.adorsys.adpharma.domain.EtatCredits;
import org.adorsys.adpharma.domain.Paiement;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RooWebScaffold(path = "etatcreditses", formBackingObject = EtatCredits.class)
@RequestMapping("/etatcreditses")
@Controller
public class EtatCreditsController {
	//@Transactional
	@RequestMapping(method = RequestMethod.POST)
	public String create(@Valid EtatCredits etatCredits, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		Client findClient = Client.findClient(etatCredits.getClientId());
		etatCredits.setClient(findClient);
		etatCredits.validate(bindingResult);
		if (bindingResult.hasErrors()) {
			uiModel.addAttribute("etatCredits", etatCredits);
			addDateTimeFormatPatterns(uiModel);
			return "etatcreditses/create";
		}
		if (etatCredits.getConsommerAvoir()) {
			etatCredits.consommerAvoir();
		}
		etatCredits.computeAmount();
		etatCredits.persist(); 
		uiModel.asMap().clear();
		List<DetteClient> listeDettes =  etatCredits.getListeDettes();
		for (DetteClient detteClient : listeDettes) {
			detteClient.setEtatCredit(etatCredits);
			detteClient.merge();

		}
		return "redirect:/etatcreditses/" + encodeUrlPathSegment(etatCredits.getId().toString(), httpServletRequest);
	}

	@RequestMapping(params = { "find=BySearch", "form" }, method = RequestMethod.GET)
	public String Search(Model uiModel) {
		DetteClient detteClient = new DetteClient();
		detteClient.setDateCreation(null);
		uiModel.addAttribute("etatCreditFinder",new EtatCreditFinder() );
		return "etatcreditses/search";
	}

	@RequestMapping(value = "/BySearch", method = RequestMethod.GET)
	public String Search(EtatCreditFinder etat  , Model uiModel) {
		uiModel.addAttribute("results", EtatCredits.search(etat.getClientName(), etat.getEtatNumber(), etat.getDateEditionMin(), etat.getDateEditionMax(),etat.getSolder(), etat.getAnnuler(), etat.getEncaisser()).getResultList());
		addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("etatCreditFinder", new EtatCredits());

		return "etatcreditses/search";
	}


	@RequestMapping("/{etatId}/annuler")
	public String anullerEtat(@PathVariable("etatId")Long etatId, Model uiModel){
		EtatCredits etatCredits = 	EtatCredits.findEtatCredits(etatId);
		if (etatCredits!=null) {

			if (etatCredits.getEncaisser()) {
				uiModel.addAttribute("apMessage", "IMPOSSIBLE D'ANNULER L' ETAT EST DEJA ENCAISSE! ");
			}else {
				etatCredits.initListDetteWhithoutSort();
				List<DetteClient> listeDettes = etatCredits.getListeDettes();
				if (!listeDettes.isEmpty()) {
					for (DetteClient detteClient : listeDettes) {
						detteClient.setEtatCredit(null);
						detteClient.merge();
						detteClient.flush();

					}
				}
				etatCredits.remove();
				uiModel.addAttribute("apMessage", "ETAT SUPRIME AVEC SUCCES ! ");

			}
			return "caisses/infos";     
		}else {
			uiModel.addAttribute("apMessage", "ETAT DEJA SUPRIME  ! ");
			return "caisses/infos";     

		}
	}
	// imprime les factures 
	@RequestMapping("/print/{etatId}.pdf")
	public String print(  @PathVariable("etatId")Long etatId, Model uiModel){

		EtatCredits etatCredits = EtatCredits.findEtatCredits(etatId);
		etatCredits.initListeDettes();
		uiModel.addAttribute("etatCredits", etatCredits);
		return "etatCreditPdf";

	}

	@RequestMapping("/printfacture/{etatId}.pdf")
	public String printfacuture(  @PathVariable("etatId")Long etatId, Model uiModel){
		EtatCredits etatCredits = EtatCredits.findEtatCredits(etatId);
		etatCredits.initListeDettes();
		uiModel.addAttribute("etatCredits", etatCredits);
		return "factureGlobalePayeur";

	}


	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String show(@PathVariable("id") Long id, Model uiModel) {
		addDateTimeFormatPatterns(uiModel);
		EtatCredits etatCredits = EtatCredits.findEtatCredits(id);
		return initShowView(uiModel, etatCredits);
	}

	//@Transactional
	@RequestMapping(value = "/encaisser/{id}", method = RequestMethod.POST)
	public String encaisser( Paiement paiement,@PathVariable("id") Long id, Model uiModel ,HttpServletRequest httpServletRequest) {
		addDateTimeFormatPatterns(uiModel);
		EtatCredits etatCredits = EtatCredits.findEtatCredits(id);
		if (etatCredits.getSolder()) {
			uiModel.addAttribute("apMessage","Impossible d'encaisse Etat deja Solde !" );
			return  initShowView(uiModel, etatCredits);
		}

		if (etatCredits.getAnnuler()) {
			uiModel.addAttribute("apMessage","Impossible d'encaisse Etat Annuler !" );
			return initShowView(uiModel, etatCredits);
		}
		etatCredits.encaisser(paiement);
		etatCredits.merge();
		return "redirect:/etatcreditses/" + encodeUrlPathSegment(etatCredits.getId().toString(), httpServletRequest);

	}

	public String initShowView( Model uiModel, EtatCredits etatCredits){

		etatCredits.initListeDettes();
		Paiement paiement = new Paiement();
		paiement.setMontant(etatCredits.getReste());
		uiModel.addAttribute("etatcredits",etatCredits);
		uiModel.addAttribute("itemId", etatCredits.getId());
		uiModel.addAttribute("paiement",paiement );
		return "etatcreditses/show";
	} 

	
	@RequestMapping(value = "/searchCredit", method = RequestMethod.GET)
	public String search(@RequestParam("name") String  name,  Model uiModel) {
		
		if("".equals(name)){
			Integer page = 1;
			Integer size = 50;
			int sizeNo = size == null ? 10 : size.intValue();
            uiModel.addAttribute("etatcredits", EtatCredits.findEtatCreditsEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) EtatCredits.countEtatCreditses() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
		}else{
				List<EtatCredits> list = EtatCredits.findEtatCreditsByNomClientLike(name).setMaxResults(50).getResultList();
				uiModel.addAttribute("etatcredits", list);
		}
		return "etatcredits/list";
	}


	@ModelAttribute("clients")
	public Collection<Client> populateClients() {
		// return Client.findAllClients();
		return new ArrayList<Client>();
	}

	@ModelAttribute("detteclients")
	public Collection<DetteClient> populateDetteClients() {
		// return DetteClient.findAllDetteClients();
		return	new ArrayList<DetteClient>();
	}

	@ModelAttribute("etatcreditses")
	public Collection<EtatCredits> populateEtatCreditses() {
		// return EtatCredits.findAllEtatCreditses();
		return	new ArrayList<EtatCredits>();
	}

}
