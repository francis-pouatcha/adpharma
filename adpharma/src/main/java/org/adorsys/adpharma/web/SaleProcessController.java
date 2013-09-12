package org.adorsys.adpharma.web;

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
import javax.servlet.http.HttpUtils;
import javax.validation.Valid;

import org.adorsys.adpharma.beans.process.PaiementProcess;
import org.adorsys.adpharma.beans.process.SaleProcess;
import org.adorsys.adpharma.beans.process.SessionBean;
import org.adorsys.adpharma.domain.Caisse;
import org.adorsys.adpharma.domain.CategorieClient;
import org.adorsys.adpharma.domain.Client;
import org.adorsys.adpharma.domain.CommandeClient;
import org.adorsys.adpharma.domain.Configuration;
import org.adorsys.adpharma.domain.Etat;
import org.adorsys.adpharma.domain.Facture;
import org.adorsys.adpharma.domain.Genre;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.LigneCmdClient;
import org.adorsys.adpharma.domain.LigneFacture;
import org.adorsys.adpharma.domain.Ordonnancier;
import org.adorsys.adpharma.domain.Paiement;
import org.adorsys.adpharma.domain.PharmaUser;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.RoleName;
import org.adorsys.adpharma.domain.SalesConfiguration;
import org.adorsys.adpharma.domain.TypeCommande;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.services.SaleService;
import org.adorsys.adpharma.utils.LocaleUtil;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.adorsys.adpharma.utils.ProcessHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * @author adorsys-clovis
 *
 */
@RequestMapping("/saleprocess")
@Controller
@ManagedResource(objectName="org.adorsys.adpharma.web:name=SaleProcessControllerr") 
public class SaleProcessController {
	
public static Logger LOGS= Logger.getLogger(SaleProcessController.class);
	
	
	@Resource(name="messageSource")
	ReloadableResourceBundleMessageSource messageSource;
	
	@ResponseBody
	@RequestMapping(value="/{saleId}/findByCipmAjax/{cipm}", method = RequestMethod.GET)
	public String findByCipmAjax(@PathVariable("saleId") Long saleId ,@PathVariable("cipm") String cipm,Model uiModel, HttpServletRequest httpServletRequest) {
		
		String	cipMaison = cipm;
		Configuration config = Configuration.findConfiguration(new Long(1));
		LigneApprovisionement item = new LigneApprovisionement() ;
		if(config.getOnlySaleOld()){
			CommandeClient sale = CommandeClient.findCommandeClient(saleId);
			List<LigneApprovisionement> lines = LigneApprovisionement.findLigneApprovisionementsByCipMaisonEquals(cipMaison).setMaxResults(2).getResultList();
			if (!lines.isEmpty()) {
				LigneApprovisionement next = lines.iterator().next();
				List<LigneApprovisionement> oldcimplist = SaleService.getoldProductLisForSale(next.getProduit(), sale);
				if(next.isOldItem(oldcimplist)){
					return lines.iterator().next().clone().toJson();
				}else {
						item.setViewMsg(messageSource.getMessage("sale_enter_oldcipm", null, LocaleUtil.getCurrentLocale())) ;
					return item.toJson() ;
				}
			}else {
					item.setViewMsg(messageSource.getMessage("sale_find_product", null, LocaleUtil.getCurrentLocale()));
				    return item.toJson() ;
			}	
		}
		return new ProduitController().findByCipmAjax(cipMaison, uiModel) ;

	}


	@RequestMapping(value = "/newPublicCmd", method = RequestMethod.GET)
	public String newCommand(Model uiModel,HttpServletRequest httpServletRequest) {
		CommandeClient commandeClient = initCmdClient();
		commandeClient.persist() ;
		uiModel.asMap().clear();
		return "redirect:/saleprocess/" + ProcessHelper.encodeUrlPathSegment(commandeClient.getId().toString(), httpServletRequest)+"/edit";
	}

	public CommandeClient initCmdClient(){
		CommandeClient commandeClient = new CommandeClient();
		commandeClient.setTypeCommande(TypeCommande.VENTE_AU_PUBLIC);
		commandeClient.setClient(Client.findClient(new Long(1)));
		return commandeClient ;
	}

	@RequestMapping(value = "/{cmdId}/getclientajax", method = RequestMethod.GET)
	@ResponseBody
	public String getClientByAjax(@PathVariable("cmdId") Long cmdId, Model uiModel,HttpServletRequest httpServletRequest) {
		CommandeClient commandeClient = CommandeClient.findCommandeClient(cmdId);
		return commandeClient.getClient().toJson();
	}

	// Visualize a Sale
	@RequestMapping(value = "/{mvtId}/visualiserCmd/{factureNumber}", method = RequestMethod.GET)
	public String visualiserCmd(@PathVariable("mvtId") Long mvtId,@PathVariable("factureNumber") String factureNumber, Model uiModel,HttpServletRequest httpServletRequest) {
		List<Facture> resultList = Facture.findFacturesByFactureNumberEquals(factureNumber).getResultList();
		if (!resultList.isEmpty()) {
			Facture next = resultList.iterator().next();
			uiModel.asMap().clear();
			return "redirect:/commandeclients/" + ProcessHelper.encodeUrlPathSegment( next.getCommande().getId().toString(), httpServletRequest);

		}else{
				uiModel.addAttribute("apMessage", messageSource.getMessage("sale_find_order", null, LocaleUtil.getCurrentLocale()));
		}
		uiModel.asMap().clear();
		return "redirect:/mouvementstocks/" + ProcessHelper.encodeUrlPathSegment( mvtId.toString(), httpServletRequest);

	}

	@RequestMapping(value = "/{mvtId}/printTicket/{factureNumber}", method = RequestMethod.GET)
	public String printTicket(@PathVariable("mvtId") Long mvtId,@PathVariable("factureNumber") String factureNumber, Model uiModel,HttpServletRequest httpServletRequest) {
		List<Paiement> resultList = Paiement.findPaiementsByInvoiceNumberEquals(factureNumber).getResultList();
		if (!resultList.isEmpty()) {
			Paiement next = resultList.iterator().next();
			uiModel.asMap().clear();
			return "redirect:/paiementprocess/printTicket/"+next.getId().toString()+".pdf";

		}
		uiModel.asMap().clear();
		uiModel.addAttribute("apMessage", messageSource.getMessage("sale_find_ticket", null, LocaleUtil.getCurrentLocale()));
		return "redirect:/mouvementstocks/" + ProcessHelper.encodeUrlPathSegment( mvtId.toString(), httpServletRequest);

	}



