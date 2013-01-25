package org.adorsys.adpharma.web;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import org.adorsys.adpharma.beans.EtatManagerBean;
import org.adorsys.adpharma.domain.Rayon;
import org.adorsys.adpharma.services.JasperPrintService;
import org.adorsys.adpharma.utils.DateConfig;
import org.adorsys.adpharma.utils.DateConfigPeriod;
import org.adorsys.adpharma.utils.DocumentsPath;
import org.adorsys.adpharma.utils.ProcessHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ibm.icu.math.BigDecimal;

@RequestMapping("/etats")
@Controller
public class DocumentsPrinterController {
	@Autowired
	private JasperPrintService jasperPrintService ;

	
	@RequestMapping(value = "/etatPeriodique",params="form", method = RequestMethod.GET)
	public String ventes(Model uiModel) {
		uiModel.addAttribute("etatManagerBean", new EtatManagerBean());
		return "etats/periodiqueVente";
	}
	
	@RequestMapping(value = "/docsmanagerview",params="form", method = RequestMethod.GET)
	public String goToDocsManagerView(Model uiModel) {
		uiModel.addAttribute("etatManagerBean", new EtatManagerBean());
		uiModel.addAttribute("rayons",ProcessHelper.populateRayon());
		uiModel.addAttribute("filiales", ProcessHelper.populateFiliale());
		uiModel.addAttribute("typeMouvements", ProcessHelper.populateTypeMouvements());
		uiModel.addAttribute("users", ProcessHelper.populateUsers());
		return "etats/docpages";
	}
	
