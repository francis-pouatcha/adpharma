package org.adorsys.adpharma.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.adorsys.adpharma.beans.InventoryUisearchModel;
import org.adorsys.adpharma.beans.LigneApprovisionementExcelRepresentation;
import org.adorsys.adpharma.beans.importExport.ExportLignesApprovisionnement;
import org.adorsys.adpharma.beans.importExport.LigneApproImportExportService;
import org.adorsys.adpharma.beans.process.InventaireProcess;
import org.adorsys.adpharma.domain.Etat;
import org.adorsys.adpharma.domain.Inventaire;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.LigneInventaire;
import org.adorsys.adpharma.domain.MouvementStock;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.Rayon;
import org.adorsys.adpharma.services.DefaultInventoryService;
import org.adorsys.adpharma.services.JasperPrintService;
import org.adorsys.adpharma.utils.Contract;
import org.adorsys.adpharma.utils.DocumentsPath;
import org.adorsys.adpharma.utils.LocaleUtil;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.adorsys.adpharma.utils.ProcessHelper;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/inventaireProcess")
@Controller
public class InventaireProcessController {
	
	@Autowired
	private JasperPrintService jasperPrintService ;
	
	@Autowired
	ExportLignesApprovisionnement exportLignesApprovisionnement;
	
	@Autowired
	LigneApproImportExportService  ligneApproImportExportService ;
	
	@Resource(name="messageSource")
	ReloadableResourceBundleMessageSource messageSource;

	@Produces({"application/pdf"})
	@Consumes({""})
	@RequestMapping(value = "/print/{invId}/ficheDeComptage.pdf", method = RequestMethod.GET)
	public void ficheDeComptage(@PathVariable("invId") Long invId  ,HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		parameters.put("inventaireid",invId);
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.FICHE_INVENTAIRE_COMPTAGE_FILE_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}


