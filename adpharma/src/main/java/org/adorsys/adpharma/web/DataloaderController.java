package org.adorsys.adpharma.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.adorsys.adpharma.beans.ApprovisonementProcess;
import org.adorsys.adpharma.beans.importExport.LigneApproImportExportService;
import org.adorsys.adpharma.beans.importExport.ProductsImportExportService;
import org.adorsys.adpharma.beans.importExport.RayonImportExportService;
import org.adorsys.adpharma.domain.AdPharmaBaseEntity;
import org.adorsys.adpharma.domain.Approvisionement;
import org.adorsys.adpharma.domain.Devise;
import org.adorsys.adpharma.domain.Filiale;
import org.adorsys.adpharma.domain.Fournisseur;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.Rayon;
import org.adorsys.adpharma.domain.Site;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/dataloader")
@Controller
public class DataloaderController {
    @Autowired
	RayonImportExportService rayonImportExportService ;
    @Autowired
   	ProductsImportExportService productsImportExportService ;
    @Autowired
   	LigneApproImportExportService ligneApproImportExportService ;
	
   	@RequestMapping(value = "/loadrayon",params="form",method = RequestMethod.GET)
	public String loadrayonForm( Model uiModel) {
   		uiModel.addAttribute("rayons", new ArrayList<Rayon>());
		return "dataloader/loadrayon";
	}
    
	@RequestMapping(value = "/loadrayon",method = RequestMethod.POST)
   	public String loadrayon(@RequestParam("fichier")MultipartFile fichier, Model uiModel) {
		List<Rayon> listFromSheet = new ArrayList<Rayon>();
		 try
        {
            Workbook workbook = Workbook.getWorkbook(fichier.getInputStream());
            Sheet sheet = workbook.getSheet(0);
            listFromSheet = rayonImportExportService.importListFromSheet(sheet);
        }
        catch(BiffException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        uiModel.asMap().clear();
             uiModel.addAttribute("rayons", listFromSheet);
    	return "dataloader/loadrayon";
   	}
	

   	@RequestMapping(value = "/loadproduit",params="form",method = RequestMethod.GET)
	public String loadproduitForm( Model uiModel) {
   		uiModel.addAttribute("produits", new ArrayList<Produit>());
		return "dataloader/loadproduit";
	}
    
	@RequestMapping(value = "/loadproduit",method = RequestMethod.POST)
   	public String loadproduit(@RequestParam("fichier")MultipartFile fichier,@RequestParam("rayon")Long rayon, Model uiModel) {
		List<Produit> listFromSheet = new ArrayList<Produit>();
		Rayon findRayon = Rayon.findRayon(rayon);
		 try
        {
            Workbook workbook = Workbook.getWorkbook(fichier.getInputStream());
            Sheet sheet = workbook.getSheet(0);
            listFromSheet = productsImportExportService.importListFromSheet(sheet,findRayon);
        }
        catch(BiffException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        uiModel.asMap().clear();
             uiModel.addAttribute("produits", listFromSheet);
    	return "dataloader/loadproduit";
   	}
	
	@RequestMapping(value = "/loadentree",params="form",method = RequestMethod.GET)
	public String loadentreeForm( Model uiModel) {
   		uiModel.addAttribute("ligneapprovisionements", new ArrayList<LigneApprovisionement>());
		return "dataloader/loadentree";
	}
    
	@RequestMapping(value = "/loadentree",method = RequestMethod.POST)
   	public String loadentree(@RequestParam("fichier")MultipartFile fichier, Model uiModel) {
		List<LigneApprovisionement> listFromSheet = new ArrayList<LigneApprovisionement>();
		 try
        {
			 Approvisionement ap = new Approvisionement(Fournisseur.findFournisseur(new Long(1)),RandomStringUtils.randomAlphanumeric(6), Devise.findDevise(new Long(1)), Filiale.findFiliale(new Long(1)).getLibelle(), Site.findSite(new Long(1)));
             ap.persist();
			 Workbook workbook = Workbook.getWorkbook(fichier.getInputStream());
            Sheet sheet = workbook.getSheet(0);
            listFromSheet = ligneApproImportExportService.importListFromSheet(sheet,ap);
            Approvisionement  merge = (Approvisionement) ap.merge();
            new ApprovisonementProcess(merge.getId()).closeApprovisionement();
        }
        catch(BiffException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        uiModel.asMap().clear();
             uiModel.addAttribute("ligneapprovisionements", listFromSheet);
    	return "dataloader/loadentree";
   	}
	
	

}
