package org.adorsys.adpharma.web;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.adorsys.adpharma.beans.ApprovisonementProcess;
import org.adorsys.adpharma.beans.CommandeProcess;
import org.adorsys.adpharma.domain.AdPharmaBaseEntity;
import org.adorsys.adpharma.domain.Approvisionement;
import org.adorsys.adpharma.domain.CommandeFournisseur;
import org.adorsys.adpharma.domain.Etat;
import org.adorsys.adpharma.domain.Fournisseur;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.LigneCmdFournisseur;
import org.adorsys.adpharma.domain.ModeSelection;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.Site;
import org.adorsys.adpharma.domain.TVA;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.adorsys.adpharma.utils.ProcessHelper;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@RequestMapping("/commandprocesses")
@Controller
public class CommandProcessController {
	@Transactional
	 @RequestMapping(method = RequestMethod.POST)
	    public String createCommande(@Valid CommandeFournisseur commandeFournisseur, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest,HttpSession session) {
	        if (bindingResult.hasErrors()) {
	            uiModel.addAttribute("commandeFournisseur", commandeFournisseur);
	            ProcessHelper.addDateTimeFormatPatterns(uiModel);
	            return "commandprocesses/create";
	        }
	        uiModel.asMap().clear();
	        session.setAttribute("productResult", Produit.findAllProduits());
	        commandeFournisseur.persist();
	        return "redirect:/commandprocesses/" + ProcessHelper.encodeUrlPathSegment(commandeFournisseur.getId().toString(), httpServletRequest)+"/editCommand";
	    }
	 
	 @RequestMapping(value = "/{cmdId}/addLine", method = RequestMethod.POST)
    public String addLine(@PathVariable("cmdId") Long cmdId,@RequestParam Long pId,@RequestParam BigInteger qte,
          @RequestParam BigDecimal pa ,Model uiModel,HttpSession session) {
          CommandeFournisseur commandeFournisseur = CommandeFournisseur.findCommandeFournisseur(cmdId);
         Produit produit = Produit.findProduit(pId);
         if (commandeFournisseur.contientProduit(produit)) {
			uiModel.addAttribute("apMessage", "Ce produit est deja dans la liste ");
		}else{
			LigneCmdFournisseur line = new LigneCmdFournisseur();
			line.setCommande(commandeFournisseur);
			line.setPrixAchatMin(pa);
			line.setProduit(produit);
			line.setQuantiteCommande(qte);
			line.persist();
              }
         CommandeProcess commandeProcess = new CommandeProcess(cmdId, LigneCmdFournisseur.findLigneCmdFournisseursByCommande(commandeFournisseur).getResultList(),
        		 CommandeFournisseur.findCommandeFournisseur(cmdId).getFournisseur().getName());
        uiModel.addAttribute("commandeProcess",commandeProcess);
        return "commandprocesses/editCommand";

	 }
	 
