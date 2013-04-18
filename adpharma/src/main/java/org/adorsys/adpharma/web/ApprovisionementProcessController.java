package org.adorsys.adpharma.web;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.adorsys.adpharma.beans.ApprovisonementProcess;
import org.adorsys.adpharma.beans.process.PrintBareCodeBean;
import org.adorsys.adpharma.domain.Approvisionement;
import org.adorsys.adpharma.domain.Client;
import org.adorsys.adpharma.domain.CommandeFournisseur;
import org.adorsys.adpharma.domain.Devise;
import org.adorsys.adpharma.domain.Etat;
import org.adorsys.adpharma.domain.Filiale;
import org.adorsys.adpharma.domain.Fournisseur;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.LigneCmdFournisseur;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.Rayon;
import org.adorsys.adpharma.domain.Site;
import org.adorsys.adpharma.domain.TVA;
import org.adorsys.adpharma.domain.TauxMarge;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.services.ClaimsService;
import org.adorsys.adpharma.services.JasperPrintService;
import org.adorsys.adpharma.utils.DocumentsPath;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.adorsys.adpharma.utils.ProcessHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/approvisionementprocess")
@Controller
public class ApprovisionementProcessController {
	
	@Autowired
	private JasperPrintService jasperPrintService ;
	
	@Autowired
	private ClaimsService reclamationsService;

	@RequestMapping(params = "form", method = RequestMethod.GET)
	public String createForm(Model uiModel) {
		uiModel.addAttribute("approvisionement", new Approvisionement());
		uiModel.addAttribute("filiales", ProcessHelper.populateAllFiliale());
		ProcessHelper.addDateTimeFormatPatterns(uiModel);
		return "approvisionementprocess/create";
	}

	@RequestMapping(value  = "/printBareCode", method = RequestMethod.GET)
	public String printBareCodeView(Model uiModel) {
		uiModel.addAttribute("printBareCodeBean", new PrintBareCodeBean());
		return "/approvisionementprocess/printBareCode";
	}

	@RequestMapping(value  = "/printBareCode.pdf", method = RequestMethod.GET)
	public String printBareCode(@Valid PrintBareCodeBean printBareCodeBean , BindingResult bindingResult,Model uiModel) {
		printBareCodeBean.validate(bindingResult);
		if (bindingResult.hasErrors()) {
			uiModel.addAttribute("printBareCodeBean", printBareCodeBean);
			return "/approvisionementprocess/printBareCode";
		}
		uiModel.addAttribute("printBareCodeBean", printBareCodeBean);
		return "printBarecodeDocView";
	}


	@RequestMapping(method = RequestMethod.POST)
	public String create(@Valid Approvisionement approvisionement, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		if (bindingResult.hasErrors()) {
			uiModel.addAttribute("approvisionement", approvisionement);
			ProcessHelper.addDateTimeFormatPatterns(uiModel);
			return "approvisionementprocess/create";
		}
		uiModel.asMap().clear();
		if (approvisionement.getFiliales()!=null)approvisionement.setFiliale(approvisionement.getFiliales().getFilialeNumber());
		approvisionement.persist();
		return "redirect:/approvisionementprocess/" + ProcessHelper.encodeUrlPathSegment(approvisionement.getId().toString(), httpServletRequest)+"/edit";
	}

	// redirect to edit approvisionement form
	@RequestMapping(value = "/{apId}/edit", method = RequestMethod.GET)
	public String editApprovisionnement(@PathVariable("apId") Long apId, Model uiModel,HttpSession session) {
		Approvisionement approvisionement = Approvisionement.findApprovisionement(apId);
		String fournisseur = approvisionement.getFounisseur().getFournisseurNumber();
		ApprovisonementProcess approvisonementProcess = new ApprovisonementProcess(apId);
		approvisonementProcess.setLigneApprovisionements(LigneApprovisionement.findLigneApprovisionementsByApprovisionement(approvisionement).getResultList());
		uiModel.addAttribute("approvisonementProcess",approvisonementProcess);
		uiModel.addAttribute("numero", fournisseur);
		initProcurementViewDependencies(uiModel);
		return "approvisionementprocess/edit";
	}