	@RequestMapping(value = "/{cmdId}/updateclientajax", method = RequestMethod.GET)
	@ResponseBody
	public String updateClientByAjax(@PathVariable("cmdId") Long cmdId, Model uiModel,HttpServletRequest httpServletRequest) {
		CommandeClient commandeClient = CommandeClient.findCommandeClient(cmdId);
		String cltid = httpServletRequest.getParameter("cltid");
		Client client = new Client();
		client.setNom(httpServletRequest.getParameter("nom"));
		client.setPrenom(httpServletRequest.getParameter("prenom"));
		client.setEmail(httpServletRequest.getParameter("email"));
		client.setEmployeur(httpServletRequest.getParameter("employeur"));
		client.setNote(httpServletRequest.getParameter("note"));
		client.setTelephoneMobile(httpServletRequest.getParameter("telephoneMobile"));
		client.setSexe(Genre.getGenreByName(httpServletRequest.getParameter("sexe")));

		if ("1".equalsIgnoreCase(cltid.trim())) {
			client.persist();
			commandeClient.setClient(client);

		}else {
			client = Client.findClient(Long.valueOf(cltid.trim()));
			client.setNom(httpServletRequest.getParameter("nom"));
			client.setPrenom(httpServletRequest.getParameter("prenom"));
			client.setEmail(httpServletRequest.getParameter("email"));
			client.setEmployeur(httpServletRequest.getParameter("employeur"));
			client.setNote(httpServletRequest.getParameter("note"));
			client.setTelephoneMobile(httpServletRequest.getParameter("telephoneMobile"));
			client.setSexe(Genre.getGenreByName(httpServletRequest.getParameter("sexe")));

			Client merge = (Client) client.merge();
			commandeClient.setClient(merge);

		}
		CommandeClient merge = (CommandeClient) commandeClient.merge();
		return merge.toString();
	}

	@RequestMapping(value = "/newProformat", method = RequestMethod.GET)
	public String newProformat(Model uiModel,HttpServletRequest httpServletRequest) {
		CommandeClient commandeClient = new CommandeClient();
		commandeClient.setTypeCommande(TypeCommande.VENTE_PROFORMAT);
		commandeClient.setClient(Client.findClient(new Long(1)));
		commandeClient.persist() ;
		uiModel.asMap().clear();
		return "redirect:/saleprocess/" + ProcessHelper.encodeUrlPathSegment(commandeClient.getId().toString(), httpServletRequest)+"/edit";
	}

	@RequestMapping(value = "/{cmdId}/edit", method = RequestMethod.GET)
	public String editCommand(@PathVariable("cmdId") Long cmdId, Model uiModel, HttpServletRequest httpServletRequest) {
		uiModel.asMap().clear();
		CommandeClient commandeClient = CommandeClient.findCommandeClient(cmdId);
		SaleProcess saleProcess = new SaleProcess(commandeClient, uiModel);
		saleProcess.setLigneCommande(LigneCmdClient.findLigneCmdClientsByCommande(commandeClient).getResultList());
		List<SalesConfiguration> salesConfigurations= SalesConfiguration.findAllSalesConfigurations();
		String configurations = SalesConfiguration.toJsonArray(salesConfigurations);
		uiModel.addAttribute("saleProcess",saleProcess);
		uiModel.addAttribute("salesConfig",configurations);
		uiModel.addAttribute("configurations", salesConfigurations);
		uiModel.addAttribute("apMessage", httpServletRequest.getAttribute("apMessage"));

		return "saleprocess/edit";
	}


	//met a jour un client lor d'une vente au comptant  
	@RequestMapping(value = "/{cmdId}/updateClient/{clId}",params = "form", method = RequestMethod.GET)
	public String updateClientForm(@PathVariable("cmdId") Long cmdId,@PathVariable("clId") Long clId, Model uiModel) {
		Client clients = Client.findClient(clId);
		if ("CL-0001".equals(clients.getClientNumber())) {
			Client client = Client.findClient(clId).clone();
			client.persist();
			CommandeClient commandeClient = CommandeClient.findCommandeClient(cmdId);
			commandeClient.setClient(client);
			commandeClient.merge();
			uiModel.addAttribute("client", client);
		}else {
			uiModel.addAttribute("client",clients);

		}
		ProcessHelper.addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("cmdId", cmdId);

		return "saleprocess/updateClient";

	}

	//permet d associer un ordonnancier a un client  
	//cette fonction ne travaille plus :
	@RequestMapping(value = "/{cmdId}/ordonnancier",params = "form", method = RequestMethod.GET)
	public String createOrUpdateOrdonnanceForm(@PathVariable("cmdId") Long cmdId, Model uiModel) {
		List<Ordonnancier> ordonnanciers = Ordonnancier.findOrdonnanciersByCommande(CommandeClient.findCommandeClient(cmdId)).getResultList();
		Collection<CommandeClient> commandeclients = new ArrayList<CommandeClient>();
		commandeclients.add(CommandeClient.findCommandeClient(cmdId));
		uiModel.addAttribute("cmdId", cmdId);
		uiModel.addAttribute("commandeclients", commandeclients);

		if (!ordonnanciers.isEmpty()) {
			uiModel.addAttribute("ordonnancier", ordonnanciers.iterator().next());
			return "saleprocess/updateOrdonnancier";

		}else {
			Ordonnancier ordonnancier = new Ordonnancier();
			uiModel.addAttribute("ordonnancier", ordonnancier);
			return "saleprocess/CreateOrdonnancier";
		}

	}
	//permet d associer un ordonnancier a un client  
	@RequestMapping(value = "/{cmdId}/ordonnancier")
	public String createOrUpdateOrdonnance(HttpServletRequest httpServletRequest, Ordonnancier ordonnancier, 
			BindingResult bindingResult,@PathVariable("cmdId") Long cmdId, Model uiModel) {
		Long id = ordonnancier.getId();
		uiModel.asMap().clear();
		if (id==null) {
			ordonnancier.setCommande(CommandeClient.findCommandeClient(cmdId));
			ordonnancier.persist();

		}else {
			ordonnancier.merge();
		}
		ordonnancier.flush();
		return "redirect:/saleprocess/" + ProcessHelper.encodeUrlPathSegment(cmdId.toString(), httpServletRequest)+"/edit";
	}
	
