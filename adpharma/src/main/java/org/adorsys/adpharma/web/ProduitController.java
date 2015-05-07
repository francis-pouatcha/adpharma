package org.adorsys.adpharma.web;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.adorsys.adpharma.domain.ConfigurationSoldes;
import org.adorsys.adpharma.domain.FamilleProduit;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.SousFamilleProduit;
import org.adorsys.adpharma.services.DefaultInventoryService;
import org.adorsys.adpharma.utils.ProcessHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author clovisgakam
 * product entity
 *
 */
@RooWebScaffold(path = "produits", formBackingObject = Produit.class)
@RequestMapping("/produits")
@Controller
public class ProduitController {
	@Autowired
	DefaultInventoryService inventoryService ;

	//a redefinir
	@RequestMapping(value="/findByCipAjax/{cip}", method = RequestMethod.GET)
	@ResponseBody
	public String findProductByCip(@PathVariable("cip") String cip,Model uiModel) {
		List<Produit> produits = Produit.findProduitsByCipEquals(cip).setMaxResults(200).getResultList();
		Produit prd = null ;
		String reponse = "Aucun produit Trouve" ;
		if (!produits.isEmpty()) {
			prd = produits.iterator().next();
			prd.calculPrixTotalStock();
			reponse= ""+prd.getCip()+","+prd.getDesignation()+","+prd.getTauxDeMarge().getMargeValue()+","+prd.getId()+","+prd.getQuantiteEnStock()+","+prd.getSeuilComande()+","+prd.getPrixTotalStock();
		}
		return reponse;
	}
	
	// Recherche ajax d'un produit par son CIP
	@RequestMapping(value="/searchProductByCipAjax/{cip}", method = RequestMethod.GET)
	@ResponseBody
	public String searchProductByCipAjax(@PathVariable("cip") String cip, HttpServletRequest request, Model uiModel) {
	    Produit produit= new Produit();
	    List<Produit> produits = Produit.findProduitsByCipEquals(cip).setMaxResults(1).getResultList();
	    if(!produits.isEmpty()){
	    	produit=produits.iterator().next();
	    }
		return produit.toJson1();
	}
	
	

	@RequestMapping(value="/attributionFamile", params = "form", method = RequestMethod.GET)
	public String attributionFamile(Model uiModel) {
		uiModel.addAttribute("produit", new Produit());
		return "produits/attributionfsf";
	}
	