	@RequestMapping(value = "/{apId}/specialEdit", method = RequestMethod.GET)
	public String specialEdit(@PathVariable("apId") Long apId, Model uiModel,HttpSession session) {
		Approvisionement approvisionement = Approvisionement.findApprovisionement(apId);
		ApprovisonementProcess approvisonementProcess = new ApprovisonementProcess(apId);
		approvisonementProcess.setEtat(Etat.CLOS);
		approvisonementProcess.setLigneApprovisionements(LigneApprovisionement.findLigneApprovisionementsByApprovisionement(approvisionement).getResultList());
		uiModel.addAttribute("approvisonementProcess",approvisonementProcess);
		initProcurementViewDependencies(uiModel);
		return "approvisionementprocess/edit";
	}

	@RequestMapping(value = "/{apId}/addLine", method = RequestMethod.POST)
	public String addLine(@PathVariable("apId") Long apId,@RequestParam Long pId,@RequestParam String qte, @RequestParam(required=false) BigInteger qteReclam,@RequestParam String qteug,
			@RequestParam String pa,@RequestParam String pv,@RequestParam(required = false) String tvaj,@RequestParam String prm,Model uiModel,HttpSession session) {
		
		System.out.println("Qte reclammee: "+qteReclam);
		
		Approvisionement approvisionement = Approvisionement.findApprovisionement(apId);
		Produit produit = Produit.findProduit(pId);
		qteug = StringUtils.isBlank(qteug)?"0":qteug;
		
		if (approvisionement.contientProduit(produit)) {
			uiModel.addAttribute("apMessage", "Ce produit est deja dans la liste ");
			return initViewContent(approvisionement, tvaj, uiModel);
		    }
			if (approvisionement.CommandeContientProduit(produit)) {
				uiModel.addAttribute("apMessage", "Ce produit ne fait Pas partie de la commande recuperer !");
				return initViewContent(approvisionement, tvaj, uiModel);
			}
			if ((new BigInteger(qteug)).compareTo(new BigInteger(qte))==1) {
				uiModel.addAttribute("apMessage", "les unites gratuites sont superieur a la quantite approvisione !");
				return initViewContent(approvisionement, tvaj, uiModel);
			}
			
			LigneApprovisionement ligneApprovisionement = new LigneApprovisionement();
			if(prm==""){
			       if(!produit.isPerissable()){
			    	   ligneApprovisionement.setDatePeremtion(new DateUtils().addYears(new Date(), 5));
			       }else{
			    	   uiModel.addAttribute("apMessage", "Veuillez entrer la date de peremption du produit");
			    	   return initViewContent(approvisionement, tvaj, uiModel);
			       }
			}else{
				ligneApprovisionement.setDatePeremtion( PharmaDateUtil.parseToDate(prm, PharmaDateUtil.DATE_PATTERN_LONG2));
				System.out.println("Date de peremption: "+ligneApprovisionement.getDatePeremtion());
			}
			if(qteReclam!=null){
				ligneApprovisionement.setQuantiteReclame(qteReclam);
			}
			ligneApprovisionement.setAgentSaisie(SecurityUtil.getUserName());
			ligneApprovisionement.setApprovisionement(approvisionement);
			ligneApprovisionement.setQuantiteAprovisione(new BigInteger(qte.trim()));
			ligneApprovisionement.setQuantiteUniteGratuite(new BigInteger(qteug.trim()));
			ligneApprovisionement.setPrixAchatUnitaire(new BigDecimal(pa.trim()));
			if (StringUtils.isNotBlank(pv)) ligneApprovisionement.setPrixVenteUnitaire(new BigDecimal(pv.trim()));
			if(!new Date().before(ligneApprovisionement.getDatePeremtion())){
				uiModel.addAttribute("apMessage", "La date de Peremtion de ce produit Doit etre Superieure a la date du jour !");
				return initViewContent(approvisionement, tvaj, uiModel);
			}
			ligneApprovisionement.setProduit(produit);
			ligneApprovisionement.persist();
			produit.setPrixAchatU(ligneApprovisionement.getPrixVenteUnitaire());
			produit.setPrixVenteU(ligneApprovisionement.getPrixVenteUnitaire());
			produit.merge();
			approvisionement.increaseMontant(ligneApprovisionement.getPrixAchatTotal());
			approvisionement.merge();
			return initViewContent(approvisionement, tvaj, uiModel);
		}

