package org.adorsys.adpharma.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import org.adorsys.adpharma.beans.ReclamationForm;
import org.adorsys.adpharma.beans.process.EtatManagerBean;
import org.adorsys.adpharma.beans.process.ReclamationBean;
import org.adorsys.adpharma.domain.Approvisionement;
import org.adorsys.adpharma.domain.Filiale;
import org.adorsys.adpharma.domain.Fournisseur;
import org.adorsys.adpharma.domain.Rayon;
import org.adorsys.adpharma.domain.TypeMouvement;
import org.adorsys.adpharma.domain.TypeOpCaisse;
import org.adorsys.adpharma.domain.TypeSortieProduit;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.services.JasperPrintService;
import org.adorsys.adpharma.services.SaleService;
import org.adorsys.adpharma.utils.DateConfig;
import org.adorsys.adpharma.utils.DateConfigPeriod;
import org.adorsys.adpharma.utils.DocumentsPath;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.adorsys.adpharma.utils.ProcessHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
@RequestMapping("/etats")
@Controller
public class DocumentsPrinterController {
	@Autowired
	private JasperPrintService jasperPrintService ;
	
	@Autowired
    private SaleService saleService;
	
	@RequestMapping(value = "/etatPeriodique",params="form", method = RequestMethod.GET)
	public String ventes(Model uiModel) {
		uiModel.addAttribute("etatManagerBean", new EtatManagerBean());
		return "etats/periodiqueVente";
	}
	
	@RequestMapping(value = "/docsmanagerview",params="form", method = RequestMethod.GET)
	public String goToDocsManagerView(Model uiModel) {
		uiModel.addAttribute("etatManagerBean", new EtatManagerBean());
		uiModel.addAttribute("rayons",ProcessHelper.populateRayon());
		uiModel.addAttribute("filiales", Filiale.findAllFiliales());
		uiModel.addAttribute("typeMouvements", ProcessHelper.populateTypeMouvements());
		uiModel.addAttribute("users", ProcessHelper.populateUsers());
		List<TypeSortieProduit> allTypeSortieProduits = TypeSortieProduit.findAllTypeSortieProduits();
		allTypeSortieProduits.add(0, new TypeSortieProduit());
		uiModel.addAttribute("typeSortieProduit", allTypeSortieProduits);
		return "etats/docpages";
	}
	
	// Liste des types d'operations de caisse
	@ModelAttribute("typesOperationsCaisses")
	public Collection<TypeOpCaisse> populateTypesOperationsCaisse(){
		return Arrays.asList(TypeOpCaisse.class.getEnumConstants());
	}
	
