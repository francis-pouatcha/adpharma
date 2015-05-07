package org.adorsys.adpharma.web;
/**
 * @author adorsys-clovis
 *
 */
import java.awt.Desktop;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.adorsys.adpharma.beans.PrintService;
import org.adorsys.adpharma.beans.process.PaiementProcess;
import org.adorsys.adpharma.domain.AvoirClient;
import org.adorsys.adpharma.domain.Caisse;
import org.adorsys.adpharma.domain.Client;
import org.adorsys.adpharma.domain.CommandeClient;
import org.adorsys.adpharma.domain.CommandeFournisseur;
import org.adorsys.adpharma.domain.Configuration;
import org.adorsys.adpharma.domain.DestinationMvt;
import org.adorsys.adpharma.domain.DetteClient;
import org.adorsys.adpharma.domain.Etat;
import org.adorsys.adpharma.domain.Facture;
import org.adorsys.adpharma.domain.Fournisseur;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.LigneCmdClient;
import org.adorsys.adpharma.domain.LigneCmdFournisseur;
import org.adorsys.adpharma.domain.ModeSelection;
import org.adorsys.adpharma.domain.MouvementStock;
import org.adorsys.adpharma.domain.OperationCaisse;
import org.adorsys.adpharma.domain.Paiement;
import org.adorsys.adpharma.domain.PharmaUser;
import org.adorsys.adpharma.domain.PrinterConfiguration;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.QuiPaye;
import org.adorsys.adpharma.domain.Site;
import org.adorsys.adpharma.domain.TypeCommande;
import org.adorsys.adpharma.domain.TypeFacture;
import org.adorsys.adpharma.domain.TypeMouvement;
import org.adorsys.adpharma.domain.TypeOpCaisse;
import org.adorsys.adpharma.domain.TypePaiement;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.utils.LocaleUtil;
import org.adorsys.adpharma.utils.ProcessHelper;
import org.adorsys.adpharma.utils.TicketPrinter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/paiementprocess")
@Controller
public class PaiementProcessController {
	Logger logger = LoggerFactory.getLogger(getClass());

	@Resource(name="messageSource")
	ReloadableResourceBundleMessageSource messageSource;

	@RequestMapping(value="/getUnpayCloseSalesNumber/ByAjax", method = RequestMethod.GET)
	@ResponseBody
	public Long getUnpayCloseSalesNumber() {
		Long unpayCloseSales = Long.valueOf(0) ; //CommandeClient.findUnpayCloseSales(Etat.CLOS, Boolean.FALSE,  Boolean.FALSE, TypeCommande.VENTE_PROFORMAT);
		List<Facture> factureResult = Facture.findFacturesByCaisseAndEncaisserNot(null, Boolean.TRUE).getResultList();
		if(!factureResult.isEmpty())
			unpayCloseSales = Long.valueOf(factureResult.size());
		return  unpayCloseSales ;
	}
	@RequestMapping(value = "/editPaiement", method = RequestMethod.GET)
	public String editPaiement( Model uiModel, HttpServletRequest httpServletRequest) {

		Caisse openCaisse = PaiementProcess.getMyOpenCaisse(SecurityUtil.getPharmaUser());
		if (openCaisse == null) {
			uiModel.addAttribute("apMessage", messageSource.getMessage("payment_cash_warning", null, LocaleUtil.getCurrentLocale()));
			return "caisses/infos";
		}else {

			List<Facture> factureResult = Facture.findFacturesByCaisseAndEncaisserNot(null, Boolean.TRUE).getResultList();
			PaiementProcess paiementProcess = new PaiementProcess(openCaisse);
			paiementProcess.setFactureResult(factureResult);
			uiModel.addAttribute("paiementProcess",paiementProcess);
			return "paiementprocess/rechercheFacture";

		}
	}


	@RequestMapping(value = "/encaisser" ,params = "form", method = RequestMethod.GET)
	public String encaisserPaiementForm( Model uiModel, HttpServletRequest httpServletRequest) {

		Caisse openCaisse = PaiementProcess.getMyOpenCaisse(SecurityUtil.getPharmaUser());
		if (openCaisse == null) {
			uiModel.addAttribute("apMessage", messageSource.getMessage("payment_cash_warning", null, LocaleUtil.getCurrentLocale()));
			return "caisses/infos";
		}else {

			PaiementProcess paiementProcess = new PaiementProcess(openCaisse);
			uiModel.addAttribute("paiementProcess",paiementProcess);
			Paiement paiement = new Paiement() ;
			uiModel.addAttribute("paiement",paiement);
			return "paiementprocess/encaisserPaiement";

		}
	}

	@RequestMapping(value = "/findFactureByFactureNumber" , method = RequestMethod.POST)
	public String findInvoice(@RequestParam("invoiceNumber") String invoiceNumber , Model uiModel, HttpServletRequest httpServletRequest) {
		List<Facture> invoice = Facture.findFacturesByFactureNumberEquals("FAC-"+ StringUtils.remove(invoiceNumber, "FAC-")).getResultList();
		if (invoice.iterator().hasNext()) {
			return "redirect:/paiementprocess/selectFacture/" + ProcessHelper.encodeUrlPathSegment(invoice.iterator().next().getId().toString(), httpServletRequest);

		}else {
			uiModel.addAttribute("apMessage", messageSource.getMessage("payment_cash_invoice_notfound", null, LocaleUtil.getCurrentLocale()));
			return "paiementprocess/encaisserPaiement";
		}
	}



