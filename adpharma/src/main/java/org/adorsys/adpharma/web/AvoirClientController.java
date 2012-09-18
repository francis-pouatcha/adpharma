package org.adorsys.adpharma.web;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javassist.expr.NewArray;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.adorsys.adpharma.domain.AdPharmaBaseEntity;
import org.adorsys.adpharma.domain.AvoirClient;
import org.adorsys.adpharma.domain.Client;
import org.adorsys.adpharma.domain.Filiale;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.Rayon;
import org.adorsys.adpharma.domain.TypeBon;
import org.adorsys.adpharma.utils.ProcessHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RooWebScaffold(path = "avoirclients", formBackingObject = AvoirClient.class)
@RequestMapping("/avoirclients")
@Controller
public class AvoirClientController {
	
	@RequestMapping(params = { "find=BySearch", "form" }, method = RequestMethod.GET)
	public String Search(Model uiModel) {
		uiModel.addAttribute("avoir", new AvoirClient());
		return "avoirclients/search";
	}

	@RequestMapping(value = "/BySearch", method = RequestMethod.GET)
	public String Search(AvoirClient avoir , Model uiModel) {
		uiModel.addAttribute("results", AvoirClient.search(avoir.getDateEdition(),avoir.getNumero(), avoir.getClientName(), avoir.getClientNumber(), avoir.getTypeBon(), avoir.getAnnuler(), avoir.getsolder()).getResultList());
		addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("avoir", avoir);
		return "avoirclients/search";
	}
	
	@RequestMapping(value = "/annuler/{id}", method = RequestMethod.GET)
	public String annuler(@PathVariable("id") Long id, Model uiModel,HttpServletRequest httpServletRequest) {
		 AvoirClient avoirClient = AvoirClient.findAvoirClient(id);
		 if (avoirClient!=null) {
			 avoirClient.setAnnuler(true);
			 avoirClient.merge();
		}
	        return "redirect:/avoirclients/" + encodeUrlPathSegment(id.toString(), httpServletRequest);
	}
	
	@RequestMapping(value = "/restorer/{id}", method = RequestMethod.GET)
	public String restorer(@PathVariable("id") Long id, Model uiModel,HttpServletRequest httpServletRequest) {
		 AvoirClient avoirClient = AvoirClient.findAvoirClient(id);
		 if (avoirClient!=null) {
			 avoirClient.setAnnuler(false);
			 avoirClient.merge();
		}
	        return "redirect:/avoirclients/" + encodeUrlPathSegment(id.toString(), httpServletRequest);
	}
	@RequestMapping(value = "/byNumber/{numero}", method = RequestMethod.GET)
    public String getAvoirByNumero(@PathVariable("numero") String numero, Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
         AvoirClient avoirClient = AvoirClient.findAvoirClientsByNumeroEquals(numero.trim()).getResultList().iterator().next();
        uiModel.addAttribute("avoirclient", avoirClient);
        uiModel.addAttribute("itemId", avoirClient.getId());
        return "avoirclients/show";
    }
	
	@RequestMapping(value = "/findByNumberAjax", method = RequestMethod.GET)
	@ResponseBody
    public String findByNumberAjax(@RequestParam("numero") String numero,@RequestParam("montant") BigDecimal montant, Model uiModel) {
 List<AvoirClient> avoirClients= AvoirClient.search(null,numero, null, null, null, null, null).getResultList();
        if (avoirClients.isEmpty()) return "Informations Du bon Incorrect !" ;
	     AvoirClient avoirClient =	avoirClients.iterator().next();	
	    if (avoirClient.getAnnuler()) return " Ce Bon Est Annuller ! " ;
	    if (avoirClient.getReste().intValue() != montant.intValue()) return "Informations  Incorrect !" ;
        return "ok";
    }

	
	@RequestMapping(value = "/search", method = RequestMethod.GET)
    public String getSearchForm(Model uiModel) {
        uiModel.addAttribute("avoirclient", new AvoirClient());
        return "avoirclients/search";
    }
	
	
	@RequestMapping(value = "/search", method = RequestMethod.POST)
    public String getSearch( AvoirClient avoir,Model uiModel) {
        List<AvoirClient> resultList = AvoirClient.search(avoir.getDateEdition(),avoir.getNumero(), avoir.getClientName(), avoir.getClientNumber(), avoir.getTypeBon(), avoir.getAnnuler(),null).setMaxResults(50).getResultList();
        System.out.println(resultList);
        uiModel.addAttribute("avoirclients", resultList);
        uiModel.addAttribute("avoirclient", new AvoirClient());
        return "avoirclients/search";
    }
	
	@RequestMapping(value = "/print/{id}.pdf", method = RequestMethod.GET)
    public String printAvoir(@PathVariable("id") Long id, Model uiModel) {
        AvoirClient avoirClient = AvoirClient.findAvoirClient(id);
        avoirClient.setImprimer(true);
        AvoirClient merge = (AvoirClient) avoirClient.merge();
        uiModel.addAttribute("avoirClient", merge);
        return "avoirPdfView";
    }
	
	 @RequestMapping(method = RequestMethod.POST)
	    public String create(@Valid AvoirClient avoirClient, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
	        if (bindingResult.hasErrors()) {
	            uiModel.addAttribute("avoirClient", avoirClient);
	            addDateTimeFormatPatterns(uiModel);
	            return "avoirclients/create";
	        }
	        uiModel.asMap().clear();
	        avoirClient.setClientName(Client.findClientsByClientNumberEquals(avoirClient.getClientNumber()).getResultList().iterator().next().getNomComplet());
	       avoirClient.defineReste();
	        avoirClient.persist();
	        return "redirect:/avoirclients/" + encodeUrlPathSegment(avoirClient.getId().toString(), httpServletRequest);
	    }
	 
	 @RequestMapping(method = RequestMethod.PUT)
	    public String  update(@Valid AvoirClient avoirClient, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
	        if (bindingResult.hasErrors()) {
	            uiModel.addAttribute("avoirClient", avoirClient);
	            addDateTimeFormatPatterns(uiModel);
	            return "avoirclients/update";
	        }
	        uiModel.asMap().clear();
	        avoirClient.setClientName(Client.findClientsByClientNumberEquals(avoirClient.getClientNumber()).getResultList().iterator().next().getNomComplet());
	        avoirClient.merge();
	        return "redirect:/avoirclients/" + encodeUrlPathSegment(avoirClient.getId().toString(), httpServletRequest);
	    }
	 
	 @ModelAttribute("avoirclients")
	    public Collection<AvoirClient> populateAvoirClients() {
	       // return AvoirClient.findAllAvoirClients();
		 return new ArrayList<AvoirClient>();
	    }
	    
	    @ModelAttribute("typebons")
	    public Collection<TypeBon> populateTypeBons() {
	        return Arrays.asList(TypeBon.class.getEnumConstants());
	    }
	    
}
