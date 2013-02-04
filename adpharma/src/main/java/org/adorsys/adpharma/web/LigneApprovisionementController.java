package org.adorsys.adpharma.web;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.adorsys.adpharma.beans.ReclamationBean;
import org.adorsys.adpharma.domain.Approvisionement;
import org.adorsys.adpharma.domain.DestinationMvt;
import org.adorsys.adpharma.domain.Etat;
import org.adorsys.adpharma.domain.Filiale;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.MouvementStock;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.Rayon;
import org.adorsys.adpharma.domain.TypeMouvement;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.services.ClaimsService;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.adorsys.adpharma.utils.ProcessHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RooWebScaffold(path = "ligneapprovisionements", formBackingObject = LigneApprovisionement.class)
@RequestMapping("/ligneapprovisionements")
@Controller
public class LigneApprovisionementController {
	
	@Autowired
	private ClaimsService reclamationService;

	@RequestMapping(method = RequestMethod.PUT)
	public String update(@Valid() LigneApprovisionement ligneApprovisionement, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		ligneApprovisionement.setProduit(LigneApprovisionement.findLigneApprovisionement(ligneApprovisionement.getId()).getProduit());
		if (!ligneApprovisionement.isValide(uiModel)) {
			uiModel.addAttribute("ligneApprovisionement", ligneApprovisionement);
			addDateTimeFormatPatterns(uiModel);
			return "ligneapprovisionements/update";
		}
		LigneApprovisionement merge = (LigneApprovisionement)ligneApprovisionement.merge();
		Long nextProductId = merge.getId() ;
		uiModel.addAttribute("apMessage", merge.getCipMaison() +" mis a jour avec succes ! ") ;
		List<LigneApprovisionement> results = LigneApprovisionement.findNextProduits(nextProductId).setMaxResults(5).getResultList();
		if(!results.isEmpty()) {
			nextProductId = results.iterator().next().getId();
		}
		return  updateForm(nextProductId, uiModel) ;
	}

	@RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
	public String updateForm(@PathVariable("id") Long id, Model uiModel) {
		uiModel.addAttribute("ligneApprovisionement", LigneApprovisionement.findLigneApprovisionement(id));
		addDateTimeFormatPatterns(uiModel);
		return "ligneapprovisionements/update";
	}
	
	
	// Recherche d'une ligne d'approvisionement ayant une reclamation
	@RequestMapping(value="/findByCipm/{cipm}", method=RequestMethod.GET)
	@ResponseBody
	public String findLigneApprovisionementByCipmAndReclamation(@PathVariable("cipm")String cipm){
		   LigneApprovisionement ligne = LigneApprovisionement.findLigneApprovisionementsByCipMaisonEqualsAndReclamations(cipm).getResultList().iterator().next(); 
		   if(ligne!=null){
			   return ligne.toJson2();
		   }else{
			   return null;
		   }
	}
	

    @RequestMapping(value = "selectProduct/{id}", params = "action", method = RequestMethod.GET)
    public String selectProductToUpdate(@RequestParam("action") String action, @PathVariable("id") Long id,Model uiModel) {
    	 Long nextProductId = id ;
    	 List<LigneApprovisionement> resultList = new ArrayList<LigneApprovisionement>() ;
    	if(StringUtils.equalsIgnoreCase(action, "next")){
        	 resultList = LigneApprovisionement.findNextProduits(id).setMaxResults(5).getResultList();
        }else {
        	resultList = LigneApprovisionement.findPreviousProduits(id).setMaxResults(5).getResultList();
		}
    	if(!resultList.isEmpty()){
    		nextProductId = resultList.iterator().next().getId() ;
    	}
        return updateForm(nextProductId, uiModel);
    }

	@RequestMapping(params = { "find=ByDes", "form" }, method = RequestMethod.GET)
	public String findByDesForm(Model uiModel) {
		return "ligneapprovisionements/findProduct";
	}

	@RequestMapping(params = "find=ByDes", method = RequestMethod.GET)
	public String findByDes( @RequestParam("designation") String designation, Model uiModel) {
		uiModel.addAttribute("results", LigneApprovisionement.findLigneApprovisionementsByQuantieEnStockUpThanAndDesignationLike(BigInteger.ONE, designation , Etat.CLOS).getResultList());
		return "ligneapprovisionements/findProduct";
	}

	@RequestMapping(params = { "find=ByCipMaison", "form" }, method = RequestMethod.GET)
	public String findByCipMaison(Model uiModel) {
		return "ligneapprovisionements/ficheDetailCipm";
	}

