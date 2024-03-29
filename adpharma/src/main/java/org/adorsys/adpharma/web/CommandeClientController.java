
package org.adorsys.adpharma.web;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.adorsys.adpharma.beans.CommandeCredit;
import org.adorsys.adpharma.beans.process.PaiementProcess;
import org.adorsys.adpharma.beans.process.ReturnedProductBean;
import org.adorsys.adpharma.beans.process.SearchSalesBean;
import org.adorsys.adpharma.beans.process.SessionBean;
import org.adorsys.adpharma.domain.AvoirClient;
import org.adorsys.adpharma.domain.Caisse;
import org.adorsys.adpharma.domain.CategorieClient;
import org.adorsys.adpharma.domain.Client;
import org.adorsys.adpharma.domain.CommandeClient;
import org.adorsys.adpharma.domain.Configuration;
import org.adorsys.adpharma.domain.DestinationMvt;
import org.adorsys.adpharma.domain.Etat;
import org.adorsys.adpharma.domain.Facture;
import org.adorsys.adpharma.domain.Genre;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.LigneCmdClient;
import org.adorsys.adpharma.domain.MouvementStock;
import org.adorsys.adpharma.domain.Ordonnancier;
import org.adorsys.adpharma.domain.PharmaUser;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.RoleName;
import org.adorsys.adpharma.domain.TypeClient;
import org.adorsys.adpharma.domain.TypeCommande;
import org.adorsys.adpharma.domain.TypeFacture;
import org.adorsys.adpharma.domain.TypeMouvement;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.services.DefaultReturnedProductService;
import org.adorsys.adpharma.services.ReturnedProductException;
import org.adorsys.adpharma.utils.DateConfig;
import org.adorsys.adpharma.utils.DateConfigPeriod;
import org.adorsys.adpharma.utils.ProcessHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RooWebScaffold(path = "commandeclients", formBackingObject = CommandeClient.class)
@RequestMapping("/commandeclients")
@Controller
public class CommandeClientController {

	@Autowired 
	DefaultReturnedProductService returnProductService ;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String show(@PathVariable("id") Long id, Model uiModel,HttpServletRequest request) {
		addDateTimeFormatPatterns(uiModel);
		CommandeClient commandeClient = CommandeClient.findCommandeClient(id);
		AvoirClient bonClient = commandeClient.getBonClient();
		List<Ordonnancier> ordonnanciers = Ordonnancier.findOrdonnanciersByCommande(commandeClient).getResultList();
		if (!ordonnanciers.isEmpty())  uiModel.addAttribute("ordNumber",ordonnanciers.iterator().next().getOrdNumber());
		if (bonClient !=null)  uiModel.addAttribute("bonClient",bonClient);
		if (commandeClient.getFactureId()!=null) {
			Facture facture = Facture.findFacture(commandeClient.getFactureId());
			if (facture!=null)uiModel.addAttribute("factureNumber",facture.getFactureNumber());
		}
		uiModel.addAttribute("itemId", id);
		uiModel.addAttribute("appMessage", request.getAttribute("msg"));
		uiModel.addAttribute("commandeclient",commandeClient);
		return "saleprocess/showCmd";
	}


	@RequestMapping(value = "/find=venteJournalier", method = RequestMethod.GET)
	public String venteJournalier(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
		DateConfigPeriod period = DateConfig.getBegingEndOfDay(new Date()) ;
		TypedQuery<CommandeClient> typeQuery = CommandeClient.searchTypeQuery(null,Etat.ALL, period.getBegin(), period.getEnd(), null, TypeCommande.ALL) ;
		List<CommandeClient> resultList = typeQuery.getResultList();
		int maxResults = resultList.size();
		if (page != null || size != null) {
			int sizeNo = size == null ? 100 : size.intValue();
			uiModel.addAttribute("commandeclients", typeQuery.setMaxResults(sizeNo).setFirstResult(page == null ? 0 : (page.intValue() - 1) * sizeNo).getResultList());
			float nrOfPages = (float) maxResults/ sizeNo;
			uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
		} else {
			uiModel.addAttribute("commandeclients",resultList);
		}
		addDateTimeFormatPatterns(uiModel);
		return "commandeclients/list";
	}

