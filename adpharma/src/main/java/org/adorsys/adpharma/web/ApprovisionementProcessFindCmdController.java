	package org.adorsys.adpharma.web;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.adorsys.adpharma.beans.ApprovisonementProcess;
import org.adorsys.adpharma.domain.Approvisionement;
import org.adorsys.adpharma.domain.CommandeFournisseur;
import org.adorsys.adpharma.domain.Etat;
import org.adorsys.adpharma.domain.Fournisseur;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.utils.ProcessHelper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/approvisionementprocess/{apId}/findCmdFour")
@Controller
public class ApprovisionementProcessFindCmdController {
	
	@RequestMapping(params = "form", method = RequestMethod.GET)
    public String findProductForm(@PathVariable("apId") Long apId,Model uiModel,HttpSession session) {
		 Approvisionement approvisionement = Approvisionement.findApprovisionement(apId);
		 ApprovisonementProcess approvisonementProcess = new ApprovisonementProcess(apId);
	        uiModel.addAttribute("approvisonementProcess",approvisonementProcess);
		    uiModel.addAttribute("apId", apId);
		return "approvisionementprocess/findcmdfour";
    }

	 
    @RequestMapping(params = "find=ByCmdNumberEquals", method = RequestMethod.GET)
    public String findCommandeFournisseursByCmdNumberEquals(@RequestParam("cmdNumber") String cmdNumber,@PathVariable("apId") Long apId, HttpServletRequest httpServletRequest, Model uiModel) {
    	 List<CommandeFournisseur> commandefournisseurs = CommandeFournisseur.findCommandeFournisseursByCmdNumberEquals(cmdNumber).getResultList();
    	return showCmdFournisseur(commandefournisseurs, uiModel, httpServletRequest, apId);
    }
    
    @RequestMapping(params = "find=ByFournisseurAndDateCreationBetween", method = RequestMethod.GET)
    public String  findCommandeFournisseursByFournisseurAndDateCreationBetween(@RequestParam("fournisseur") Fournisseur fournisseur,
    		@RequestParam("minDateCreation") @DateTimeFormat(pattern = "dd-MM-yyyy") Date minDateCreation,
    		@RequestParam("maxDateCreation") @DateTimeFormat(pattern = "dd-MM-yyyy") Date maxDateCreation,@PathVariable("apId") Long apId, HttpServletRequest httpServletRequest, Model uiModel) {
       List<CommandeFournisseur> commandefournisseurs = CommandeFournisseur.findCommandeFournisseursByFournisseurAndDateCreationBetween(fournisseur, minDateCreation, maxDateCreation).getResultList();
    	return showCmdFournisseur(commandefournisseurs, uiModel, httpServletRequest, apId);
    }
    
    
    @RequestMapping(params = "find=ByDateCreationBetweenAndEtatCmd", method = RequestMethod.GET)
    public String findCommandeFournisseursByDateCreationBetweenAndEtatCmd(@RequestParam("minDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date minDateCreation,
    		@RequestParam("maxDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date maxDateCreation,
    		@RequestParam("etatCmd") Etat etatCmd , @PathVariable("apId") Long apId, HttpServletRequest httpServletRequest, Model uiModel) {
    	List<CommandeFournisseur> commandefournisseurs = CommandeFournisseur.findCommandeFournisseursByDateCreationBetweenAndEtatCmd(minDateCreation, maxDateCreation, etatCmd).getResultList();
       
    	return showCmdFournisseur(commandefournisseurs, uiModel, httpServletRequest, apId);
    }
	

	   // display the list of commande to browser 
	  public String showCmdFournisseur(List<CommandeFournisseur>  commandefournisseurs ,Model uiModel,HttpServletRequest httpServletRequest,Long apId){
		 
				Approvisionement approvisionement = Approvisionement.findApprovisionement(apId);
				ApprovisonementProcess process = new ApprovisonementProcess(apId);
				process.setCommandeFournisseur(commandefournisseurs);
		        uiModel.addAttribute("approvisonementProcess",process);
		        uiModel.addAttribute("apId", apId);
   		return "approvisionementprocess/findcmdfour";

	  }
	
	
	
	  @ModelAttribute("etats")
	    public Collection<Etat> populateEtats() {
	        return Arrays.asList(Etat.class.getEnumConstants());
	    }
	
	 @ModelAttribute("fournisseurs")
	    public Collection<Fournisseur> populateFournisseurs() {
	        return Fournisseur.findAllFournisseurs();
	    }
	    
}