	@RequestMapping(params = "find=ByCipMaison", method = RequestMethod.GET)
	public String findByCipMaison( @RequestParam("cipMaison") String cipMaison, Model uiModel) {
		List<LigneApprovisionement> resultList = LigneApprovisionement.findLigneApprovisionementsByCipMaisonEquals(cipMaison).getResultList();
		uiModel.addAttribute("results", LigneApprovisionement.findLigneApprovisionementsByCipMaisonEquals(cipMaison).getResultList());
		uiModel.addAttribute("mouvements", MouvementStock.findMouvementStocksByCipMEquals(cipMaison).getResultList());
		uiModel.addAttribute("cipm", cipMaison);

		if (!resultList.isEmpty()) {
			uiModel.addAttribute("produit", Produit.findProduitsByCipEquals(resultList.iterator().next().getCip()).getResultList());
		}

		return "ligneapprovisionements/ficheDetailCipm";
	}

	@RequestMapping(params = { "find=BySearch", "form" }, method = RequestMethod.GET)
	public String Search(Model uiModel) {
		uiModel.addAttribute("rayon", ProcessHelper.populateRayon());
		uiModel.addAttribute("filiale", ProcessHelper.populateFiliale());
		return "ligneapprovisionements/search";
	}

	@RequestMapping(value="/sortieProduit", method = RequestMethod.GET)
	public String sortieProduit(@RequestParam(value="mode",required=false) String mode, Model uiModel) {
		Date minDate =  PharmaDateUtil.getBeginDayDate();
		Date maxDate =  PharmaDateUtil.getEndDayDate();
		List<MouvementStock> mvts = MouvementStock.search(TypeMouvement.SORTIE_PRODUIT, minDate, maxDate);
		uiModel.addAttribute("sorties", mvts);
		if(StringUtils.equals("ByCipm", mode)){
			return "ligneapprovisionements/sortiesByCipm";
		}else{
			return "ligneapprovisionements/sortiesByCip";
		}
	}

	@RequestMapping(value="/annulerSortie/{mvtid}", method = RequestMethod.GET)
	public String annulerSortie(@PathVariable("mvtid") Long id ,Model uiModel) {
		MouvementStock mvt = MouvementStock.findMouvementStock(id);
		mvt.setAnnuler(true);
		List<LigneApprovisionement> resultList = LigneApprovisionement.findLigneApprovisionementsByCipMaisonEquals(mvt.getCipM()).getResultList();
		if (!resultList.isEmpty()) {
			LigneApprovisionement line = resultList.iterator().next();
			Produit produit = line.getProduit();
			produit.setQuantiteEnStock(produit.getQuantiteEnStock().add(mvt.getQteDeplace()));
			MouvementStock mouvementStock = new MouvementStock();
			mouvementStock.setAgentCreateur(SecurityUtil.getUserName());
			mouvementStock.setAnnuler(false);
			mouvementStock.setCaisse("-//-");
			mouvementStock.setCip(line.getCip());
			mouvementStock.setCipM(line.getCipMaison());
			mouvementStock.setDesignation(line.getDesignation());
			mouvementStock.setDestination(DestinationMvt.MAGASIN);
			mouvementStock.setOrigine(DestinationMvt.FOURNISSEUR);
			mouvementStock.setQteDeplace(mvt.getQteDeplace());
			mouvementStock.setQteInitiale(line.getQuantieEnStock());
			line.setQuantiteSortie(line.getQuantiteSortie().subtract(mvt.getQteDeplace()));
			line.CalculeQteEnStock();
			mouvementStock.setQteFinale(line.getQuantieEnStock());
			mouvementStock.setTypeMouvement(TypeMouvement.ANNULATION_SORTIE);
			mouvementStock.setNote("-//-");
			mouvementStock.setNumeroTicket("-//-");
			mouvementStock.persist();
			line.merge();
			produit.merge();


		}


		return "redirect:/ligneapprovisionements/sortieProduit";
	}

	@RequestMapping(value="/pushProductOut",params="mode", method = RequestMethod.POST)
	public String addSortie(@RequestParam("mode")String mode ,@RequestParam("qte")BigInteger qte,Model uiModel,HttpServletRequest request) {
		String raison = request.getParameter("raison");

		if("byCipm".equals(mode)){
			String cipm = request.getParameter("cipm");
			List<LigneApprovisionement> resultList = LigneApprovisionement.findLigneApprovisionementsByCipMaisonEquals(cipm).getResultList();
			if (!resultList.isEmpty()) {
				LigneApprovisionement line = resultList.iterator().next();
				pushLineOut(line, qte, raison);
			}
		}else {
			String cip = request.getParameter("cip");
			Produit produit = Produit.findProduitsByCipEquals(cip).getResultList().iterator().next();
			List<LigneApprovisionement> resultList = LigneApprovisionement.findLigneApprovisionementsByQteStockAndProduit(produit, BigInteger.ONE).getResultList();
			if (!resultList.isEmpty()) {
				for (LigneApprovisionement line : resultList) {
					BigInteger enStock = line.getQuantieEnStock();
					if(qte.intValue() >= enStock.intValue()){
						pushLineOut(line, enStock, raison);
						qte = qte.subtract(enStock);
					}else {
						pushLineOut(line, qte, raison);
						break;

					}


				}

			}
		}
		uiModel.asMap().clear();
		return sortieProduit(mode, uiModel);
	}  

