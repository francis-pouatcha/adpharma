package org.adorsys.adpharma.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.adorsys.adpharma.domain.CommandeFournisseur;
import org.adorsys.adpharma.domain.Etat;
import org.adorsys.adpharma.domain.Fournisseur;
import org.adorsys.adpharma.domain.LigneCmdFournisseur;
import org.adorsys.adpharma.platform.rest.SImplePlatformRestService;
import org.adorsys.adpharma.platform.rest.exchanges.AdpHarmaExchangeParser;
import org.adorsys.adpharma.platform.rest.exchanges.ExchangeData;
import org.adorsys.adpharma.utils.ProcessHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RooWebScaffold(path = "commandefournisseurs", formBackingObject = CommandeFournisseur.class)
@RequestMapping("/commandefournisseurs")
@Controller
public class CommandeFournisseurController {

	@Autowired
	AdpHarmaExchangeParser exchangeParser ;

	
	SImplePlatformRestService exchangeService = new SImplePlatformRestService();
	//redirige la requette vers un autre show
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String show(@PathVariable("id") String id, Model uiModel , HttpServletRequest httpServletRequest) {
		uiModel.asMap().clear();
		return "redirect:/commandprocesses/" + ProcessHelper.encodeUrlPathSegment(id, httpServletRequest)+"/enregistrerCmd";
	}
    
	@RequestMapping(value = "/sendToPlatform/{id}", method = RequestMethod.GET)
	public String sendToPlatform(@PathVariable("id") Long id, Model uiModel , HttpServletRequest httpServletRequest) {
		CommandeFournisseur order = CommandeFournisseur.findCommandeFournisseur(id);
		ExchangeData exchangeData = new ExchangeData();
		ExchangeData postData= null;
		if(order != null) {
			exchangeData = exchangeParser.parseToTransferableFormat(order);
		}
		postData = exchangeService.postData(exchangeData, exchangeService.POST_DATA_URI, MediaType.APPLICATION_JSON);
		System.out.println("REMOTE MESSAGE: "+postData.getRemoteMessage());
		uiModel.addAttribute("apMessage", postData.getRemoteMessage());
		return new CommandProcessController().enregistrer(id, uiModel);
	}


	@RequestMapping(method = RequestMethod.PUT)
	public String update(@Valid CommandeFournisseur commandeFournisseur, 
			BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		if (bindingResult.hasErrors()) {
			uiModel.addAttribute("commandeFournisseur", commandeFournisseur);
			ProcessHelper.addDateTimeFormatPatterns(uiModel);
			return "commandefournisseurs/update";
		}
		uiModel.asMap().clear();
		CommandeFournisseur merge = (CommandeFournisseur) commandeFournisseur.merge();
		System.out.println(merge.getEtatCmd());
		return "redirect:/commandprocesses/" + ProcessHelper.encodeUrlPathSegment(merge.getId().toString(), httpServletRequest)+"/enregistrerCmd";
	}


	@RequestMapping(params = { "find=BySearch", "form" }, method = RequestMethod.GET)
	public String Search(Model uiModel) {
		addDateTimeFormatPatterns(uiModel);
		return "commandefournisseurs/search";
	}

	@RequestMapping(params = "find=BySearch", method = RequestMethod.GET)
	public String Search( @RequestParam("cmdNumber") String cmdNumber,@RequestParam("etatCmd") Etat etatCmd ,@RequestParam("fournisseur") Fournisseur fournisseur ,
			@RequestParam("minDateCreation") @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") Date minDateCreation, @RequestParam("maxDateCreation") @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") Date maxDateCreation ,Model uiModel) {
		List<CommandeFournisseur> search = CommandeFournisseur.search(cmdNumber, etatCmd, minDateCreation, maxDateCreation, fournisseur);

		if (search.isEmpty()) {
			uiModel.addAttribute("apMessage", "Aucune commande Fournisseur trouvee" );
			addDateTimeFormatPatterns(uiModel);

		}else {
			uiModel.addAttribute("results",search );

		}

		return "commandefournisseurs/search";
	} 


	@ModelAttribute("commandefournisseurs")
	public Collection<CommandeFournisseur> populateCommandeFournisseurs() {
		return new ArrayList<CommandeFournisseur>();
	}
	@ModelAttribute("lignecmdfournisseurs")
	public Collection<LigneCmdFournisseur> populateLigneCmdFournisseurs() {
		return new ArrayList<LigneCmdFournisseur>();
	}

	@ModelAttribute("fournisseurs")
	public Collection<Fournisseur> populateFournisseurs() {
		return Fournisseur.findAllFournisseurs();
	} 
	@ModelAttribute("etats")
	public Collection<Etat> populateEtats() {
		return Arrays.asList(Etat.class.getEnumConstants());
	}

}

