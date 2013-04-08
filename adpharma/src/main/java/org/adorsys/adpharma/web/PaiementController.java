package org.adorsys.adpharma.web;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.adorsys.adpharma.beans.process.PaiementProcess;
import org.adorsys.adpharma.domain.Caisse;
import org.adorsys.adpharma.domain.Client;
import org.adorsys.adpharma.domain.CommandeClient;
import org.adorsys.adpharma.domain.DetteClient;
import org.adorsys.adpharma.domain.Facture;
import org.adorsys.adpharma.domain.Paiement;
import org.adorsys.adpharma.domain.PharmaUser;
import org.adorsys.adpharma.domain.QuiPaye;
import org.adorsys.adpharma.domain.Site;
import org.adorsys.adpharma.domain.TypePaiement;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.utils.ProcessHelper;
import org.apache.commons.lang.StringUtils;
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

@RooWebScaffold(path = "paiements", formBackingObject = Paiement.class)
@RequestMapping("/paiements")
@Controller
public class PaiementController {

	@RequestMapping(params = { "find=BySearch", "form" }, method = RequestMethod.GET)
	public String Search(Model uiModel) {
		addDateTimeFormatPatterns(uiModel);

		uiModel.addAttribute("pharmausers", PharmaUser.findAllPharmaUsers());

		uiModel.addAttribute("paiement", new Paiement());
		return "paiements/search";
	}

	@RequestMapping(value = "/BySearch", method = RequestMethod.GET)
	public String Search(Paiement paiement , Model uiModel) {
		paiement.searchCaisse();
		uiModel.addAttribute("results", paiement.search(paiement.getCassier(), paiement.getDateSaisie(), paiement.getCaisse(),paiement.getTypePaiement()).getResultList());
		addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("pharmausers", PharmaUser.findAllPharmaUsers());

		uiModel.addAttribute("paiement", new Paiement());
		return "paiements/search";
	}