	// Recherche ajax de l'ordonnance de la commande
	@RequestMapping(value="/{cmdId}/findOrdonance", method=RequestMethod.GET)
	@ResponseBody
	public String findOrdonnanceAjax(@PathVariable("cmdId")Long cmdId, @RequestParam(value="datePrescrip", required=false)String datePrescription, Ordonnancier ordonnancier){
		CommandeClient commande = CommandeClient.findCommandeClient(cmdId);
		List<Ordonnancier> ordonnances = Ordonnancier.findOrdonnanciersByCommande(commande).getResultList();
		Ordonnancier ordonnance= null;
		if(!ordonnances.isEmpty()){
			ordonnance= ordonnances.iterator().next();
			return ordonnance.toJson();
		}
		return null;
	}
	
	// Creation ou mise a jour de l'ordonnance de la commande
	@RequestMapping(value="/{cmdId}/saveOrdonance", method=RequestMethod.GET)
	@ResponseBody
	public String createOrUpdateOrdonnanceAjax(@PathVariable("cmdId")Long cmdId, 
			@RequestParam(value="datePrescrip", required=false) String datePrescription, Ordonnancier ordonnance, Model uiModel){
		CommandeClient commande = CommandeClient.findCommandeClient(cmdId);
		List<Ordonnancier> ordonnanciers = Ordonnancier.findOrdonnanciersByCommande(commande).getResultList();
		Ordonnancier ordonnancier=null;
		// Texte de l'ordonnance a afficher 
		StringBuilder display= new StringBuilder();
		if(!ordonnanciers.isEmpty()){
			ordonnancier= ordonnanciers.iterator().next();
			ordonnancier.setPrescripteur(ordonnance.getPrescripteur());
			ordonnancier.setHospital(ordonnance.getHospital());
			ordonnancier.setNomDuPatient(ordonnance.getNomDuPatient());
			if(datePrescription!=null){
				   ordonnancier.setDatePrescription(PharmaDateUtil.parseToDate(datePrescription, PharmaDateUtil.DATE_PATTERN_LONG));
		    }
			ordonnancier.merge();
			SaleProcess saleProcess= new SaleProcess(cmdId, uiModel);
			/*display.append("ORDONNANCE- Numero: "+ordonnancier.getOrdNumber());
			if(ordonnancier.getNomDuPatient()!=null){
			 display.append("  Nom du patient: "+ordonnancier.getNomDuPatient());
			}*/
			return saleProcess.getDisplayOrdonance();
		}else{
			if(datePrescription!=null){
				ordonnance.setDatePrescription(PharmaDateUtil.parseToDate(datePrescription, PharmaDateUtil.DATE_PATTERN_LONG));
			}
			ordonnance.setCommande(commande);
			ordonnance.persist();
			SaleProcess saleProcess = new SaleProcess(cmdId, uiModel);
			/*display.append("ORDONNANCE- Numero: "+ordonnance.getOrdNumber());
			if(ordonnance.getNomDuPatient()!=null){
			 display.append("  Nom du patient: "+ordonnance.getNomDuPatient());
			}*/
			return saleProcess.getDisplayOrdonance();
		}
	}


	//met a jour un client lor d'une vente au comptant  
	@RequestMapping(value = "/rechercheCmd",params = "form", method = RequestMethod.GET)
	public String rechercheCmdForm( Model uiModel) {

		return "saleprocess/rechercheCmd";

	}

	@RequestMapping(value = "/{cmdId}/updateClient", method = RequestMethod.PUT)
	public String updateClient(@PathVariable("cmdId") String cmdId,@Valid Client client, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		/* Client clt = Client.findClient(client.getId());
	        client.setClientPayeur(clt.getClientPayeur());
	        client.setClientPayeurNumber(clt.getClientPayeurNumber());
		 */ if (bindingResult.hasErrors()) {
			 uiModel.addAttribute("client", client);
			 ProcessHelper.addDateTimeFormatPatterns(uiModel);
			 return "saleprocess/updateClient";
		 }
		 uiModel.asMap().clear();
		 client.merge();
		 return "redirect:/saleprocess/" + ProcessHelper.encodeUrlPathSegment(cmdId, httpServletRequest)+"/edit";
	}
	
	

	//@Transactional
	@RequestMapping(value = "/{cmdId}/enregistrer", method = RequestMethod.GET)
	public String enregistrerCmd(@PathVariable("cmdId") Long cmdId, Model uiModel, HttpServletRequest httpServletRequest) {
		// Get the code language
		String langue = (String)httpServletRequest.getSession().getAttribute("lang");
		
		SessionBean	  sessionBean = (SessionBean) httpServletRequest.getSession().getAttribute("sessionBean");
		CommandeClient commandeClient = CommandeClient.findCommandeClient(cmdId);
		Caisse caisse = PaiementProcess.getOpenCaisse();
		if (caisse==null) {
			/*if(langue.equals("fr")){
				uiModel.addAttribute("apMessage", "Impossible de cloturer la commande, aucune Caisse ouverte") ;
			}
			if(langue.equals("en")){
				uiModel.addAttribute("apMessage", "Impossible to close the order, no cash registry is open") ;
			}*/
			
			return "forward:/saleprocess/" + ProcessHelper.encodeUrlPathSegment(cmdId.toString(), httpServletRequest)+"/edit";
		}
		if (!commandeClient.validaterCmd(uiModel)) {
			SaleProcess saleProcess = new SaleProcess(commandeClient, uiModel);
			saleProcess.setLigneCommande(LigneCmdClient.findLigneCmdClientsByCommande(commandeClient).getResultList());
			uiModel.addAttribute("saleProcess",saleProcess);
			return "saleprocess/edit";
		}
		if (!commandeClient.getStatus().equals(Etat.CLOS)) {
			saveAndCloseCmd(commandeClient ,caisse,SecurityUtil.getPharmaUser(null));
		}
		uiModel.asMap().clear();
		if(SaleService.enableSaleCash(sessionBean.getConfiguration(),SecurityUtil.getPharmaUser())){
			return "redirect:/paiementprocess/factureSuivante/" + ProcessHelper.encodeUrlPathSegment(commandeClient.getFacture().getId().toString(), httpServletRequest);
		}else {
			return "redirect:/commandeclients/" + ProcessHelper.encodeUrlPathSegment(commandeClient.getId().toString(), httpServletRequest);
		}


	}
	

