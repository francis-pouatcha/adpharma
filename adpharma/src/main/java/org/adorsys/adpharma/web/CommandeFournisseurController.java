package org.adorsys.adpharma.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.adorsys.adpharma.domain.CommandeFournisseur;
import org.adorsys.adpharma.domain.Etat;
import org.adorsys.adpharma.domain.Fournisseur;
import org.adorsys.adpharma.domain.LigneCmdFournisseur;
import org.adorsys.adpharma.platform.rest.SImplePlatformRestService;
import org.adorsys.adpharma.platform.rest.exchanges.AdpHarmaExchangeParser;
import org.adorsys.adpharma.platform.rest.exchanges.ExchangeData;
import org.adorsys.adpharma.utils.LocaleUtil;
import org.adorsys.adpharma.utils.ProcessHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RooWebScaffold(path = "commandefournisseurs", formBackingObject = CommandeFournisseur.class)
@RequestMapping("/commandefournisseurs")
@Controller
public class CommandeFournisseurController {

	@Autowired
	AdpHarmaExchangeParser exchangeParser ;
	
	@Resource(name="messageSource")
	ReloadableResourceBundleMessageSource messageSource;

	
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
	public String Search( @RequestParam(value="cmdNumber", required=false)String cmdNumber,@RequestParam(value="etatCmd", required=false) Etat etatCmd ,@RequestParam(value="fournisseur", required=false) Fournisseur fournisseur ,
			@RequestParam(value="minDateCreation", required=false) @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") Date minDateCreation, @RequestParam(value="maxDateCreation", required=false) @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") Date maxDateCreation ,Model uiModel) {
		List<CommandeFournisseur> search = CommandeFournisseur.search(cmdNumber, etatCmd, minDateCreation, maxDateCreation, fournisseur);

		if (search.isEmpty()) {
			uiModel.addAttribute("apMessage", messageSource.getMessage("command_found_error", null, LocaleUtil.getCurrentLocale()));
			addDateTimeFormatPatterns(uiModel);

		}else {
			uiModel.addAttribute("results",search );

		}

		return "commandefournisseurs/search";
	} 

	
	@RequestMapping(value="/searchCmd")
	public String searchLigneApp( @RequestParam("name") String name, Model uiModel) {
		
		if("".equals(name)){
			Integer page = 1;
			Integer size = 50;
			int sizeNo = size == null ? 10 : size.intValue();
            uiModel.addAttribute("commandefournisseurs", CommandeFournisseur.findCommandeFournisseurEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) CommandeFournisseur.countCommandeFournisseurs() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
		}else{
				
				uiModel.addAttribute("commandefournisseurs", CommandeFournisseur.findCmdByFournisseurLike(name).
						setMaxResults(50).getResultList());
		}
		return "commandefournisseurs/list";
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