	//@Transactional
	@RequestMapping(value = "/annulerCmd/{cmdId}", method = RequestMethod.GET)
	public String annulerCmd(@PathVariable("cmdId") Long cmdId, Model uiModel , HttpServletRequest httpServletRequest) {
		CommandeClient commandeClient = CommandeClient.findCommandeClient(cmdId);
		if (commandeClient.getEncaisse()) {
			uiModel.addAttribute("apMessage", "impossible d'annuler COMMANDE DEJA ENCAISSEE !");

		}else {
			commandeClient.annulerCommande(SecurityUtil.getUserName());
			uiModel.addAttribute("apMessage", "Commande Annnuler Avec Succes !");

		}
		addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("commandeclient", CommandeClient.findCommandeClient(cmdId));
		uiModel.addAttribute("itemId", cmdId);

		return "saleprocess/showCmd";
	}
	//@Transactional
	@RequestMapping(value = "/annulerCmdByKey/{cmdId}/{key}", method = RequestMethod.GET)
	public String annulerCmdByKey(@PathVariable("key") String key , @PathVariable("cmdId") Long cmdId, Model uiModel , HttpServletRequest httpServletRequest) {
		CommandeClient commandeClient = CommandeClient.findCommandeClient(cmdId);
		PharmaUser pharmaUser = SecurityUtil.getPharmaUser(key);

		if (commandeClient.getEncaisse()) {
			uiModel.addAttribute("apMessage", "impossible d'annuler COMMANDE DEJA ENCAISSEE !");

		}else if (pharmaUser == null) {
			uiModel.addAttribute("apMessage", "aucun utilisateur trouve avec cette cle !");
		}else if (!pharmaUser.hasAnyRole(RoleName.ROLE_VENDEUR)) {
			uiModel.addAttribute("apMessage", "Desole Vous n'avez pas les droits neccessaires pour Anuller !");
		}else{
			commandeClient.annulerCommande(pharmaUser.getUserName());
			uiModel.addAttribute("apMessage", "Commande Annnuler Avec Succes !");

		}
		addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("commandeclient", CommandeClient.findCommandeClient(cmdId));
		uiModel.addAttribute("itemId", cmdId);

		return "saleprocess/showCmd";
	}

	@Transactional
	@RequestMapping(value = "/restorerCmd/{cmdId}", method = RequestMethod.GET)
	public String restorerCmd(@PathVariable("cmdId") Long cmdId, Model uiModel , HttpServletRequest httpServletRequest) {
		Caisse caisse = PaiementProcess.getOpenCaisse();
		CommandeClient commandeClient = CommandeClient.findCommandeClient(cmdId);
		SessionBean sessionBean =	 (SessionBean) httpServletRequest.getSession().getAttribute("sessionBean") ;
		Configuration configuration = sessionBean.getConfiguration();
		if(configuration.getRestoreCancelSale()){


			if (caisse == null) {
				uiModel.addAttribute("apMessage", "Impossble de restorer cette commande ! AUCUNE CAISSE OUVERTE");

			}else {
				//new SaleProcessController().saveAndCloseCmd(commandeClient, caisse ,SecurityUtil.getPharmaUser(null));
				commandeClient.restorerCommande();
				uiModel.addAttribute("apMessage", "Commande Restorer Avec Succes !");
			}
		}else {
			uiModel.addAttribute("apMessage", "La restoration des commandes Annuler est DESACTIVE !");  
		}
		addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("commandeclient", CommandeClient.findCommandeClient(cmdId));
		uiModel.addAttribute("itemId", cmdId);
		return "saleprocess/showCmd";
	}


