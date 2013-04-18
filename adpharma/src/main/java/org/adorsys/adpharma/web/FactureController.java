package org.adorsys.adpharma.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.adorsys.adpharma.domain.Caisse;
import org.adorsys.adpharma.domain.Client;
import org.adorsys.adpharma.domain.CommandeClient;
import org.adorsys.adpharma.domain.Facture;
import org.adorsys.adpharma.domain.Filiale;
import org.adorsys.adpharma.domain.LigneCmdClient;
import org.adorsys.adpharma.domain.LigneFacture;
import org.adorsys.adpharma.domain.Paiement;
import org.adorsys.adpharma.domain.PharmaUser;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.Rayon;
import org.adorsys.adpharma.domain.Site;
import org.adorsys.adpharma.domain.TypeCommande;
import org.adorsys.adpharma.domain.TypeFacture;
import org.adorsys.adpharma.utils.ProcessHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RooWebScaffold(path = "factures", formBackingObject = Facture.class)
@RequestMapping("/factures")
@Controller
public class FactureController {

	// imprime les factures 
	@RequestMapping(value = "/print/{invNumber}.pdf", method = RequestMethod.GET)
	public String print(@PathVariable("invNumber")String invNumber, Model uiModel){
		List<Facture> resultList = Facture.findFacturesByFactureNumberEquals(invNumber).getResultList();
		if (!resultList.isEmpty()) {
			uiModel.addAttribute("facture", resultList.iterator().next());
			return "facturePdfDocViews";

		}else {
			uiModel.addAttribute("apMessage", "Aucune Facture Trouvee");
			return "caisses/infos";
		}
	}
	
	@RequestMapping(value="findDetails/ByAjax/{invId}", method = RequestMethod.GET)
	@ResponseBody
	public String findDetails(@PathVariable("invId") Long invId, Model uiModel) {
		CommandeClient commandeClient =  Facture.findFacture(invId).getCommande();
		       if (commandeClient!=null) {
				List<LigneCmdClient> resultList = LigneCmdClient.findLigneCmdClientsByCommande(commandeClient).getResultList();
				    return  LigneCmdClient.toDeepJsonArray(resultList);
				}
		       return null ;
	}
	
	@RequestMapping(params = { "find=BySearch", "form" }, method = RequestMethod.GET)
	public String Search(Model uiModel) {
		 Facture facture = new Facture() ;
		 facture.setSolder(Boolean.TRUE);
		 uiModel.addAttribute("facture", facture);
		return "factures/search";
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public String Search(Facture facture , Model uiModel) {
		uiModel.addAttribute("results", Facture.search(facture.getSolder(),facture.getCip(), facture.getDesignation(), facture.getNetPayer(), facture.getDateCreation()));
		uiModel.addAttribute("facture", facture);
		return "factures/search";
	}

	@ModelAttribute("caisses")
	public Collection<Caisse> populateCaisses() {
		// return Caisse.findAllCaisses();
		return new ArrayList<Caisse>();
	}

	@ModelAttribute("clients")
	public Collection<Client> populateClients() {
		// return Client.findAllClients();
		return new ArrayList<Client>();

	}

	@ModelAttribute("commandeclients")
	public Collection<CommandeClient> populateCommandeClients() {
		// return CommandeClient.findAllCommandeClients();
		return new ArrayList<CommandeClient>();

	}

	@ModelAttribute("factures")
	public Collection<Facture> populateFactures() {
		return new ArrayList<Facture>();
	}

	@ModelAttribute("lignefactures")
	public Collection<LigneFacture> populateLigneFactures() {
		return new ArrayList<LigneFacture>();
	}

	@ModelAttribute("paiements")
	public Collection<Paiement> populatePaiements() {
		return new ArrayList<Paiement>();
	}

	@ModelAttribute("pharmausers")
	public Collection<PharmaUser> populatePharmaUsers() {
		return new ArrayList<PharmaUser>();
	}

	@ModelAttribute("sites")
	public Collection<Site> populateSites() {
		return new ArrayList<Site>();
	}

	@ModelAttribute("typecommandes")
	public Collection<TypeCommande> populateTypeCommandes() {
		return new ArrayList<TypeCommande>();
	}

	@ModelAttribute("typefactures")
	public Collection<TypeFacture> populateTypeFactures() {
		// return Arrays.asList(TypeFacture.class.getEnumConstants());
		return new ArrayList<TypeFacture>();

	}
}
