package org.adorsys.adpharma.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.adorsys.adpharma.domain.Configuration;
import org.adorsys.adpharma.beans.process.EtatCreditFinder;
import org.adorsys.adpharma.beans.process.PaiementProcess;
import org.adorsys.adpharma.domain.AdPharmaBaseEntity;
import org.adorsys.adpharma.domain.Caisse;
import org.adorsys.adpharma.domain.Client;
import org.adorsys.adpharma.domain.CommandeClient;
import org.adorsys.adpharma.domain.DetteClient;
import org.adorsys.adpharma.domain.EtatCredits;
import org.adorsys.adpharma.domain.Facture;
import org.adorsys.adpharma.domain.Paiement;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.utils.LocaleUtil;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
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
	
	
	@Resource(name="messageSource")
	ReloadableResourceBundleMessageSource messageSource;
	
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

	@RequestMapping(value = "/BySearch", method = RequestMethod.POST)
	public String Search(EtatCreditFinder etat  , Model uiModel) {
		System.out.println(etat.getClientName());
		uiModel.addAttribute("results", EtatCredits.search(etat.getClientName(), etat.getEtatNumber(), etat.getDateEditionMin(), etat.getDateEditionMax(),etat.getSolder(), etat.getAnnuler(), etat.getEncaisser()).getResultList());
		addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("etatCreditFinder", new EtatCreditFinder());
		return "etatcreditses/search";
	}


	@RequestMapping("/{etatId}/annuler")
	public String anullerEtat(@PathVariable("etatId")Long etatId, Model uiModel){
		EtatCredits etatCredits = 	EtatCredits.findEtatCredits(etatId);
		if (etatCredits!=null) {
			if (etatCredits.getEncaisser()) {
				uiModel.addAttribute("apMessage", messageSource.getMessage("etatcredit_cancel", null, LocaleUtil.getCurrentLocale()));
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
				uiModel.addAttribute("apMessage", messageSource.getMessage("etatcredit_remove_success", null, LocaleUtil.getCurrentLocale()));
			}
			return "caisses/infos";     
		}else {
			uiModel.addAttribute("apMessage", messageSource.getMessage("etatcredit_remove_ok", null, LocaleUtil.getCurrentLocale()));
			return "caisses/infos";     
		}
	}
	
	
	
	// imprime les factures 
	@RequestMapping("/print/{etatId}.pdf")
	public String print(@PathVariable("etatId")Long etatId, Model uiModel){

		EtatCredits etatCredits = EtatCredits.findEtatCredits(etatId);
		etatCredits.initListeDettes();
		uiModel.addAttribute("etatCredits", etatCredits);
		return "etatCreditPdf";

	}

	@RequestMapping("/printfacture/{etatId}.pdf")
	public String printfactureGlobale(  @PathVariable("etatId")Long etatId, Model uiModel){
		EtatCredits etatCredits = EtatCredits.findEtatCredits(etatId);
		etatCredits.initListeDettes();
		uiModel.addAttribute("etatCredits", etatCredits);
		return "facturePayeur";

	}
	
	@RequestMapping("/printfacturedetails/{etatId}.pdf")
	public String printfactureDetaillee(@PathVariable("etatId")Long etatId, Model uiModel){
		EtatCredits etatCredits = EtatCredits.findEtatCredits(etatId);
		etatCredits.initListeDettes();
		uiModel.addAttribute("etatCredits", etatCredits);
		return "factureGlobaleDetaillee";

	}


	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String show(@PathVariable("id") Long id, Model uiModel) {
		addDateTimeFormatPatterns(uiModel);
		EtatCredits etatCredits = EtatCredits.findEtatCredits(id);
		return initShowView(uiModel, etatCredits);
	}

	@RequestMapping(value = "/sendToCash/{id}", method = RequestMethod.GET)
	public String sendToCash(@PathVariable("id") Long id, Model uiModel) {
		addDateTimeFormatPatterns(uiModel);
		EtatCredits etatCredits = EtatCredits.findEtatCredits(id);
		if(etatCredits.getSolder()){
			uiModel.addAttribute("appMessage", messageSource.getMessage("etatcredit_invoice_sendcash_warning", null, LocaleUtil.getCurrentLocale()));
		}else {
			etatCredits.setSendToCash(Boolean.TRUE);
			etatCredits = (EtatCredits) etatCredits.merge();
			uiModel.addAttribute("appMessage", messageSource.getMessage("etatcredit_invoice_send_success", null, LocaleUtil.getCurrentLocale()));
		}
		return initShowView(uiModel, etatCredits);
	}

	@RequestMapping(value = "/removeToCash/{id}", method = RequestMethod.GET)
	public String removeToCash(@PathVariable("id") Long id, Model uiModel) {
		addDateTimeFormatPatterns(uiModel);
		EtatCredits etatCredits = EtatCredits.findEtatCredits(id);
		etatCredits.setSendToCash(Boolean.FALSE);
		EtatCredits merge = (EtatCredits) etatCredits.merge();
		uiModel.addAttribute("appMessage", messageSource.getMessage("etatcredit_invoice_remove_success", null, LocaleUtil.getCurrentLocale()));
		return initShowView(uiModel, merge);
	}

	@RequestMapping(value = "/getDetteToPay", method = RequestMethod.GET)
	public String getDetteToPay(Model uiModel) {
		uiModel.addAttribute("search",false);
		Caisse openCaisse = PaiementProcess.getMyOpenCaisse(SecurityUtil.getPharmaUser());
		if (openCaisse == null) {
			uiModel.addAttribute("apMessage", messageSource.getMessage("payment_cash_warning", null, LocaleUtil.getCurrentLocale()));
			return "caisses/infos";
		}else {
			List<EtatCredits> list = EtatCredits.findEtatCreditsSendToCash().getResultList();
			uiModel.addAttribute("etatcreditses", list);
			return "etatcreditses/list";
		}
	}

	@Transactional
	@RequestMapping(value = "/encaisser/{id}", method = RequestMethod.POST)
	public String encaisser( Paiement paiement,@PathVariable("id") Long id, Model uiModel ,HttpServletRequest httpServletRequest) {
		addDateTimeFormatPatterns(uiModel);
		EtatCredits etatCredits = EtatCredits.findEtatCredits(id);
		Client client = etatCredits.getClient();
		Configuration configuration = Configuration.findConfiguration(new Long(1)) ;
		if(configuration.getOnlyCashReceiveCreditPay()){
			Caisse myOpenCaisse = PaiementProcess.getMyOpenCaisse(SecurityUtil.getPharmaUser());
			if(myOpenCaisse==null){
				uiModel.addAttribute("apMessage", messageSource.getMessage("payment_cash_warning", null, LocaleUtil.getCurrentLocale()));
			}else {
				etatCredits.encaisser(paiement, myOpenCaisse);
				myOpenCaisse.merge();
				etatCredits.setSendToCash(Boolean.FALSE);
				etatCredits.merge();
				uiModel.addAttribute("apMessage", messageSource.getMessage("etatcredit_payment_success", null, LocaleUtil.getCurrentLocale()));
			}
			client.calculeTotalDette();
			client.merge();
			return  initShowView(uiModel, etatCredits);
		}
		if (etatCredits.getSolder()) {
			uiModel.addAttribute("apMessage", messageSource.getMessage("etatcredit_payment_warning", null, LocaleUtil.getCurrentLocale()));
			return  initShowView(uiModel, etatCredits);
		}

		if (etatCredits.getAnnuler()) {
			uiModel.addAttribute("apMessage", messageSource.getMessage("etatcredit_payment_cancelled", null, LocaleUtil.getCurrentLocale()));
			return initShowView(uiModel, etatCredits);
		}
		etatCredits.encaisser(paiement);
		etatCredits.merge();
		client.calculeTotalDette();
		client.merge();
		uiModel.addAttribute("apMessage", messageSource.getMessage("etatcredit_payment_success", null, LocaleUtil.getCurrentLocale()));
		return   initShowView(uiModel, etatCredits);

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
		uiModel.addAttribute("search",true);
		if("".equals(name)){
			Integer page = 1;
			Integer size = 50;
			int sizeNo = size == null ? 10 : size.intValue();
			uiModel.addAttribute("etatcreditses", EtatCredits.findEtatCreditsEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
			float nrOfPages = (float) EtatCredits.countEtatCreditses() / sizeNo;
			uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
		}else{
			List<EtatCredits> list = EtatCredits.findEtatCreditsByNomClientLike(name).setMaxResults(50).getResultList();
			uiModel.addAttribute("etatcreditses", list);
		}
		return "etatcreditses/list";
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
