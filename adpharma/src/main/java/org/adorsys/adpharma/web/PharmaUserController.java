package org.adorsys.adpharma.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.adorsys.adpharma.domain.AdPharmaBaseEntity;
import org.adorsys.adpharma.domain.PharmaUser;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.RoleName;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.utils.CipMgenerator;
import org.adorsys.adpharma.utils.ProcessHelper;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RooWebScaffold(path = "pharmausers", formBackingObject = PharmaUser.class)
@RequestMapping("/pharmausers")
@Controller
public class PharmaUserController {
	

    @RequestMapping(value = "/suspenduser/{id}", method = RequestMethod.GET)
	public String suspendUser(@PathVariable("id") Long id, Model uiModel){
        addDateTimeFormatPatterns(uiModel);
        PharmaUser pharmauser = PharmaUser.findPharmaUser(id);
        if(pharmauser.isAccountLocked()){
            uiModel.addAttribute("pharmauser",pharmauser);
            uiModel.addAttribute("itemId", id);
            uiModel.addAttribute("appMessages",Arrays.asList("Compte Utilisateur deja Suspendu."));
        } else {
        	pharmauser.setAccountLocked(true);
        	 PharmaUser merge = (PharmaUser) pharmauser.merge();
            uiModel.addAttribute("pharmauser", merge);
            uiModel.addAttribute("itemId", id);
            uiModel.addAttribute("appMessages",Arrays.asList("Compte Utilisateur Suspendu"));
        }
        return "pharmausers/show";
	}
    
    @RequestMapping(value = "/userInfos", method = RequestMethod.GET)
    @ResponseBody
   	public String userInfos( Model uiModel){
          PharmaUser pharmaUser = SecurityUtil.getPharmaUser();
          
           return pharmaUser.toJson();
   	}
    
    @RequestMapping(value = "/resetSaleKey", method = RequestMethod.GET)
    @ResponseBody
   	public String resetSaleKey( Model uiModel){
          PharmaUser pharmaUser = SecurityUtil.getPharmaUser();
          pharmaUser.setSaleKey(CipMgenerator.generateSaleKey());
          PharmaUser merge = (PharmaUser) pharmaUser.merge();
          
           return merge.toJson();
   	}


    @RequestMapping(value = "/resumeuser/{id}", method = RequestMethod.GET)
	public String resumeUser(@PathVariable("id") Long id, Model uiModel){
        addDateTimeFormatPatterns(uiModel);
        PharmaUser pharmauser = PharmaUser.findPharmaUser(id);
        if(!pharmauser.isAccountLocked()){
        	 uiModel.addAttribute("pharmauser",pharmauser);
             uiModel.addAttribute("itemId", id);
            uiModel.addAttribute("appMessages",Arrays.asList("Compte Utilisateur Non Suspendu."));
        } else {
        	pharmauser.setAccountLocked(false);
       	 PharmaUser merge = (PharmaUser) pharmauser.merge();
           uiModel.addAttribute("pharmauser", merge);
           uiModel.addAttribute("itemId", id);
            uiModel.addAttribute("appMessages",Arrays.asList("Compte Utilisateur Retabli."));
        }
        return "pharmausers/show";
	}

    @RequestMapping(value = "/resetpassword/{id}", method = RequestMethod.GET)
	public String resetPassword(@PathVariable("id") Long id, Model uiModel){
        addDateTimeFormatPatterns(uiModel);
        PharmaUser pharmauser = PharmaUser.findPharmaUser(id);
        String newPassword = RandomStringUtils.randomAlphanumeric(5).toLowerCase();
        pharmauser.changePassword(newPassword);
        PharmaUser merge = (PharmaUser) pharmauser.merge();
        uiModel.addAttribute("pharmauser", merge);
        uiModel.addAttribute("itemId", id);
        uiModel.addAttribute("appMessages",Arrays.asList(
        		"Password mis a jour aves succes. Le Nouveau Password est : <b>"+newPassword+"</b>",
        		"Appeller l'utilisateur et donner lui ce nouveau Password .",
        		"Demander a l'utilisateur de changer ce PassWord "));
        return "pharmausers/show";
	}
    
    @RequestMapping(method = RequestMethod.POST)
    public String create(@Valid PharmaUser pharmaUser, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
       pharmaUser.validate(bindingResult);
    	if (bindingResult.hasErrors()) {
            uiModel.addAttribute("pharmaUser", pharmaUser);
            addDateTimeFormatPatterns(uiModel);
            return "pharmausers/create";
        }
        uiModel.asMap().clear();
        pharmaUser.persist();
        return "redirect:/pharmausers/" + encodeUrlPathSegment(pharmaUser.getId().toString(), httpServletRequest);
    }
    @RequestMapping(method = RequestMethod.PUT)
    public String update(@Valid PharmaUser pharmaUser, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
    	if (bindingResult.hasErrors()) {
            uiModel.addAttribute("pharmaUser", pharmaUser);
            addDateTimeFormatPatterns(uiModel);
            return "pharmausers/update";
        }
        uiModel.asMap().clear();
        pharmaUser.merge();
        return "redirect:/pharmausers/" + encodeUrlPathSegment(pharmaUser.getId().toString(), httpServletRequest);
    }
    
    @ModelAttribute("rolenames")
    public Collection<RoleName> populateRoleNames() {
    	return ProcessHelper.populateRoleNames();
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
	public String searchUser(@RequestParam("name") String  name,  Model uiModel) {
		
		if("".equals(name)){
			Integer page = 1;
			Integer size = 50;
			int sizeNo = size == null ? 10 : size.intValue();
            uiModel.addAttribute("pharmausers", PharmaUser.findPharmaUserEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) PharmaUser.countPharmaUsers() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
		}else{
				List<PharmaUser> list = PharmaUser.findPharmaUsersByUserNameLike(name).setMaxResults(50).getResultList();
				uiModel.addAttribute("pharmausers", list);
		}
		return "pharmausers/list";
	}	
}