	@Produces({"application/pdf"})
	@Consumes({""})
	@RequestMapping(value = "/print/etatPeriodiqueTransformation.pdf", method = RequestMethod.GET)
	public void etatPeriodiqueTransformation(EtatManagerBean etatBean  ,HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		parameters.put("DateD",etatBean.getDateDebut());
		parameters.put("DateF",etatBean.getDateFin());
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.ETAT_PERIODIQUE_DECOMPOSITION_FILE_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	
	@Produces({"application/pdf"})
	@Consumes({""})
	@RequestMapping(value = "/print/etatProduitPerisable.pdf", method = RequestMethod.GET)
	public void etatProduitPerisable(@RequestParam("value") BigInteger value ,HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		parameters.put("nombre_jour",value);
		parameters.put("datejour",new Date());
		
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.ETAT_PRODUIT_PERISABLE_FILE_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	
	@Produces({"application/pdf"})
	@Consumes({""})
	@RequestMapping(value = "/print/etatPeriodiqueVente.pdf", method = RequestMethod.GET)
	public void etatPeriodiqueVente(EtatManagerBean etatBean  ,HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		parameters.put("DateD",etatBean.getDateDebut());
		parameters.put("DateF",etatBean.getDateFin());
		
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.ETAT_PERIODIQUE_VENTE_FILE_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	

	@Produces({"application/pdf"})
	@Consumes({""})
	@RequestMapping(value = "/print/chiffeAffaireVendeur.pdf", method = RequestMethod.GET)
	public void chiffeAffaireVendeur(EtatManagerBean etatBean  ,HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		parameters.put("DateD",etatBean.getDateDebut());
		parameters.put("DateF",etatBean.getDateFin());
		
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.ETAT_PERIODIQUE_CHIFFRE_AFFAIRE_VENDEUR_FILE_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	
	@Produces({"application/pdf"})
	@Consumes({""})
	@RequestMapping(value = "/print/etatPeriodiqueMouvenentStock.pdf", method = RequestMethod.GET)
	public void etatPeriodiqueMouvenentStock(EtatManagerBean etatBean  ,HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		parameters.put("DateD",etatBean.getDateDebut());
		parameters.put("DateF",etatBean.getDateFin());
		
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.ETAT_PERIODIQUE_MVTS_FILE_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	
	@Produces({"application/pdf"})
	@Consumes({""})
	@RequestMapping(value = "/print/etatJournalierVente.pdf", method = RequestMethod.GET)
	public void etatPeriodiqueVente(HttpServletRequest request,HttpServletResponse response) {
		DateConfigPeriod begingEndOfDay = DateConfig.getBegingEndOfDay(new Date());
		Map parameters = new HashMap();
		parameters.put("DateD",begingEndOfDay.getBegin());
		parameters.put("DateF",begingEndOfDay.getEnd());
		
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.ETAT_PERIODIQUE_VENTE_FILE_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	
	@Produces({"application/pdf"})
	@Consumes({""})
	@RequestMapping(value = "/print/etatPeriodiqueDettes.pdf", method = RequestMethod.GET)
	public void etatPeriodiqueDette(EtatManagerBean etatBean  ,HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		parameters.put("DateD",etatBean.getDateDebut());
		parameters.put("DateF",etatBean.getDateFin());
		
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.ETAT_PERIODIQUE_DETTES);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	
	@Produces({"application/pdf"})
	@Consumes({""})
	@RequestMapping(value = "/print/etatJournalierDettes.pdf", method = RequestMethod.GET)
	public void etatJournalierDettes(HttpServletRequest request,HttpServletResponse response) {
		DateConfigPeriod begingEndOfDay = DateConfig.getBegingEndOfDay(new Date());
		Map parameters = new HashMap();
		parameters.put("DateD",begingEndOfDay.getBegin());
		parameters.put("DateF",begingEndOfDay.getEnd());
		
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.ETAT_PERIODIQUE_DETTES);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	
	@Produces({"application/pdf"})
	@Consumes({""})
	@RequestMapping(value = "/print/etatPeriodiqueCaisse.pdf", method = RequestMethod.GET)
	public void etatPeriodiqueCaisse(EtatManagerBean etatBean  ,HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		parameters.put("DateD",etatBean.getDateDebut());
		parameters.put("DateF",etatBean.getDateFin());
		
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.ETAT_PERIODIQUE_CAISSE_FILE_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	@Produces({"application/pdf"})
	@Consumes({""})
	@RequestMapping(value = "/print/etatFicheStockRayon.pdf", method = RequestMethod.GET)
	public void etatFicheStockRayon(EtatManagerBean etatBean  ,HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		Rayon rayon = etatBean.getRayon();
		parameters.put("rayonId",rayon.getId());
		
		try {
			if(rayon.getId().intValue()==0){
				jasperPrintService.printDocument(parameters, response, DocumentsPath.CATALOGUE_FILE_PATH);
			}else {
				jasperPrintService.printDocument(parameters, response, DocumentsPath.ETAT_CATALOGUE_RAYON_FILE_PATH);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	
	@Produces({"application/pdf"})
	@Consumes({""})
	@RequestMapping(value = "/print/etatVAlorisationRayon.pdf", method = RequestMethod.GET)
	public void etatVAlorisationRayon(EtatManagerBean etatBean  ,HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		Rayon rayon = etatBean.getRayon();
		parameters.put("rayonId",rayon.getId());
		
		try {
			if(rayon.getId().intValue()==0){
				jasperPrintService.printDocument(parameters, response, DocumentsPath.ETA_VALORISATION_STOCK_FILE_PATH);
			}else {
				jasperPrintService.printDocument(parameters, response, DocumentsPath.ETAT_VALORISATION_RAYON_FILE_PATH);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	
	@Produces({"application/pdf"})
	@Consumes({""})
	@RequestMapping(value = "/print/etatJournalierCaisse.pdf", method = RequestMethod.GET)
	public void etatJournalierCaisse(HttpServletRequest request,HttpServletResponse response) {
		DateConfigPeriod begingEndOfDay = DateConfig.getBegingEndOfDay(new Date());
		Map parameters = new HashMap();
		parameters.put("DateD",begingEndOfDay.getBegin());
		parameters.put("DateF",begingEndOfDay.getEnd());
		
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.ETAT_PERIODIQUE_CAISSE_FILE_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	
	@Produces({"application/pdf"})
	@Consumes({""})
	@RequestMapping(value = "/print/etatValorisationStock.pdf", method = RequestMethod.GET)
	public void etatValorisationStock(HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.ETAT_VALORISATION_STOCK);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	
	
	
	@Produces({"application/pdf"})
	@RequestMapping(value = "/print/listingCatalogue.pdf", method = RequestMethod.GET)
	public @ResponseBody  void printListingCatalogue(HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.CATALOGUE_FILE_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	@Produces({"application/pdf"})
	@RequestMapping(value = "/print/listingRayon.pdf", method = RequestMethod.GET)
	public @ResponseBody  void printListingRayon(HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.RAYON_FILE_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	@Produces({"application/pdf"})
	@RequestMapping(value = "/print/listingClient.pdf", method = RequestMethod.GET)
	public @ResponseBody  void printListingClient(HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.CLIENT_FILE_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	@Produces({"application/pdf"})
	@RequestMapping(value = "/print/listingRelationDecomposition.pdf", method = RequestMethod.GET)
	public @ResponseBody  void listingRelationDecomposition(HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.RELATION_FILE_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}
	
	
	@Produces({"application/pdf"})
	@RequestMapping(value = "/print/listingRupture.pdf", method = RequestMethod.GET)
	public @ResponseBody  void listingRupture(HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.PRODUIT_RUPTURE_FILE_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}



}
