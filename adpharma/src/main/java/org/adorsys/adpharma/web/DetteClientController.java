package org.adorsys.adpharma.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.adorsys.adpharma.domain.AvoirClient;
import org.adorsys.adpharma.domain.Client;
import org.adorsys.adpharma.domain.CommandeClient;
import org.adorsys.adpharma.domain.DetteClient;
import org.adorsys.adpharma.domain.EtatCredits;
import org.adorsys.adpharma.domain.LigneCmdClient;
import org.apache.commons.lang.StringUtils;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RooWebScaffold(path = "detteclients", formBackingObject = DetteClient.class)
@RequestMapping("/detteclients")
@Controller
public class DetteClientController {
	
	 @RequestMapping(params = { "find=BySolderNotAndClientNoEquals", "form" }, method = RequestMethod.GET)
	    public String findDetteClientsBySolderNotOrClientNoEqualsForm(Model uiModel) {
	        return "detteclients/findDetteClientsBySolderNotOrClientNoEquals";
	    }
	 
	 @RequestMapping(params = { "find=BySearch", "form" }, method = RequestMethod.GET)
		public String Search(Model uiModel) {
		 DetteClient detteClient = new DetteClient();
		 detteClient.setDateCreation(null);
			uiModel.addAttribute("detteClient",detteClient );
			return "detteclients/search";
		}
	 
	 @RequestMapping(value="findDetails/ByAjax/{cltId}", method = RequestMethod.GET)
		@ResponseBody
		public String findDetails(@PathVariable("cltId") Long cltId, Model uiModel) {
		 List<DetteClient> detteclients = DetteClient.findDetteClientsByClientIdEqualsAndSolderNot(cltId, Boolean.TRUE).getResultList();
       return  DetteClient.toJsonArray(detteclients);
	 }

		@RequestMapping(value = "/BySearch", method = RequestMethod.POST)
		public String Search(DetteClient detteClient , Model uiModel) {
			uiModel.addAttribute("results", DetteClient.search(detteClient.getClientName(),detteClient.getAssurer(), detteClient.getDateCreation(), detteClient.getSolder(), detteClient.getFactureNo()).getResultList());
			addDateTimeFormatPatterns(uiModel);
			uiModel.addAttribute("detteClient", detteClient);
			return "detteclients/search";
		}
	    
	    @RequestMapping(params = "find=BySolderNotAndClientNoEquals", method = RequestMethod.GET)
	    public String findDetteClientsBySolderNotOrClientNoEquals(@RequestParam(value = "solder", required = false) Boolean solder, @RequestParam("clientNo") String clientNo, Model uiModel) {
	    	List<DetteClient> resultList = DetteClient.findDetteClientsBySolderNotAndClientNoEquals(solder == null ? new Boolean(false) : solder, "CL-"+ StringUtils.remove(clientNo, "CL-")).getResultList() ;
	    	if (!resultList.isEmpty()) {
		    	uiModel.addAttribute("clientNo",clientNo);
		    	uiModel.addAttribute("solder",solder == null ? new Boolean(false) : solder );

			}
	    	uiModel.addAttribute("factures",resultList);
	        return "detteclients/findDetteClientsBySolderNotOrClientNoEquals";
	    }
	    
	    @RequestMapping("/printfactureGlobal/{solder}/{clientNo}.pdf")
		public String printfiche(@PathVariable("solder")Boolean solder,@PathVariable("clientNo") String clientNo, Model uiModel){
	    	uiModel.addAttribute("clientNo","CL-"+ StringUtils.remove(clientNo, "CL-"));
	    	uiModel.addAttribute("solder",solder);
	    	return "listeFacturePdfView";

		}
	    
	    @RequestMapping("/{etatId}/annuler/{detteId}")
		public String anullerDette(@PathVariable("etatId")Long etatId,@PathVariable("detteId") Long detteId, Model uiModel){
	    	DetteClient detteClient = DetteClient.findDetteClient(detteId);
	    	EtatCredits etatCredits = 	EtatCredits.findEtatCredits(etatId);

	    	if (detteClient!=null) {
	    		if (etatCredits.getEncaisser()) {
			         uiModel.addAttribute("apMessage", "IMPOSSIBLE D'ANNULER LA DETTE ETAT DEJA ENCAISSE! ");
	
				}else {
					detteClient.setAnnuler(true);
					detteClient.merge();
			         uiModel.addAttribute("apMessage", "DETTE ANNULEE AVEC SUCCES ! ");
                     etatCredits.computeAmount();

	}
				
			}
		    return	new EtatCreditsController().initShowView(uiModel, (EtatCredits)etatCredits.merge() );
		}
	    
	    @RequestMapping("/{etatId}/retorer/{detteId}")
		public String restorerDette(@PathVariable("etatId")Long etatId,@PathVariable("detteId") Long detteId, Model uiModel){
	    	DetteClient detteClient = DetteClient.findDetteClient(detteId);
	    	EtatCredits etatCredits = 	EtatCredits.findEtatCredits(etatId);
	    	if (detteClient!=null) {
	    		if (etatCredits.getEncaisser()) {
			         uiModel.addAttribute("apMessage", "IMPOSSIBLE DE RESTORER LA DETTE ETAT DEJA ENCAISSE! ");
	
				}else {
					detteClient.setAnnuler(false);
					detteClient.merge();
			         uiModel.addAttribute("apMessage", "DETTE RESTOREE AVEC SUCCES ! ");
                     etatCredits.computeAmount();
                    
				}
				

			}
	    return	new EtatCreditsController().initShowView(uiModel, (EtatCredits)etatCredits.merge() );
		}
	    
	    
	    @RequestMapping(value = "/searchDette", method = RequestMethod.GET)
		public String searchDette(@RequestParam("name") String  name,  Model uiModel) {
			
			if("".equals(name)){
				Integer page = 1;
				Integer size = 50;
				int sizeNo = size == null ? 10 : size.intValue();
	            uiModel.addAttribute("detteclients", DetteClient.findDetteClientEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
	            float nrOfPages = (float) DetteClient.countDetteClients() / sizeNo;
	            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
			}else{
					List<DetteClient> list = DetteClient.findDetteClientsByClient(name).setMaxResults(50).getResultList();
					uiModel.addAttribute("detteclients", list);
			}
			return "detteclients/list";
		}
	    
	    @ModelAttribute("detteclients")
	    public Collection<DetteClient> populateDetteClients() {
	       // return DetteClient.findAllDetteClients();
	    	return new ArrayList<DetteClient>();
	    }
	    
	    @ModelAttribute("etatcreditses")
	    public Collection<EtatCredits> populateEtatCreditses() {
	      //  return EtatCredits.findAllEtatCreditses();
	    	return new   ArrayList<EtatCredits>();
	    }
}