	//usefful method for preparing approvisionnement content view
		public String initViewContent(Approvisionement approvisionement ,String tvaj,Model uiModel){
			ApprovisonementProcess approvisonementProcess = new ApprovisonementProcess(approvisionement.getId());
			if (StringUtils.isNotBlank(tvaj)) {
				approvisonementProcess.setTaux(new BigDecimal(tvaj.trim()));
			}
			approvisonementProcess.setLigneApprovisionements(LigneApprovisionement.findLigneApprovisionementsByApprovisionement(approvisionement).getResultList());
			uiModel.addAttribute("approvisonementProcess",approvisonementProcess);
			initProcurementViewDependencies(uiModel);
			return "approvisionementprocess/edit";

		}
		
		@Transactional
		@RequestMapping(value = "/{apId}/specialaddLine", method = RequestMethod.POST)
		public String specialaddLine(@PathVariable("apId") Long apId,@RequestParam Long pId,@RequestParam String qte, @RequestParam String qteug,
				@RequestParam String pa,@RequestParam String pv,@RequestParam(required = false) String tvaj,@RequestParam String prm,Model uiModel,HttpSession session) {
			Approvisionement approvisionement = Approvisionement.findApprovisionement(apId);
			Produit produit = Produit.findProduit(pId);
			if (approvisionement.contientProduit(produit)) {
				uiModel.addAttribute("apMessage", "Ce produit est deja dans la liste ");
			}else if (approvisionement.CommandeContientProduit(produit)) {
				uiModel.addAttribute("apMessage", "Ce produit ne fait Pas partie de la commande recuperer !");
			}else if((new BigInteger(qteug)).compareTo(new BigInteger(qte))==1) {
				uiModel.addAttribute("apMessage", "les unites gratuites sont superieur a la quantite approvisione !");		
			}else{
				LigneApprovisionement ligneApprovisionement = new LigneApprovisionement();
				ligneApprovisionement.setAgentSaisie(SecurityUtil.getUserName());
				ligneApprovisionement.setApprovisionement(approvisionement);
				if (StringUtils.isNotBlank(qte)) {
					ligneApprovisionement.setQuantiteAprovisione(new BigInteger(qte.trim()));
				}
				if (StringUtils.isNotBlank(qteug)) {
					ligneApprovisionement.setQuantiteUniteGratuite(new BigInteger(qteug.trim()));
				}
				if (StringUtils.isNotBlank(pa)) {
					ligneApprovisionement.setPrixAchatUnitaire(new BigDecimal(pa.trim()));
				}
				ligneApprovisionement.setDatePeremtion( PharmaDateUtil.parseToDate(prm, PharmaDateUtil.DATE_PATTERN_LONG2) );
				if (StringUtils.isNotBlank(pv)) {
					ligneApprovisionement.setPrixVenteUnitaire(new BigDecimal(pv.trim()));
				}
				produit.addproduct(ligneApprovisionement.getQuantiteAprovisione());
				ligneApprovisionement.setProduit(produit);
				ligneApprovisionement.persist();
				approvisionement.increaseMontant(ligneApprovisionement.getPrixAchatTotal());
				approvisionement.merge();
				produit.merge();
			}
			ApprovisonementProcess approvisonementProcess = new ApprovisonementProcess(apId);
			approvisonementProcess.setEtat(Etat.CLOS);
			if (StringUtils.isNotBlank(tvaj)) {
				approvisonementProcess.setTaux(new BigDecimal(tvaj.trim()));
			}
			approvisonementProcess.setLigneApprovisionements(LigneApprovisionement.findLigneApprovisionementsByApprovisionement(approvisionement).getResultList());
			uiModel.addAttribute("approvisonementProcess",approvisonementProcess);
			initProcurementViewDependencies(uiModel);
			return "approvisionementprocess/edit";

		}