	//@Transactional
	@RequestMapping(value = "/{cmdId}/enregistrementValider",method = RequestMethod.POST)
	public String enregistrementValider(@PathVariable("cmdId") Long cmdId, @RequestParam("cle") String key , Model uiModel, HttpServletRequest httpServletRequest) {
		// Get the code language
	    String langue = (String)httpServletRequest.getSession().getAttribute("lang");
		CommandeClient commandeClient = CommandeClient.findCommandeClient(cmdId);
		Caisse caisse = PaiementProcess.getOpenCaisse();
		ArrayList<RoleName> role = new ArrayList<RoleName>();
		role.add(RoleName.ROLE_VENDEUR);
		role.add(RoleName.ROLE_SITE_MANAGER);
		if (caisse==null) {
				uiModel.addAttribute("apMessage", messageSource.getMessage("sale_close_warning", null, LocaleUtil.getCurrentLocale()));
			SaleProcess saleProcess = new SaleProcess(commandeClient, uiModel);
			saleProcess.setLigneCommande(LigneCmdClient.findLigneCmdClientsByCommande(commandeClient).getResultList());
			uiModel.addAttribute("saleProcess",saleProcess);
			return "saleprocess/edit";	
		}
		if (key == null) {
				uiModel.addAttribute("apMessage", messageSource.getMessage("sale_enter_key", null, LocaleUtil.getCurrentLocale())) ;
			SaleProcess saleProcess = new SaleProcess(commandeClient, uiModel);
			saleProcess.setLigneCommande(LigneCmdClient.findLigneCmdClientsByCommande(commandeClient).getResultList());
			uiModel.addAttribute("saleProcess",saleProcess);
			return "saleprocess/edit";	
		} 
		PharmaUser pharmaUser = SecurityUtil.getPharmaUser(key);
		if (pharmaUser == null) {
			uiModel.addAttribute("apMessage", messageSource.getMessage("sale_key_warning", null, LocaleUtil.getCurrentLocale()));
			SaleProcess saleProcess = new SaleProcess(commandeClient, uiModel);
			saleProcess.setLigneCommande(LigneCmdClient.findLigneCmdClientsByCommande(commandeClient).getResultList());
			uiModel.addAttribute("saleProcess",saleProcess);
			return "saleprocess/edit";
		} 
		if(pharmaUser.isAccountLocked()){
				uiModel.addAttribute("apMessage", messageSource.getMessage("sale_account_locked", null, LocaleUtil.getCurrentLocale()));
			SaleProcess saleProcess = new SaleProcess(commandeClient, uiModel);
			saleProcess.setLigneCommande(LigneCmdClient.findLigneCmdClientsByCommande(commandeClient).getResultList());
			uiModel.addAttribute("saleProcess",saleProcess);
			return "saleprocess/edit";	
		}
		if (!pharmaUser.hasAnyRole(role)) {
				uiModel.addAttribute("apMessage", messageSource.getMessage("sale_close_grant", null, LocaleUtil.getCurrentLocale())); 
			SaleProcess saleProcess = new SaleProcess(commandeClient, uiModel);
			saleProcess.setLigneCommande(LigneCmdClient.findLigneCmdClientsByCommande(commandeClient).getResultList());
			uiModel.addAttribute("saleProcess",saleProcess);
			return "saleprocess/edit";		
		}

		if (!commandeClient.validaterCmd(uiModel)) {
			SaleProcess saleProcess = new SaleProcess(commandeClient, uiModel);
			saleProcess.setLigneCommande(LigneCmdClient.findLigneCmdClientsByCommande(commandeClient).getResultList());
			uiModel.addAttribute("saleProcess",saleProcess);
			return "saleprocess/edit";
		}

		if (!commandeClient.getStatus().equals(Etat.CLOS)) {
			saveAndCloseCmd(commandeClient ,caisse,pharmaUser);
		}
		uiModel.asMap().clear();

		return "redirect:/commandeclients/" + ProcessHelper.encodeUrlPathSegment(commandeClient.getId().toString(), httpServletRequest);
	}

	// add line to de client commande 