	@Produces({"application/pdf"})
	@Consumes({""})
	@RequestMapping(value = "/print/etatPeriodiqueTransformation.pdf", method = RequestMethod.GET)
	public void etatPeriodiqueTransformation(EtatManagerBean etatBean  ,HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		parameters.put("DateD",etatBean.getDateDebut());
		parameters.put("DateF",etatBean.getDateFin());
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.ETAT_PERIODIQUE_DECOMPOSITION_FILE_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	
	@Produces({"application/pdf"})
	@Consumes({""})
	@RequestMapping(value = "/print/etatPeriodiqueSortie.pdf", method = RequestMethod.GET)
	public void etatPeriodiqueSortie(EtatManagerBean etatBean  ,HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		parameters.put("DateD",etatBean.getDateDebut());
		parameters.put("DateF",etatBean.getDateFin());
		parameters.put("user",SecurityUtil.getUserName());
		parameters.put("raison",etatBean.getTypeSortieProduit() == null ? "%" : etatBean.getTypeSortieProduit().getLibelle()+"%");
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.ETAT_PERIODIQUE_SORTIE_PRODUIT_FILE_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	
	@Produces({"application/pdf"})
	@Consumes({""})
	@RequestMapping(value = "/print/etatProduitPerisable.pdf", method = RequestMethod.GET)
	public void etatProduitPerisable(@RequestParam("value") Long value ,HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		parameters.put("nombre_jour",value);
		parameters.put("datejour",new Date());
		
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.ETAT_PRODUIT_PERISABLE_FILE_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	
	@Produces({"application/pdf"})
	@Consumes({""})
	@RequestMapping(value = "/print/etatProduitSortie.pdf", method = RequestMethod.GET)
	public void etatProduitSortie(@RequestParam(value="dateD") @DateTimeFormat(pattern="dd-mm-yy 00:00") Date dateD, 
			@RequestParam("dateF") @DateTimeFormat(pattern="dd-mm-yy 00:00") Date dateF,
			@RequestParam("typeSortie") String typeSortie,
			HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		parameters.put("DateD",dateD);
		parameters.put("DateF",dateF);
		parameters.put("TypeSortie",typeSortie);
		
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.ETAT_PRODUIT_SORTIE_FILE_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	
	
	// Etat periodique des ventes
	@Produces({"application/pdf"})
	@Consumes({""})
	@RequestMapping(value = "/print/etatPeriodiqueVente.pdf", method = RequestMethod.GET)
	public void etatPeriodiqueVente(EtatManagerBean etatBean  ,HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		if(etatBean.getDateDebut()==null){
			parameters.put("DateD",saleService.getFirstSale().getDateCreation());
		}else{
			parameters.put("DateD",etatBean.getDateDebut());
		}
		if(etatBean.getDateFin()==null){
			parameters.put("DateF", new Date());
		}else{
			parameters.put("DateF",etatBean.getDateFin());
		}
		
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.ETAT_PERIODIQUE_VENTE_FILE_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	

	// Etat periodique du chiffre d'affaire par vendeur
	@Produces({"application/pdf"})
	@Consumes({""})
	@RequestMapping(value = "/print/chiffeAffaireVendeur.pdf", method = RequestMethod.GET)
	public void chiffeAffaireVendeur(EtatManagerBean etatBean  ,HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		parameters.put("DateD",etatBean.getDateDebut());
		parameters.put("DateF",etatBean.getDateFin());
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.ETAT_PERIODIQUE_CHIFFRE_AFFAIRE_VENDEUR_FILE_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	
	
	// Etat periodique des mouvements de stock
	@Produces({"application/pdf"})
	@Consumes({""})
	@RequestMapping(value = "/print/etatPeriodiqueMouvenentStock.pdf", method = RequestMethod.GET)
	public void etatPeriodiqueMouvenentStock(EtatManagerBean etatBean  ,HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		parameters.put("DateD",etatBean.getDateDebut());
		parameters.put("DateF",etatBean.getDateFin());
		
		try {
			if(TypeMouvement.SORTIE_PRODUIT.equals(etatBean.getTypeMouvement())){
				jasperPrintService.printDocument(parameters, response, DocumentsPath.ETAT_DES_SORTIE_RAISON_FILE_PATH);
			}else{
				jasperPrintService.printDocument(parameters,response, DocumentsPath.ETAT_PERIODIQUE_MVTS_FILE_PATH);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	
	
	@Produces({"application/pdf"})
	@Consumes({""})
	@RequestMapping(value = "/print/etatPeriodiqueMarges.pdf", method = RequestMethod.GET)
	public void etatPeriodiqueTauxMarge(EtatManagerBean etatBean  ,HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		parameters.put("DateD",etatBean.getDateDebut());
		parameters.put("DateF",etatBean.getDateFin());
		
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.ETAT_TAUX_MARQUE_PATH);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	
	
	
	
	// Etat des mouvements de stock par cip
	@Produces({"application/pdf"})
	@Consumes({""})
	@RequestMapping(value = "/print/etatMouvementByCip.pdf", method = RequestMethod.GET)
	public void etatMouvenentStockParCip(EtatManagerBean etatBean, HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		parameters.put("cip",etatBean.getCip());
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.ETAT_MVTS_CIP_FILE_PATH);
			System.out.println("Impression ok...");
		} catch (Exception e) {
			System.out.println("Impression not ok...");
			e.printStackTrace();
			return ;
		}
	}
	
	
	// Etat periodique des decaissements groupes par caisse. 
	@Produces({"application/pdf"})
	@Consumes({""})
	@RequestMapping(value = "/print/etatperiodiqueDecaissementgrpes.pdf", method = RequestMethod.GET)
	public void etatPeriodiqueDecaissementGrpesParCaisse(EtatManagerBean etatBean, HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		parameters.put("DateD",etatBean.getDateDebut());
		parameters.put("DateF",etatBean.getDateFin());
		if(etatBean.getTypeOperation().equals(TypeOpCaisse.ENCAISSEMENT)){
			parameters.put("typeOp", 0);
			parameters.put("nomOP", "ENCAISSEMENT");
		}
		if(etatBean.getTypeOperation().equals(TypeOpCaisse.DECAISSEMENT)){
			parameters.put("typeOp", 1);
			parameters.put("nomOP", "DECAISSEMENT");
 		}
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.ETAT_PERIODIQUE_DEC_GROUPES_FILE_PATH);
			System.out.println("Impression ok...");
		} catch (Exception e) {
			System.out.println("Impression not ok...");
			e.printStackTrace();
			return ;
		}
	}
	
	
	
	@Produces({"application/pdf"})
	@Consumes({""})
	@RequestMapping(value = "/print/etatJournalierVente.pdf", method = RequestMethod.GET)
	public void etatPeriodiqueVente(HttpServletRequest request,HttpServletResponse response) {
		DateConfigPeriod begingEndOfDay = DateConfig.getBegingEndOfDay(new Date());
		Map parameters = new HashMap();
		parameters.put("DateD",begingEndOfDay.getBegin());
		parameters.put("DateF",begingEndOfDay.getEnd());
		
		try {
			
			jasperPrintService.printDocument(parameters, response, DocumentsPath.ETAT_PERIODIQUE_VENTE_FILE_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	
	@Produces({"application/pdf"})
	@Consumes({""})
	@RequestMapping(value = "/print/etatPeriodiqueDettes.pdf", method = RequestMethod.GET)
	public void etatPeriodiqueDette(EtatManagerBean etatBean  ,HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		if(etatBean.getDateDebut()==null){
			parameters.put("DateD", saleService.getFirstDebt().getDateCreation());
		}else{
			parameters.put("DateD",etatBean.getDateDebut());
		}
		if(etatBean.getDateFin()==null){
			parameters.put("DateF", new Date());
		}else{
			parameters.put("DateF",etatBean.getDateFin());
		}
		
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.ETAT_PERIODIQUE_DETTES);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	
	@Produces({"application/pdf"})
	@Consumes({""})
	@RequestMapping(value = "/print/etatJournalierDettes.pdf", method = RequestMethod.GET)
	public void etatJournalierDettes(HttpServletRequest request,HttpServletResponse response) {
		DateConfigPeriod begingEndOfDay = DateConfig.getBegingEndOfDay(new Date());
		Map parameters = new HashMap();
		parameters.put("DateD", PharmaDateUtil.format(begingEndOfDay.getBegin(), PharmaDateUtil.DATE_PATTERN_LONG));
		parameters.put("DateF",PharmaDateUtil.format(begingEndOfDay.getEnd(), PharmaDateUtil.DATE_PATTERN_LONG));
		
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.ETAT_PERIODIQUE_DETTES);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	
	@Produces({"application/pdf"})
	@Consumes({""})
	@RequestMapping(value = "/print/etatPeriodiqueCaisse.pdf", method = RequestMethod.GET)
	public void etatPeriodiqueCaisse(EtatManagerBean etatBean  ,HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		if(etatBean.getDateDebut()==null){
			parameters.put("DateD", saleService.getFirstCash().getDateOperation());
		}else{
			parameters.put("DateD",etatBean.getDateDebut());
		}
		if(etatBean.getDateFin()==null){
			parameters.put("DateF", new Date());
		}else{
			parameters.put("DateF",etatBean.getDateFin());
		}
		
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.ETAT_PERIODIQUE_CAISSE_FILE_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	@Produces({"application/pdf"})
	@Consumes({""})
	@RequestMapping(value = "/print/etatFicheStockRayon.pdf", method = RequestMethod.GET)
	public void etatFicheStockRayon(EtatManagerBean etatBean  ,HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		Rayon rayon = etatBean.getRayon();
		parameters.put("rayonId",rayon.getId());
		
		try {
			if(rayon.getId().intValue()==0){
				jasperPrintService.printDocument(parameters, response, DocumentsPath.CATALOGUE_FILE_PATH);
			}else {
				jasperPrintService.printDocument(parameters, response, DocumentsPath.ETAT_CATALOGUE_RAYON_FILE_PATH);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	
	@Produces({"application/pdf"})
	@Consumes({""})
	@RequestMapping(value = "/print/etatVAlorisationRayon.pdf", method = RequestMethod.GET)
	public void etatVAlorisationRayon(EtatManagerBean etatBean, HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		Rayon rayon=null;
	    try {
	    	rayon = etatBean.getRayon();
	    	parameters.put("rayonId",rayon.getId());
		} catch (Exception e) {
			System.out.println("Null Rayon");
		}
		
		try {
			if(rayon==null){
				jasperPrintService.printDocument(parameters, response, DocumentsPath.ETA_VALORISATION_STOCK_FILE_PATH);
			}else {
				if(rayon.getId()!=0){
					jasperPrintService.printDocument(parameters, response, DocumentsPath.ETAT_VALORISATION_RAYON_FILE_PATH);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		}
	}
	
	@Produces({"application/pdf"})
	@Consumes({""})
	@RequestMapping(value = "/print/etatValorisationFiliale.pdf", method = RequestMethod.GET)
	public void etatValorisationFiliale(EtatManagerBean etatBean  ,HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		parameters.put("filiale",etatBean.getFiliale().getFilialeNumber());
		try {
				jasperPrintService.printDocument(parameters, response, DocumentsPath.ETAT_VALORISATION_FILIALE_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	
	
	@Produces({"application/pdf"})
	@Consumes({""})
	@RequestMapping(value = "/print/etatJournalierCaisse.pdf", method = RequestMethod.GET)
	public void etatJournalierCaisse(HttpServletRequest request,HttpServletResponse response) {
		DateConfigPeriod begingEndOfDay = DateConfig.getBegingEndOfDay(new Date());
		Map parameters = new HashMap();
		parameters.put("DateD",begingEndOfDay.getBegin());
		parameters.put("DateF",begingEndOfDay.getEnd());
		
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.ETAT_PERIODIQUE_CAISSE_FILE_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	
	@Produces({"application/pdf"})
	@Consumes({""})
	@RequestMapping(value = "/print/etatValorisationStock.pdf", method = RequestMethod.GET)
	public void etatValorisationStock(HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.ETAT_VALORISATION_STOCK);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	
	
	
	@Produces({"application/pdf"})
	@RequestMapping(value = "/print/listingCatalogue.pdf", method = RequestMethod.GET)
	public @ResponseBody  void printListingCatalogue(HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.CATALOGUE_FILE_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	@Produces({"application/pdf"})
	@RequestMapping(value = "/print/listingRayon.pdf", method = RequestMethod.GET)
	public @ResponseBody  void printListingRayon(HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.RAYON_FILE_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	@Produces({"application/pdf"})
	@RequestMapping(value = "/print/listingClient.pdf", method = RequestMethod.GET)
	public @ResponseBody  void printListingClient(HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.CLIENT_FILE_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	@Produces({"application/pdf"})
	@RequestMapping(value = "/print/listingRelationDecomposition.pdf", method = RequestMethod.GET)
	public @ResponseBody  void listingRelationDecomposition(HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.RELATION_FILE_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	
	
	@Produces({"application/pdf"})
	@RequestMapping(value = "/print/listingRupture.pdf", method = RequestMethod.GET)
	public @ResponseBody  void listingRupture(HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.PRODUIT_RUPTURE_FILE_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	
	         // Impression de la fiche de reclamations
			@RequestMapping(value="/print/ficheReclamations.pdf", method=RequestMethod.GET)
			public String ficheReclamation(@Valid ReclamationForm data, Model uiModel){
				Fournisseur fournisseur = Fournisseur.findFournisseur(new Long(data.getProvider_id()));
				Date dateMin = PharmaDateUtil.parseToDate(data.getDate_min(), PharmaDateUtil.DATE_PATTERN_LONG2);
				Date dateMax = PharmaDateUtil.parseToDate(data.getDate_max(), PharmaDateUtil.DATE_PATTERN_LONG2);
				List<Approvisionement> approvisionements = Approvisionement.findApprovisionementsByFounisseurAndDateCreationBetweenAndReclamationsNot(fournisseur, dateMin, dateMax, Boolean.FALSE).getResultList();
				List<ReclamationBean> reclamations= new ArrayList<ReclamationBean>();
				if(!approvisionements.isEmpty()){
					 reclamations = ReclamationBean.prepareReclamations(approvisionements);
				}
				uiModel.addAttribute("reclamations", reclamations);
				uiModel.addAttribute("fournisseur", fournisseur);
				uiModel.addAttribute("dateMin", dateMin);
				uiModel.addAttribute("dateMax", dateMax);
				return "reclamationsPdfView";
			}

			@RequestMapping(value = "/sortieProduitForm")
			public String etatSortieProduitForm(HttpServletRequest request,HttpServletResponse response,Model uiModel) {
				uiModel.addAttribute("typeSorties", TypeSortieProduit.findAllTypeSortieProduits());
				return "etats/sortieproduit";
			}

}