	@Transactional
	@RequestMapping(value = "/restorerCmdByKey/{cmdId}", method = RequestMethod.POST)
	public String restorerCmdByKey(@RequestParam("cle") String key ,@PathVariable("cmdId") Long cmdId, Model uiModel , HttpServletRequest httpServletRequest) {
		Caisse caisse = PaiementProcess.getOpenCaisse();
		PharmaUser pharmaUser = SecurityUtil.getPharmaUser(key);
		CommandeClient commandeClient = CommandeClient.findCommandeClient(cmdId);
		ArrayList<RoleName> role = new ArrayList<RoleName>();
		role.add(RoleName.ROLE_VENDEUR);
		role.add(RoleName.ROLE_SITE_MANAGER);
		SessionBean sessionBean =	 (SessionBean) httpServletRequest.getSession().getAttribute("sessionBean");
		Configuration configuration = sessionBean.getConfiguration();
		if(configuration.getRestoreCancelSale()){


			if (caisse == null) {
				uiModel.addAttribute("apMessage", "Impossble de restorer cette commande ! AUCUNE CAISSE OUVERTE");

			}else if (key == null) {
				uiModel.addAttribute("apMessage", " veullez saisir  La cle de validation !") ;
			}else if (pharmaUser== null) {
				uiModel.addAttribute("apMessage", "La cle de validation est incorrecte") ;
			}else if (!pharmaUser.hasAnyRole(role)) {
				uiModel.addAttribute("apMessage", "Vous n'avez pas les droits necessaire Pour Cloturer cette vente") ;
			}else if (!commandeClient.validaterCmd(uiModel) ){

			}else {
				new SaleProcessController().saveAndCloseCmd(commandeClient, caisse ,pharmaUser);
				uiModel.addAttribute("apMessage", "Commande Restorer Avec Succes !");

			}

		}else {
			uiModel.addAttribute("apMessage", "La restoration des commandes Annuler A etee Desactive !");
		}

		addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("commandeclient", commandeClient);
		uiModel.addAttribute("itemId", cmdId);

		return "saleprocess/showCmd";
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
		CommandeClient commandeClient = CommandeClient.findCommandeClient(id);
		if (commandeClient.getEncaisse()) {
			uiModel.addAttribute("apMessage", "Impossible de suprimer la commande ! COMMANDE DEJA ENCASSEE ");
			commandeClient.annulerCommande( SecurityUtil.getUserName());
			addDateTimeFormatPatterns(uiModel);
			uiModel.addAttribute("commandeclient", commandeClient);
			uiModel.addAttribute("itemId", commandeClient.getId());
			return "saleprocess/showCmd";
		}
		commandeClient.remove() ;
		uiModel.asMap().clear();
		uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
		uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
		return "redirect:/commandeclients";
	}

	@RequestMapping(value = "/cmdCredit", params = "form", method = RequestMethod.GET)
	public String cmdCreditForm(Model uiModel) {
		uiModel.addAttribute("commandeCredit",new CommandeCredit());
		uiModel.addAttribute("client",new Client());
		uiModel.addAttribute("typeclients",Arrays.asList(TypeClient.class.getEnumConstants()));
		uiModel.addAttribute("genres",Arrays.asList(Genre.class.getEnumConstants()));
		uiModel.addAttribute("categorieclients",CategorieClient.findAllCategorieClients());

		return "clients/cmdCredit";
	}

	@RequestMapping(value = "/cmdCreditInfosClient/{cmdId}", params = "form", method = RequestMethod.GET)
	public String cmdCreditInfosClientForm(@PathVariable("cmdId") Long cmdId,Model uiModel) {
		CommandeClient commandeClient = CommandeClient.findCommandeClient(cmdId);
		if (commandeClient.getStatus().equals(Etat.EN_COUR)) {
			CommandeCredit commandeCredit = new CommandeCredit(commandeClient);
			uiModel.addAttribute("commandeCredit",commandeCredit);
		}
		return "clients/cmdCredit";

	}


	@RequestMapping(value = "/cmdCredit", method = RequestMethod.POST)
	public String cmdCredit(@Valid CommandeCredit commandeCredit,Model uiModel ,HttpServletRequest httpServletRequest) {

		if (!commandeCredit.valider(uiModel)) {
			uiModel.addAttribute("commandeCredit", commandeCredit);
			return "clients/cmdCredit";
		}
		uiModel.asMap().clear();
		CommandeClient commandeClient = new CommandeClient();
		commandeClient.setTypeCommande(TypeCommande.VENTE_A_CREDIT);
		commandeClient.setTauxCouverture(commandeCredit.getTaux().intValue());
		Client client = Client.findClient(commandeCredit.getClientId());
		Client payeur = Client.findClient(commandeCredit.getPayeurId());
		commandeClient.setClient(client);
		commandeClient.setClientPaiyeur(payeur);
		commandeClient.setNumeroBon(commandeCredit.getNumeroBon());
		commandeClient.setVentePartiel(commandeCredit.isVentePartiel());
		commandeClient.persist();
		return "redirect:/saleprocess/" + ProcessHelper.encodeUrlPathSegment(commandeClient.getId().toString(), httpServletRequest)+"/edit";
	}