	@RequestMapping(value = "/caisseEnCour",method = RequestMethod.GET)
	public String PaiementCaisseEnCour(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
		Caisse caisse = PaiementProcess.getMyOpenCaisse(SecurityUtil.getPharmaUser());
		if (caisse==null) {
			uiModel.addAttribute("apMessage", "vous n'avez aucune caisse en cour !");
			return "caisses/infos";

		}
		if (page != null || size != null) {
			int sizeNo = size == null ? 10 : size.intValue();
			uiModel.addAttribute("paiements", Paiement.findPaiementByCaisseEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo,caisse));
			float nrOfPages = (float) Paiement.countPaiements() / sizeNo;
			uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
		} else {
			uiModel.addAttribute("paiements", Paiement.findPaiementsByCaisse(caisse));
		}
		addDateTimeFormatPatterns(uiModel);
		return "paiements/list";
	}

	@RequestMapping(params = { "find=ByFactureNumberEquals", "form" }, method = RequestMethod.GET)
	public String findByDesForm(Model uiModel) {
		return "paiements/findPaiementOfInvoice";
	}

	@RequestMapping(params = "find=ByFactureNumberEquals", method = RequestMethod.GET)
	public String findByDes( @RequestParam("invoiceNumber") String invoiceNumber, Model uiModel) {
		uiModel.addAttribute("results", Paiement.findPaiementsByInvoiceNumberEquals("FAC-"+ StringUtils.remove(invoiceNumber, "FAC-")).getResultList());
		return "paiements/findPaiementOfInvoice";
	}



	@RequestMapping(value = "/encaissementGroupe" ,params = "form", method = RequestMethod.GET)
	public String encaissementGroupeForm( Model uiModel, HttpServletRequest httpServletRequest) {

		Caisse openCaisse = PaiementProcess.getMyOpenCaisse(SecurityUtil.getPharmaUser());
		if (openCaisse == null) {
			uiModel.addAttribute("apMessage","Impossible d'effectuer un Encaissement Groupe  Aucune caisse Ouverte !");
			return "caisses/infos";
		}else {

			PaiementProcess paiementProcess = new PaiementProcess(openCaisse);
			uiModel.addAttribute("paiementProcess",paiementProcess);
			Paiement paiement = new Paiement() ;
			uiModel.addAttribute("paiement",paiement);
			return "paiements/encaissementGroupe";

		}
	}

	@RequestMapping(value = "/rechercherClient" ,params = "form", method = RequestMethod.GET)
	public String rechercherClient( Model uiModel, HttpServletRequest httpServletRequest) {

		Caisse openCaisse = PaiementProcess.getMyOpenCaisse(SecurityUtil.getPharmaUser());
		if (openCaisse == null) {
			uiModel.addAttribute("apMessage","Impossible d'effectuer un Encaissement Groupe  Aucune caisse Ouverte !");
			return "caisses/infos";
		}else {

			return "paiements/rechercheClient";

		}
	}

	@RequestMapping( value="/findclient", params = "find=ByNomCompletLike", method = RequestMethod.GET)
	public String findClientsByNomCompletLike(@RequestParam("nomComplet") String nomComplet, Model uiModel) {
		uiModel.addAttribute("clientfound", Client.findClientsByNomCompletLike(nomComplet).getResultList());
		return "paiements/rechercheClient";
	}

	@RequestMapping( value="/findMyPaiements", method = RequestMethod.GET)
	public String findMyPaiements(Model uiModel) {
		Caisse openCaisse = PaiementProcess.getMyOpenCaisse(SecurityUtil.getPharmaUser());
		if (openCaisse !=null) {
			uiModel.addAttribute("paiements",Paiement.findMyPaiements(openCaisse).getResultList());

		}else {
			uiModel.addAttribute("paiements",new ArrayList<Paiement>());

		}
		return "paiements/list";
	}


	@RequestMapping(value="/selectclient/{clientId}", method = RequestMethod.GET)
	public String selectClient(@PathVariable("clientId") Long clientId, Model uiModel) {
		Client client = Client.findClient(clientId);
		client.calculeTotalDette();
		Caisse openCaisse = PaiementProcess.getMyOpenCaisse(SecurityUtil.getPharmaUser());
		if (openCaisse == null) {
			uiModel.addAttribute("apMessage","Impossible d'effectuer un Encaissement Groupe  Aucune caisse Ouverte !");
			return "caisses/infos";
		}else {
			PaiementProcess paiementProcess = new PaiementProcess(openCaisse);
			paiementProcess.setClient(client);

			if (client.getTotalDette() == null || client.getTotalDette().intValue() <=0) {
				uiModel.addAttribute("apMessage","Impossible d'effectuer un Encaissement Groupe  Ce client n a aucune dette !");

			}	else {
				Paiement paiement = new Paiement() ;
				paiement.setMontant(BigDecimal.valueOf(client.getTotalDette().longValue()));
				uiModel.addAttribute("paiement",paiement);
				paiementProcess.setShowPostForm(true);
				uiModel.addAttribute("cash",true);
				uiModel.addAttribute("typepaiements",new PaiementProcessController().populateTypePaiements(true));

			}	
			uiModel.addAttribute("paiementProcess",paiementProcess);
			return "paiements/encaissementGroupe";
		}



	}

	@RequestMapping(value = "/findClientByNumber" , method = RequestMethod.POST)
	public String findInvoice(@RequestParam("Number") String Number , Model uiModel, HttpServletRequest httpServletRequest) {
		List<Client> client = Client.findClientsByClientNumberEquals("CL-"+ StringUtils.remove(Number, "CL-")).getResultList();
		if (client.iterator().hasNext()) {
			return "redirect:/paiements/selectclient/" + ProcessHelper.encodeUrlPathSegment(client.iterator().next().getId().toString(), httpServletRequest);

		}else {
			uiModel.addAttribute("apMessage","Aucun client trouve Verifier Le numero Saisie !");
			return "paiements/encaissementGroupe";

		}


	}

	@Transactional
	@RequestMapping(value = "/encaissementGlobal/{clientId}" , method = RequestMethod.POST)
	public String encaisserPaiement(@PathVariable("clientId")Long clientId,@Valid Paiement paiement ,BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		Caisse openCaisse = PaiementProcess.getMyOpenCaisse(SecurityUtil.getPharmaUser());
		if (openCaisse == null) {
			uiModel.addAttribute("apMessage","Impossible d'effectuer un Encaissement Aucune caisse Ouverte !");
			return "caisses/infos";
		}
		List<DetteClient> listeDette = DetteClient.findDetteClientsByClientIdEqualsAndSolderNot(clientId, Boolean.TRUE).getResultList();
		Client client = Client.findClient(clientId);
		Set<DetteClient> dettesSoldees = new HashSet<DetteClient>();
		/*client.calculeTotalDette();

		if (paiement.validatePaiementGlobal(uiModel , client.getTotalDette())){
			System.out.println("paiement invalide");
			return "redirect:/paiements/selectclient/" + ProcessHelper.encodeUrlPathSegment(client.getId().toString(), httpServletRequest);

		} */
		// BigDecimal montant = paiement.getMontant();
		BigDecimal montant = paiement.getSommeRecue() ;
		if (montant.intValue() > 0) {
			for (DetteClient detteClient : listeDette) {
				Facture facture = Facture.findFacture(detteClient.getFactureId());
				BigInteger reste = detteClient.getReste();

				if (reste.intValue() <=  montant.intValue()) {
					montant = montant.subtract(new BigDecimal(reste));
					facture.avancerPaiement(reste);
					detteClient.avancer(reste);
					facture.merge();
					DetteClient merge = (DetteClient) detteClient.merge();
					dettesSoldees.add(merge);
				}else {
					facture.avancerPaiement(montant.toBigInteger());
					detteClient.avancer(montant.toBigInteger());
					facture.merge();
					DetteClient merge = (DetteClient) detteClient.merge();
					dettesSoldees.add(merge);
					break;

				}
			}   




		}

		client.calculeTotalDette();
		Client merge = (Client) client.merge();
		PaiementProcess paiementProcess = new PaiementProcess(openCaisse);
		paiementProcess.setClient(merge);
		uiModel.addAttribute("paiementProcess",paiementProcess);
		uiModel.addAttribute("dettesSoldees",dettesSoldees);
		return "paiements/encaissementGroupe";

	}

	@RequestMapping("/printTicket/{ticketId}.pdf")
	public String print(@PathVariable("ticketId")Long ticketId, Model uiModel,HttpServletRequest httpServletRequest){
		Paiement paiement = Paiement.findPaiement(ticketId);

		if (!paiement.getTicketImprimer()) {
			paiement.setTicketImprimer(true);
			paiement.merge();
		}
		uiModel.addAttribute("paiement", paiement);

		return "ticketPdfDocView";


	}

	@RequestMapping( value="/printByCmd/{cmdId}")
	public String printByCmd(@PathVariable("cmdId")Long cmdId, @RequestParam("doc") String doc,@RequestParam(value="withRem",required=false) Boolean withRem , Model uiModel,HttpServletRequest httpServletRequest){
		CommandeClient commande = CommandeClient.findCommandeClient(cmdId);
		Facture facture=null;
		Paiement paiement=null;
		
		if (StringUtils.equals("ticket.pdf", doc)) {
			paiement = commande.getPaiements();
			if(withRem!=null){
			try{
				paiement.setReduction(withRem);
			}catch (NullPointerException e) {
				uiModel.addAttribute("paiement", paiement);
				return "ticketPdfDocView";
			}
			}
			uiModel.addAttribute("paiement", paiement);
			return "ticketPdfDocView";
		}else {
			 facture = commande.getFacture();
			if(withRem!=null){  
		     try{
			   facture.setPrintWithReduction(withRem);
			 }catch (NullPointerException  e) {
				 uiModel.addAttribute("facture", facture);
			     return "facturePdfDocViews";
			    }
		  }
			uiModel.addAttribute("facture", facture);
			return "facturePdfDocViews";
		}


	}


	@ModelAttribute("caisses")
	public Collection<Caisse> populateCaisses() {
		return new ArrayList<Caisse>();
	}

	@ModelAttribute("factures")
	public Collection<Facture> populateFactures() {
		return new ArrayList<Facture>();
	}

	@ModelAttribute("paiements")
	public Collection<Paiement> populatePaiements() {
		return new ArrayList<Paiement>();
	}

	@ModelAttribute("pharmausers")
	public Collection<PharmaUser> populatePharmaUsers() {
		return new ArrayList<PharmaUser>();
	}

	@ModelAttribute("quipayes")
	public Collection<QuiPaye> populateQuiPayes() {
		return Arrays.asList(QuiPaye.class.getEnumConstants());
	}

	@ModelAttribute("sites")
	public Collection<Site> populateSites() {
		return Site.findAllSites();
	}

	@ModelAttribute("typepaiements")
	public Collection<TypePaiement> populateTypePaiements() {
		return Arrays.asList(TypePaiement.class.getEnumConstants());
	}

}