	@Transactional
	@RequestMapping(value = "/{cmdId}/addLine", method = RequestMethod.POST)
	public String addLine(@PathVariable("cmdId") Long cmdId,@RequestParam Long pId,@RequestParam BigInteger qte,
		@RequestParam String rem, @RequestParam(required=false) BigDecimal pu, Model uiModel,HttpServletRequest httpServletRequest) {
		// Get the code language
	    String langue = (String)httpServletRequest.getSession().getAttribute("lang");
		SessionBean sessionBean =	 (SessionBean) httpServletRequest.getSession().getAttribute("sessionBean") ;
		Configuration configuration = sessionBean.getConfiguration();
		CommandeClient commandeClient = CommandeClient.findCommandeClient(cmdId);
		LigneApprovisionement ligneApp = LigneApprovisionement.findLigneApprovisionement(pId);
		int remiseAutorise = ProcessHelper.getRemise(ligneApp).intValue();
		BigInteger qteStock = ligneApp.getQuantieEnStock();
		LigneCmdClient sameCipm = commandeClient.getItemHasSameCipm(ligneApp.getCipMaison());
		if(sameCipm !=null)qteStock = qteStock.subtract(sameCipm.getQuantiteCommande());
		SaleProcess saleProcess = new SaleProcess(commandeClient, uiModel);
		BigDecimal remise = BigDecimal.ZERO ;
		if (!"".equals(rem)) {
			remise  = new BigDecimal(rem);
		}
		// verifier si la commande est en cour ou annule ou encaisser
		if (commandeClient.getStatus().equals(Etat.CLOS)) {
				uiModel.addAttribute("apMessage", messageSource.getMessage("sale_add_product_close_warning", null, LocaleUtil.getCurrentLocale()));
		}else if (commandeClient.getEncaisse()) {
			uiModel.addAttribute("apMessage", messageSource.getMessage("sale_add_product_cash_warningsale_add_product_cash_warning", null, LocaleUtil.getCurrentLocale()));
		}else if (commandeClient.getAnnuler()) {
			uiModel.addAttribute("apMessage", messageSource.getMessage("sale_add_product_cancel_warning", null, LocaleUtil.getCurrentLocale()));
		}else if (qte.intValue() == 0) {
			uiModel.addAttribute("apMessage",  messageSource.getMessage("sale_add_product_order_qty", null, LocaleUtil.getCurrentLocale())); 
			// test Logger
			LOGS.error("Impossible de vendre un produit dont la quantite est egale a 0");
			saleProcess.setProduit(ligneApp);
			uiModel.addAttribute("qte",qte);
		}else if (qteStock.intValue() < qte.intValue()) {
			uiModel.addAttribute("apMessage", messageSource.getMessage("sale_add_product_order_stock", null, LocaleUtil.getCurrentLocale()) +ligneApp.getQuantieEnStock().intValue());
			LOGS.error("Impossible de vendre un produit dont la quantite en stock est inferieure a la quantite commandee");
			ligneApp.setPrixVenteUnitaire(pu);
			saleProcess.setProduit(ligneApp);
			uiModel.addAttribute("qte",qte);
			uiModel.addAttribute("rem",remise);
			uiModel.addAttribute("pt",ligneApp.getPrixVenteUnitaire().multiply(BigDecimal.valueOf(qte.intValue())).longValue());
			if(configuration.getSaleForce()) {
				RoleName[] roleNames = {RoleName.ROLE_VENTE_FORCEE,RoleName.ROLE_SITE_MANAGER};
				if(SecurityUtil.getPharmaUser().hasAnyRole(Arrays.asList(roleNames)))uiModel.addAttribute("forcer",Boolean.TRUE);
			}

		}/*else if (commandeClient.contientSameCipM(ligneApp.getCipMaison())) {
			LigneCmdClient sameCipM = commandeClient.getSameCipM(ligneApp.getCipMaison());
        return updateCmdLine(cmdId,  sameCipM.getId(), qte, rem, uiModel, httpServletRequest);
		}*/ else if (!ligneApp.isVenteAutorise()) {
			uiModel.addAttribute("apMessage", messageSource.getMessage("sale_add_product_active", null, LocaleUtil.getCurrentLocale()));
			saleProcess.setProduit(ligneApp);
			uiModel.addAttribute("qte",qte);
		} else if (ligneApp.getDatePeremtion().before(new Date())) {
			uiModel.addAttribute("apMessage", messageSource.getMessage("sale_add_product_perime", null, LocaleUtil.getCurrentLocale())+" " + PharmaDateUtil.format(ligneApp.getDatePeremtion(), "dd-MM-yyyy"));
			ligneApp.setPrixVenteUnitaire(pu);
			saleProcess.setProduit(ligneApp);
			uiModel.addAttribute("qte",qte);
			uiModel.addAttribute("rem",remise);
			uiModel.addAttribute("pt",ligneApp.getPrixVenteUnitaire().multiply(BigDecimal.valueOf(qte.intValue())).longValue());
			if(configuration.getSaleForce()) {
				RoleName[] roleNames = {RoleName.ROLE_VENTE_FORCEE,RoleName.ROLE_SITE_MANAGER};
				if(SecurityUtil.getPharmaUser().hasAnyRole(Arrays.asList(roleNames)))uiModel.addAttribute("forcer",Boolean.TRUE);

			}

		} else{

			if(SecurityUtil.getPharmaUser().hasAnyRole(RoleName.ROLE_CHANGER_PRIX_VENTE)){
				if(pu!=null){
					if(pu.compareTo(ligneApp.getPrixAchatUnitaire())==1 || pu.compareTo(ligneApp.getPrixAchatUnitaire())==0){
						ligneApp.setPrixVenteUnitaire(pu);
					}else{
						uiModel.addAttribute("apMessage", messageSource.getMessage("sale_add_product_update_price1", null, LocaleUtil.getCurrentLocale()) +"["+pu+"cfa"+"]"+  messageSource.getMessage("sale_add_product_update_price2", null, LocaleUtil.getCurrentLocale()) +"["+ligneApp.getPrixAchatUnitaire()+"cfa"+"]");
					}
				}
			}

			if (remiseAutorise < remise.intValue()) {
				uiModel.addAttribute("apMessage", messageSource.getMessage("sale_add_product_discount_warning", null, LocaleUtil.getCurrentLocale())+" "+remiseAutorise);
				remise = BigDecimal.ZERO ;

			}
			
			// Condition pour modifier le prix de vente pour les ventes en details, semi-gros et gros
			
			SaleProcess.addline(pId, qte, remise, commandeClient);

		}
		saleProcess.calculPrix(commandeClient);
		saleProcess.setLigneCommande(LigneCmdClient.findLigneCmdClientsByCommande(commandeClient).getResultList());
		uiModel.addAttribute("saleProcess",saleProcess);
		return "saleprocess/edit";

	}