		@RequestMapping(value = "/{apId}/updateLine", method = RequestMethod.POST)
		public String updatedLine(@PathVariable("apId") Long apId,@RequestParam Long lineId,@RequestParam BigInteger qte, @RequestParam BigInteger qteug,
				@RequestParam BigDecimal pa,@RequestParam String pv, @RequestParam(required = false) String tvaj,@RequestParam String prm, 
				@RequestParam BigInteger qterecl, Model uiModel,HttpSession session) {
			Approvisionement approvisionement = Approvisionement.findApprovisionement(apId);
			LigneApprovisionement line = LigneApprovisionement.findLigneApprovisionement(lineId);
			
			if (!approvisionement.getEtat().equals(Etat.CLOS)) {
				line.setQuantiteAprovisione(qte);
				line.setQuantiteUniteGratuite(qteug);
				line.setQuantiteReclame(qterecl);
				line.setPrixAchatUnitaire(pa);
				if (!"".equals(pv)) {
					line.setPrixVenteUnitaire(new BigDecimal(pv.trim()));
				}else {
					line.setPrixVenteUnitaire(BigDecimal.ZERO);
					line.CalculerPrixVente();
				}
				line.setDatePeremtion( PharmaDateUtil.parseToDate(prm, PharmaDateUtil.DATE_PATTERN_LONG2) );
				line.CalculePaTotal();
				line.merge();
			}else{
				uiModel.addAttribute("apMessage", "impposible d effectuer une mis a jour <b> Approvisionement CLOS </b>");
				return initViewContent(approvisionement, tvaj, uiModel);
			}
			approvisionement.calculateMontant();
			approvisionement.merge();
			return initViewContent(approvisionement, tvaj, uiModel);

		}




		@RequestMapping(value = "/{apId}/showProduct/{pId}", method = RequestMethod.GET)
		public String showProduct(@PathVariable("apId") Long apId,@PathVariable("pId") Long pId, Model uiModel,HttpSession session) {
			ProcessHelper.addDateTimeFormatPatterns(uiModel);
			uiModel.addAttribute("produit", Produit.findProduit(pId));
			uiModel.addAttribute("itemId", pId);
			uiModel.addAttribute("apId", apId);

			return "approvisionementprocess/showProduct";

		}
		@RequestMapping(value = "/{apId}/showCmd/{cmdId}", method = RequestMethod.GET)
		public String showCmd(@PathVariable("apId") Long apId,@PathVariable("cmdId") Long cmdId, Model uiModel,HttpSession session) {
			ProcessHelper.addDateTimeFormatPatterns(uiModel);
			uiModel.addAttribute("commandefournisseur", CommandeFournisseur.findCommandeFournisseur(cmdId));
			uiModel.addAttribute("itemId", cmdId);
			uiModel.addAttribute("apId", apId);
			return "commandprocesses/show";

		}

		@RequestMapping(value = "/{apId}/updateLine/{lnId}",  method = RequestMethod.GET)
		public String updateLineForm(@PathVariable("lnId") Long lnId,@PathVariable("apId") Long apId, Model uiModel) {
			Approvisionement approvisionement = Approvisionement.findApprovisionement(apId);
			ApprovisonementProcess approvisonementProcess = new ApprovisonementProcess(apId);
			approvisonementProcess.setLigneApprovisionements(LigneApprovisionement.findLigneApprovisionementsByApprovisionement(approvisionement).getResultList());
			approvisonementProcess.setLineToUpdate(LigneApprovisionement.findLigneApprovisionement(lnId));
			uiModel.addAttribute("approvisonementProcess",approvisonementProcess);
			initProcurementViewDependencies(uiModel);
			return "approvisionementprocess/edit";
		}
		//assure la convertion des lignes de commande en ligne d'approvisionement 

		@RequestMapping(value = "/{apId}/recupererCmd/{cmId}",  method = RequestMethod.GET)
		public String recupererCmd(@PathVariable("cmId") Long cmId,@PathVariable("apId") Long apId, Model uiModel) {
			
			Approvisionement approvisionement = Approvisionement.findApprovisionement(apId);
			if(approvisionement!=null){
				CommandeFournisseur.findCommandeFournisseur(cmId).convertToLineAprov(approvisionement);
				approvisionement.calculateMontant();
				approvisionement.merge();
			}else {
				uiModel.addAttribute("apMessage", "impposible d'effectuer la recuperation l'aprovisionnement est deja close !");
			}
			
			ApprovisonementProcess approvisonementProcess = new ApprovisonementProcess(apId);
			approvisonementProcess.setLigneApprovisionements(LigneApprovisionement.findLigneApprovisionementsByApprovisionement(approvisionement).getResultList());
			uiModel.addAttribute("approvisonementProcess",approvisonementProcess);
			initProcurementViewDependencies(uiModel);
			return "approvisionementprocess/edit";
		}
		