	@RequestMapping(value = "/findClientForCmd",  method = RequestMethod.GET)
	public String findClientForCmd(Model uiModel) {
		uiModel.addAttribute("isClient","true");
		return "clients/findClientForcmdCredit";
	}

	@RequestMapping( value="/findclient/{isClient}/{clientId}", params = "find=ByNomCompletLike", method = RequestMethod.GET)
	public String findClientsByNomCompletLike(@PathVariable("clientId") String clientId ,@PathVariable("isClient") Boolean isClient, @RequestParam("nomComplet") String nomComplet, Model uiModel) {
		System.out.println("in");
		uiModel.addAttribute("clientfound", Client.findClientsByNomLike(nomComplet).getResultList());
		uiModel.addAttribute("isClient", isClient);
		if (!isClient) {
			uiModel.addAttribute("idClient", clientId);
		}
		return "clients/findClientForcmdCredit";
	}
	@RequestMapping(value="/findclient/{isClient}/{clientId}",params = "find=ByClientNumberEquals", method = RequestMethod.GET)
	public String findClientsByClientNumberEquals(@PathVariable("isClient") Boolean isClient,@PathVariable("clientId") String clientId  ,@RequestParam("clientNumber") String clientNumber, Model uiModel) {
		uiModel.addAttribute("clientfound", Client.findClientsByClientNumberEquals(clientNumber).getResultList());
		uiModel.addAttribute("isClient", isClient);
		if (!isClient) {
			uiModel.addAttribute("idClient", clientId);
		}
		return "clients/findClientForcmdCredit";
	}

	@RequestMapping(value="/selectclient/{clientId}", method = RequestMethod.GET)
	public String selectClient(@PathVariable("clientId") Long clientId, Model uiModel) {
		Client client = Client.findClient(clientId);
		Client payeur = client.getClientPayeur();
		CommandeCredit commandeCredit = new CommandeCredit(client) ;
		commandeCredit.setTaux(client.getTauxCouverture().toBigInteger()); 
		if (payeur!=null) {
			commandeCredit.setPayeurId(payeur.getId());
			commandeCredit.setPayeurName(payeur.getNomComplet());
		}
		uiModel.addAttribute("commandeCredit", commandeCredit);
		return "clients/cmdCredit";
	}

	@RequestMapping(value="/{clientId}/findClientPayeur", method = RequestMethod.GET)
	public String findClientPayeur(@PathVariable("clientId") String clientId, Model uiModel) {
		uiModel.addAttribute("isClient","false");
		uiModel.addAttribute("idClient", clientId);
		return "clients/findClientForcmdCredit";
	}

	@RequestMapping(value="/{clientId}/selectpayeur/{payeurId}", method = RequestMethod.GET)
	public String selectPayeur(@PathVariable("clientId") Long clientId,@PathVariable("payeurId") Long payeurId, Model uiModel) {
		Client client = Client.findClient(clientId);
		Client payeur = client.findClient(payeurId);
		CommandeCredit commandeCredit = new CommandeCredit(client) ;
		commandeCredit.setTaux(client.getTauxCouverture().toBigInteger()); 
		commandeCredit.setPayeurId(payeur.getId());
		commandeCredit.setPayeurName(payeur.getNomComplet());
		uiModel.addAttribute("commandeCredit", commandeCredit);
		return "clients/cmdCredit";
	}

	@Transactional
	@RequestMapping(value="/convertirEnVentePublic/{cmdId}", method = RequestMethod.GET)
	public String convertirEnVentePublic(@PathVariable("cmdId") Long cmdId, Model uiModel) {
		CommandeClient commandeClient = CommandeClient.findCommandeClient(cmdId);
		commandeClient.setTypeCommande(TypeCommande.VENTE_AU_PUBLIC);
		Facture facture = Facture.findFacture(commandeClient.getFactureId());
		facture.setTypeFacture(TypeFacture.CAISSE);
		commandeClient.merge();
		facture.setTypeCommande(commandeClient.getTypeCommande());
		facture.merge();
		uiModel.addAttribute("apMessage", "Commande convertie avec success !");
		addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("commandeclient", commandeClient);
		uiModel.addAttribute("itemId", cmdId);
		return "saleprocess/showCmd";
	}