	//@Transactional
	@RequestMapping(value = "/{cmdId}/addLineForcer", method = RequestMethod.POST)
	public String addLineForcer(@PathVariable("cmdId") Long cmdId,@RequestParam Long pId,@RequestParam BigInteger qte,
			@RequestParam String rem,Model uiModel,HttpServletRequest httpServletRequest) {
		CommandeClient commandeClient = CommandeClient.findCommandeClient(cmdId);
		LigneApprovisionement ligneApp = LigneApprovisionement.findLigneApprovisionement(pId);
		SaleProcess saleProcess = new SaleProcess(commandeClient, uiModel);
		int remiseAutorise = ProcessHelper.getRemise(ligneApp).intValue();
		BigDecimal remise = BigDecimal.ZERO ;
		if (StringUtils.isNotBlank(rem)) {
			remise  = new BigDecimal(rem);
		}
		if (remiseAutorise < remise.intValue()) {
			uiModel.addAttribute("apMessage", messageSource.getMessage("sale_add_product_discount_warning", null, LocaleUtil.getCurrentLocale()) +remiseAutorise);
		}

		// verifier si la commande est en cour ou annuler ou encaisser
		if (commandeClient.getStatus().equals(Etat.CLOS)) {
			uiModel.addAttribute("apMessage", messageSource.getMessage("sale_add_product_close_warning", null, LocaleUtil.getCurrentLocale()));
		}else if (commandeClient.getEncaisse()) {
			uiModel.addAttribute("apMessage", messageSource.getMessage("sale_add_product_cash_warning", null, LocaleUtil.getCurrentLocale()));
		}else if (commandeClient.getAnnuler()) {
			uiModel.addAttribute("apMessage", messageSource.getMessage("sale_add_product_cancel_warning", null, LocaleUtil.getCurrentLocale()));
		}else if (qte.intValue() == 0) {
			uiModel.addAttribute("apMessage", messageSource.getMessage("sale_add_product_order_qty", null, LocaleUtil.getCurrentLocale()));
		}else{

			Configuration config = Configuration.findConfiguration(new Long(1));
			if(config.getForceToSameLine()){
				SaleProcess.addToExistingline(pId, qte, remise, commandeClient);
			}else {
				SaleProcess.addline(pId, qte, remise, commandeClient);
			}

		}
		saleProcess.calculPrix(commandeClient);
		saleProcess.setLigneCommande(LigneCmdClient.findLigneCmdClientsByCommande(commandeClient).getResultList());
		uiModel.addAttribute("saleProcess",saleProcess);
		return "saleprocess/edit";

	}

	@RequestMapping(value = "/{cmdId}/anulerVenteForcer/{lineId}", method = RequestMethod.GET)
	public String anulleVenteFore(@PathVariable("cmdId") Long cmdId,@PathVariable Long lineId,Model uiModel) {
		CommandeClient commandeClient = CommandeClient.findCommandeClient(cmdId);
		LigneApprovisionement ligneApp = LigneApprovisionement.findLigneApprovisionement(lineId);
		SaleProcess saleProcess = new SaleProcess(commandeClient, uiModel);
		if (ligneApp.getQuantieEnStock().intValue() > 0) {
			saleProcess.setProduit(ligneApp);
			uiModel.addAttribute("qte",1);
			uiModel.addAttribute("pt",ligneApp.getPrixVenteUnitaire().multiply(BigDecimal.valueOf(1)).longValue());

		}
		saleProcess.setLigneCommande(LigneCmdClient.findLigneCmdClientsByCommande(commandeClient).getResultList());
		uiModel.addAttribute("saleProcess",saleProcess);
		return "saleprocess/edit";

	}

	// send the line to update to the browser
	//@Transactional
	@RequestMapping(value = "/{cmdId}/updateLine/{lnId}",  method = RequestMethod.GET)
	public String updateLineForm(@PathVariable("lnId") Long lnId,@PathVariable("cmdId") Long cmdId, Model uiModel) {
		CommandeClient commandeClient = CommandeClient.findCommandeClient(cmdId);
		LigneCmdClient ligne = LigneCmdClient.findLigneCmdClient(lnId);
		String cipm = ligne.getCipM();
		LigneApprovisionement ligneApprovisionement = LigneApprovisionement.findLigneApprovisionementsByCipMaisonEquals(cipm).getResultList().iterator().next();
		BigDecimal prixVenteUnitaire = ligneApprovisionement.getPrixVenteUnitaire();
		SaleProcess saleProcess = new SaleProcess(commandeClient, uiModel);
		saleProcess.setLigneCommande(LigneCmdClient.findLigneCmdClientsByCommande(commandeClient).getResultList());
		saleProcess.setLineToUpdate(ligne);
		uiModel.addAttribute("saleProcess",saleProcess);
		uiModel.addAttribute("prixunit", prixVenteUnitaire);
		return "saleprocess/edit";
	}

	@Transactional
	@RequestMapping(value = "/{cmdId}/addGlobalRemise",  method = RequestMethod.POST)
	public String addGlobalRemise(@PathVariable("cmdId") Long cmdId,@RequestParam("rem2") BigDecimal remise , Model uiModel) {
		CommandeClient commandeClient = CommandeClient.findCommandeClient(cmdId);
		List<LigneCmdClient> resultList = LigneCmdClient.findLigneCmdClientsByCommande(commandeClient).getResultList();
		BigDecimal remises = BigDecimal.ZERO;
		SaleProcess saleProcess = new SaleProcess(uiModel);
		if (resultList.isEmpty()) {
			uiModel.addAttribute("apMessage", messageSource.getMessage("sale_add_product_discount_not", null, LocaleUtil.getCurrentLocale())); 

		}else {
			if (remise != null) {
				if (commandeClient.validaterRemiseGlobale(uiModel, remise)) {
					remises = remise ;
				}				
			}
		}
		saleProcess = new  SaleProcess(commandeClient,remises, null);
		saleProcess.setLigneCommande(resultList);
		uiModel.addAttribute("saleProcess",saleProcess);
		return "saleprocess/edit";
	}
	@Transactional
	@RequestMapping(value = "/{cmdId}/addGlobalRemisePourcentage",  method = RequestMethod.POST)
	public String addGlobalRemisePourcentage(@PathVariable("cmdId") Long cmdId,@RequestParam("rem3") BigDecimal remise , Model uiModel) {
		CommandeClient commandeClient = CommandeClient.findCommandeClient(cmdId);
		List<LigneCmdClient> resultList = LigneCmdClient.findLigneCmdClientsByCommande(commandeClient).getResultList();
		BigDecimal remises = BigDecimal.ZERO;
		SaleProcess saleProcess = new SaleProcess(uiModel);
		if (resultList.isEmpty()) {
			uiModel.addAttribute("apMessage", messageSource.getMessage("sale_add_product_discount_not", null, LocaleUtil.getCurrentLocale()));

		}else {
			if (remise != null) {
				remise = commandeClient.getMontantTtc().multiply(remise).divide(BigDecimal.valueOf(100));
				if (commandeClient.validaterRemiseGlobale(uiModel, remise)) {
					remises = remise ;
				}

			}


		}
		saleProcess = new  SaleProcess(commandeClient,remises, null);
		saleProcess.setLigneCommande(resultList);
		uiModel.addAttribute("saleProcess",saleProcess);
		return "saleprocess/edit";
	}

