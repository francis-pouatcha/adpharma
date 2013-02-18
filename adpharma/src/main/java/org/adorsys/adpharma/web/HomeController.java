package org.adorsys.adpharma.web;

import javax.servlet.http.HttpServletRequest;

import org.adorsys.adpharma.domain.Site;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping("/")
public class HomeController {
	
	@RequestMapping(method=RequestMethod.GET)
	public String getSite(HttpServletRequest httpServletRequest,  Model uiModel){
		  Site site = Site.findSite(new Long(1));
		  uiModel.addAttribute("pharmacie", site);
		  return "index";
	}
	

}