	 @RequestMapping(method = RequestMethod.PUT)
	    public String update(@Valid Produit produit, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
	        if (bindingResult.hasErrors()) {
	            uiModel.addAttribute("produit", produit);
	            addDateTimeFormatPatterns(uiModel);
	            return "produits/update";
	        }
	        Produit merge = (Produit) produit.merge();
	        Long nextProductId = produit.getId() ;
	        uiModel.addAttribute("apMessage", merge.getDesignation() +" mis a jour avec succes ! ") ;
	        List<Produit> results = Produit.findNextProduits(produit.getId()).setMaxResults(5).getResultList();
	        if(!results.isEmpty()) {
	        	nextProductId = results.iterator().next().getId();
	        }
	        return  updateForm(nextProductId, uiModel) ;
	    }
	    
	    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
	    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
	        uiModel.addAttribute("produit", Produit.findProduit(id));
	        addDateTimeFormatPatterns(uiModel);
	        return "produits/update";
	    }
	    
	    @RequestMapping(value = "selectProduct/{id}", params = "action", method = RequestMethod.GET)
	    public String selectProductToUpdate(@RequestParam("action") String action, @PathVariable("id") Long id,Model uiModel) {
	    	 Long nextProductId = id ;
	    	 List<Produit> resultList = new ArrayList<Produit>() ;
	    	if(StringUtils.equalsIgnoreCase(action, "next")){
	        	 resultList = Produit.findNextProduits(id).setMaxResults(5).getResultList();
	        }else {
	        	resultList = Produit.findPreviousProduits(id).setMaxResults(5).getResultList();
			}
	    	if(!resultList.isEmpty()){
	    		nextProductId = resultList.iterator().next().getId() ;
	    	}
	        return updateForm(nextProductId, uiModel);
	    }
	    
	    @RequestMapping(value = "validatedStockToCipm/{id}" , method = RequestMethod.GET)
	    public String validatedStockToCipm( @PathVariable("id") Long id,Model uiModel) {
	    	 Produit product = Produit.findProduit(id);
	    	 BigInteger stockIncludeNegativeQte = DefaultInventoryService.getStockIncludeNegativeQte(product);
	    	 product.setQuantiteEnStock(stockIncludeNegativeQte);
	    	 product.merge();
	    	 uiModel.addAttribute("appMessage", "stock modifier succes !") ;
	    	 return updateForm(id, uiModel) ;
	    	 
	    }
	    
	    @RequestMapping(value = "updateProduct", params = "form", method = RequestMethod.GET)
	    public String selectProductToUpdate(Model uiModel) {
	        return updateForm(new Long(1), uiModel);
	    }

	@RequestMapping(value="/setFamilleAndSousFamille",method = RequestMethod.GET)
	public String setFamilleAndSousFamille(@RequestParam("rem") BigDecimal rem ,@RequestParam("lineId") Long lineId,@RequestParam(value="famille" ,required = false) Long familleId ,@RequestParam(value="sfamille" ,required = false) Long sfamilleId, Model uiModel, HttpServletRequest httpServletRequest) {
		Produit produit = Produit.findProduit(lineId);
		if (familleId != null)  produit.setFamilleProduit(FamilleProduit.findFamilleProduit(familleId)); 
		if (sfamilleId != null)  produit.setSousfamilleProduit(SousFamilleProduit.findSousFamilleProduit(sfamilleId)); 	
		if (rem != null)  produit.setTauxRemiseMax(rem); 	
		Produit merge = (Produit) produit.merge();
		ArrayList<Produit> arrayList = new ArrayList<Produit>();
		arrayList.add(produit);
		uiModel.addAttribute("produits", arrayList);
		return "produits/attributionfsf";
	}

	@RequestMapping(value="/findProductApByCipAjax/{cip}", method = RequestMethod.GET)
	@ResponseBody
	public String findProductApByCipAjax(@PathVariable("cip") String cip,Model uiModel) {
		List<Produit> produits = Produit.findProduitsByCipEqualsAndVenteAutorise(cip,Boolean.TRUE).setMaxResults(200).getResultList();
		Produit prd = new Produit() ;
		if (!produits.isEmpty()) {
			prd = produits.iterator().next();
			//prd.calculPrixTotalStock();
		}

		return prd.toJson();
	}
	
	@RequestMapping(value="/{prdId}/makeFusionWith/{fusionId}", method = RequestMethod.GET)
	public String makeFusionWith(@PathVariable("prdId") Long prdId,@PathVariable("fusionId") Long fusionId,Model uiModel) {
		Produit product =Produit.findProduit(prdId);
		Produit mergeWith =(Produit) Produit.findProduit(fusionId);
		inventoryService.mergeProduct(product, mergeWith);
		Produit merge = (Produit)product.merge();
	    uiModel.addAttribute("apMessage", " Fusion Effectuee avec  succes ! ") ;
	   	return updateForm(merge.getId(), uiModel) ;
	}

	//Recherche ajax de produits par designation
	@RequestMapping(value="/findProductByCipAjax", method = RequestMethod.GET)
	@ResponseBody
	public String findProductByCipAjax(HttpServletRequest httpServletRequest) {
		String des = httpServletRequest.getParameter("designation");
		List<Produit> resultList = Produit.findProduitsByDesignationLike(des).setMaxResults(300).getResultList();
		return Produit.toJsonArray(resultList);
	}
	
	//Recherche ajax de produits par designation
		@RequestMapping(value="/findProductByCipAjaxForOrder", method = RequestMethod.GET)
		@ResponseBody
		public String findProductByCipAjaxForOrder(HttpServletRequest httpServletRequest) {
			String des = httpServletRequest.getParameter("designation");
			List<Produit> resultList = Produit.findProduitsForOrderByDesignationLike(des).setMaxResults(300).getResultList();
			return Produit.toJsonArray(resultList);
		}
	
	
	//Recherche ajax de produits par designation
		@RequestMapping(value="/findStockedProducts", method = RequestMethod.GET)
		@ResponseBody
		public String findStockedProducts(HttpServletRequest httpServletRequest) {
			return new saleProcessFindProduct().findProduct(httpServletRequest);
		}

	@RequestMapping(value="/findProductByIdAjax/{id}", method = RequestMethod.GET)
	@ResponseBody
	public String findProductByIdAjax(@PathVariable("id") Long id,Model uiModel ,  HttpServletRequest httpServletRequest) {
		Produit produit = Produit.findProduit(id);
		produit.calculPrixTotalStock();
		return produit.toJson();
	}


	//retourne les information apres un ajax request

	@RequestMapping(value="/findByCipmAjax/{cipm}", method = RequestMethod.GET)
	@ResponseBody
	public String findByCipmAjax(@PathVariable("cipm") String cipm,Model uiModel) {
		String	cipMaison = cipm;
		//cipMaison = StringUtils.removeStart(cipMaison, "0");
		List<LigneApprovisionement> lines = LigneApprovisionement.findLigneApprovisionementsByCipMaisonEquals(cipMaison).setMaxResults(2).getResultList();
		if (!lines.isEmpty()) {
			return lines.iterator().next().clone().toJson();
		}else {
			return new LigneApprovisionement().toJson();
		}	

	}


	
	@RequestMapping(value="/create/{cip}",method = RequestMethod.POST)
	public String create(@PathVariable("cip") boolean cip, @Valid Produit produit, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		if(!cip) produit.validate(bindingResult,cip);
		if (bindingResult.hasErrors()) {
			uiModel.addAttribute("produit", produit);
			uiModel.addAttribute("cip", cip);
			addDateTimeFormatPatterns(uiModel);
			return "produits/create";
		}
		uiModel.asMap().clear();
		produit.persist();
		return "redirect:/produits/" + encodeUrlPathSegment(produit.getId().toString(), httpServletRequest);
	}

	@ResponseBody
	@RequestMapping(value="/createByAjax",method = RequestMethod.GET)
	public String create( @Valid Produit produit, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		produit.validate(bindingResult,null);
		if (bindingResult.hasErrors()) {
			return null;
		}
		uiModel.asMap().clear();
		produit.persist();
		return produit.toJson() ;
	}

	@RequestMapping(value="/ProduitRuptureStock",method = RequestMethod.GET)
	public String ProduitRuptureStock(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
		if (page != null || size != null) {
			int sizeNo = size == null ? 10 : size.intValue();
			uiModel.addAttribute("produits", Produit.findProduitRuptureStockEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
			float nrOfPages = (float) Produit.countOutProduits() / sizeNo;
			uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
		} else {
			uiModel.addAttribute("produits", Produit.findProduitRuptureStockEntries(0, 1));
		}
		addDateTimeFormatPatterns(uiModel);
		return "produits/list";
	}

	@RequestMapping(value="/ProduitAlerteStock",method = RequestMethod.GET)
	public String ProduitAlerteStock(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
		if (page != null || size != null) {
			int sizeNo = size == null ? 10 : size.intValue();
			uiModel.addAttribute("produits", Produit.findProduitAlerteStockEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
			float nrOfPages = (float) Produit.countProduits() / sizeNo;
			uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
		} else {
			uiModel.addAttribute("produits", Produit.findProduitAlerteStockEntries(1, 10));
		}
		addDateTimeFormatPatterns(uiModel);
		return "produits/list";
	}


	@RequestMapping(value="/create/{cip}", params = "form", method = RequestMethod.GET)
	public String createForm(@PathVariable("cip") Boolean cip,Model uiModel) {
		uiModel.addAttribute("produit", new Produit());
		uiModel.addAttribute("cip", cip);
		ProcessHelper.addDateTimeFormatPatterns(uiModel);
		return "produits/create";
	}
	
	
	  @RequestMapping(method = RequestMethod.GET)
	    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
	        if (page != null || size != null) {
	            int sizeNo = size == null ? 10 : size.intValue();
	            uiModel.addAttribute("produits", Produit.findProduitEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
	            float nrOfPages = (float) Produit.countProduits() / sizeNo;
	            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
	        } else {
	            uiModel.addAttribute("produits", Produit.findAllProduits());
	        }
	        addDateTimeFormatPatterns(uiModel);
	        return "produits/list";
	    }

	@ModelAttribute("produits")
	public Collection<Produit> populateProduits() {
		return new ArrayList<Produit>();
	}	 

	  @RequestMapping(value = "/search", method = RequestMethod.GET)
		public String searchDette(@RequestParam("name") String  name,  Model uiModel) {
			
			if("".equals(name)){
				Integer page = 1;
				Integer size = 50;
				int sizeNo = size == null ? 10 : size.intValue();
	            uiModel.addAttribute("produits", Produit.findProduitEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
	            float nrOfPages = (float) Produit.countProduits() / sizeNo;
	            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
			}else{
					List<Produit> list = Produit.findProduitsByDesignationLike(name).setMaxResults(50).getResultList();
					uiModel.addAttribute("produits", list);
			}
			return "produits/list";
		}
	  
	  
	  
	  // Formulaire de creation de solde
	  @RequestMapping(value="/soldes", params="form", method=RequestMethod.GET)
	  public String soldesForm(Model uiModel){
		   ConfigurationSoldes soldes = new ConfigurationSoldes();
		   uiModel.addAttribute("solde", soldes);
		  return "produits/soldes";
	  }
	  
	  // Creer ou mettre a jour un solde
	  @RequestMapping(value="/soldes/create", method=RequestMethod.POST)
	  @Transactional
	  public String createOrUpdateSolde(@ModelAttribute("solde") @Valid ConfigurationSoldes solde, BindingResult bindingResult, 
			  @RequestParam(value="pId", required=false)Long pId, Model uiModel, HttpServletRequest httpServletRequest){
		  ConfigurationSoldes.validateAll(bindingResult, solde);
		  if(bindingResult.hasErrors()){
			  uiModel.addAttribute("errors", "Erreurs!");
			  uiModel.addAttribute("solde", solde);
			  return "produits/soldes";
		  }
		  Produit produit = Produit.findProduitsByCipEquals(solde.getCipProduit()).getSingleResult();
		  ConfigurationSoldes config = produit.getConfigSolde();
		  if(config==null){
			  produit.setConfigSolde(solde);// creation
		  }else{
				  if(pId!=null){
					  uiModel.addAttribute("forbid", "Solde en cours pour ce produit");
					  return "produits/soldes";
			      }
			  produit.setConfigSolde(solde);// Mise a jour
 		  }
		  uiModel.asMap().clear();
		  produit.merge();
		  produit.flush();
//		  uiModel.addAttribute("produitsoldes", Produit.findProduitsWithConfigSolde().getResultList());
//		  return "produits/soldes";
		  return "redirect:/produits/show/"+encodeUrlPathSegment(produit.getId().toString(), httpServletRequest);
	  }
	  
	  @RequestMapping(value = "/show/{id}", method = RequestMethod.GET)
	  public String showConfig(@PathVariable("id") Long id, Model uiModel){
		  List<Produit> produits= Produit.findProduitsWithConfigSolde().getResultList();
		  uiModel.addAttribute("produitsoldes", produits);
		  return "produits/soldes";
	  }
	  
	  // Desactiver un solde
	  @RequestMapping(value="/cancelsolde/{id}", method=RequestMethod.GET)
	  public String cancelSolde(@PathVariable("id")Long id, Model uiModel, HttpServletRequest request){
		  Produit produit = Produit.findProduit(id);
		  if(produit.getConfigSolde().getActiveConfig().equals(Boolean.TRUE)){
			  Produit.cancelSolde(produit);
		  }
		  return "produits/soldes";
	  }
	  
	  // Activer un solde
	  @RequestMapping(value="/activesolde/{id}", method=RequestMethod.GET)
	  public String activeSolde(@PathVariable("id")Long id, Model uiModel, HttpServletRequest request){
		  Produit produit = Produit.findProduit(id);
		  if(produit.getConfigSolde().getActiveConfig().equals(Boolean.FALSE)){
			  Produit.activateSolde(produit);
		  }
		  return "produits/soldes";
	  }
	  
	  // Supprimer un solde
	  @RequestMapping(value="/deletesolde/{id}", method=RequestMethod.GET)
	  @Transactional
	  public String deleteSolde(@PathVariable("id")Long id, Model uiModel, HttpServletRequest request){
		  Produit produit = Produit.findProduit(id);
		  if(!produit.getConfigSolde().equals(null)){
			  Produit.deleteSolde(produit);
		  }
		  uiModel.addAttribute("produitsoldes", Produit.findProduitsWithConfigSolde().getResultList());
		  return "produits/soldes";
	  }
	  
	  // Mettre a jour un solde
	  @RequestMapping(value="/updatesolde/{id}", method=RequestMethod.GET) 
	  public String updateSolde(@PathVariable("id")Long id, Model uiModel, HttpServletRequest request){
		         Produit produit = Produit.findProduit(id);
		         ConfigurationSoldes solde = produit.getConfigSolde();
		         solde.setCipProduit(produit.getCip());
		         uiModel.addAttribute("solde", solde);
		         uiModel.addAttribute("produitsoldes", Produit.findProduitsWithConfigSolde().getResultList());
		  return "produits/soldes";
	  }
	  
	  @ModelAttribute("solde")
	  public ConfigurationSoldes soldes(){
		  return new ConfigurationSoldes();
	  }
	  
	  // Chargement des produits ayant une configuration de solde
	    @ModelAttribute("produitsoldes")
		public Collection<Produit> populateProduitsSolde() {
			return Produit.findProduitsWithConfigSolde().getResultList();
		}
	    
	    @RequestMapping(value="/findProductAjax")
	    @ResponseBody
		public String findByDesAjax( @RequestParam("designation") String designation, Model uiModel) {
			
			List<Produit> list = Produit.findProduitsByDesignationLike(designation).getResultList();
			return Produit.toJsonArray(list);
		}
	    
	    @RequestMapping(value="/findProductByidAjax/{id}")
	    @ResponseBody
		public String findByIdAjax( @PathVariable("id") Long id, Model uiModel) {
			Produit produit = Produit.findProduit(id);
			return produit.toJson();
		}
	    
	    @RequestMapping(value = "updateCip")
	    public String updateCip(@RequestParam(value="id", required=false) Long id,
	    		@RequestParam(value="cip", required=false) String cip, Model uiModel) {
	    	
	    	if(id!=null && cip!=null && !cip.equals("")){
	    		List<Produit> list = Produit.findProduitsByCipEquals(cip).getResultList();
	    		if(list!=null && !list.isEmpty()){
	    			uiModel.addAttribute("message", "Le cip que vous avez entrez, existe deja! ");
	    		}else{
	    			Produit produit = Produit.findProduit(id);
	    			produit.setCip(cip);
	    			produit.merge();
	    			uiModel.addAttribute("message", "Le cip du produit a ete modifie! ");
	    		}
	    	}else{
	    		uiModel.addAttribute("message", "Selectionner un produit et entrer un nouveau cip! ");
	    	}
	    	
	        return "produits/changecip";
	    }

}