	@Transactional
	@RequestMapping(value="/convertirEnVenteCredit/{cmdId}", method = RequestMethod.GET)
	public String convertirEnVenteCredit(@PathVariable("cmdId") Long cmdId, Model uiModel) {
		CommandeClient commandeClient = CommandeClient.findCommandeClient(cmdId);
		commandeClient.setTypeCommande(TypeCommande.VENTE_A_CREDIT);
		Facture facture = Facture.findFacture(commandeClient.getFactureId());
		facture.setTypeFacture(TypeFacture.CAISSE);
		commandeClient.merge();
		facture.setTypeCommande(commandeClient.getTypeCommande());
		facture.merge();
		uiModel.addAttribute("apMessage", "Commande convertie avec success !");
		addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("commandeclient", commandeClient);
		uiModel.addAttribute("itemId", cmdId);
		return "saleprocess/showCmd";

	}


	@RequestMapping(value="/{cmdId}/retourProduit/{cipm}", method = RequestMethod.GET)
	@ResponseBody
	public String findReturnedLineAjax(@PathVariable("cmdId") Long cmdId,@PathVariable("cipm") String cipm, Model uiModel) {
		CommandeClient commandeClient = CommandeClient.findCommandeClient(cmdId);
		if (commandeClient!=null) {
			List<LigneCmdClient> resultList = LigneCmdClient.findLigneCmdClientsByCipMEqualsAndCommande(cipm, commandeClient).getResultList();
			if (!resultList.isEmpty()) {
				LigneCmdClient next = resultList.iterator().next();
				return next.getStringFormat();
			}
		}

		return null;

	}

	@RequestMapping(value="findDetails/ByAjax/{cmdId}", method = RequestMethod.GET)
	@ResponseBody
	public String findDetails(@PathVariable("cmdId") Long cmdId, Model uiModel) {
		CommandeClient commandeClient = CommandeClient.findCommandeClient(cmdId);
		List<LigneCmdClient> resultList= new ArrayList<LigneCmdClient>();
		if (commandeClient==null) return null;
		resultList = LigneCmdClient.findLigneCmdClientsByCommande(commandeClient).getResultList();
		if(resultList.isEmpty()){
			return null;
		}
		return  LigneCmdClient.toDeepJsonArray(resultList);
	}