		@RequestMapping(value = "/convertOrderToAppro/{cmId}",  method = RequestMethod.GET)
		public String convertOrderToAppro(@PathVariable("cmId") Long cmId, Model uiModel) {
			CommandeFournisseur cmd = CommandeFournisseur.findCommandeFournisseur(cmId);
			List<Approvisionement> appro = Approvisionement.findApprovisionementByCommandeFournisseur(cmd);
			if(!appro.isEmpty()){
				Approvisionement next = appro.iterator().next();
				uiModel.addAttribute("apMessage", "Cette Commande a deja ete Convertie Voir L'approvisionnement No : "+next.getApprovisionementNumber());
				return new CommandProcessController().enregistrer(cmId, uiModel);
			}
			Approvisionement approvisionement = new Approvisionement(cmd);
			approvisionement.persist();
			CommandeFournisseur.findCommandeFournisseur(cmId).convertToLineAprov(approvisionement);
			approvisionement.calculateMontant();
			approvisionement.setMontantHt(approvisionement.getMontant());
			approvisionement.setMontantTtc(approvisionement.getMontant());
			approvisionement.merge();
			ApprovisonementProcess approvisonementProcess = new ApprovisonementProcess(approvisionement.getId());
			approvisonementProcess.setLigneApprovisionements(LigneApprovisionement.findLigneApprovisionementsByApprovisionement(approvisionement).getResultList());
			uiModel.addAttribute("approvisonementProcess",approvisonementProcess);
			initProcurementViewDependencies(uiModel);
			return "approvisionementprocess/edit";
		}

		@RequestMapping(value = "/{apId}/deleteLine/{lnId}", method = RequestMethod.GET)
		public String deleteLine(@PathVariable("apId") Long apId,@PathVariable("lnId") Long lnId, HttpServletRequest httpServletRequest) {
			Approvisionement approvisionement = Approvisionement.findApprovisionement(apId);
			LigneApprovisionement ligneApprovisionement = LigneApprovisionement.findLigneApprovisionement(lnId);
			approvisionement.decreaseMontant(ligneApprovisionement.getPrixAchatTotal());
			approvisionement.merge();
			ligneApprovisionement.remove();

			return "redirect:/approvisionementprocess/" + ProcessHelper.encodeUrlPathSegment(apId.toString(), httpServletRequest)+"/edit";


		}
		@RequestMapping(value = "/{apId}/specialdeleteLine/{lnId}", method = RequestMethod.GET)
		public String specialDeleteLine(@PathVariable("apId") Long apId,@PathVariable("lnId") Long lnId, HttpServletRequest httpServletRequest) {
			Approvisionement approvisionement = Approvisionement.findApprovisionement(apId);
			LigneApprovisionement ligneApprovisionement = LigneApprovisionement.findLigneApprovisionement(lnId);
			approvisionement.decreaseMontant(ligneApprovisionement.getPrixAchatTotal());
			approvisionement.merge();
			ApprovisonementProcess.specialDeleteLine(ligneApprovisionement);
			return "redirect:/approvisionementprocess/" + ProcessHelper.encodeUrlPathSegment(apId.toString(), httpServletRequest)+"/specialEdit";


		}
		@RequestMapping(value = "/{apId}/selectProduct/{pId}",  method = RequestMethod.GET)
		public String selectProduct(@PathVariable("pId") Long pId,@PathVariable("apId") Long apId, Model uiModel) {
			Approvisionement approvisionement = Approvisionement.findApprovisionement(apId);
			Produit produit = Produit.findProduit(pId);
			ApprovisonementProcess approvisonementProcess = new ApprovisonementProcess(apId);
			approvisonementProcess.setLigneApprovisionements(LigneApprovisionement.findLigneApprovisionementsByApprovisionement(approvisionement).getResultList());
			approvisonementProcess.setProduit(produit);
			uiModel.addAttribute("approvisonementProcess",approvisonementProcess);
			initProcurementViewDependencies(uiModel);
			return "approvisionementprocess/edit";
		}
		@RequestMapping(value = "/{apId}/enregistrer", method = RequestMethod.GET)
		public String enregistrer(@PathVariable("apId") Long apId, Model uiModel) {
			Approvisionement approvisionement = Approvisionement.findApprovisionement(apId);
			boolean hasReclamations = reclamationsService.hasReclamations(approvisionement);
			if (hasReclamations==true) {
				approvisionement.setReclamations(Boolean.TRUE);
				approvisionement.merge();
			}
			
			ProcessHelper.addDateTimeFormatPatterns(uiModel);
			uiModel.addAttribute("approvisionement", approvisionement);
			uiModel.addAttribute("itemId",apId);
			return "approvisionementprocess/show";
		}

