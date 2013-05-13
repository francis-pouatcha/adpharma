package org.adorsys.adpharma.web;

import java.math.BigInteger;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.adorsys.adpharma.domain.Caisse;
import org.adorsys.adpharma.domain.CommandeClient;
import org.adorsys.adpharma.domain.Customers;
import org.adorsys.adpharma.domain.LigneCmdClient;
import org.adorsys.adpharma.domain.PharmaUser;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.RoleName;
import org.adorsys.adpharma.domain.Site;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.services.ProductsOut;
import org.adorsys.adpharma.services.StatisticsService;
import org.adorsys.adpharma.services.homestatisticsclasses.TopSelling;
import org.adorsys.adpharma.utils.DateConfig;
import org.adorsys.adpharma.utils.DateConfigPeriod;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping("/")
public class HomeController {
	
	@Resource(name="statisticsServiceImpl")
	private StatisticsService statistics;
	
	//Here we import all the statistics that will be visible by the manager.
	
	//@Secured({"ROLE_SITE_MANAGER"})
	@RequestMapping(method=RequestMethod.GET)
	public String getSite(HttpServletRequest httpServletRequest,  Model uiModel){
		  Site site = Site.findSite(new Long(1));
		  Principal principal = httpServletRequest.getUserPrincipal();
		  PharmaUser pharmaUser = SecurityUtil.getPharmaUser();
		  // Date configuration
		  DateConfigPeriod begingEndOfMonth= null;
		  DateConfigPeriod beginEndOfDay= null;
	      begingEndOfMonth = DateConfig.getBegingEndOfMonth(new Date());
	      beginEndOfDay = DateConfig.getBegingEndOfDay(new Date());
	       if(principal!=null && pharmaUser.hasAnyRole(RoleName.ROLE_SITE_MANAGER) ){
	    	  List<TopSelling> products = statistics.topSellingProducts(begingEndOfMonth.getBegin(), begingEndOfMonth.getEnd());
	 		  List<Caisse> openCashs = statistics.stateOfOpenCash(beginEndOfDay.getBegin(), beginEndOfDay.getEnd());
	 		  List<ProductsOut> outProducts = statistics.productOutOfStock(BigInteger.ZERO, begingEndOfMonth.getBegin(), begingEndOfMonth.getEnd());
	 		  List<ProductsOut> avaries= statistics.productsAvaries();
	 		  List<Customers> avoirs= statistics.avoirsClients(begingEndOfMonth.getBegin(), begingEndOfMonth.getEnd());
	 		  //List<Customers> dettesClients = statistics.dettesClients(begingEndOfMonth.getBegin(), begingEndOfMonth.getEnd());
	 		  uiModel.addAttribute("pharmacie", site);
			  uiModel.addAttribute("products", products);
			  uiModel.addAttribute("cashs", openCashs);
			  uiModel.addAttribute("ruptures", outProducts);
			  uiModel.addAttribute("avaries", avaries);
			  uiModel.addAttribute("avoirs", avoirs);
			//  uiModel.addAttribute("dettes", dettesClients);
			  
			  return "index";
	       }
	       ArrayList<RoleName> arrayList = new ArrayList<RoleName>();
	       arrayList.add(RoleName.ROLE_VENDEUR);
	       arrayList.add(RoleName.ROLE_OPEN_SALE_SESSION);
	       if(principal!=null && pharmaUser.hasAnyRole(arrayList)){
		    	
	    	  return  "redirect:/saleprocess/newPublicCmd";
		       }
	      
		 if(pharmaUser==null) {
		 
			 return "redirect:/login";
			 
		 }else{
			 return "index";
		 }
	}
	

}