	public void pushLineOut(LigneApprovisionement line ,BigInteger qte ,String raison ){
		if(qte.intValue() <= 0) return ;
		Produit produit = line.getProduit();
		produit.setQuantiteEnStock(produit.getQuantiteEnStock().subtract(qte));
		MouvementStock mouvementStock = new MouvementStock();
		mouvementStock.setAgentCreateur(SecurityUtil.getUserName());
		mouvementStock.setAnnuler(false);
		mouvementStock.setCaisse("-//-");
		mouvementStock.setCip(line.getCip());
		mouvementStock.setCipM(line.getCipMaison());
		mouvementStock.setDesignation(line.getDesignation());
		mouvementStock.setDestination(DestinationMvt.FOURNISSEUR);
		mouvementStock.setOrigine(DestinationMvt.MAGASIN);
		mouvementStock.setQteDeplace(qte);
		mouvementStock.setQteInitiale(line.getQuantieEnStock());
		line.setQuantiteSortie(line.getQuantiteSortie().add(qte));
		line.CalculeQteEnStock();
		mouvementStock.setQteFinale(line.getQuantieEnStock());
		mouvementStock.setTypeMouvement(TypeMouvement.SORTIE_PRODUIT);
		mouvementStock.setNote(raison);
		mouvementStock.setNumeroTicket("-//-");
		mouvementStock.persist();
		line.merge();
		produit.merge();
	}


	@RequestMapping(params = "find=BySearch", method = RequestMethod.GET)
	public String Search(@RequestParam("rayon") Rayon rayon , @RequestParam("cipMaison") String cipMaison,@RequestParam("filiale") Filiale filiale ,@RequestParam("designation") String designation , 
			@RequestParam("minDateSaisie") @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") Date minDateSaisie, @RequestParam("maxDateSaisie") @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") Date maxDateSaisie,Model uiModel) {

		if (StringUtils.isBlank(designation)&& StringUtils.isBlank(cipMaison) && filiale==null && rayon ==null ) {
			return "redirect:/ligneapprovisionements?page=1" ;

		}  

		List<LigneApprovisionement> search = LigneApprovisionement.search(null,null,designation, cipMaison,rayon, null ,null, filiale, minDateSaisie, maxDateSaisie,null,null).getResultList();
		if (search.isEmpty()) {
			uiModel.addAttribute("apMessage",  "Aucun prosuits trouve !");

		}else {
			uiModel.addAttribute("results", search );

		}
		addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("rayon", ProcessHelper.populateRayon());
		uiModel.addAttribute("filiale", ProcessHelper.populateFiliale());

		return "ligneapprovisionements/search";
	}
	
	
	// Formulaire d'entree des reclamations fournisseurs
	@RequestMapping(value="/reclamations", params="form", method=RequestMethod.GET)
	public String returnOfReclamationsForm(Model uiModel, HttpServletRequest httpServletRequest){
	    uiModel.addAttribute("reclamation", new ReclamationBean());
		return "ligneapprovisionements/reclamations";
	}
	
	@RequestMapping(value="/reclamation/retour", method=RequestMethod.POST)
	public String returnOfReclamations(@ModelAttribute("reclamation")ReclamationBean reclamation, Model uiModel){
		System.out.println("Quantite retournee: "+reclamation.getReturnQuantity());
		LigneApprovisionement ligneApprovisionement = LigneApprovisionement.findLigneApprovisionementsByCipMaisonEqualsAndReclamations(reclamation.getCipm()).getResultList().iterator().next();
		reclamationService.setQteRetour(reclamation.getReturnQuantity());
		System.out.println("Qte Retour: "+reclamationService.getQteRetour());
		reclamationService.updateStock(ligneApprovisionement);
		uiModel.addAttribute("ligneRetourne", ligneApprovisionement);
		System.out.println("Ligne approvisionement: "+ligneApprovisionement);
		return "ligneapprovisionements/reclamations";
	}
	
	
	
	
	
	@ModelAttribute("approvisionements")
	public Collection<Approvisionement> populateApprovisionements() {
		return new ArrayList<Approvisionement>();	    }

	@ModelAttribute("ligneapprovisionements")
	public Collection<LigneApprovisionement> populateLigneApprovisionements() {
		return new ArrayList<LigneApprovisionement>();		    }

	@ModelAttribute("produits")
	public Collection<Produit> populateProduits() {
		return new ArrayList<Produit>();			    }


}