	 @RequestMapping(value = "/{cmdId}/showProduct/{pId}", method = RequestMethod.GET)
	    public String showProduct(@PathVariable("cmdId") Long cmdId,@PathVariable("pId") Long pId, Model uiModel,HttpSession session) {
		ProcessHelper.addDateTimeFormatPatterns(uiModel);
	        uiModel.addAttribute("produit", Produit.findProduit(pId));
	        uiModel.addAttribute("itemId", pId);
	        return "commandprocesses/showProduct";
		 
	    }
@RequestMapping(value = "/{cmdId}/deleteLine/{lineId}",method = RequestMethod.GET)
    public String unselectLine(@PathVariable("cmdId") Long cmdId, @PathVariable("lineId") Long lineId ,Model uiModel, HttpServletRequest httpServletRequest) {
	        LigneCmdFournisseur line = LigneCmdFournisseur.findLigneCmdFournisseur(lineId) ;
	        line.remove();
	        return "redirect:/commandprocesses/" + ProcessHelper.encodeUrlPathSegment(cmdId.toString(), httpServletRequest)+"/editCommand";
        }


@RequestMapping(value = "/{cmdId}/updateLine/{lineId}", method = RequestMethod.GET)
    public String updateLineForm(@PathVariable("cmdId") Long cmdId, @PathVariable("lineId") Long lineId  ,Model uiModel, HttpServletRequest httpServletRequest) {
	LigneCmdFournisseur line = LigneCmdFournisseur.findLigneCmdFournisseur(lineId);
    CommandeFournisseur commandeFournisseur = CommandeFournisseur.findCommandeFournisseur(cmdId);
    CommandeProcess commandeProcess = new CommandeProcess(cmdId, LigneCmdFournisseur.findLigneCmdFournisseursByCommande(commandeFournisseur).getResultList(),
   		 CommandeFournisseur.findCommandeFournisseur(cmdId).getFournisseur().getName());
    commandeProcess.setLineToUpdate(LigneCmdFournisseur.findLigneCmdFournisseur(lineId));
   uiModel.addAttribute("commandeProcess",commandeProcess);
   return "commandprocesses/editCommand";
        }


@RequestMapping(value = "/{cmdId}/selectProduct/{pId}", method = RequestMethod.GET)
    public String selectProduct(@PathVariable("cmdId") Long cmdId, @PathVariable("pId") Long pId  ,Model uiModel, HttpServletRequest httpServletRequest) {
    CommandeFournisseur commandeFournisseur = CommandeFournisseur.findCommandeFournisseur(cmdId);
    Produit produit = Produit.findProduit(pId);
    CommandeProcess commandeProcess = new CommandeProcess(cmdId, LigneCmdFournisseur.findLigneCmdFournisseursByCommande(commandeFournisseur).getResultList(),
   		 CommandeFournisseur.findCommandeFournisseur(cmdId).getFournisseur().getName());
    commandeProcess.setProduit(produit);
   uiModel.addAttribute("commandeProcess",commandeProcess);
   return "commandprocesses/editCommand";
        }


@RequestMapping(value = "/{cmdId}/updateLine/{lineId}", method = RequestMethod.POST)
    public String updateLine(@PathVariable("cmdId") Long cmdId, @PathVariable("lineId") Long lineId , @RequestParam BigInteger qte, @RequestParam BigDecimal pa 
    		,Model uiModel, HttpServletRequest httpServletRequest) {
	LigneCmdFournisseur line = LigneCmdFournisseur.findLigneCmdFournisseur(lineId);
		line.setQuantiteCommande(qte);
		line.setPrixAchatMin(pa);
		line.merge();
    CommandeFournisseur commandeFournisseur = CommandeFournisseur.findCommandeFournisseur(cmdId);
    CommandeProcess commandeProcess = new CommandeProcess(cmdId, LigneCmdFournisseur.findLigneCmdFournisseursByCommande(commandeFournisseur).getResultList(),
   		 CommandeFournisseur.findCommandeFournisseur(cmdId).getFournisseur().getName());
   uiModel.addAttribute("commandeProcess",commandeProcess);
   return "commandprocesses/editCommand";
        }

	 @RequestMapping(params = "form", method = RequestMethod.GET)
	    public String createForm(Model uiModel) {
	        uiModel.addAttribute("commandeFournisseur", new CommandeFournisseur());
	        ProcessHelper.addDateTimeFormatPatterns(uiModel);
	        List dependencies = new ArrayList();
	        if (Fournisseur.countFournisseurs() == 0) {
	            dependencies.add(new String[]{"fournisseur", "fournisseurs"});
	        }
	        uiModel.addAttribute("dependencies", dependencies);
	        return "commandprocesses/create";
	    }
	 
	 @RequestMapping(value = "/{cmdId}/editCommand", method = RequestMethod.GET)
	    public String editCommand(@PathVariable("cmdId") Long cmdId, Model uiModel,HttpSession session) {
		 CommandeFournisseur commandeFournisseur = CommandeFournisseur.findCommandeFournisseur(cmdId) ;
		 CommandeProcess commandeProcess = new CommandeProcess(cmdId, LigneCmdFournisseur.findLigneCmdFournisseursByCommande(commandeFournisseur).getResultList(),
				 commandeFournisseur.getFournisseur().getName());
	        uiModel.addAttribute("commandeProcess",commandeProcess);
	        return "commandprocesses/editCommand";
	    }