		@RequestMapping(value = "/{apId}/annuler", method = RequestMethod.GET)
		public String annuler(@PathVariable("apId") Long apId, Model uiModel) {
			Approvisionement approvisionement = Approvisionement.findApprovisionement(apId);
			if (approvisionement.getEtat().equals(Etat.EN_COUR)) {
				CommandeFournisseur commande = approvisionement.getCommande();
				if(commande!=null){
					commande.setApprovisionnementId(null);
					commande.setLivre(false);
					commande.setValider(false);
					commande.setEtatCmd(Etat.EN_COUR);
					commande.merge();
				}
				approvisionement.remove();
			}
			uiModel.addAttribute("Approvisionnement suprimee avec sucess !");

			return "caisses/infos";
		}


		//@Transactional
		@RequestMapping(value = "/{apId}/cloturer", method = RequestMethod.GET)
		public String cloturer(@PathVariable("apId") Long apId, Model uiModel,HttpServletRequest httpServletRequest) {
			Approvisionement apr = Approvisionement.findApprovisionement(apId);
			if (!apr.isValide(uiModel)) {
				ProcessHelper.addDateTimeFormatPatterns(uiModel);
				uiModel.addAttribute("approvisionement", apr);
				uiModel.addAttribute("itemId",apId);
				return "approvisionementprocess/show";
			}
			new ApprovisonementProcess(apId).closeApprovisionement();
			Approvisionement approvisionement = Approvisionement.findApprovisionement(apId);
			ProcessHelper.addDateTimeFormatPatterns(uiModel);
			uiModel.addAttribute("approvisionement", approvisionement);
			uiModel.addAttribute("itemId",apId);
			uiModel.addAttribute("appMessages",Arrays.asList("Approvisionement cloturer avec succes ! "));
			return "approvisionementprocess/show";
		}

		// imprime la fiche d 'approvisionenment
		
		@Produces({"application/pdf"})
		@Consumes({""})
		@RequestMapping(value = "/{apId}/printFicheAp/{ficheApId}.pdf", method = RequestMethod.GET)
		public void printFicheApprov(@PathVariable("apId") Long apId  ,HttpServletRequest request,HttpServletResponse response) {
			Map parameters = new HashMap();
			parameters.put("approvisionementid",apId);
			try {
				jasperPrintService.printDocument(parameters, response, DocumentsPath.FICHE_APPROVISIONNEMENT_FILE_PATH);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ;
			}
		}
		
