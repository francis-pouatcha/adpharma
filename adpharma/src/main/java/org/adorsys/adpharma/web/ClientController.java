package org.adorsys.adpharma.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.adorsys.adpharma.beans.CommandeCredit;
import org.adorsys.adpharma.domain.Approvisionement;
import org.adorsys.adpharma.domain.CategorieClient;
import org.adorsys.adpharma.domain.Client;
import org.adorsys.adpharma.domain.CommandeClient;
import org.adorsys.adpharma.domain.Filiale;
import org.adorsys.adpharma.domain.Genre;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.Rayon;
import org.adorsys.adpharma.domain.TypeClient;
import org.adorsys.adpharma.utils.ProcessHelper;
import org.apache.commons.beanutils.converters.LongArrayConverter;
import org.apache.commons.lang.StringUtils;
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
import org.springframework.web.bind.annotation.ResponseBody;

@RooWebScaffold(path = "clients", formBackingObject = Client.class)
@RequestMapping("/clients")
@Controller
public class ClientController {

	@RequestMapping(value = "/{id}/defineClientPayeur", params = "form", method = RequestMethod.GET)
	public String defineClientPayeur(@PathVariable("id") Long id, Model uiModel) {
		uiModel.addAttribute("client", Client.findClient(id));
		addDateTimeFormatPatterns(uiModel);
		return "clients/defineClientPayeur";
	}

	@RequestMapping(value = "/findClientByAjax", method = RequestMethod.GET)
	@ResponseBody
	public String findClientByAjax(@RequestParam("name") String  name,  Model uiModel) {
		List<Client> list = Client.findClientsByNomLike(name).setMaxResults(50).getResultList();
		ArrayList<Client> arrayList = new ArrayList<Client>();
		for (Client client : list) {
			client.calculeTotalDette();
			arrayList.add(client);
		}
		return  Client.toJsonArray(arrayList);
	}
	
	@RequestMapping(value = "/searchCustomer", method = RequestMethod.GET)
	public String searchCustomer(@RequestParam("name") String  name,  Model uiModel) {
		
		if("".equals(name)){
			Integer page = 1;
			Integer size = 50;
			int sizeNo = size == null ? 10 : size.intValue();
            uiModel.addAttribute("clients", Client.findClientEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) Client.countClients() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
		}else{
				List<Client> list = Client.findClientsByNomLike(name).setMaxResults(50).getResultList();
				uiModel.addAttribute("clients", list);
		}
		return "clients/list";
	}

	@RequestMapping(value = "/{cmdId}/addClientByAjax/{clientId}", method = RequestMethod.GET)
	@ResponseBody
	public String addClientFromCmdByAjax(@PathVariable("cmdId") Long  cmdId,@PathVariable("clientId") Long  clientId,  Model uiModel) {
		CommandeClient 	commandeClient = CommandeClient.findCommandeClient(cmdId);
		commandeClient.setClient(Client.findClient(clientId)) ;
		CommandeClient cmdClient =(CommandeClient)	commandeClient.merge();
		return  cmdClient.toString();
	}

	@RequestMapping(value = "/getClientByAjax/{clientId}", method = RequestMethod.GET)
	@ResponseBody
	public String getClientByAjax(@PathVariable("clientId") Long  clientId,  Model uiModel) {
		Client client = Client.findClient(clientId);
		return  client.toJson();
	}


	@RequestMapping(method = RequestMethod.POST)
	public String create(@Valid Client client, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		client.validate(bindingResult);
		if (bindingResult.hasErrors()) {
			uiModel.addAttribute("client", client);
			addDateTimeFormatPatterns(uiModel);
			return "clients/create";
		}
		uiModel.asMap().clear();
		client.persist();
		return "redirect:/clients/" + encodeUrlPathSegment(client.getId().toString(), httpServletRequest);
	}
	
	
	@RequestMapping(value = "/createForCreditSale",method = RequestMethod.POST)
	public String createByAjax(@Valid Client client, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		client.validate(bindingResult);
		if (bindingResult.hasErrors()) {
			uiModel.addAttribute("client", client);
			addDateTimeFormatPatterns(uiModel);
			return "clients/create";
		}
		uiModel.asMap().clear();
		client.persist();
		uiModel.addAttribute("commandeCredit",new CommandeCredit(client));
		uiModel.addAttribute("client",new Client());
		uiModel.addAttribute("typeclients",Arrays.asList(TypeClient.class.getEnumConstants()));
		uiModel.addAttribute("genres",Arrays.asList(Genre.class.getEnumConstants()));
		uiModel.addAttribute("categorieclients",CategorieClient.findAllCategorieClients());
		
		return "clients/cmdCredit";
	}

	@RequestMapping(params = { "find=BySearch", "form" }, method = RequestMethod.GET)
	public String Search(Model uiModel) {
		uiModel.addAttribute("client", new Client());
		return "clients/search";
	}

	@RequestMapping(value="/searchClient",params = "Bysearch", method = RequestMethod.POST)
	public String Search(Client client,Model uiModel) {
		List<Client> clients = Client.search(client.getClientNumber(), client.getNom(), client.getEmployeur(), client.getClientPayeur(),
				client.getCategorie(), client.getTypeClient(), client.getTotalDette()) ;
		uiModel.addAttribute("clients", clients);
		return "clients/search";
	}

	@RequestMapping(method = RequestMethod.PUT)
	public String update(@Valid Client client, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		client.validate(bindingResult);
		if (bindingResult.hasErrors()) {
			uiModel.addAttribute("client", client);
			addDateTimeFormatPatterns(uiModel);
			return "clients/update";
		}
		Client merge = (Client) client.merge();
        Long nextProductId = merge.getId() ;
        uiModel.addAttribute("apMessage","Le CLient No : "+ merge.getClientNumber() +" mis a jour avec succes ! ") ;
        List<Client> results = Client.findNextClients(merge.getId()).setMaxResults(5).getResultList();
        if(!results.isEmpty()) {
        	nextProductId = results.iterator().next().getId();
        }
        return  updateForm(nextProductId, uiModel) ;
	}
	
	  @RequestMapping(value = "selectClient/{id}", params = "action", method = RequestMethod.GET)
	    public String selectClientToUpdate(@RequestParam("action") String action, @PathVariable("id") Long id,Model uiModel) {
	    	 Long nextProductId = id ;
	    	 List<Client> resultList = new ArrayList<Client>() ;
	    	if(StringUtils.equalsIgnoreCase(action, "next")){
	        	 resultList = Client.findNextClients(id).setMaxResults(5).getResultList();
	        }else {
	        	resultList = Client.findPreviousClients(id).setMaxResults(5).getResultList();
			}
	    	if(!resultList.isEmpty()){
	    		nextProductId = resultList.iterator().next().getId() ;
	    	}
	        return updateForm(nextProductId, uiModel);
	    }
	
	@RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("client", Client.findClient(id));
        addDateTimeFormatPatterns(uiModel);
        return "clients/update";
    }

	
	
	
	@ModelAttribute("clients")
	public Collection<Client> populateClients() {
		return new ArrayList<Client>();
		
	}
    @ModelAttribute("categorieclients")
    public Collection<CategorieClient> populateCategorieClients() {
        return ProcessHelper.populateCategorieCLients();
    }
    
   
}
