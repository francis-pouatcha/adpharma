package org.adorsys.adpharma.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.adorsys.adpharma.beans.CommandeProcess;
import org.adorsys.adpharma.domain.CommandeFournisseur;
import org.adorsys.adpharma.domain.FamilleProduit;
import org.adorsys.adpharma.domain.Fournisseur;
import org.adorsys.adpharma.domain.LigneCmdFournisseur;
import org.adorsys.adpharma.domain.ModeSelection;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.Rayon;
import org.adorsys.adpharma.utils.Contract;
import org.adorsys.adpharma.utils.ProcessHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@RequestMapping("/commandprocesses/{cmdId}/findProduct")
@Controller
public class CommandProcessFindProductController {
	
	@RequestMapping(params = "form", method = RequestMethod.GET)
    public String findProductForm(@PathVariable("cmdId") Long cmdId,Model uiModel,HttpSession session) {
				 CommandeFournisseur commandeFournisseur = CommandeFournisseur.findCommandeFournisseur(cmdId) ;
					 CommandeProcess commandeProcess = new CommandeProcess(cmdId,commandeFournisseur.getFournisseur().getName());
					 ModeSelection modeDeSelection = commandeFournisseur.getModeDeSelection();
					 if (modeDeSelection.equals(ModeSelection.RUPTURE_STOCK)) {
					      commandeProcess.setProductResult(Produit.findProduitRuptureStockEntries(1, 100));

					}
					 if (modeDeSelection.equals(ModeSelection.ALERTE_STOCK)) {
					      commandeProcess.setProductResult(Produit.findProduitAlerteStockEntries(1, 100));

					}
					 if (modeDeSelection.equals(ModeSelection.MANUELLE)) {
					      commandeProcess.setProductResult(new ArrayList<Produit>());

					}
					 uiModel.addAttribute("commandeProcess",commandeProcess);
        return "commandprocesses/findCommandProduct";
    }
	
	 
    @RequestMapping(params = "find=ByDesignationLike", method = RequestMethod.GET)
    public String findProduitsByDesignationLike(@PathVariable("cmdId") Long cmdId,@RequestParam("designation") String designation, Model uiModel,
    		HttpServletRequest httpServletRequest ,HttpSession session) {
        Contract.notBlank("designation", designation);
    	List<Produit> produits = Produit.findProduitsByDesignationLike(designation).getResultList();
    return	showPoduct(produits, uiModel, cmdId);
    }
    
    @RequestMapping(params = "find=ByProduitNumberLike", method = RequestMethod.GET)
    public String findProduitsByProduitNumberLike(@PathVariable("cmdId") Long cmdId,HttpServletRequest httpServletRequest ,HttpSession session
    		, @RequestParam("produitNumber") String produitNumber, Model uiModel) {
        Contract.notBlank("produitNumber", produitNumber);

        List<Produit> produits = Produit.findProduitsByProduitNumberLike(produitNumber).getResultList();
    	return showPoduct(produits, uiModel, cmdId);
    	
    }
    
    @RequestMapping(params = "find=ByRayon", method = RequestMethod.GET)
    public String findProduitsByRayon(@PathVariable("cmdId") Long cmdId,@RequestParam("rayon") Rayon rayon,HttpServletRequest httpServletRequest
    		,HttpSession session, Model uiModel) {
        Contract.notNull("rayont", rayon);
        List<Produit> produits = Produit.findProduitsByRayon(rayon).getResultList();
        return showPoduct(produits, uiModel, cmdId);
    }

    @RequestMapping(params = "find=ByFamilleProduit", method = RequestMethod.GET)
    public String findProduitsByFamilleProduit(@PathVariable("cmdId") Long cmdId,HttpServletRequest httpServletRequest ,HttpSession session,@RequestParam("familleProduit") FamilleProduit familleProduit, Model uiModel) {
       Contract.notNull("familleProduit", familleProduit);
    	List<Produit> produits = Produit.findProduitsByFamilleProduit(familleProduit).getResultList();
        return showPoduct(produits, uiModel, cmdId);
    }
    
    
    // display the list of product to the client 
    
  public String showPoduct(List<Produit>  produits ,Model uiModel,Long cmdId){
	  CommandeFournisseur commandeFournisseur = CommandeFournisseur.findCommandeFournisseur(cmdId) ;
		 CommandeProcess commandeProcess = new CommandeProcess(cmdId,commandeFournisseur.getFournisseur().getName());
   commandeProcess.setProductResult(Produit.findAllProduits());
   commandeProcess.setProductResult(produits);
		 uiModel.addAttribute("commandeProcess",commandeProcess);
return "commandprocesses/findCommandProduct";
  }
	
	//les dependences
	
    @ModelAttribute("rayons")
    public Collection<Rayon> populateRayons() {
        return Rayon.findAllRayons();
    }
    @ModelAttribute("familleproduits")
    public Collection<FamilleProduit> populateFamilleProduits() {
        return FamilleProduit.findAllFamilleProduits();
    }

}
