package org.adorsys.adpharma.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.adorsys.adpharma.domain.Approvisionement;
import org.adorsys.adpharma.domain.CommandeFournisseur;
import org.adorsys.adpharma.domain.Devise;
import org.adorsys.adpharma.domain.Etat;
import org.adorsys.adpharma.domain.Filiale;
import org.adorsys.adpharma.domain.Fournisseur;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.PharmaUser;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.Site;
import org.adorsys.adpharma.utils.ProcessHelper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RooWebScaffold(path = "approvisionements", formBackingObject = Approvisionement.class)
@RequestMapping("/approvisionements")
@Controller
public class ApprovisionementController {

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String show(@PathVariable("id") Long id, Model uiModel) {
		addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("approvisionement", Approvisionement.findApprovisionement(id));
		uiModel.addAttribute("itemId", id);
		return "approvisionementprocess/show";
	}

	@RequestMapping(params = { "find=BySearch", "form" }, method = RequestMethod.GET)
	public String Search(Model uiModel) {
		uiModel.addAttribute("fournisseur", ProcessHelper.populateFournisseur());
		return "approvisionements/search";
	}

	@RequestMapping(params = "find=BySearch", method = RequestMethod.GET)
	public String Search(@RequestParam("fournisseur") Fournisseur fournisseur ,@RequestParam("agentCreateur") PharmaUser agentCreateur, @RequestParam("apNumber") String apNumber,@RequestParam("etat") Etat etat ,@RequestParam("minDateCreation") @DateTimeFormat(pattern = "dd-MM-yyyy") Date minDate, @RequestParam("maxDateCreation") @DateTimeFormat(pattern = "dd-MM-yyyy") Date maxDate, Model uiModel) {
		List<Approvisionement> search = Approvisionement.search(apNumber, etat, minDate, maxDate, fournisseur,agentCreateur);
		if (search.isEmpty()) {
			uiModel.addAttribute("apMessage", "Aucun Approvisionnement Trouve");

		}else {
			uiModel.addAttribute("results", search);

		}
		addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("fournisseur", ProcessHelper.populateFournisseur());

		return "approvisionements/search";
	}

	@RequestMapping(method = RequestMethod.PUT)
	public String update(@Valid Approvisionement approvisionement, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		if (bindingResult.hasErrors()) {
			uiModel.addAttribute("approvisionement", approvisionement);
			addDateTimeFormatPatterns(uiModel);
			return "approvisionements/update";
		}
		uiModel.asMap().clear();
		if (approvisionement.getFiliales()!=null)approvisionement.setFiliale(approvisionement.getFiliales().getFilialeNumber());
		approvisionement.merge();
		return "redirect:/approvisionements/" + encodeUrlPathSegment(approvisionement.getId().toString(), httpServletRequest);
	}

	@ModelAttribute("approvisionements")
	public Collection<Approvisionement> populateApprovisionements() {
		return new ArrayList<Approvisionement>();	    }

	@ModelAttribute("commandefournisseurs")
	public Collection<CommandeFournisseur> populateCommandeFournisseurs() {
		return new ArrayList<CommandeFournisseur>();	    }


	@ModelAttribute("devises")
	public Collection<Devise> populateDevises() {
		return Devise.findAllDevises();
	}

	@ModelAttribute("etats")
	public Collection<Etat> populateEtats() {
		return Arrays.asList(Etat.class.getEnumConstants());
	}

	@ModelAttribute("fournisseurs")
	public Collection<Fournisseur> populateFournisseurs() {
		return Fournisseur.findAllFournisseurs();
	}
	@ModelAttribute("ligneapprovisionements")
	public Collection<LigneApprovisionement> populateLigneApprovisionements() {
		return new ArrayList<LigneApprovisionement>();	    }

	@ModelAttribute("pharmausers")
	public Collection<PharmaUser> populatePharmaUsers() {
		return ProcessHelper.populateUsers();
	}

	@ModelAttribute("sites")
	public Collection<Site> populateSites() {
		return Site.findAllSites();
	}
	@ModelAttribute("filiales")
	public Collection<Filiale> populateFiliales() {
		return Filiale.findAllActifFiliales();
	}

}