	//update line

	@RequestMapping(value = "/{cmdId}/updateLine", method = RequestMethod.POST)
	public String updatedLine(@PathVariable("cmdId") Long cmdId,@RequestParam Long lineId,@RequestParam BigInteger qte, 
			@RequestParam(required=false)BigDecimal pu,	@RequestParam String rem,Model uiModel,HttpServletRequest httpServletRequest) {
		return updateCmdLine(cmdId, lineId, qte, pu, rem, uiModel, httpServletRequest) ;
	}
	/*
	 * use for update line of commande
	 */
	public String updateCmdLine(Long cmdId,Long lineId,BigInteger qte, BigDecimal pu, String rem,Model uiModel,HttpServletRequest httpServletRequest){
		SessionBean sessionBean =	 (SessionBean) httpServletRequest.getSession().getAttribute("sessionBean") ;
		Configuration configuration = sessionBean.getConfiguration();
		LigneCmdClient line =  LigneCmdClient.findLigneCmdClient(lineId);
		CommandeClient commandeClient=line.getCommande();
		BigInteger qtc  =  qte  ;//line.getQuantiteCommande().add(qte);
		SaleProcess saleProcess = new SaleProcess(commandeClient, uiModel);
		int remiseAutorise = ProcessHelper.getRemise(line.getProduit()).intValue();
		BigDecimal remise = BigDecimal.ZERO ;
		line.setPrixUnitaire(pu);
		if (!"".equals(rem)) {
			remise  = new BigDecimal(rem);
		}
		if (commandeClient.getAnnuler()|| commandeClient.getEncaisse()||commandeClient.getStatus().equals(Etat.CLOS)) {
			uiModel.addAttribute("apMessage","");
			saleProcess.calculPrix(commandeClient);
			saleProcess.setLigneCommande(LigneCmdClient.findLigneCmdClientsByCommande(commandeClient).getResultList());
			uiModel.addAttribute("saleProcess",saleProcess);
			return "saleprocess/edit";
		}
		if (line.getProduit().getQuantieEnStock().intValue() < qtc.intValue()) {
			LigneApprovisionement ligneApp = LigneApprovisionement.findLigneApprovisionement(line.getProduit().getId());
			uiModel.addAttribute("apMessage", messageSource.getMessage("sale_add_product_order_stock", null, LocaleUtil.getCurrentLocale()) +line.getProduit().getQuantieEnStock().intValue());
			saleProcess.setProduit(ligneApp);
			uiModel.addAttribute("qte",qte);
			uiModel.addAttribute("rem",remise);
			uiModel.addAttribute("pt",ligneApp.getPrixVenteUnitaire().multiply(BigDecimal.valueOf(qte.intValue())).longValue());
			if(configuration.getSaleForce()) {
				RoleName[] roleNames = {RoleName.ROLE_VENTE_FORCEE,RoleName.ROLE_SITE_MANAGER};
				if(SecurityUtil.getPharmaUser().hasAnyRole(Arrays.asList(roleNames)))uiModel.addAttribute("forcer",Boolean.TRUE);

			}
			saleProcess.calculPrix(commandeClient);
			saleProcess.setLigneCommande(LigneCmdClient.findLigneCmdClientsByCommande(commandeClient).getResultList());
			uiModel.addAttribute("saleProcess",saleProcess);
			return "saleprocess/edit";


		}
		if (remiseAutorise < remise.intValue()) {
			uiModel.addAttribute("apMessage", messageSource.getMessage("sale_add_product_discount_warning", null, LocaleUtil.getCurrentLocale()) +remiseAutorise);
			saleProcess.setLineToUpdate(line);
			remise = BigDecimal.ZERO ;
		}
		line.setQuantiteCommande(qtc);
		line.setRemise(remise);
		line.calculPrixTotal();
		line.calculremiseTotal();
		line.merge();

		saleProcess.calculPrix(commandeClient);
		saleProcess.setLigneCommande(LigneCmdClient.findLigneCmdClientsByCommande(commandeClient).getResultList());
		uiModel.addAttribute("saleProcess",saleProcess);
		return "saleprocess/edit";
	}

	//use to select product after chearch
	@RequestMapping(value = "/{cmdId}/selectProduct/{pId}",  method = RequestMethod.GET)
	@ResponseBody
	public String selectProduct(@PathVariable("pId") Long pId,@PathVariable("cmdId") Long cmdId, Model uiModel) {
		LigneApprovisionement ligneApprovisionement = LigneApprovisionement.findLigneApprovisionement(pId);
		ligneApprovisionement.calculRemise();
		/// return ligneApprovisionement.clone().toJson();
		return ligneApprovisionement.toJson();
	}
	@Transactional
	public void saveAndCloseCmd(CommandeClient commandeClient ,Caisse caisse , PharmaUser vendeur){

		Set<LigneCmdClient> lineCommande = commandeClient.getLineCommande();
		commandeClient.calculPrixHtAndRemise();
		commandeClient.calculNetApayer();
		Facture facture= commandeClient.generateFacture();
		facture.setSite(SecurityUtil.getPharmaUser().getOffice());
		facture.avancerPaiement(BigInteger.ZERO);
		facture.setCaisse(caisse);
		facture.setTypeFacture(commandeClient.getTypeFacture());
		facture.setVendeur(vendeur);
		facture.persist();

		if (!lineCommande.isEmpty()) {
			for (LigneCmdClient ligne : lineCommande) {
				LigneFacture ligneFacture= ligne.convertToLigneFacture();
				ligneFacture.setFacture(facture);
				ligneFacture.persist();
			}
		}
		commandeClient.setVendeur(vendeur);
		commandeClient.setStatus(Etat.CLOS);
		commandeClient.setFactureId(facture.getId());
		commandeClient.setAnnuler(Boolean.FALSE);
		commandeClient.merge();

	}