	 @Transactional
	 @RequestMapping(value = "/{cmdId}/closeCommand", method = RequestMethod.GET)
	    public String closeCommand(@PathVariable("cmdId") Long cmdId, Model uiModel,HttpSession session) {
		 CommandeFournisseur commandeFournisseur = CommandeFournisseur.findCommandeFournisseur(cmdId) ;
		 if (commandeFournisseur.getLigneCommande().isEmpty()) {
		        uiModel.addAttribute("appMessages",Arrays.asList("Impossible de cloturer Cette Commande ! Car ne contient Aucun Produit "));

		}else {
			 commandeFournisseur.setEtatCmd(Etat.CLOS);
		      uiModel.addAttribute("appMessages",Arrays.asList("Commande cloturer avec succes "));

		}
		  CommandeFournisseur merge = (CommandeFournisseur) commandeFournisseur.merge();
		 ProcessHelper.addDateTimeFormatPatterns(uiModel);
	        uiModel.addAttribute("commandefournisseur", merge);
	        uiModel.addAttribute("itemId", merge.getId());
			return "redirect:/saleprocess/newPublicCmd";
	    }
	 
    @Transactional
	 @RequestMapping(value = "/{cmdId}/enregistrerCmd", method = RequestMethod.GET)
	    public String enregistrer(@PathVariable("cmdId") Long cmdId, Model uiModel,HttpSession session) {
		 CommandeFournisseur commandeFournisseur = CommandeFournisseur.findCommandeFournisseur(cmdId) ;
		  ProcessHelper.addDateTimeFormatPatterns(uiModel);
	        uiModel.addAttribute("commandefournisseur", commandeFournisseur);
	        uiModel.addAttribute("itemId",cmdId);
	        commandeFournisseur.merge();
	        return "commandprocesses/show";
	    }


   @Transactional
	 @RequestMapping(value = "/{cmdId}/annulerCmd", method = RequestMethod.GET)
	    public String annuler(@PathVariable("cmdId") Long cmdId, Model uiModel,HttpSession session) {
		 CommandeFournisseur commandeFournisseur = CommandeFournisseur.findCommandeFournisseur(cmdId) ;
	        session.removeAttribute("productResult");
	        if (commandeFournisseur!=null) {
	        	if (commandeFournisseur.getEtatCmd().equals(Etat.CLOS) || ! commandeFournisseur.getLivre()) {
				commandeFournisseur.setAnnuler(true);
				 commandeFournisseur.merge();
			        uiModel.addAttribute("appMessages",Arrays.asList("Commande  Annuler Avec Succes ! "));
				 uiModel.addAttribute("commandefournisseur", commandeFournisseur);
			        uiModel.addAttribute("itemId",cmdId);
			        return "commandprocesses/show";
			
			}else {
				commandeFournisseur.remove() ;
		        uiModel.addAttribute("appMessages",Arrays.asList("Commande Supprimee avec Success ! "));
			}
			}else {
		        uiModel.addAttribute("appMessages",Arrays.asList("Commande  deja Supprimee ou inexistant ! "));
	
			}
	           return "caisses/infos";
	    }
   
   // imprime le bon de commande
   @RequestMapping("/{cmdId}/print/{bonCmdId}.pdf")
	public String print( @PathVariable("cmdId")Long cmdId, @PathVariable("bonCmdId")String invoiceNumber, Model uiModel){
CommandeFournisseur commande = CommandeFournisseur.findCommandeFournisseur(cmdId);
uiModel.addAttribute("commande", commande);
       return "commandProsessDocView";

	}
	 
	 
	 //necessaire pour les vues 

	    @ModelAttribute("fournisseurs")
	    public Collection<Fournisseur> populateFournisseurs() {
	        return Fournisseur.findAllFournisseurs();
	    }
	    
	    @ModelAttribute("modeselections")
	    public Collection<ModeSelection> populateModeSelections() {
	        return Arrays.asList(ModeSelection.class.getEnumConstants());
	    }
	    

	    @ModelAttribute("sites")
	    public Collection<Site> populateSites() {
	        return Site.findAllSites();
	    }
	    
	    @ModelAttribute("tvas")
	    public Collection<TVA> populateTVAS() {
	        return TVA.findAllTVAS();
	    }
	    
	    

}