	//@Transactional
	@RequestMapping(value = "/encaisser/{factureId}" , method = RequestMethod.POST)
	public String encaisserPaiement(@PathVariable("factureId")Long factureId,@Valid Paiement paiement ,BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		Caisse openCaisse = PaiementProcess.getMyOpenCaisse(SecurityUtil.getPharmaUser());
		Facture facture = Facture.findFacture(factureId);
		if (facture == null) {
			uiModel.addAttribute("apMessage", messageSource.getMessage("payment_cash_cancel_order", null, LocaleUtil.getCurrentLocale()));
		}else {
			if (facture.estSolder()) {
				uiModel.addAttribute("apMessage", messageSource.getMessage("payment_cash_invoice", null, LocaleUtil.getCurrentLocale()));

			}else if (facture.getEncaisser()) {
				uiModel.addAttribute("apMessage", messageSource.getMessage("payment_cash_invoice_boxed", null, LocaleUtil.getCurrentLocale()));

			}else{

				paiement.setFacture(facture);
				paiement.definirPayeur();
				PaiementProcess paiementProcess = new PaiementProcess(openCaisse ,facture);
				if (paiement.validate(uiModel , paiementProcess.getDetteclient())) {
					paiementProcess.setShowPostForm(true);
					uiModel.addAttribute("paiementProcess",paiementProcess);
					uiModel.addAttribute("paiement",paiement);
					uiModel.addAttribute("cash",facture.isCashPaiement());
					uiModel.addAttribute("typepaiements",populateTypePaiements(facture.getCommande()));
					return "paiementprocess/encaisserPaiement";
				}
				paiement.setCaisse(openCaisse);
				paiement.persist();
			    encaisser(facture, openCaisse, paiement);
				paiement.merge();
				try {
					gererPreparationAutomatisee(facture);
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
				paiementProcess.setShowDetailForm(true);
				uiModel.addAttribute("paiementProcess",paiementProcess);
				uiModel.addAttribute("paiement",paiement);
				try {
					//String remoteAddr = httpServletRequest.getRemoteAddr();
					//List<PrinterConfiguration> printers = PrinterConfiguration.findPrinterConfigurationsByComputerAdresseEquals(remoteAddr).getResultList();
					//String printerName = printers.isEmpty()?null:printers.iterator().next().getPrinterName();
					TicketPrinter.buildTicket(paiement,Boolean.TRUE);
					PrintService.silentPrint(TicketPrinter.TICKET_FILE);
				} catch (Exception e) {
					logger.error("erreur d'impression !");
					e.printStackTrace();
				}
			}
		}
		return "paiementprocess/encaisserPaiement";

	}
	void gererPreparationAutomatisee(Facture facture){
		Configuration configuration = Configuration.findConfiguration(new Long(1));
		if(configuration.getPreparationAutomatisee() == null || configuration.getPreparationAutomatisee() == Boolean.FALSE){
			return;
		}
		Set<LigneCmdClient> ligneCmdClients = facture.getCommande().getLineCommande();
		for (LigneCmdClient ligneCmdClient : ligneCmdClients) {
			Produit produit = ligneCmdClient.getProduit().getProduit();
			if(!produit.isCommander()){
				continue;
			}
			CommandeFournisseur cmdFournEnCours = getPreparationAutomatiseeEnCours();
			if(cmdFournEnCours == null){
				cmdFournEnCours = creerPreparationAutomatisee();
			}
			ajouterLignePreparationAutomatisee(cmdFournEnCours, produit);
		}
	}

	LigneCmdFournisseur ajouterLignePreparationAutomatisee(CommandeFournisseur commandeFournisseur,Produit produit){
		LigneCmdFournisseur ligneCmdFournisseur = new LigneCmdFournisseur();
		BigDecimal pa = produit.getPrixAchatSTock();
		BigDecimal pv = produit.getPrixVenteStock();
		BigInteger qte = produit.getQteCommande();
		pa = pa == null ? BigDecimal.ZERO : pa;
		pv = pv == null ? BigDecimal.ZERO : pv;
		qte = qte == null ? BigInteger.ONE : qte;
		BigDecimal pt = pa.multiply(new BigDecimal(qte));
		if (commandeFournisseur.contientProduit(produit)) {

		} else {
			LigneCmdFournisseur line = new LigneCmdFournisseur();
			line.setCommande(commandeFournisseur);
			line.setPrixAchatMin(pa);
			line.setPrixAVenteMin(pv);
			line.setProduit(produit);
			line.setQuantiteCommande(qte);
			line.setPrixAchatTotal(pt);
			line.persist();
			commandeFournisseur.increaseMontant(pt);
			commandeFournisseur.merge();
		}
		return ligneCmdFournisseur;
	}

	private CommandeFournisseur creerPreparationAutomatisee() {
		CommandeFournisseur commandeFournisseur = new CommandeFournisseur();
		commandeFournisseur.setModeDeSelection(ModeSelection.AUTOMATISEE);
		commandeFournisseur.setEtatCmd(Etat.EN_COUR);
		commandeFournisseur.setFournisseur(Fournisseur.findFournisseur(new Long(1)));
		commandeFournisseur.setDateLimiteLivraison(DateUtils.addDays(new Date(), 2));
		commandeFournisseur.setSite(Site.findSite(new Long(1)));
		commandeFournisseur.persist();
		return commandeFournisseur;
	}
	private CommandeFournisseur getPreparationAutomatiseeEnCours() {
		List<CommandeFournisseur> preparationAutomatiseeEnCours = CommandeFournisseur.findPreparationAutomatiseeEnCours();
		if(preparationAutomatiseeEnCours == null ||  preparationAutomatiseeEnCours.isEmpty()){
			return null;
		}
		return preparationAutomatiseeEnCours.iterator().next();
	}
	@RequestMapping(value = "/selectFacture/{factureId}" ,method = RequestMethod.GET)
	public String selectFacture(@PathVariable("factureId") Long factureId ,  Model uiModel,  HttpServletRequest httpServletRequest) {

		Caisse openCaisse = PaiementProcess.getMyOpenCaisse(SecurityUtil.getPharmaUser());
		if (openCaisse == null) {
			uiModel.addAttribute("apMessage", messageSource.getMessage("payment_cash_warning", null, LocaleUtil.getCurrentLocale()));
			return "caisses/infos";
		}else {
			Facture facture = Facture.findFacture(factureId);
			PaiementProcess paiementProcess = new PaiementProcess(openCaisse ,facture);
			uiModel.addAttribute("paiementProcess",paiementProcess);
			if (facture.getEncaisser()) {
				uiModel.addAttribute("apMessage", messageSource.getMessage("payment_cash_invoice_boxed", null, LocaleUtil.getCurrentLocale()));
				return "paiementprocess/encaisserPaiement";
			}
			if (facture.estSolder()) {
				uiModel.addAttribute("apMessage", messageSource.getMessage("payment_cash_invoice", null, LocaleUtil.getCurrentLocale()));
				return "paiementprocess/encaisserPaiement";

			}
			if (facture.getTypeFacture().equals(TypeFacture.PROFORMAT)) {
				uiModel.addAttribute("apMessage", messageSource.getMessage("payment_cash_invoice_proformat", null, LocaleUtil.getCurrentLocale()));
				return "paiementprocess/encaisserPaiement";

			}
			paiementProcess.setShowPostForm(true);
			uiModel.addAttribute("paiementProcess",paiementProcess);
			Paiement paiement = new Paiement() ;
			initPaiementView(facture, paiement, uiModel);
			return "paiementprocess/encaisserPaiement";

		}
	}

	@RequestMapping(value = "/factureSuivante/{factureId}" ,method = RequestMethod.GET)
	public String factureSuivante(@PathVariable("factureId") Long factureId,Model uiModel,  HttpServletRequest httpServletRequest) {
		List<Facture> factureResult ;
		Caisse openCaisse = PaiementProcess.getMyOpenCaisse(SecurityUtil.getPharmaUser());
		if (openCaisse == null) {
			uiModel.addAttribute("apMessage", messageSource.getMessage("payment_cash_warning", null, LocaleUtil.getCurrentLocale()));
			return "caisses/infos";
		}
		factureResult = Facture.findFacturesByCaisseAndEncaisserNot(null, Boolean.TRUE).getResultList();
		if (factureResult.isEmpty()) {
			uiModel.addAttribute("apMessage", messageSource.getMessage("payment_cash_invoice_wait", null, LocaleUtil.getCurrentLocale()));
			return "paiementprocess/encaisserPaiement";

		}
		if (factureResult.size() >1) {
			Facture facture = Facture.findFacture(factureId);
			if (factureResult.contains(facture)) {
				int index = factureResult.indexOf(facture);
				index = index+1;
				if (index <factureResult.size()) {
					Facture facture2 = factureResult.get(index);
					return "redirect:/paiementprocess/selectFacture/"+ ProcessHelper.encodeUrlPathSegment(facture2.getId().toString(), httpServletRequest);

				}
			}
		}

		return "redirect:/paiementprocess/selectFacture/"+ ProcessHelper.encodeUrlPathSegment(factureResult.iterator().next().getId().toString(), httpServletRequest);
	}
	@RequestMapping(value = "/facturePrecc/{factureId}" ,method = RequestMethod.GET)
	public String facturePrecc(@PathVariable("factureId") Long factureId,Model uiModel,  HttpServletRequest httpServletRequest) {
		List<Facture> factureResult ;
		Caisse openCaisse = PaiementProcess.getMyOpenCaisse(SecurityUtil.getPharmaUser());
		if (openCaisse == null) {
			uiModel.addAttribute("apMessage", messageSource.getMessage("payment_cash_warning", null, LocaleUtil.getCurrentLocale()));
			return "caisses/infos";
		}
		factureResult = Facture.findFacturesByCaisseAndEncaisserNot(null, Boolean.TRUE).getResultList();
		if (factureResult.isEmpty()) {
			uiModel.addAttribute("apMessage", messageSource.getMessage("payment_cash_invoice_wait", null, LocaleUtil.getCurrentLocale()));
			return "paiementprocess/encaisserPaiement";

		}
		int size = factureResult.size();
		if (size >1) {
			Facture facture = Facture.findFacture(factureId);
			if (factureResult.contains(facture)) {
				int index = factureResult.indexOf(facture);
				index = index-1;
				if (index >=0) {
					Facture facture2 = factureResult.get(index);
					return "redirect:/paiementprocess/selectFacture/"+ ProcessHelper.encodeUrlPathSegment(facture2.getId().toString(), httpServletRequest);
				}
			}
		}
		return "redirect:/paiementprocess/selectFacture/"+ ProcessHelper.encodeUrlPathSegment(factureResult.get(size-1).getId().toString(), httpServletRequest);

	}

	@ResponseBody
	@RequestMapping(value = "/getUnCashInvoicesNumber" ,method = RequestMethod.GET)
	public String getUnCashInvoicesNumber(){
		List<Facture> resultList = Facture.findFacturesByCaisseAndEncaisserNot(null, Boolean.TRUE).getResultList();
		return ""+resultList.size();
	}


	//@Transactional
	@RequestMapping(value = "/annulFacture/{cmdId}" ,method = RequestMethod.GET)
	public String annulFacture(@PathVariable("cmdId") Long cmdId ,Model uiModel,  HttpServletRequest httpServletRequest) {
		CommandeClient commandeClient = CommandeClient.findCommandeClient(cmdId);
		if (commandeClient.getEncaisse()) {
			uiModel.addAttribute("apMessage", messageSource.getMessage("payment_cash_invoice_cancel_warning", null, LocaleUtil.getCurrentLocale()));

		}else {
			commandeClient.annulerCommande(SecurityUtil.getUserName());
			uiModel.addAttribute("apMessage", messageSource.getMessage("payment_cash_invoice_cancel_success", null, LocaleUtil.getCurrentLocale()));

		}
		Caisse openCaisse = PaiementProcess.getMyOpenCaisse(SecurityUtil.getPharmaUser());
		PaiementProcess paiementProcess = new PaiementProcess(openCaisse);
		uiModel.addAttribute("paiementProcess",paiementProcess);
		Paiement paiement = new Paiement() ;
		uiModel.addAttribute("paiement",paiement);
		return "paiementprocess/encaisserPaiement";

	}


	@RequestMapping(value = "/findFacture" ,params = "find=ByClientAndDateCreationBetween", method = RequestMethod.GET)
	public String findFacturesByClientAndDateCreationBetween(@RequestParam("client") Client client, @RequestParam("minDateCr") @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") Date minDateCreation, @RequestParam("maxDateCr") @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") Date maxDateCreation, Model uiModel) {
		List<Facture> factures = Facture.findFacturesByClientAndDateCreationBetweenNotsolder(client, minDateCreation, maxDateCreation).getResultList();
		return	showPoduct(factures, uiModel);
	}



