package org.adorsys.adpharma.web;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.adorsys.adpharma.domain.Client;
import org.adorsys.adpharma.domain.EtatDette;
import org.adorsys.adpharma.domain.ListingEtatDette;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RooWebScaffold(path = "listingetatdettes", formBackingObject = ListingEtatDette.class)
@RequestMapping("/listingetatdettes")
@Controller
public class ListingEtatDetteController {
	 @RequestMapping(method = RequestMethod.POST)
	    public String create(@Valid ListingEtatDette listingEtatDette, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
	        if (bindingResult.hasErrors()) {
	            uiModel.addAttribute("listingEtatDette", listingEtatDette);
	            return "listingetatdettes/create";
	        }
	        if(listingEtatDette.getToutLesClients()==true){
	        	listingEtatDette.setClients(Client.findAllClients());
	        }else{
	        	listingEtatDette.getClients().add(listingEtatDette.getClient());
	        }
	        
	        for(Client c : listingEtatDette.getClients()){
	        	
	        	EtatDette etatDette = new EtatDette();
	        	etatDette.setAvoir(listingEtatDette.getAvoir());
	        	etatDette.setDateReglage(listingEtatDette.getDate());
	        	etatDette.setClient(c);
	        	etatDette.persist();
	        	etatDette.flush();
	        	listingEtatDette.getEtatDettes().add(etatDette);
	        }
	       
	        uiModel.addAttribute("listing", listingEtatDette);
	        return"listingetatdettes/show";
	 }
}