	@RequestMapping(value = "/searchVente", method = RequestMethod.GET)
	public String search(@RequestParam("name") String  name,  Model uiModel) {

		if("".equals(name)){
			Integer page = 1;
			Integer size = 50;
			int sizeNo = size == null ? 10 : size.intValue();
			uiModel.addAttribute("commandeclients", CommandeClient.findCommandeClientEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
			float nrOfPages = (float) CommandeClient.countCommandeClients() / sizeNo;
			uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
		}else{
			List<CommandeClient> list = CommandeClient.findCommandeClientByNomClientLike(name).setMaxResults(50).getResultList();
			uiModel.addAttribute("commandeclients", list);
		}
		return "commandeclients/list";
	}

	@Transactional
	@RequestMapping(value="/{cmdId}/annulerRetourProduit")
	public String annulerRetourProduit(@PathVariable("cmdId") Long cmdId,@RequestParam("cipm") String cipm, Model uiModel,HttpServletRequest request) {
		LigneCmdClient line = LigneCmdClient.findLigneCmdClientsByCipMEqualsAndCommande(cipm, CommandeClient.findCommandeClient(cmdId)).getResultList().iterator().next();
		Produit produit = line.getProduit().getProduit();
		LigneApprovisionement ligneApprovisionement = line.getProduit();
		CommandeClient commandeClient = CommandeClient.findCommandeClient(cmdId);
		AvoirClient bonClient = commandeClient.getBonClient();
		if (bonClient!=null) {
			bonClient.decreaseAmount((line.getPrixUnitaire().multiply(new BigDecimal(line.getQuantiteRetourne()))));
			bonClient.merge();


		}
		MouvementStock mouvementStock = new MouvementStock();
		mouvementStock.setAgentCreateur(SecurityUtil.getUserName());
		mouvementStock.setCip(line.getCip());
		mouvementStock.setCipM(line.getCipM());
		mouvementStock.setDesignation(line.getDesignation());
		mouvementStock.setOrigine(DestinationMvt.MAGASIN);
		mouvementStock.setDestination(DestinationMvt.CLIENT);
		mouvementStock.setTypeMouvement(TypeMouvement.ANNULATION_RETOUR);
		mouvementStock.setQteInitiale(produit.getQuantiteEnStock());
		mouvementStock.setQteDeplace(line.getQuantiteRetourne());
		produit.removeProduct(line.getQuantiteRetourne());
		mouvementStock.setQteFinale(produit.getQuantiteEnStock());
		mouvementStock.setNumeroTicket(Facture.findFacture(commandeClient.getFactureId()).getFactureNumber());
		produit.merge();
		mouvementStock.persist();
		ligneApprovisionement.annulerRetourEnStock(line.getQuantiteRetourne());
		line.setQuantiteRetourne(BigInteger.ZERO);
		line.merge();
		ligneApprovisionement.merge();
		request.setAttribute("msg", "PRODUIT RETOURNE AVEC SUCCES ! "+cipm);
		return "forward:/commandeclients/" + encodeUrlPathSegment(commandeClient.getId().toString(), request);



	}

	@Transactional
	@RequestMapping(value="/{cmdId}/retourProduit", method = RequestMethod.POST)
	public String saveRetour(@PathVariable("cmdId") Long cmdId,@Valid ReturnedProductBean returnedProductBean ,  Model uiModel,HttpServletRequest request) {
		LigneCmdClient line = LigneCmdClient.findLigneCmdClientsByCipMEqualsAndCommande(returnedProductBean.getCipm(), CommandeClient.findCommandeClient(cmdId)).getResultList().iterator().next();
		Produit produit = line.getProduit().getProduit();
		LigneApprovisionement ligneApprovisionement = line.getProduit();
		CommandeClient commandeClient = CommandeClient.findCommandeClient(cmdId);

		if(commandeClient.getTypeCommande().equals(TypeCommande.VENTE_PROFORMAT)){
			uiModel.addAttribute("apMessage", "Impossible de retourner Les Produits D'une Vente  Proformat !");
			return show(commandeClient.getId(), uiModel, request);
		}
		try {
			returnProductService.returnProductFromSaleOrder(line, returnedProductBean);
		} catch (ReturnedProductException e) {
			uiModel.addAttribute("apMessage", e.getMessage());
			e.printStackTrace();
		}
		return "redirect:/commandeclients/" + encodeUrlPathSegment(commandeClient.getId().toString(), request);
	}

	@RequestMapping(params = { "find=BySearch", "form" }, method = RequestMethod.GET)
	public String Search(Model uiModel) {
		addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("searchSalesBean", new SearchSalesBean());
		return "commandeclients/search";
	}

	@RequestMapping(params = "find=BySearch", method = RequestMethod.GET)
	public String Search(SearchSalesBean searchSalesBean,Model uiModel) {
		uiModel.addAttribute("results", searchSalesBean.search());
		uiModel.addAttribute("searchSalesBean", searchSalesBean);
		addDateTimeFormatPatterns(uiModel);
		return "commandeclients/search";
	}

	@ModelAttribute("clients")
	public Collection<Client> populateClients() {
		return new ArrayList<Client>();	 
	}

	@ModelAttribute("commandeclients")
	public Collection<CommandeClient> populateCommandeClients() {
		return new ArrayList<CommandeClient>();	 
	}

	@ModelAttribute("lignecmdclients")
	public Collection<LigneCmdClient> populateLigneCmdClients() {
		return new ArrayList<LigneCmdClient>();	 
	}

	@ModelAttribute("pharmausers")
	public Collection<PharmaUser> populatePharmaUsers() {
		return ProcessHelper.populateUsers();
	}
}