	@RequestMapping(value = "/findFacture" ,params = "find=ByDateCreationBetween", method = RequestMethod.GET)
	public String findFacturesByDateCreationBetween(@RequestParam("minDate") @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") Date minDateCreation, @RequestParam("maxDate") @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") Date maxDateCreation, Model uiModel) {
		if (minDateCreation == null || maxDateCreation == null) {
			uiModel.addAttribute("apMessage", messageSource.getMessage("payment_cash_find_period", null, LocaleUtil.getCurrentLocale()));
			return	showPoduct(new ArrayList<Facture>(), uiModel);	   
		}
		List<Facture> factures = Facture.findFacturesByDateCreationBetweenNotsolder(minDateCreation, maxDateCreation).getResultList();
		return	showPoduct(factures, uiModel);	   

	}



	@RequestMapping(value = "/findFacture",params = "find=ByFactureNumberEquals", method = RequestMethod.GET)
	public String findFacturesByFactureNumberEquals(@RequestParam("factureNumber") String factureNumber, Model uiModel) {
		if (StringUtils.isBlank(factureNumber)) {
			uiModel.addAttribute("apMessage",  messageSource.getMessage("payment_cash_invoice_number", null, LocaleUtil.getCurrentLocale()));
			return	showPoduct(new ArrayList<Facture>(), uiModel);	   
		}
		List<Facture> factures =  Facture.findFacturesByFactureNumberEqualsNotSolder(factureNumber).getResultList();
		return	showPoduct(factures, uiModel);	   
	}

	@RequestMapping(params = "find=ByVendeurAndDateCreationBetween", method = RequestMethod.GET)
	public String findFacturesByVendeurAndDateCreationBetween(@RequestParam("vendeur") PharmaUser vendeur, @RequestParam("minDateC") @DateTimeFormat(pattern = "dd-MM-yyyy") Date minDateCreation, @RequestParam("maxDateC") @DateTimeFormat(pattern = "dd-MM-yyyy") Date maxDateCreation, Model uiModel) {
		if (minDateCreation == null || maxDateCreation == null) {
			uiModel.addAttribute("apMessage", messageSource.getMessage("payment_cash_find_period", null, LocaleUtil.getCurrentLocale()));
			return	showPoduct(new ArrayList<Facture>(), uiModel);	   
		}
		List<Facture> factures = Facture.findFacturesByVendeurAndDateCreationBetweenNotSolder(vendeur, minDateCreation, maxDateCreation).getResultList();
		return	showPoduct(factures, uiModel);	   
	}


	// display the list of product to the client 
	public String showPoduct(List<Facture>  factureResult ,Model uiModel){
		Caisse openCaisse = PaiementProcess.getMyOpenCaisse(SecurityUtil.getPharmaUser());
		if (openCaisse == null) {
			uiModel.addAttribute("apMessage", messageSource.getMessage("payment_cash_warning", null, LocaleUtil.getCurrentLocale()));
			return "caisses/infos";
		}

		PaiementProcess paiementProcess = new PaiementProcess(openCaisse);
		paiementProcess.setFactureResult(factureResult);
		uiModel.addAttribute("paiementProcess",paiementProcess);
		ProcessHelper.addDateTimeFormatPatterns(uiModel);
		return "paiementprocess/rechercheFacture";
	}

	// imprime les tickets de caisse 
	@RequestMapping("/printTicket/{ticketId}.pdf")
	public String print(@PathVariable("ticketId")Long ticketId, Model uiModel,HttpServletRequest httpServletRequest){
		Paiement paiement = Paiement.findPaiement(ticketId);

		if (!paiement.getTicketImprimer()) {
			paiement.setTicketImprimer(true);
			paiement.merge();
		}
		uiModel.addAttribute("paiement", paiement);
		try {
			String remoteAddr = httpServletRequest.getRemoteAddr();
			List<PrinterConfiguration> printers = PrinterConfiguration.findPrinterConfigurationsByComputerAdresseEquals(remoteAddr).getResultList();
			String printerName = printers.isEmpty()?null:printers.iterator().next().getPrinterName();

			TicketPrinter.buildTicket(paiement,Boolean.TRUE);
			PrintService.silentPrint(TicketPrinter.TICKET_FILE) ;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			return "ticketPdfDocView";
		}
		/*if (PrintService.print(TicketPrinter.TICKET_FILE , httpServletRequest)) {
			return "redirect:/paiementprocess/encaisser?form";
		}
		 */
		return "redirect:/paiementprocess/encaisser?form";

	}

	@RequestMapping("/printTicketWithourReduce/{ticketId}.pdf")
	public String printTicketWithourReduce(@PathVariable("ticketId")Long ticketId, Model uiModel,HttpServletRequest httpServletRequest){
		Paiement paiement = Paiement.findPaiement(ticketId);

		if (!paiement.getTicketImprimer()) {
			paiement.setTicketImprimer(true);
			paiement.merge();
		}
		paiement.setReduction(Boolean.FALSE);
		uiModel.addAttribute("paiement", paiement);

		try {
			//String remoteAddr = httpServletRequest.getRemoteAddr();
			//List<PrinterConfiguration> printers = PrinterConfiguration.findPrinterConfigurationsByComputerAdresseEquals(remoteAddr).getResultList();
			//String printerName = printers.isEmpty()?null:printers.iterator().next().getPrinterName();

			TicketPrinter.buildTicket(paiement,Boolean.TRUE);
			PrintService.silentPrint(TicketPrinter.TICKET_FILE);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return "ticketPdfDocView";
		}


		return "redirect:/paiementprocess/encaisser?form";
	}

	//@Transactional
	public void encaisser( Facture facture ,Caisse caisse,Paiement paiement) {
		CommandeClient commande = facture.getCommande();
		TypeCommande typeCommande = commande.getTypeCommande();

		if (typeCommande.equals(TypeCommande.VENTE_AU_PUBLIC)) {
			encaisserVenteComptant(facture, caisse, paiement);
		}
		if (typeCommande.equals(TypeCommande.VENTE_A_CREDIT)) {
			encaisserVentePartiel(facture, caisse, paiement);
		}
		commande.setEncaisse(true);
		commande.merge();

	}


	// gere les paiements des facture a credit	
	@Transactional
	public void avanceVenteCredit(Facture facture ,Caisse caisse,Paiement paiement){
		QuiPaye quiPaye = paiement.getQuiPaye();
		DetteClient detteClient = null ;
		Client client;
		if (quiPaye.equals(QuiPaye.CLIENT)) {
			client =facture.getCommande().getClient();
			paiement.setNomClient(client.getNomComplet());
			List<DetteClient> dette = DetteClient.findDetteClientsByFactureIdEqualsAndClientIdEquals(facture.getId(), facture.getCommande().getClient().getId()).getResultList();
			if (dette.iterator().hasNext()) {
				detteClient = dette.iterator().next(); 
			}
		}else {
			client =facture.getCommande().getClientPaiyeur();
			paiement.setNomClient(client.getNomComplet());
			List<DetteClient> dette = DetteClient.findDetteClientsByFactureIdEqualsAndClientIdEquals(facture.getId(), facture.getCommande().getClientPaiyeur().getId()).getResultList();
			detteClient = dette.iterator().next(); 
		}
		detteClient.avancer(paiement.getMontant().toBigInteger());
		detteClient.setDateDernierVersement(new Date());
		detteClient.merge();
		genererOperationCaisse(caisse, paiement);
		client.calculeTotalDette();
		client.merge();

	}

	//@Transactional
	public void encaisserVenteComptant(Facture facture ,Caisse caisse,Paiement paiement){
		genererMvtStock(facture , caisse);
		genererOperationCaisse(caisse, paiement);
		paiement.setNomClient(facture.getClient().getNomComplet());
		facture.avancerPaiement(paiement.getMontant().toBigInteger());
		facture.setEncaisser(Boolean.TRUE);
		facture.merge();
	}



	// gere les encaissement des facture a credit
	public void encaisserVenteCredit(Facture facture ,Caisse caisse,Paiement paiement){
		genererDetteClient(facture,BigInteger.ZERO);
		genererMvtStock(facture, caisse);
		genererOperationCaisse(caisse, paiement);
		facture.avancerPaiement(paiement.getMontant().toBigInteger());
		facture.setEncaisser(Boolean.TRUE);
		facture.merge();

	}

	public void encaisserVentePartiel(Facture facture ,Caisse caisse,Paiement paiement){
		CommandeClient cmd = facture.getCommande() ;
		if (cmd.getTypeCommande().equals(TypeCommande.VENTE_A_CREDIT) && cmd.getVentePartiel()) {
			Paiement paiement2 = paiement.getPaiementClientPayeur();
			if (paiement2!=null) {
				caisse.updateBonCmd(paiement2.getMontant());
				genererOperationCaisse(caisse, paiement2.getMontant(), paiement2.getTypePaiement());
			}
			genererDetteClientPayeur(facture);
			genererOperationCaisse(caisse, paiement);
			facture.avancerPaiement(paiement.getMontant().toBigInteger());

		}

		if (cmd.getTypeCommande().equals(TypeCommande.VENTE_A_CREDIT) && !cmd.getVentePartiel()) {
			Paiement paiement2 = paiement.getPaiementClientPayeur();
			if (paiement2!=null) {
				caisse.updateBonCmd(paiement2.getMontant());
				genererOperationCaisse(caisse, paiement2.getMontant(), paiement2.getTypePaiement());
			}
			genererDetteClientPayeur(facture);
			genererDetteClient(facture,paiement.getAvancePartClient());
			genererOperationCaisse(caisse, paiement);
			facture.avancerPaiement(paiement.getAvancePartClient());
		}
		facture.setEncaisser(Boolean.TRUE);
		facture.merge();
		genererMvtStock(facture,caisse);

	}
	public void genererDetteClient(Facture facture , BigInteger avance){
		CommandeClient commande = facture.getCommande();
		Client client = commande.getClient();
		avance = avance!=null ?avance : BigInteger.ZERO ;
		BigDecimal amount = new BigDecimal(facture.getMontantTotal());
		amount=amount.multiply(BigDecimal.valueOf(commande.getTauxCouverture())).divide(BigDecimal.valueOf(100));
		BigInteger partPayeur = amount.toBigInteger();
		BigInteger partClient = facture.getNetPayer().subtract(partPayeur.add(avance));
		//defini la dette du client 
		if (partClient.intValue()!=0) {
			DetteClient detteClient = new DetteClient() ;
			detteClient.setFactureNo(facture.getFactureNumber());
			detteClient.setClientNo(client.getClientNumber());
			detteClient.setMontantInitial(partClient);
			detteClient.setClientId(client.getId());
			detteClient.setClientName(client.getNomComplet());
			detteClient.setFactureId(facture.getId());
			detteClient.avancer(BigInteger.ZERO);
			detteClient.persist();
		}

	}

	public void genererDetteClientPayeur(Facture facture){
		CommandeClient commande = facture.getCommande();
		Client paiyeur = commande.getClientPaiyeur();
		BigDecimal amount = new BigDecimal(facture.getNetPayer());
		amount=amount.multiply(BigDecimal.valueOf(commande.getTauxCouverture())).divide(BigDecimal.valueOf(100));
		BigInteger partPayeur = amount.toBigInteger();
		//defini la dette du client  payeur
		if (partPayeur.intValue()!=0) {
			DetteClient dettePayeur = new DetteClient() ;
			dettePayeur.setFactureNo(facture.getFactureNumber());
			dettePayeur.setClientNo(paiyeur.getClientNumber());
			dettePayeur.setMontantInitial(partPayeur);
			dettePayeur.setClientId(paiyeur.getId());
			dettePayeur.setClientName(paiyeur.getNomComplet());
			dettePayeur.setFactureId(facture.getId());
			dettePayeur.avancer(BigInteger.ZERO);
			dettePayeur.persist();
			paiyeur.calculeTotalDette();
			paiyeur.merge();
		}

	}

	// genere les mvt de stock
	//@Transactional
	public void genererMvtStock(Facture facture , Caisse caisse){
		Set<LigneCmdClient> lineCommande = facture.getCommande().getLineCommande();
		if (!lineCommande.isEmpty()) {
			for (LigneCmdClient ligne : lineCommande) {
				String filiale = "";
				MouvementStock mouvementStock = new MouvementStock();
				mouvementStock.setAgentCreateur(facture.getVendeur().getDisplayName());
				mouvementStock.setCip(ligne.getCip());
				mouvementStock.setCipM(ligne.getCipM());
				mouvementStock.setDesignation(ligne.getDesignation());
				mouvementStock.setDestination(DestinationMvt.CLIENT);
				mouvementStock.setNumeroTicket(facture.getFactureNumber());
				mouvementStock.setOrigine(DestinationMvt.MAGASIN);
				mouvementStock.setQteDeplace(ligne.getQuantiteCommande());
				mouvementStock.setTypeMouvement(TypeMouvement.VENTE);
				mouvementStock.setRemiseTotal(ligne.getTotalRemise().toBigInteger());
				mouvementStock.setSite(facture.getSite());
				mouvementStock.setAgentCreateur(facture.getVendeur().getUserName());
				LigneApprovisionement ligneApprovisionement = ligne.getProduit();
				mouvementStock.setQteInitiale(ligneApprovisionement.getQuantieEnStock());
				Produit prd = ligneApprovisionement.getProduit() ;
				ligneApprovisionement.setQuantiteVendu(ligneApprovisionement.getQuantiteVendu().add(ligne.getQuantiteCommande()));
				ligneApprovisionement.CalculeQteEnStock();
				ligneApprovisionement.merge();
				prd.setQuantiteEnStock(prd.getQuantiteEnStock().subtract(ligne.getQuantiteCommande()));
				prd.setDateDerniereSortie(new Date());
				mouvementStock.setQteFinale(ligneApprovisionement.getQuantieEnStock());
				mouvementStock.setPVenteTotal(ligne.getPrixTotal().subtract(ligne.getTotalRemise()).toBigInteger());
				mouvementStock.setPAchatTotal(ligne.getQuantiteCommande().multiply(ligneApprovisionement.getPrixAchatUnitaire().toBigInteger()));
				mouvementStock.setRemiseTotal(ligne.getTotalRemise().toBigInteger());
				mouvementStock.setCaisse(caisse.getCaisseNumber());
				filiale = ligneApprovisionement.getApprovisionement().getFiliale();
				filiale = StringUtils.isNotBlank(filiale)?filiale:"";
				if (StringUtils.isNotBlank(filiale)) {
					mouvementStock.setFiliale(filiale);
				} else {
					if (prd.getFiliale() != null) {
						mouvementStock.setFiliale(prd.getFiliale().getFilialeNumber());
					}
				}
				prd.setTrueStockValue();

				prd.merge();   
				mouvementStock.persist();
			}
		}

	}

	//genere les operation de caisse
	//@Transactional
	public void genererOperationCaisse(Caisse caisse,Paiement paiement){
		OperationCaisse operationCaisse = new OperationCaisse();
		operationCaisse.setCaisse(caisse);
		operationCaisse.setModePaiement(paiement.getTypePaiement());
		operationCaisse.setOperateur(SecurityUtil.getPharmaUser());
		operationCaisse.setTypeOperation(TypeOpCaisse.ENCAISSEMENT);
		operationCaisse.setDateOperation(new Date());
		operationCaisse.setRaisonOperation("Paiement Facture No : "+paiement.getFacture().getFactureNumber());
		TypePaiement typePaiement = paiement.getTypePaiement();

		if (typePaiement.equals(TypePaiement.CASH)) caisse.updateCash(paiement.getMontant());

		if (typePaiement.equals(TypePaiement.CHEQUE)) caisse.updateCheque(paiement.getMontant());

		/*if (typePaiement.equals(TypePaiement.CREDIT)) {
			caisse.updateCredit(BigDecimal.valueOf(paiement.getFacture().getNetPayer().longValue()));
			operationCaisse.setMontant(BigDecimal.valueOf(paiement.));

		}*/
		//definir le total des remise
		caisse.updateRemiseTotal(BigDecimal.valueOf(paiement.getFacture().getMontantRemise().longValue()));
		if (typePaiement.equals(TypePaiement.VENTE_PARTIEL))caisse.updateCash(paiement.getMontant());
		if (typePaiement.equals(TypePaiement.BON_CMD)) caisse.updateBonCmd(paiement.getMontant());
		if (typePaiement.equals(TypePaiement.BON_CMD_PARTIEL)) caisse.updateCash(paiement.getMontant());
		if (typePaiement.equals(TypePaiement.CARTE_CREDIT)) caisse.updateCarteCredit(paiement.getMontant());
		if (typePaiement.equals(TypePaiement.CREDIT)) {
			caisse.updateCash(new BigDecimal(paiement.getAvancePartClient()));
			caisse.updateCredit(new BigDecimal(paiement.getRestePartClient()));

		}
		if (typePaiement.equals(TypePaiement.BON_AVOIR_CLIENT)) {
			AvoirClient avoirClient =	AvoirClient.search(null,paiement.getNumeroBon(), null, null, null, null, null).getResultList().iterator().next();
			if (paiement.getCompenser()) {
				if (avoirClient.getReste().intValue() >= paiement.getMontant().intValue()) {
					caisse.updateBonClient(paiement.getMontant());
					avoirClient.avancer(paiement.getMontant());
					//caisse.updateCash(paiement.getMontant());
				}else {
					caisse.updateBonClient(avoirClient.getReste());
					caisse.updateCash(paiement.getMontant().subtract(avoirClient.getReste()));
					avoirClient.avancer(avoirClient.getReste());

				}

			}else {

				if (avoirClient.getReste().intValue() >= paiement.getMontant().intValue()) {
					caisse.updateBonClient(paiement.getMontant());
					avoirClient.avancer(paiement.getMontant());

				}else {
					caisse.updateBonClient(paiement.getMontantBon());
					avoirClient.avancer(paiement.getMontantBon());
					caisse.updateCash(paiement.getMontant().subtract(paiement.getMontantBon()));
				}
			}

		}
		operationCaisse.setMontant(paiement.getMontant());
		operationCaisse.persist();
		caisse.merge();

	}

	public void genererOperationCaisse(Caisse caisse,BigDecimal montant, TypePaiement type){
		OperationCaisse operationCaisse = new OperationCaisse();
		operationCaisse.setCaisse(caisse);
		operationCaisse.setModePaiement(type);
		operationCaisse.setOperateur(SecurityUtil.getPharmaUser());
		operationCaisse.setTypeOperation(TypeOpCaisse.ENCAISSEMENT);
		operationCaisse.setDateOperation(new Date());
		//operationCaisse.setRaisonOperation("Paiement Facture No : "+paiement.getFacture().getFactureNumber());
		operationCaisse.setMontant(montant);
		operationCaisse.persist();

	}




	//  neccessaire pour les pour vues

	/* @ModelAttribute("typepaiements")
		    public Collection<TypePaiement> populateTypePaiements() {
		         Collection<TypePaiement> list = Arrays.asList(TypePaiement.class.getEnumConstants());
		         return list ;
		    }*/
	public Collection<TypePaiement> populateTypePaiements(boolean cash) {
		Collection<TypePaiement> list = new ArrayList<TypePaiement>();
		if (cash) {
			list.add(TypePaiement.CASH);
			list.add(TypePaiement.CHEQUE);
			list.add(TypePaiement.CARTE_CREDIT);
			list.add(TypePaiement.BON_AVOIR_CLIENT);



		}else {
			//list.add(TypePaiement.VENTE_PARTIEL);
			list.add(TypePaiement.CREDIT);
		}
		return list ;
	}

	public Collection<TypePaiement> populateTypePaiements(CommandeClient cmd) {
		Collection<TypePaiement> list = new ArrayList<TypePaiement>();
		if (cmd.getTypeCommande().equals(TypeCommande.VENTE_AU_PUBLIC)) {
			list.add(TypePaiement.CASH);
			list.add(TypePaiement.CHEQUE);
			list.add(TypePaiement.CARTE_CREDIT);
			list.add(TypePaiement.BON_AVOIR_CLIENT);
			return list ;


		}
		if (cmd.getTypeCommande().equals(TypeCommande.VENTE_A_CREDIT) && cmd.getVentePartiel()) {
			list.add(TypePaiement.BON_CMD_PARTIEL);
			return list;
		}

		if (cmd.getTypeCommande().equals(TypeCommande.VENTE_A_CREDIT) && !cmd.getVentePartiel()) {
			//list.add(TypePaiement.BON_CMD);
			list.add(TypePaiement.CREDIT);
			return list;
		}

		list.add(TypePaiement.CASH);
		list.add(TypePaiement.CHEQUE);
		list.add(TypePaiement.BON_AVOIR_CLIENT);
		list.add(TypePaiement.CARTE_CREDIT);
		return list ;

	}

	public void initPaiementView(Facture facture,Paiement paiement,Model uiModel){
		CommandeClient cmd = facture.getCommande();
		uiModel.addAttribute("typepaiements",populateTypePaiements(facture.getCommande()));

		if (cmd.getTypeCommande().equals(TypeCommande.VENTE_AU_PUBLIC)) {
			paiement.setMontant(BigDecimal.valueOf(facture.getNetPayer().longValue()));
		}
		if (cmd.getTypeCommande().equals(TypeCommande.VENTE_A_CREDIT)) {
			paiement.setMontant(BigDecimal.valueOf(facture.calculerPartClient().longValue()));

		}
		/*if (cmd.getTypeCommande().equals(TypeCommande.VENTE_A_CREDIT) && !cmd.getVentePartiel()) {
			paiement.setMontant(BigDecimal.ZERO);

		}*/
		uiModel.addAttribute("typepaiements",populateTypePaiements(facture.getCommande()));
		uiModel.addAttribute("paiement",paiement);
	}

	/*@ModelAttribute("pharmausers")
	public Collection<PharmaUser> populatePharmaUsers() {
		return PharmaUser.findAllPharmaUsers();
	}*/



	@ModelAttribute("quipayes")
	public Collection<QuiPaye> populateQuiPayes() {
		return Arrays.asList(QuiPaye.CLIENT);
	}

	/*@ModelAttribute("clients")
	public Collection<Client> populateClients() {
		return Client.findAllClients();
	}*/

}
