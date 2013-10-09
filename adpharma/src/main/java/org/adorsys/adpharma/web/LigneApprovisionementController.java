package org.adorsys.adpharma.web;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.adorsys.adpharma.beans.ChangeDatePrice;
import org.adorsys.adpharma.beans.process.ReclamationBean;
import org.adorsys.adpharma.domain.Approvisionement;
import org.adorsys.adpharma.domain.Client;
import org.adorsys.adpharma.domain.DestinationMvt;
import org.adorsys.adpharma.domain.Etat;
import org.adorsys.adpharma.domain.Filiale;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.MouvementStock;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.Rayon;
import org.adorsys.adpharma.domain.TypeMouvement;
import org.adorsys.adpharma.domain.TypeSortieProduit;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.services.ClaimsService;
import org.adorsys.adpharma.services.DefaultInventoryService;
import org.adorsys.adpharma.utils.LocaleUtil;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.adorsys.adpharma.utils.ProcessHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
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
	
	@Resource(name="messageSource")
	ReloadableResourceBundleMessageSource messageSource;

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
		   List<LigneApprovisionement> liste = LigneApprovisionement.findLigneApprovisionementsByCipMaisonEqualsAndReclamations(cipm).getResultList();
		   if(!liste.isEmpty()){
			   LigneApprovisionement ligne = liste.iterator().next();
			   return ligne.toJson2();
		   }else{
			   return new LigneApprovisionement().toJson();
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
		uiModel.addAttribute("typeSorties", TypeSortieProduit.findAllTypeSortieProduits());
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
		mouvementStock.setpAchatTotal(qte.multiply(line.getPrixAchatUnitaire().toBigInteger()));
		mouvementStock.setpVenteTotal(qte.multiply(line.getPrixVenteUnitaire().toBigInteger()));
		mouvementStock.setQteInitiale(line.getQuantieEnStock());
		line.setQuantiteSortie(line.getQuantiteSortie().add(qte));
		line.CalculeQteEnStock();
		mouvementStock.setQteFinale(line.getQuantieEnStock());
		mouvementStock.setTypeMouvement(TypeMouvement.SORTIE_PRODUIT);
		mouvementStock.setNote(raison);
		mouvementStock.setNumeroTicket("-//-");
		mouvementStock.persist();
		line.merge();
		produit.setQuantiteEnStock(new DefaultInventoryService().getTrueStockQuantity(produit));
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
			uiModel.addAttribute("apMessage",  messageSource.getMessage("sale_find_product", null, LocaleUtil.getCurrentLocale()));

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
		LigneApprovisionement ligneApprovisionement = LigneApprovisionement.findLigneApprovisionementsByCipMaisonEqualsAndReclamations(reclamation.getCipm()).getResultList().iterator().next();
		reclamationService.setQteRetour(reclamation.getReturnQuantity());
		reclamationService.updateStock(ligneApprovisionement);
		uiModel.addAttribute("ligneRetourne", ligneApprovisionement);
		return "ligneapprovisionements/reclamations";
	}
	
	
	@RequestMapping(value="/searchLigneApp")
	public String searchLigneApp( @RequestParam("name") String name, Model uiModel) {
		
		if("".equals(name)){
			Integer page = 1;
			Integer size = 50;
			int sizeNo = size == null ? 10 : size.intValue();
            uiModel.addAttribute("ligneapprovisionements", LigneApprovisionement.findLigneApprovisionementEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) LigneApprovisionement.countLigneApprovisionements() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
		}else{
				
				uiModel.addAttribute("ligneapprovisionements", LigneApprovisionement.findLigneApprovisionementsByDesignationLike(name).
						setMaxResults(50).getResultList());
		}
		return "ligneapprovisionements/list";
	}
	
	
	
	@RequestMapping(value="/changeDatePrice")
    public String changeDatePrice(ChangeDatePrice changeDatePrice, Model uiModel, HttpServletRequest httpServletRequest) {
        if (changeDatePrice.getNouveauPrix()==null && changeDatePrice.getNouvelDatePeremption()==null) {
        	
            uiModel.addAttribute("changeDatePrice", changeDatePrice);
            uiModel.addAttribute("message", "la date et le prix sont null, veuillez remplir o moins un!");
            return "ligneapprovisionements/changedateprices";
        }
        
        
        if(changeDatePrice.getNouveauPrix()!=null && changeDatePrice.getId()!=null){
        	if(changeDatePrice.isAppliqueLeNouveauPrixATousLesCipmDeMemeCip()){
        		
        		List<LigneApprovisionement> list = LigneApprovisionement.findLigneApprovisionementsByCipMaisonEquals(changeDatePrice.getCipm()).getResultList();
        		try {
        			List<LigneApprovisionement> list2 = LigneApprovisionement.findLigneApprovisionementsByQuantieEnStockUpThanAndCipEquals(BigInteger.ONE, list.get(0).getCip()).getResultList();
        		
					for(LigneApprovisionement approvisionement:list2){
						approvisionement.setPrixVenteUnitaire(changeDatePrice.getNouveauPrix());
						approvisionement.merge();
						approvisionement.flush();
					}
					uiModel.addAttribute("message", messageSource.getMessage("sale_modify_sale_price", null, LocaleUtil.getCurrentLocale()));
				} catch (Exception e) {
					uiModel.addAttribute("message", messageSource.getMessage("sale_enter_cipm_warning", null, LocaleUtil.getCurrentLocale())); 
				}
        		
        	}else{
        		
        		LigneApprovisionement approvisionement = LigneApprovisionement.findLigneApprovisionement(changeDatePrice.getId());
            	approvisionement.setPrixVenteUnitaire(changeDatePrice.getNouveauPrix());
            	approvisionement.merge();
            	approvisionement.flush();
            	uiModel.addAttribute("message", messageSource.getMessage("sale_modify_sale_price", null, LocaleUtil.getCurrentLocale()));
        	}
        	
        }
        if(changeDatePrice.getNouvelDatePeremption()!=null && changeDatePrice.getId()!=null){
        	if(changeDatePrice.isAppliqueLaNouvelDateATousLesCipmDeMemeCip()){
        		
        		List<LigneApprovisionement> list = LigneApprovisionement.findLigneApprovisionementsByCipMaisonEquals(changeDatePrice.getCipm()).getResultList();
        		try {
        			List<LigneApprovisionement> list2 = LigneApprovisionement.findLigneApprovisionementsByQuantieEnStockUpThanAndCipEquals(BigInteger.ONE, list.get(0).getCip()).getResultList();
        		
					for(LigneApprovisionement approvisionement:list2){
						approvisionement.setDatePeremtion(changeDatePrice.getNouvelDatePeremption());
						approvisionement.merge();
						approvisionement.flush();
					}
					uiModel.addAttribute("message", messageSource.getMessage("sale_modify_sale_price", null, LocaleUtil.getCurrentLocale())); 
				} catch (Exception e) {
					uiModel.addAttribute("message", messageSource.getMessage("sale_enter_cipm_warning", null, LocaleUtil.getCurrentLocale()));
				}
        		
        	}else{
        		
        		LigneApprovisionement approvisionement = LigneApprovisionement.findLigneApprovisionement(changeDatePrice.getId());
        		approvisionement.setDatePeremtion(changeDatePrice.getNouvelDatePeremption());
            	approvisionement.merge();
            	approvisionement.flush();
            	uiModel.addAttribute("message", messageSource.getMessage("sale_modify_sale_price", null, LocaleUtil.getCurrentLocale()));
        	}
        	
        	
        }
        uiModel.addAttribute("changeDatePrice", new ChangeDatePrice());
        return "ligneapprovisionements/changedateprices";
    }
    
    @RequestMapping(value="changeDatePriceForm")
    public String createFormChangeDatePrice(Model uiModel) {
        uiModel.addAttribute("changeDatePrice", new ChangeDatePrice());
        return "ligneapprovisionements/changedateprices";
    }
	
	
    @RequestMapping(value="/findProductAjax")
    @ResponseBody
	public String findByDesAjax( @RequestParam("designation") String designation, Model uiModel) {
		
		List<LigneApprovisionement> list = LigneApprovisionement.findLigneApprovisionementsByQuantieEnStockUpThanAndDesignationLike(BigInteger.ONE, designation , Etat.CLOS).getResultList();
		return LigneApprovisionement.toJsonArray(list);
	}
	
    @RequestMapping(value="/findProductByidAjax/{id}")
    @ResponseBody
	public String findByIdAjax( @PathVariable("id") Long id, Model uiModel) {
		LigneApprovisionement approvisionement = LigneApprovisionement.findLigneApprovisionement(id);
		return approvisionement.toJson();
	}
	
    @RequestMapping(value="/findProductByCipmAjax/{cipm}")
    @ResponseBody
	public String findByCipmAjax( @PathVariable("cipm") String cipm, Model uiModel) {
		List<LigneApprovisionement> list = LigneApprovisionement.findLigneApprovisionementsByCipMaisonEquals(cipm).getResultList();
		try {
				return list.get(0).toJson();
		} catch (Exception e) {
			return null;
		}
	
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
