package org.adorsys.adpharma.web;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.adorsys.adpharma.beans.DatabaseLoader;
import org.adorsys.adpharma.domain.Client;
import org.adorsys.adpharma.domain.virtualtable.ClientV;
import org.adorsys.adpharma.domain.virtualtable.ProduitV;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/dataloader")
@Controller
public class DataloaderController {

	@Transactional
	@RequestMapping(value = "/load",method = RequestMethod.GET)
	public String create( Model uiModel) {
	//	boolean loadprd=  ProduitV.countProduitVs()> 0  ;
	
		int loadRayons = 0;
		int loadProduct = 0;
		
		
	
		
		
	       // loadRayons = DatabaseLoader.loadRayons( );
			//DatabaseLoader.loadApprovisionnement();
           // DatabaseLoader.loadClients();
	       //  DatabaseLoader.findNullDesLine();
		uiModel.addAttribute("apMessage",loadRayons + " Rayons Charche   "+loadProduct+ " Produits charges ") ;

		return "caisses/infos";
	}

}
