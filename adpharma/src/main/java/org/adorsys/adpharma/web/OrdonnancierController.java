package org.adorsys.adpharma.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.adorsys.adpharma.domain.CommandeClient;
import org.adorsys.adpharma.domain.Ordonnancier;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RooWebScaffold(path = "ordonnanciers", formBackingObject = Ordonnancier.class)
@RequestMapping("/ordonnanciers")
@Controller
public class OrdonnancierController {
	
	 // imprime les ordonnanciers 
	   @RequestMapping("/printOrdonnancier/{ordNumber}.pdf")
		public String print(  @PathVariable("ordNumber") String ordNumber, Model uiModel){
		   List<Ordonnancier> ordonnanciers =  Ordonnancier.findOrdonnanciersByOrdNumberEquals(ordNumber).getResultList();
		   if (!ordonnanciers.isEmpty()) {
				uiModel.addAttribute("ordonnancier", ordonnanciers.iterator().next());
			       return "ordonnancierPdfDocView";
		}else {
		       return null;

		}

		}
	   
	   @ModelAttribute("commandeclients")
	    public Collection<CommandeClient> populateCommandeClients() {
	        return  new ArrayList<CommandeClient>();
	    }
	    
	    @ModelAttribute("ordonnanciers")
	    public Collection<Ordonnancier> populateOrdonnanciers() {
	        return new ArrayList<Ordonnancier>();
	    }
}