	//delete line to  the commande
	@RequestMapping(value = "/{cmdId}/deleteLine/{lnId}", method = RequestMethod.GET)
	public String deleteLine(@PathVariable("cmdId") Long cmdId,@PathVariable("lnId") Long lnId, HttpServletRequest httpServletRequest) {
		LigneCmdClient line =  LigneCmdClient.findLigneCmdClient(lnId);
		CommandeClient commandeClient=line.getCommande();
		if (commandeClient.getAnnuler()|| commandeClient.getEncaisse()||commandeClient.getStatus().equals(Etat.CLOS)) {
			httpServletRequest.setAttribute("apMessage", messageSource.getMessage("sale_add_product_operation", null, LocaleUtil.getCurrentLocale()));
			return "forward:/saleprocess/" + ProcessHelper.encodeUrlPathSegment(cmdId.toString(), httpServletRequest)+"/edit";
		}
		else{
			LigneCmdClient.findLigneCmdClient(lnId).remove();
		} 
		return "redirect:/saleprocess/" + ProcessHelper.encodeUrlPathSegment(cmdId.toString(), httpServletRequest)+"/edit";

	}
	@RequestMapping(value="/findCmd", params = "find=ByDateCreationBetween", method = RequestMethod.GET)
	public String findCommandeClientsByDateCreationBetween(@RequestParam("minDateCreation")  @DateTimeFormat(pattern = "dd-MM-yyyy") Date minDateCreation, @RequestParam("maxDateCreation")  @DateTimeFormat(pattern = "dd-MM-yyyy") Date maxDateCreation, Model uiModel) {
		uiModel.asMap().clear();
		uiModel.addAttribute("commandeclients", CommandeClient.findCommandeClientsByDateCreationBetweenNotEncaisser(minDateCreation, maxDateCreation).getResultList());
		return "saleprocess/rechercheCmd";
	}
	@RequestMapping(value="/findCmd",params = "find=ByStatusAndDateCreationBetween", method = RequestMethod.GET)
	public String findCommandeClientsByStatusAndDateCreationBetween(@RequestParam("status") Etat status, @RequestParam("minDate")  @DateTimeFormat(pattern = "dd-MM-yyyy") Date minDateCreation, @RequestParam("maxDate")  @DateTimeFormat(pattern = "dd-MM-yyyy") Date maxDateCreation, Model uiModel) {
		uiModel.asMap().clear();
		uiModel.addAttribute("commandeclients", CommandeClient.findCommandeClientsByStatusAndDateCreationBetweenNotEncaisser(status, minDateCreation, maxDateCreation).getResultList());
		return "saleprocess/rechercheCmd";
	}


	// imprime les factures 
	@RequestMapping(value = "/print/{invId}.pdf", method = RequestMethod.GET)
	public String print(@RequestParam(value = "nom", required = false) String nom,
			@RequestParam(value= "dateFacturation", required = false)  @DateTimeFormat(pattern = "dd-MM-yyyy") Date dateFacturation,
			@RequestParam(value = "remise", required = false) Boolean remise , @PathVariable("invId")Long invId, Model uiModel){
		Facture facture = Facture.findFacture(invId);
		uiModel.addAttribute("nom", nom);
		uiModel.addAttribute("dateFacturation", dateFacturation);
		System.out.println(remise);
		facture.setPrintWithReduction(remise!=null?Boolean.TRUE:Boolean.FALSE);
		uiModel.addAttribute("facture", facture);
		return "facturePdfDocViews";
	}
	// imprime les ticket
	@RequestMapping(value = "/printTickets/{invId}.pdf", method = RequestMethod.GET)
	public String printTicket(@RequestParam(value = "nom", required = false) String nom,
			@RequestParam(value= "dateTicket", required = false)  @DateTimeFormat(pattern = "dd-MM-yyyy") Date dateTicket,
			@RequestParam(value = "remise", required = false) Boolean remise , @PathVariable("invId")Long invId, Model uiModel){
		Facture facture = Facture.findFacture(invId);
		uiModel.addAttribute("nom", nom);
		uiModel.addAttribute("dateTicket", dateTicket);
		Paiement paiement = facture.getCommande().getPaiements();
		paiement.setReduction(remise!=null?Boolean.TRUE:Boolean.FALSE);
		uiModel.addAttribute("paiement", paiement);
		return "ticketPdfDocView";

	}


	//suprime la commande en cour de creation
	@RequestMapping(value = "/{cmdId}/annuler", method = RequestMethod.GET)
	public String annuler(@PathVariable("cmdId") Long cmdId, Model uiModel) {

		CommandeClient commandeClient = CommandeClient.findCommandeClient(cmdId);

		if (commandeClient.getStatus().equals(Etat.CLOS) && !commandeClient.getEncaisse()) {
			commandeClient.setAnnuler(true);
			commandeClient.setStatus(Etat.EN_COUR);
			commandeClient.merge();
			uiModel.addAttribute("apMessage", messageSource.getMessage("command_cancel_success", null, LocaleUtil.getCurrentLocale()));
		}

		if (commandeClient.getStatus().equals(Etat.EN_COUR)) {
			commandeClient.remove();
			uiModel.addAttribute("apMessage", messageSource.getMessage("command_remove_success", null, LocaleUtil.getCurrentLocale()));
		}
		return "caisses/infos"	  ; 
	}

	@ModelAttribute("genres")
	public Collection<Genre> populateGenres() {
		return Arrays.asList(Genre.class.getEnumConstants());
	}
	@ModelAttribute("categorieclients")
	public Collection<CategorieClient> populateCategorieClients() {
		Collection<CategorieClient> aray = new ArrayList<CategorieClient>();
		aray.addAll(CategorieClient.findCategorieClientsByCategorieNumberEquals("CAT-0000").getResultList());//a revoir
		return aray;

	}
	@ModelAttribute("etats")
	public Collection<Etat> populateEtats() {
		return Arrays.asList(Etat.class.getEnumConstants());
	}
	
	// Retourne la liste des configurations 
	@ModelAttribute("salesConfig")
	public String getSalesConfiguration(){
		List<SalesConfiguration> salesConfigurations= SalesConfiguration.findAllSalesConfigurations();
		String configurations = SalesConfiguration.toJsonArray(salesConfigurations);
		return configurations;
	}
}
