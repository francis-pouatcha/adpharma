package org.adorsys.adpharma.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.adorsys.adpharma.beans.importExport.ExportLignesApprovisionnement;
import org.adorsys.adpharma.beans.importExport.LigneInventaireImportExportService;
import org.adorsys.adpharma.domain.Client;
import org.adorsys.adpharma.domain.Etat;
import org.adorsys.adpharma.domain.Filiale;
import org.adorsys.adpharma.domain.Inventaire;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.LigneInventaire;
import org.adorsys.adpharma.domain.PharmaUser;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.Rayon;
import org.adorsys.adpharma.domain.Site;
import org.adorsys.adpharma.utils.LocaleUtil;
import org.adorsys.adpharma.utils.ProcessHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@RooWebScaffold(path = "inventaires", formBackingObject = Inventaire.class)
@RequestMapping("/inventaires")
@Controller
public class InventaireController {
	@Autowired
	private LigneInventaireImportExportService ligneinventaireImportExportService ;
	
	@Autowired
	ExportLignesApprovisionnement exportLignesApprovisionnement;
	
	@Resource(name="messageSource")
	ReloadableResourceBundleMessageSource messageSource;
	
	
	@RequestMapping(params = { "find=BySearch", "form" }, method = RequestMethod.GET)
	public String Search(Model uiModel) {
		uiModel.addAttribute("pharmausers", ProcessHelper.populateUsers());
		uiModel.addAttribute("inventaire",new Inventaire() );
		return "inventaire/search";
	}

	@RequestMapping(value="/search", method = RequestMethod.GET)
	public String Search(Inventaire inventaire , Model uiModel) {
		uiModel.addAttribute("results", inventaire.searchInventaire(inventaire.getNumeroInventaire(), inventaire.getAgent(), inventaire.getEtat() ,inventaire.getDateDebut(), inventaire.getDateFin()));
		uiModel.addAttribute("pharmausers", ProcessHelper.populateUsers());
		uiModel.addAttribute("inventaire",inventaire);
		return "inventaire/search";
	}
	