		@Produces({""})
		@Consumes({""})
		@RequestMapping(value = "/{apId}/exporto/{ficheApId}.xls", method = RequestMethod.GET)
		public void exportoxls(@PathVariable("apId") Long apId  ,HttpServletRequest request,HttpServletResponse response) {
			Approvisionement ap = Approvisionement.findApprovisionement(apId);
			String fournisseur = ap.getFounisseur().getShortname();
			String site =ap.getMagasin()==null? Site.findSite(new Long(1)).displayName() : ap.getMagasin().getDisplayName();
		    site =	StringUtils.removeStartIgnoreCase(site, "PHARMACIE");
			Set<LigneApprovisionement> ligneap = ap.getLigneApprivisionement();
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				WritableWorkbook workbook = Workbook.createWorkbook(baos);
				WritableSheet sheet = workbook.createSheet(ap.getApprovisionementNumber(),0);
				Label ciph= new Label(0, 0,"cipm" );
				sheet.addCell(ciph);
				Label desh = new Label(1, 0, "designation");
				sheet.addCell(desh);
				Label pvh = new Label(2, 0, "pv");
				sheet.addCell(pvh);
				Label fourh = new Label(3, 0, "fournisseur");
				sheet.addCell(fourh);
				Label magasinh = new Label(4, 0, "site");
				sheet.addCell(magasinh);
				int i = 1 ;
				for (LigneApprovisionement line : ligneap) {
					int stock = line.getQuantieEnStock().intValue();
					for (int j = i; j < i+stock; j++) {
							Label cip= new Label(0, j, line.getCipMaison());
							sheet.addCell(cip);
							Label des = new Label(1, j, line.getProduit().getDesignation());
							sheet.addCell(des);
							Label pv = new Label(2, j, line.getPrixVenteUnitaire().longValue()+"F CFA");
							sheet.addCell(pv);
							Label four = new Label(3, j, fournisseur);
							sheet.addCell(four);
							Label magasin = new Label(4, j, site);
							sheet.addCell(magasin);
						
					}
					i = i+stock ;
				}

				workbook.write();
				workbook.close();
				response.setContentLength(baos.size());
				ServletOutputStream out1 = response.getOutputStream();
				baos.writeTo(out1);
				out1.flush();
				return ;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ;
			}
		}


		@RequestMapping(value = "/searchAppro", method = RequestMethod.GET)
		public String searchAppro(@RequestParam("name") String  name,  Model uiModel) {
			
			if("".equals(name)){
				Integer page = 1;
				Integer size = 50;
				int sizeNo = size == null ? 10 : size.intValue();
	            uiModel.addAttribute("approvisionements", Approvisionement.findApprovisionementEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
	            float nrOfPages = (float) Approvisionement.countApprovisionements() / sizeNo;
	            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
			}else{
				List<Approvisionement> list = Approvisionement.findApproByFournisseurLike(name).setMaxResults(50).getResultList();
			    uiModel.addAttribute("approvisionements", list);
			}
			
			return "approvisionements/list";
		}
		
		// imprime la fiche de code bare
		@RequestMapping("/{apId}/printFicheCodeBare/{ficheCodebarId}.pdf")
		public String printFicheCodeBar( @PathVariable("apId")Long apId, @PathVariable("ficheCodebarId")String ficheCodebarId, Model uiModel){
			Approvisionement approvisionement = Approvisionement.findApprovisionement(apId);
			List<LigneApprovisionement> ligneApprivisionement = LigneApprovisionement.findLigneApprovisionementsByApprovisionement(approvisionement).getResultList();
			uiModel.addAttribute("ligneApprivisionement", ligneApprivisionement);
			uiModel.addAttribute("apNumber", approvisionement.getApprovisionementNumber());
			return "ficheCodeBarePdfDocView";

		}
		
		// Formulaire d'impression des listes de reclamations
		@RequestMapping(value="/reclamations", params="form", method=RequestMethod.GET)
		public String ReclamationsForm(Model uiModel, HttpServletRequest httpServletRequest){
			String fournisseurNumber = Fournisseur.findFournisseur(new Long(1)).getFournisseurNumber();
			List<Fournisseur> fournisseurs = Fournisseur.findAllFournisseursExceptFirst();
			String array = Fournisseur.toJsonArray(fournisseurs);
			uiModel.addAttribute("listefournisseurs", array);
			return "reclamations/create";
		}
		


		/*
	 @ModelAttribute("produits")
	    public Collection<Produit> populateProduits() {
	        return Produit.findAllProduits();
	    }*/

		@ModelAttribute("fournisseurs")
		public Collection<Fournisseur> populateFournisseurs() {
			return Fournisseur.findAllFournisseurs();
		}

		@ModelAttribute("sites")
		public Collection<Site> populateSites() {
			return Site.findAllSites();
		}

		@ModelAttribute("devises")
		public Collection<Devise> populateDevises() {
			return Devise.findAllDevises();
		}

		public void initProcurementViewDependencies(Model uiModel){
			uiModel.addAttribute("produit", new Produit());
			uiModel.addAttribute("rayons", Rayon.findAllRayons());
			uiModel.addAttribute("filiales", Filiale.findAllFiliales());
			uiModel.addAttribute("tauxmarges", TauxMarge.findAllTauxMarges());
			uiModel.addAttribute("tvas", TVA.findAllTVAS());


		}

	}