	@Produces({"application/pdf"})
	@Consumes({""})
	@RequestMapping(value = "/print/{invId}/ficheInventaire.pdf", method = RequestMethod.GET)
	public void ficheInventaire(@PathVariable("invId") Long invId  ,HttpServletRequest request,HttpServletResponse response) {
		Map parameters = new HashMap();
		parameters.put("inventaireid",invId);
		try {
			jasperPrintService.printDocument(parameters, response, DocumentsPath.FICHE_INVENTAIRE_FILE_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
	}

	@RequestMapping(value = "/{invId}/editInventaire", method = RequestMethod.GET)
	public String editCommand(@PathVariable("invId") Long invId, Model uiModel,HttpSession session) {
		Inventaire inventaire = Inventaire.findInventaire(invId);
		InventaireProcess inventaireProcess = new InventaireProcess(invId, LigneInventaire.findLigneInventairesByInventaire(inventaire).getResultList());
		uiModel.addAttribute("inventaireProcess",inventaireProcess);
		return "inventaireProcess/editInventaire";
	}

	@RequestMapping(value = "/{invId}/findProduct",params = "form", method = RequestMethod.GET)
	public String findProductForm(@PathVariable("invId") Long invId,Model uiModel,HttpSession session) {
		Inventaire inventaire = Inventaire.findInventaire(invId);
		InventaireProcess inventaireProcess = new InventaireProcess(invId);
		uiModel.addAttribute("inventaireProcess",inventaireProcess);
		return "inventaireProcess/findProduct";
	}
	
	
	// Export des lignes d'approvisionnement des produits du rayon choisi pour l'inventaire
	@RequestMapping(value = "/{invId}/exportLines", method = RequestMethod.GET)
	public void exportLines(@PathVariable("invId") Long invId,Model uiModel, HttpServletResponse response) throws IOException, ServletException {
		Inventaire inventaire = Inventaire.findInventaire(invId);
		List<LigneApprovisionementExcelRepresentation> data= new ArrayList<LigneApprovisionementExcelRepresentation>();
		Rayon rayon = Rayon.findRayon(inventaire.getRayonId());
		List<LigneApprovisionement> list = LigneApprovisionement.findLignesApprovisionementByRayon(rayon);
		for(LigneApprovisionement line: list){
			LigneApprovisionementExcelRepresentation representation = new LigneApprovisionementExcelRepresentation(line.getCip(), line.getCipMaison(), line.getDesignation(), line.getQuantieEnStock(), line.getProduit().getRayon().getEmplacement());
			data.add(representation);
		}
		try {
			exportLignesApprovisionnement.exportData("produits.xls", data, exportLignesApprovisionnement.getColumns());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Download the file to the Client
		File file = new File("/tools/produits.kk");
		String path = file.getAbsolutePath();
		String name = file.getName();
		String fileName=name;
		FileInputStream fileToDownload = new FileInputStream(path);
		ServletOutputStream outputStream = response.getOutputStream();
		response.reset();
		response.resetBuffer();
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-disposition", "attachment; filename="+fileName);
		ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
		int readBytes=0;
	    byte[] buffer= new byte[10000];
	    while ((readBytes=fileToDownload.read(buffer, 0, 10000))!=-1) {
			outputStream.write(buffer, 0, readBytes);
			outputStream1.write(buffer, 0, readBytes);
		}
	    outputStream.flush();
	    outputStream.close();
	    fileToDownload.close();
//		return "inventaireProcess/editInventaire";
	    return;
	}


	@RequestMapping(value = "/{invId}/findProduct",params = "find=ByDesignationLike", method = RequestMethod.GET)
	public String findProduitsByDesignationLike(@PathVariable("invId") Long invId,@RequestParam("designation") String designation, Model uiModel,
			HttpServletRequest httpServletRequest ,HttpSession session) {
		Contract.notBlank("designation", designation);
		List<Produit> produits = Produit.findProduitsByDesignationLike(designation).getResultList();
		return	showPoduct(produits, uiModel, invId);
	}

	@RequestMapping(value = "/{invId}/selectProduct/{pId}", method = RequestMethod.GET)
	public String selectProduct(@PathVariable("invId") Long invId, @PathVariable("pId") Long pId  ,Model uiModel, HttpServletRequest httpServletRequest) {
		Produit produit = Produit.findProduit(pId);
		InventaireProcess inventaireProcess = new InventaireProcess(invId, LigneInventaire.findLigneInventairesByInventaire(Inventaire.findInventaire(invId)).getResultList());
		inventaireProcess.setProduit(produit);
		uiModel.addAttribute("inventaireProcess",inventaireProcess);
		return "inventaireProcess/editInventaire";
	}

	@RequestMapping(value = "/{invId}/addLine", method = RequestMethod.POST)
	public String addLine(@PathVariable("invId") Long invId,@RequestParam Long pId,@RequestParam BigInteger qte,Model uiModel,HttpSession session) {
		Inventaire inventaire = Inventaire.findInventaire(invId);
		LigneApprovisionement ligne = LigneApprovisionement.findLigneApprovisionement(pId);
		Produit produit = Produit.findProduit(pId);
		if (inventaire.contientProductOrOpproItems(produit,ligne)) {
			uiModel.addAttribute("apMessage", messageSource.getMessage("appro_product_exist", null, LocaleUtil.getCurrentLocale())); 
		}else{
			LigneInventaire ligneInventaire = null ;
			if(inventaire.isInventoryBycipm()){
				 ligneInventaire = new LigneInventaire(ligne, inventaire, qte);
			}else {
				 ligneInventaire = new LigneInventaire(Produit.findProduit(pId), inventaire, qte);
			}
			
			ligneInventaire.persist();
		}
		InventaireProcess inventaireProcess = new InventaireProcess(invId, LigneInventaire.findLigneInventairesByInventaire(inventaire).getResultList());
		uiModel.addAttribute("inventaireProcess",inventaireProcess);
		return "inventaireProcess/editInventaire";

	}
	@ResponseBody
	@RequestMapping(value = "/{invId}/deleteLine/{lineId}",method = RequestMethod.GET)
	public String unselectLine(@PathVariable("invId") String invId, @PathVariable("lineId") Long lineId ,Model uiModel, HttpServletRequest httpServletRequest) {
		LigneInventaire ligneInventaire = LigneInventaire.findLigneInventaire(lineId);
		if(ligneInventaire!=null){
			ligneInventaire.remove();
			return "ok" ;
		}
		return "ko";
	}


	@ResponseBody
	@RequestMapping(value = "/{invId}/updateLine/{lineId}", method = RequestMethod.GET)
	public String updateLineForm(@PathVariable("invId") Long invId, @PathVariable("lineId") Long lineId  ,Model uiModel, HttpServletRequest httpServletRequest) {
		Inventaire inventaire = Inventaire.findInventaire(invId);
		LigneInventaire ligneInventaire = LigneInventaire.findLigneInventaire(lineId);
		//InventaireProcess inventaireProcess = new InventaireProcess(invId, LigneInventaire.findLigneInventairesByInventaire(inventaire).getResultList());
		//inventaireProcess.setLineToUpdate(ligneInventaire);
		//uiModel.addAttribute("inventaireProcess",inventaireProcess);
		if (ligneInventaire == null) return null;
		return ligneInventaire.toJson();
	}

	@ResponseBody
	@RequestMapping(value = "/{invId}/updateLineByAjax/{lineId}", method = RequestMethod.GET)
	public String updateLine(@PathVariable("invId") String invId, @PathVariable("lineId") Long lineId , @RequestParam BigInteger qte, Model uiModel, HttpServletRequest httpServletRequest) {
		LigneInventaire ligneInventaire = LigneInventaire.findLigneInventaire(lineId);
		ligneInventaire.setQteReel(qte);
		ligneInventaire.calculerEcart();
		ligneInventaire.caculMontantEcart();
	    LigneInventaire merge = (LigneInventaire)	ligneInventaire.merge();
		return merge.toJson();
	}
	
	@Transactional
	@RequestMapping(value = "/{invId}/enregistrerInv", method = RequestMethod.GET)
	public String enregistrer(@PathVariable("invId") Long invId, Model uiModel,HttpSession session) {
		Inventaire inventaire = Inventaire.findInventaire(invId);
		ProcessHelper.addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("inventaire", inventaire);
		uiModel.addAttribute("itemId",invId);
		inventaire.calculateMontantEcart();
		inventaire.merge();
		return "inventaireProcess/show";
	}
	
	@RequestMapping(value="/findApproLine", method = RequestMethod.GET)
	@ResponseBody
	public String findApproLine(Model uiModel,HttpServletRequest httpServletRequest) {
		String designation = httpServletRequest.getParameter("designation") ;
		List<LigneApprovisionement> search = LigneApprovisionement.findLigneApprovisionementsByDesignationLike(designation).getResultList();
		ArrayList<LigneApprovisionement> arrayList = new ArrayList<LigneApprovisionement>();
			for (LigneApprovisionement ligneApprovisionement : search) {
				arrayList.add(ligneApprovisionement.clone());
			}
		return LigneApprovisionement.toJsonArray(arrayList);
	}
	@RequestMapping(value = "/selectedAprroLine/{cipm}",  method = RequestMethod.GET)
	@ResponseBody
	public String selectProduct(@PathVariable("cipm") String cipm, Model uiModel) {
		LigneApprovisionement ligneApprovisionement = LigneApprovisionement.findLigneApprovisionementsByCipMaisonEquals(cipm).getResultList().iterator().next();
		/// return ligneApprovisionement.clone().toJson();
		return ligneApprovisionement.toJson();
	}
	
	@Transactional
	@RequestMapping(value = "/{invId}/annulerInv", method = RequestMethod.GET)
	public String annuler(@PathVariable("invId") Long invId, Model uiModel,HttpSession session) {
		Inventaire.findInventaire(invId).remove();
		return "redirect:/menu/inventaire";
	}

	//@Transactional
	@RequestMapping(value = "/{invId}/closeInventaire", method = RequestMethod.GET)
	public String closeInventaire(@PathVariable("invId") Long invId, Model uiModel,HttpSession session) {
		Inventaire inventaire = Inventaire.findInventaire(invId);
		List<LigneInventaire> resultList = LigneInventaire.findLigneInventairesByInventaire(inventaire).getResultList();
		if (resultList.isEmpty()) {
			uiModel.addAttribute("apMessage", messageSource.getMessage("inventory_process_close_warning", null, LocaleUtil.getCurrentLocale()));
			return "caisses/infos";
		}
		if (inventaire.getEtat().equals(Etat.CLOS)) {
			uiModel.addAttribute("apMessage", messageSource.getMessage("inventory_process_closed", null, LocaleUtil.getCurrentLocale()));
			return "caisses/infos";

		}

		inventaire.restoreStock() ;
		inventaire.setEtat(Etat.CLOS);
		Inventaire  merge = (Inventaire) inventaire.merge();
		uiModel.addAttribute("inventaire", merge);
		uiModel.addAttribute("itemId", merge.getId());
		ProcessHelper.addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("apMessage", messageSource.getMessage("inventory_process_close_success", null, LocaleUtil.getCurrentLocale()));
		return "inventaireProcess/show";
	}


	// display the list of product to the client 

	public String showPoduct(List<Produit>  produits ,Model uiModel,Long invId){
		Inventaire inventaire = Inventaire.findInventaire(invId);
		InventaireProcess inventaireProcess = new InventaireProcess(invId);
		inventaireProcess.setProductResult(produits);
		uiModel.addAttribute("inventaireProcess",inventaireProcess);
		return "inventaireProcess/findProduct";
	}


	@RequestMapping(value = "/ficheInventaireParOrdreAlpha",params = {  "form" }, method = RequestMethod.GET)
	public String ficheSuivieStockForm(Model uiModel) {
		uiModel.addAttribute("inventaire", new Inventaire());
		uiModel.addAttribute("rayons", ProcessHelper.populateRayon() );
		uiModel.addAttribute("filiales", ProcessHelper.populateFiliale());
		uiModel.addAttribute("familleProduits", ProcessHelper.populateFamilleProduit());
		uiModel.addAttribute("sousFamilleProduits", ProcessHelper.populateSousFamilleProduit());
		return "inventaires/inventaireParOrdreAlpha";
	}

	@RequestMapping(value = "/ficheSuivieStock", method = RequestMethod.GET)
	public String ficheSuivieStock(@Valid Inventaire inp, BindingResult bindingResult,HttpServletRequest request , Model uiModel) {
		if (inp.isInventoryBycipm()) {
			inp.setLigneApprovisionements(LigneApprovisionement.search(inp.getFamilleProduit(),inp.getSousFamilleProduit(),inp.getDesignation(), null, inp.getRayon(), inp.getBeginBy(), inp.getEndBy(), inp.getFiliale(), null, null,null,null).getResultList());

		}else {
			
			List<Produit> resultList = Produit.search(inp.getFamilleProduit(),inp.getSousFamilleProduit(),null, inp.getDesignation(), inp.getBeginBy(), inp.getEndBy(), inp.getRayon(), inp.getFiliale(),null,null).getResultList();
			if (!resultList.isEmpty()) {
				for (Produit produit : resultList) {
					produit.calculTransientPrice();
				}
				inp.setProduits(resultList);
			}
		}
		uiModel.asMap().clear();
		uiModel.addAttribute("inventaire", inp);
		uiModel.addAttribute("headTexte", "Fiche De Suivie de Stock Du "+PharmaDateUtil.format(inp.getDateDebut(), PharmaDateUtil.DATETIME_PATTERN_LONG)+" AU :" +
				"" +PharmaDateUtil.format(inp.getDateFin(), PharmaDateUtil.DATETIME_PATTERN_LONG));
		return "inventaires/ficheSuivieStock";
	}



	@RequestMapping(value = "/ficheSuivieQte",params = {"form" }, method = RequestMethod.GET)
	public String inventaireFicheQteForm(Model uiModel) {
		uiModel.addAttribute("inventaire", new Inventaire());
		uiModel.addAttribute("rayons", ProcessHelper.populateRayon() );
		uiModel.addAttribute("filiales", ProcessHelper.populateFiliale() );
		return "inventaires/inventaireFicheQte";
	}
	@RequestMapping(value = "/cataloqueCipm",params = {"form" }, method = RequestMethod.GET)
	public String catalogueCipmForm(Model uiModel) {
		uiModel.addAttribute("InventoryUisearchModel", new InventoryUisearchModel());
		uiModel.addAttribute("rayons", ProcessHelper.populateRayon() );
		uiModel.addAttribute("catalogue", new ArrayList<LigneApprovisionement>());
		return "inventaires/cataloqueCipm";
	}
	
	@RequestMapping(value = "/cataloqueCipm", method = RequestMethod.POST)
	public String cataloqueCipm(@Valid InventoryUisearchModel inventoryUisearchModel ,HttpServletRequest request , Model uiModel) throws IOException {
		List<LigneApprovisionement> seach = inventoryUisearchModel.seach();
		uiModel.addAttribute("catalogue", seach);
		uiModel.addAttribute("InventoryUisearchModel", inventoryUisearchModel);
		uiModel.addAttribute("rayons", ProcessHelper.populateRayon() );
		return "inventaires/cataloqueCipm";
	}
	@RequestMapping(value = "/cataloqueCipm.pdf", method = RequestMethod.GET)
	public String cataloqueCipmPdf(@Valid InventoryUisearchModel inventoryUisearchModel,HttpServletRequest request , Model uiModel) {
		List<LigneApprovisionement> seach = inventoryUisearchModel.seach();
		uiModel.addAttribute("catalogue", seach);
		return "catalogueCipmPdf";
	}
	@RequestMapping(value = "/cataloqueCipm.xls", method = RequestMethod.GET)
	public void cataloqueCipmXls(@Valid InventoryUisearchModel inventoryUisearchModel,HttpServletResponse response , Model uiModel) throws IOException {
		List<LigneApprovisionement> seach = inventoryUisearchModel.seach();
		HSSFWorkbook file = ligneApproImportExportService.exportToxlsFile(seach);
		uiModel.addAttribute("catalogue", seach);
		response.setContentType("application/vnd.ms-excel");
		String fileName = "cataloqueCipm"+new Date()+"_.xls";
		response.setHeader("Content-Disposition","attachement;filename=\"" + fileName + "\"");
		ServletOutputStream outputStream = response.getOutputStream();
		file.write(outputStream);
	}
	@ResponseBody
	@RequestMapping(value = "/cataloqueCipm.txt", method = RequestMethod.GET)
	public void cataloqueCipmTxt(@Valid InventoryUisearchModel inventoryUisearchModel,HttpServletResponse response , Model uiModel) throws IOException {
		List<LigneApprovisionement> seach = inventoryUisearchModel.seach();
		File file = ligneApproImportExportService.exportToTxtFile(seach);
		response.setContentType("text/plain");
		response.setHeader("Content-Disposition","attachement;filename=\"" + file.getName() + "\"");
	    FileInputStream istr = null;
        OutputStream ostr = null;
	    try {  
	          istr = new FileInputStream(file);
	          ostr = response.getOutputStream();
	          int curByte=-1;
	                while( (curByte=istr.read()) !=-1)
	                    ostr.write(curByte);

	          ostr.flush();
	        } catch(Exception ex){
	          ex.printStackTrace(System.out);
	        } finally{
	          try {
	            if(istr!=null)  istr.close();
	            if(ostr!=null)  ostr.close();
	          } catch(Exception ex){

	              ex.printStackTrace();
	            System.out.println(ex.getMessage());
	          }
	        }  
	        try {
	            response.flushBuffer();
	        } catch(Exception ex){

	            ex.printStackTrace();
	            System.out.println(ex.getMessage());
	        }

		return  ;
	}
	 // Impression de la fiche de code bar de l'inventaire
	@RequestMapping(value="/{invId}/printCodeBar/{invNumber}.pdf", method=RequestMethod.GET)
	public String printFicheCodeBare(@PathVariable("invId")Long invId, @PathVariable("invNumber")String invNumber,  Model uiModel, HttpServletRequest httpServletRequest){
		Inventaire inventaire = Inventaire.findInventaire(invId);
		List<LigneInventaire> lignesInventaire = LigneInventaire.findLigneInventairesByInventaire(inventaire).getResultList();
		// Map to store all the data
		Map<String, List<LigneApprovisionement>> mapping = new HashMap<String, List<LigneApprovisionement>>();
		for(LigneInventaire ligne: lignesInventaire){
			String cip = ligne.getProduit().getCip();
			List<LigneApprovisionement> lines = LigneApprovisionement.findLigneApprovisionementsByProduit(ligne.getProduit()).getResultList();
			mapping.put(cip, lines);
		}
		uiModel.addAttribute("produits", mapping);
		uiModel.addAttribute("invNumber", inventaire.getNumeroInventaire());
		return "inventaireCodebarPdfView";
	}

	@RequestMapping(value = "/ficheSuivieQte.pdf", method = RequestMethod.GET)
	public String inventaireFicheQte(@Valid Inventaire inp, BindingResult bindingResult,HttpServletRequest request , Model uiModel) {
		List<Produit> result = new ArrayList<Produit>();
		DefaultInventoryService inventoryService = new DefaultInventoryService();
		List<Object[]> etatVente = MouvementStock.findProduitAndQuantiteVendue(inp.getCipProduct(), inp.getDesignation(), inp.getBeginBy(), inp.getEndBy(),inp.getDateDebut(), inp.getDateFin(),inp.getRayon(),inp.getFiliale());
		if (!etatVente.isEmpty()) {
			for (Object[] obj : etatVente) {
				Produit produit =  (Produit)obj[0]; 
				produit.setQuantiteEnStock(inventoryService.getTrueStockQuantity(produit));
				produit.setQtevendu((BigInteger)obj[1]);
				result.add(produit);
			}

		}
		uiModel.addAttribute("listeProduit", result);
		uiModel.addAttribute("headTexte", "Fiche De Suivie de Quantitee Du "+PharmaDateUtil.format(inp.getDateDebut(), PharmaDateUtil.DATETIME_PATTERN_LONG)+" AU :" +
				"" +PharmaDateUtil.format(inp.getDateFin(), PharmaDateUtil.DATETIME_PATTERN_LONG));
		return "ficheSuivieQtePdf";
	}

	@RequestMapping(params = {"find=BySearch", "form"}, method = RequestMethod.GET)
	public String SearchForm(HttpServletRequest request, Model uiModel) {
		uiModel.addAttribute("rayon", ProcessHelper.populateRayon());
		uiModel.addAttribute("filiale", ProcessHelper.populateFiliale());
		uiModel.addAttribute("produit",new Produit());
		return "produits/search";
	}

	@RequestMapping(value="/search", method = RequestMethod.GET)
	public String Search(Produit prd , Model uiModel) {
		if (StringUtils.isBlank(prd.getDesignation())&& StringUtils.isBlank(prd.getCip()) && prd.getFiliale()==null && prd.getRayon() ==null ) {
			return "redirect:/produits?page=1" ;
		}
		uiModel.addAttribute("results", Produit.search(prd.getFamilleProduit(),prd.getSousfamilleProduit(),prd.getCip(), prd.getDesignation(),null,null, prd.getRayon(), prd.getFiliale() ,prd.getDateDerniereRupture(),null ).getResultList());
		uiModel.addAttribute("rayon", ProcessHelper.populateRayon());
		uiModel.addAttribute("filiale", ProcessHelper.populateFiliale());
		return "produits/search";
	}
	


}