	@RequestMapping(params = "form", method = RequestMethod.GET)
	public String createForm(Model uiModel) {
		
			uiModel.addAttribute("inventaire", new Inventaire());
			addDateTimeFormatPatterns(uiModel);
			return "inventaires/create";
	}
	@RequestMapping(method = RequestMethod.POST)
	public String create(@Valid Inventaire inventaire, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		if (bindingResult.hasErrors()) {
			uiModel.addAttribute("inventaire", inventaire);
			addDateTimeFormatPatterns(uiModel);
			return "inventaires/create";
		}
		Rayon rayon = inventaire.getRayon();
		
		try {
			inventaire.setRayonId(rayon.getId());
		} catch (Exception e) {
			inventaire.setRayonId(Long.valueOf(0));
		}
		
		uiModel.asMap().clear();
		inventaire.persist();
		MultipartFile fichier = inventaire.getFichier();
		if(fichier!=null){
		 Workbook workbook = null;
		try {
			workbook = Workbook.getWorkbook(fichier.getInputStream());
			if(workbook != null){
				 List<LigneInventaire> importListFromSheet = ligneinventaireImportExportService.importListFromSheet(workbook.getSheet(0), inventaire);
				 inventaire.getLigneInventaire().addAll(importListFromSheet);
				 inventaire.calculateMontantEcart();
				}
		} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		if(!inventaire.isDoNotSelectAnyProduct()){
		List<Produit> search = Produit.search(null, null, null, null, inventaire.getBeginBy(), inventaire.getEndBy(), inventaire.getRayon(), null, null ,null).getResultList();
		for (Produit produit : search) {
			if(!inventaire.contientProduit(produit)){
				LigneInventaire itemFromProduct = Inventaire.itemFromProduct(produit);
				if(itemFromProduct!=null){
					itemFromProduct.setInventaire(inventaire);
					itemFromProduct.persist();
				}
			}
			
			
		}
		}
		inventaire = (Inventaire) inventaire.merge();
		
		return "redirect:/inventaireProcess/" + encodeUrlPathSegment(inventaire.getId().toString(), httpServletRequest)+"/editInventaire";
	}
	
	@RequestMapping(value="/loadfile/{id}",method = RequestMethod.POST)
	  public String loadfile(@PathVariable("id")Long id, @RequestParam("fichier")MultipartFile fichier, Model uiModel, HttpServletRequest httpServletRequest)
	    {
	        Inventaire inventaire = Inventaire.findInventaire(id);
	        try
	        {
	            Workbook workbook = Workbook.getWorkbook(fichier.getInputStream());
	            Sheet sheet = workbook.getSheet(0);
	                ligneinventaireImportExportService.mergeFromWorkbook(inventaire, sheet);
	                inventaire.calculateMontantEcart();
	                inventaire.merge();
	        }
	        catch(BiffException e)
	        {
	            e.printStackTrace();
	        }
	        catch(IOException e)
	        {
	            e.printStackTrace();
	        }
	        uiModel.asMap().clear();
			return "redirect:/inventaireProcess/" + encodeUrlPathSegment(inventaire.getId().toString(), httpServletRequest)+"/editInventaire";
	    }
	  
	  
	  
	  
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String show(@PathVariable("id") Long id, Model uiModel) {
		addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("inventaire", Inventaire.findInventaire(id));
		uiModel.addAttribute("itemId", id);
		return "inventaireProcess/show";
	}

	@RequestMapping(value = "/searchInv", method = RequestMethod.GET)
	public String search(@RequestParam("name") String  name,  Model uiModel) {
		
		if("".equals(name)){
			Integer page = 1;
			Integer size = 50;
			int sizeNo = size == null ? 10 : size.intValue();
            uiModel.addAttribute("inventaires", Inventaire.findInventaireEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) Inventaire.countInventaires() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
		}else{
				List<Inventaire> list = Inventaire.findInventaireByNomAgentLike(name).setMaxResults(50).getResultList();
				uiModel.addAttribute("inventaires", list);
		}
		return "inventaires/list";
	}

	@RequestMapping(value = "/inventaireEnCour",method = RequestMethod.GET)
	public String inventaireEnCour(Model uiModel,HttpServletRequest httpServletRequest ) {
		List<Inventaire> resultList = Inventaire.findInventairesByEtat(Etat.EN_COUR).getResultList();
		if (!resultList.iterator().hasNext()) {
			uiModel.addAttribute("apMessage",  messageSource.getMessage("inventory_found_warning", null, LocaleUtil.getCurrentLocale()));
			return "caisses/infos";

		}else {
			return "redirect:/inventaires/" + encodeUrlPathSegment(resultList.iterator().next().getId().toString(), httpServletRequest);

		}	
	}

	
	// imprime les inventaires 
	@RequestMapping("/{invId}/print/{invNumber}.pdf")
	public String print(@PathVariable("invId")Long invId, Model uiModel){
		Inventaire inventaire = Inventaire.findInventaire(invId);
		uiModel.addAttribute("inventaire", inventaire);
		return "inventairePdfDocView";

	}

	@RequestMapping("printFicheInventaireParRayon/{rayonId}.pdf")
	public String printficheRayon(@PathVariable("rayonId")Long rayonId, Model uiModel){
		uiModel.addAttribute("produits", Produit.findProduitsByRayon(Rayon.findRayon(rayonId)).getResultList());
		uiModel.addAttribute("rayonId", rayonId);

		return "ficheInventairePdfView";

	}
	@RequestMapping("printFicheInventaire/{debut}/{fin}.pdf")
	public String printfiche(@PathVariable("debut")String debut,@PathVariable("fin")String fin , Model uiModel){
		uiModel.addAttribute("produits", Produit.findProduitsByOrdreAlphabetique(debut, fin).getResultList());
		return "ficheInventairePdfView";

	}

	 @ModelAttribute("inventaires")
	   public Collection<Inventaire> populateInventaires() {
	     //   return Inventaire.findAllInventaires();
		 return null;
	    }
	    
	    @ModelAttribute("ligneinventaires")
	    public Collection<LigneInventaire> populateLigneInventaires() {
	       // return LigneInventaire.findAllLigneInventaires();
	    	return null;
	    }
	    
	    @ModelAttribute("etats")
	    public Collection<Etat> populateEtats() {
	        return Arrays.asList(Etat.class.getEnumConstants());
	    }
	    
	    @ModelAttribute("filiales")
	    public Collection<Filiale> populateFiliales() {
	        //return Filiale.findAllFiliales();
	    	return new ArrayList<Filiale>();
	    }
	    
	    @ModelAttribute("ligneapprovisionements")
	    public Collection<LigneApprovisionement> populateLigneApprovisionements() {
	      //  return LigneApprovisionement.findAllLigneApprovisionements();
	    	return new ArrayList<LigneApprovisionement>();
	    }
	    
	    @ModelAttribute("pharmausers")
	    public Collection<PharmaUser> populatePharmaUsers() {
	       // return PharmaUser.findAllPharmaUsers();
	    	return new ArrayList<PharmaUser>();
	    }
	    
	    @ModelAttribute("produits")
	    public Collection<Produit> populateProduits() {
	      //  return Produit.findAllProduits();
	    	return new ArrayList<Produit>();
	    }
	    
	    @ModelAttribute("rayons")
	    public Collection<Rayon> populateRayons() {
	    	return ProcessHelper.populateRayon();
	    }
	    
	    @ModelAttribute("sites")
	    public Collection<Site> populateSites() {
	        return Site.findAllSites();
	    }
	public Collection<String> populateLettress() {
		Collection<String> list = new ArrayList<String>();
		list.add("A");
		list.add("B");list.add("C");list.add("D");list.add("E");list.add("F");list.add("F");list.add("H");list.add("I");list.add("J");list.add("K");
		list.add("L");list.add("M");list.add("N");list.add("O");list.add("P");list.add("Q");list.add("R");list.add("S");list.add("T");list.add("U");
		list.add("V");list.add("W");list.add("X");list.add("Y");list.add("Z");
		return list ;
	}

}