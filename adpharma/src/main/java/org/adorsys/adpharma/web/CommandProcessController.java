package org.adorsys.adpharma.web;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
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

import org.adorsys.adpharma.beans.importExport.ubipharm.CsvImportExportUtil;
import org.adorsys.adpharma.beans.importExport.ubipharm.FileSystemScanner;
import org.adorsys.adpharma.beans.importExport.ubipharm.wrapper.AbstractUbipharmLigneWrapper;
import org.adorsys.adpharma.beans.process.CommandeProcess;
import org.adorsys.adpharma.beans.process.OrderPreParationBean;
import org.adorsys.adpharma.domain.CommandType;
import org.adorsys.adpharma.domain.CommandeFournisseur;
import org.adorsys.adpharma.domain.Etat;
import org.adorsys.adpharma.domain.Filiale;
import org.adorsys.adpharma.domain.Fournisseur;
import org.adorsys.adpharma.domain.LigneCmdFournisseur;
import org.adorsys.adpharma.domain.ModeSelection;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.Rayon;
import org.adorsys.adpharma.domain.Site;
import org.adorsys.adpharma.domain.TVA;
import org.adorsys.adpharma.services.JasperPrintService;
import org.adorsys.adpharma.utils.BundleMessages;
import org.adorsys.adpharma.utils.DocumentsPath;
import org.adorsys.adpharma.utils.LocaleUtil;
import org.adorsys.adpharma.utils.ProcessHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContextUtils;

@RequestMapping("/commandprocesses")
@Controller 
public class CommandProcessController {
	@Autowired
	private JasperPrintService jasperPrintService;
	
	@Resource(name="messageSource")
	ReloadableResourceBundleMessageSource messageSource;


	private static Logger LOG = LoggerFactory.getLogger(CommandProcessController.class);

	private boolean sendedToUbipharm ;
	private boolean sendToUbipharmFailed;
	private String  ubipharmCommandSendingErrorMessage;
	@Transactional
	@RequestMapping(method = RequestMethod.POST)
	public String createCommande(
			@Valid CommandeFournisseur commandeFournisseur,
			BindingResult bindingResult, Model uiModel,
			HttpServletRequest httpServletRequest, HttpSession session) {
		if (bindingResult.hasErrors()) {
			uiModel.addAttribute("commandeFournisseur", commandeFournisseur);
			ProcessHelper.addDateTimeFormatPatterns(uiModel);
			return "commandprocesses/create";
		}
		uiModel.asMap().clear();
		session.setAttribute("productResult", Produit.findAllProduits());
		commandeFournisseur.persist();
		return "redirect:/commandprocesses/"
		+ ProcessHelper.encodeUrlPathSegment(commandeFournisseur
				.getId().toString(), httpServletRequest)
				+ "/editCommand";
	}

	@Produces({ "application/pdf" })
	@Consumes({ "" })
	@RequestMapping(value = "/{cmdId}/printBonCommande.pdf", method = RequestMethod.GET)
	public void printFicheApprov(@PathVariable("cmdId") Long cmdId,
			HttpServletRequest request, HttpServletResponse response) {
		Map parameters = new HashMap();
		parameters.put("commandeid", cmdId);
		try {
			jasperPrintService.printDocument(parameters, response,
					DocumentsPath.BON_COMMANDE_FILE_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}
	
	@Produces({ "application/pdf" })
	@Consumes({ "" })
	@RequestMapping(value = "/{cmdId}/printBonLivraison.pdf", method = RequestMethod.GET)
	public void printBonLivraison(@PathVariable("cmdId") Long cmdId,
			HttpServletRequest request, HttpServletResponse response) {
		Map parameters = new HashMap();
		parameters.put("commandeid", cmdId);
		try {
			jasperPrintService.printDocument(parameters, response,
					DocumentsPath.BON_LIVRAISON_FILE_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}
	
	
	@Produces({ "application/pdf" })
	@Consumes({ "" })
	@RequestMapping(value = "/{cmdId}/printBonRupture.pdf", method = RequestMethod.GET)
	public void printBonRupture(@PathVariable("cmdId") Long cmdId,
			HttpServletRequest request, HttpServletResponse response) {
		Map parameters = new HashMap();
		parameters.put("commandeid", cmdId);
		try {
			jasperPrintService.printDocument(parameters, response,
					DocumentsPath.BON_RUPTURE_OU_INCONNU_FILE_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}

	@Produces({ "" })
	@Consumes({ "" })
	@RequestMapping(value = "/{cmdId}/exporto.xls", method = RequestMethod.GET)
	public void printFicheApprovXls(@PathVariable("cmdId") Long cmdId,
			HttpServletRequest request, HttpServletResponse response) {
		CommandeFournisseur commande = CommandeFournisseur
				.findCommandeFournisseur(cmdId);
		Set<LigneCmdFournisseur> ligneCommande = commande.getLigneCommande();
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			WritableWorkbook workbook = Workbook.createWorkbook(baos);
			WritableSheet sheet = workbook.createSheet(commande.getCmdNumber(),
					0);
			int i = 0;
			for (LigneCmdFournisseur ligneCmdFournisseur : ligneCommande) {
				Label cip = new Label(0, i, ligneCmdFournisseur.getCip());
				sheet.addCell(cip);
				Label qtec = new Label(1, i,
						ligneCmdFournisseur.getQuantiteCommande() + "");
				sheet.addCell(qtec);
				i++;
			}

			workbook.write();
			workbook.close();
			response.setContentLength(baos.size());
			ServletOutputStream out1 = response.getOutputStream();
			baos.writeTo(out1);
			out1.flush();
			return;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}

	@RequestMapping(value = "/{cmdId}/addLine", method = {RequestMethod.POST, RequestMethod.GET})
	public String addLine(@PathVariable("cmdId") Long cmdId,
			@RequestParam Long pId, @RequestParam BigInteger qte,
			@RequestParam BigDecimal pa, @RequestParam BigDecimal pv,
			Model uiModel, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		
		CommandeFournisseur commandeFournisseur = CommandeFournisseur.findCommandeFournisseur(cmdId);
		Produit produit = Produit.findProduit(pId);
		pa = pa == null ? BigDecimal.ZERO : pa;
		pv = pv == null ? BigDecimal.ZERO : pv;
		qte = qte == null ? BigInteger.ONE : qte;
		BigDecimal pt = pa.multiply(new BigDecimal(qte));
		if (commandeFournisseur.contientProduit(produit)) {
			uiModel.addAttribute("apMessage", messageSource.getMessage("command_product_exist", null, LocaleUtil.getCurrentLocale()));
		} else {
			LigneCmdFournisseur line = new LigneCmdFournisseur();
			line.setCommande(commandeFournisseur);
			line.setPrixAchatMin(pa);
			line.setPrixAVenteMin(pv);
			line.setProduit(produit);
			line.setQuantiteCommande(qte);
			line.setPrixAchatTotal(pt);
			line.persist();
			commandeFournisseur.increaseMontant(pt);
			commandeFournisseur.merge();
		}
		CommandeProcess commandeProcess = new CommandeProcess(cmdId,
				LigneCmdFournisseur.findLigneCmdFournisseursByCommande(commandeFournisseur).getResultList(),
				CommandeFournisseur.findCommandeFournisseur(cmdId).getFournisseur().getName());
		uiModel.addAttribute("commandeProcess", commandeProcess);
		uiModel.addAttribute("ligneCmdFournisseur", new LigneCmdFournisseur());
		return "commandprocesses/editCommand";

	}

	@RequestMapping(value = "/preparedOrder", method = RequestMethod.POST)
	public String preparedOrder(@Valid OrderPreParationBean preparedBean,
			Model uiModel, HttpSession session,
			HttpServletRequest httpServletRequest) {
		CommandeFournisseur order = null;
		order = preparedBean.generateOrder();
		order.persist();
		List<Object[]> listItems = new ArrayList<Object[]>();
		if (order.getModeDeSelection().equals(ModeSelection.PLUS_VENDU)) {
			listItems = CommandeProcess.findProduitAndQuantiteVendue(
					preparedBean.getBeginBy(), preparedBean.getEndBy(),
					preparedBean.getBeginDate(), preparedBean.getEndDate(),
					preparedBean.getRayon(), preparedBean.getFiliale(),
					preparedBean.getMinStock());
		} else {
			listItems = CommandeProcess.findProduitAndRuptureOrAlert(
					preparedBean.getBeginBy(), preparedBean.getBeginBy(),
					preparedBean.getRayon(), preparedBean.getFiliale(),
					preparedBean.getModeSelection());
		}
		preparedBean.getOrderItemm(order, listItems, preparedBean.getModeSelection());
		if (order != null) {
			uiModel.asMap().clear();
			return "redirect:/commandefournisseurs/"
			+ ProcessHelper.encodeUrlPathSegment(order.getId()
					.toString(), httpServletRequest);

		} else {
			uiModel.addAttribute("orderPreParationBean",
					new OrderPreParationBean());
			return "commandprocesses/create";
		}

	}

	@RequestMapping(value = "/{cmdId}/showProduct/{pId}", method = RequestMethod.GET)
	public String showProduct(@PathVariable("cmdId") Long cmdId,
			@PathVariable("pId") Long pId, Model uiModel, HttpSession session) {
		ProcessHelper.addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("produit", Produit.findProduit(pId));
		uiModel.addAttribute("itemId", pId);
		return "commandprocesses/showProduct";

	}

	@ResponseBody
	@RequestMapping(value = "/{cmdId}/deleteLine/{lineId}", method = RequestMethod.GET)
	public String unselectLine(@PathVariable("cmdId") Long cmdId,
			@PathVariable("lineId") Long lineId, Model uiModel,
			HttpServletRequest httpServletRequest) {
		LigneCmdFournisseur line = LigneCmdFournisseur.findLigneCmdFournisseur(lineId);
		CommandeFournisseur commandeFournisseur = CommandeFournisseur.findCommandeFournisseur(cmdId);
		if (line != null) {
			commandeFournisseur.decreaseMontant(line.getPrixAchatTotal());
			commandeFournisseur.getLigneCommande().remove(line);
			line.remove();
			commandeFournisseur.merge();
			return "ok";
		}
		return "ko";
	}

	@ResponseBody
	@RequestMapping(value = "/{cmdId}/updateLine/{lineId}", method = RequestMethod.GET)
	public String updateLineForm(@PathVariable("cmdId") Long cmdId,
			@PathVariable("lineId") Long lineId, Model uiModel,
			HttpServletRequest httpServletRequest) {
		LigneCmdFournisseur line = LigneCmdFournisseur.findLigneCmdFournisseur(lineId);
		if (line == null)
			return null;
		return line.toJson();
	}

	@RequestMapping(value = "/{cmdId}/selectProduct/{pId}", method = RequestMethod.GET)
	public String selectProduct(@PathVariable("cmdId") Long cmdId,
			@PathVariable("pId") Long pId, Model uiModel,
			HttpServletRequest httpServletRequest) {
		CommandeFournisseur commandeFournisseur = CommandeFournisseur
				.findCommandeFournisseur(cmdId);
		Produit produit = Produit.findProduit(pId);
		CommandeProcess commandeProcess = new CommandeProcess(cmdId,
				LigneCmdFournisseur.findLigneCmdFournisseursByCommande(
						commandeFournisseur).getResultList(),
						CommandeFournisseur.findCommandeFournisseur(cmdId)
						.getFournisseur().getName());
		commandeProcess.setProduit(produit);
		uiModel.addAttribute("commandeProcess", commandeProcess);
		return "commandprocesses/editCommand";
	}

	@ResponseBody
	@RequestMapping(value = "/{cmdId}/updateLineByAjax", method = RequestMethod.GET)
	public String updateLine(@PathVariable("cmdId") Long cmdId,
			LigneCmdFournisseur orderItem, Model uiModel,
			HttpServletRequest httpServletRequest) {
		LigneCmdFournisseur line = LigneCmdFournisseur.findLigneCmdFournisseur(orderItem.getId());
		if (line == null) return null;
		BigDecimal pv = orderItem.getPrixAVenteMin() == null ? BigDecimal.ZERO : orderItem.getPrixAVenteMin();
		line.setQuantiteCommande(orderItem.getQuantiteCommande());
		line.setPrixAchatMin(orderItem.getPrixAchatMin());
		line.setPrixAVenteMin(pv);
		line.calculPrixTotal();
		line.merge();
		CommandeFournisseur commandeFournisseur = CommandeFournisseur.findCommandeFournisseur(cmdId);
		commandeFournisseur.increaseMontant(line.getPrixAchatTotal());
		commandeFournisseur.merge();
		return line.toJson();
	}

	@RequestMapping(params = "form", method = RequestMethod.GET)
	public String createForm(Model uiModel) {
		uiModel.addAttribute("orderPreParationBean", new OrderPreParationBean());
		ProcessHelper.addDateTimeFormatPatterns(uiModel);
		List dependencies = new ArrayList();
		if (Fournisseur.countFournisseurs() == 0) {
			dependencies.add(new String[] { "fournisseur", "fournisseurs" });
		}
		uiModel.addAttribute("dependencies", dependencies);
		return "commandprocesses/create";
	}

	@RequestMapping(value = "/{cmdId}/editCommand", method = RequestMethod.GET)
	public String editCommand(@PathVariable("cmdId") Long cmdId, Model uiModel, HttpSession session) {
		CommandeFournisseur commandeFournisseur = CommandeFournisseur.findCommandeFournisseur(cmdId);
		CommandeProcess commandeProcess = new CommandeProcess(cmdId,
		LigneCmdFournisseur.findLigneCmdFournisseursByCommande(commandeFournisseur).getResultList(), commandeFournisseur.getFournisseur().getName());
		uiModel.addAttribute("commandeProcess", commandeProcess);
		uiModel.addAttribute("ligneCmdFournisseur", new LigneCmdFournisseur());

		return "commandprocesses/editCommand";
	}

	@Transactional
	@RequestMapping(value = "/{cmdId}/closeCommand", method = RequestMethod.GET)
	public String closeCommand(@PathVariable("cmdId") Long cmdId, Model uiModel, HttpSession session) {
		CommandeFournisseur commandeFournisseur = CommandeFournisseur.findCommandeFournisseur(cmdId);
		if (commandeFournisseur.getLigneCommande().isEmpty()) {
			uiModel.addAttribute("appMessages", Arrays.asList(messageSource.getMessage("command_close_warning", null, LocaleUtil.getCurrentLocale())));

		} else {
			commandeFournisseur.setEtatCmd(Etat.CLOS);
			uiModel.addAttribute("appMessages", Arrays.asList(messageSource.getMessage("command_close_sucess", null, LocaleUtil.getCurrentLocale())));

		}
		CommandeFournisseur merge = (CommandeFournisseur) commandeFournisseur.merge();
		ProcessHelper.addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("commandefournisseur", merge);
		uiModel.addAttribute("itemId", merge.getId());
		return "redirect:/saleprocess/newPublicCmd";
	}

	@Transactional
	@RequestMapping(value = "/{cmdId}/enregistrerCmd", method = RequestMethod.GET)
	public String enregistrer(@PathVariable("cmdId") Long cmdId, Model uiModel) {
		CommandeFournisseur commandeFournisseur = CommandeFournisseur.findCommandeFournisseur(cmdId);
		ProcessHelper.addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("commandefournisseur", commandeFournisseur);
		uiModel.addAttribute("itemId", cmdId);
		commandeFournisseur.merge();
		checkAndInsertUbipharmActionsLogsInUIModel(uiModel);
		return "commandprocesses/show";
	}
	
	
	private void checkAndInsertUbipharmActionsLogsInUIModel(Model uiModel){
		if(sendedToUbipharm){
			uiModel.addAttribute("apMessage", messageSource.getMessage("command_send_phml_success", null, LocaleUtil.getCurrentLocale()));
			sendedToUbipharm = false;//reset value to false.
		}
		if(this.sendToUbipharmFailed) {
			if(StringUtils.isBlank(ubipharmCommandSendingErrorMessage)){
				this.ubipharmCommandSendingErrorMessage = "Command Not sended An Unexpected " +
						"Error has been detected ! We are Working on that.";
			}
			uiModel.addAttribute("apMessage", this.ubipharmCommandSendingErrorMessage);
			this.sendToUbipharmFailed = false;
		}
	}
	@RequestMapping(value="/ubipharm/{itemId}/send", method = RequestMethod.GET)
	public String sendToUbipharm(@PathVariable("itemId")Long cmdId, Model uiModel){
		CsvImportExportUtil importExportUtil = new CsvImportExportUtil();
		importExportUtil.setCmdId(cmdId);
		List<AbstractUbipharmLigneWrapper> lignesToExport = importExportUtil.constructLigneToExport();
		importExportUtil.setLignesToExport(lignesToExport);
		try {
			importExportUtil.exportCommandsToUbipharmTxt();
			//importExportUtil.checkIfNewlyReceivedCommand();
			sendedToUbipharm= true ;
		} catch (Exception e) {
			this.sendToUbipharmFailed = true; 
			this.ubipharmCommandSendingErrorMessage =  e.getMessage();
		}
		return "redirect:/commandprocesses/"+cmdId+"/enregistrerCmd";
	}

	@Transactional
	@RequestMapping(value="/ubipharm/{itemId}/import", method = RequestMethod.GET)
	public String importFromUbipharmResponse(@PathVariable("itemId")Long cmdId, Model uiModel){
		CommandeFournisseur commandeFournisseur = CommandeFournisseur.findCommandeFournisseur(cmdId);
		String[] fileNames = new CsvImportExportUtil().getReceivedFiles();
		for (String fileName : fileNames) {
			if(!Etat.RECEIVED.equals(commandeFournisseur.getEtatCmd()) &&  fileName.startsWith(commandeFournisseur.getCmdNumber())){
				CsvImportExportUtil csvImportExportUtil = new CsvImportExportUtil();
				try {
					LOG.warn("Start Import Of : "+fileName+" ...");
					csvImportExportUtil.readCsvFile(CsvImportExportUtil.getReceptionFolder()+""+fileName);
					FileSystemScanner.oldFiles.add(fileName);
				} catch (Exception e) {
					LOG.error("",e);
					e.printStackTrace();
				}
				LOG.warn("... "+fileName+", Import Finished");
			}else {
				LOG.warn("File \""+fileName+"\" skipped");
			}
		}
		return "redirect:/commandprocesses/"+cmdId+"/enregistrerCmd";
	}
	
	
	@RequestMapping(value="/listcommandstoimport", method=RequestMethod.GET)
	public String listCommandsToImport(Model uiModel,HttpServletRequest request){
		CsvImportExportUtil csvImportExportUtil = new CsvImportExportUtil();
		List<CommandeFournisseur> listCommandToImports = csvImportExportUtil.listCommandToImport(
				CommandeFournisseur.findCommandsByEtatCommand(Etat.EN_COUR).getResultList(), 
				csvImportExportUtil.getReceivedFiles());
		List<CommandeFournisseur> otherListCommandToImports = csvImportExportUtil.listCommandToImport(
				CommandeFournisseur.findCommandsByEtatCommand(Etat.SENDED_TO_PROVIDER).getResultList(), 
				csvImportExportUtil.getReceivedFiles());
		for (CommandeFournisseur commandeFournisseur : otherListCommandToImports) {
			listCommandToImports.add(commandeFournisseur);
		}
		if (listCommandToImports.isEmpty()) {
			uiModel.addAttribute("apMessage", messageSource.getMessage("command_received_warning", null, LocaleUtil.getCurrentLocale()));
		}else {
			uiModel.addAttribute("results",listCommandToImports);

		}
		return "commandefournisseurs/search";
	}
	@Transactional
	@RequestMapping(value = "/{cmdId}/annulerCmd", method = RequestMethod.GET)
	public String annuler(@PathVariable("cmdId") Long cmdId, Model uiModel, HttpSession session) {
		CommandeFournisseur commandeFournisseur = CommandeFournisseur.findCommandeFournisseur(cmdId);
		session.removeAttribute("productResult");
		if (commandeFournisseur != null) {
			if (commandeFournisseur.getEtatCmd().equals(Etat.CLOS) || !commandeFournisseur.getLivre()) {
				commandeFournisseur.setAnnuler(true);
				commandeFournisseur.merge();
				uiModel.addAttribute("appMessages", Arrays.asList(" ! "));
				uiModel.addAttribute("commandefournisseur", commandeFournisseur);
				uiModel.addAttribute("itemId", cmdId);
				return "commandprocesses/show";
			} else {
				commandeFournisseur.remove();
				uiModel.addAttribute("appMessages", Arrays.asList(messageSource.getMessage("command_cancel_success", null, LocaleUtil.getCurrentLocale()))); 
			}
		} else {
			uiModel.addAttribute("appMessages", Arrays.asList(messageSource.getMessage("command_null", null, LocaleUtil.getCurrentLocale())));
		}
		return "caisses/infos";
	}

	// imprime le bon de commande
	@RequestMapping("/{cmdId}/print/{bonCmdId}.pdf")
	public String print(@PathVariable("cmdId") Long cmdId, @PathVariable("bonCmdId") String invoiceNumber, Model uiModel) {
		CommandeFournisseur commande = CommandeFournisseur.findCommandeFournisseur(cmdId);
		uiModel.addAttribute("commande", commande);
		return "commandProsessDocView";

	}

	// necessaire pour les vues

	@ModelAttribute("fournisseurs")
	public Collection<Fournisseur> populateFournisseurs() {
		return Fournisseur.findAllFournisseurs();
	}

	@ModelAttribute("modeselections")
	public Collection<ModeSelection> populateModeSelections() {
		return Arrays.asList(ModeSelection.class.getEnumConstants());
	}

	@ModelAttribute("sites")
	public Collection<Site> populateSites() {
		return Site.findAllSites();
	}

	@ModelAttribute("tvas")
	public Collection<TVA> populateTVAS() {
		return TVA.findAllTVAS();
	}

	@ModelAttribute("filiales")
	public Collection<Filiale> populateFiliales() {
		return ProcessHelper.populateFiliale();
	}

	@ModelAttribute("rayons")
	public Collection<Rayon> populateRayons() {
		return ProcessHelper.populateRayon();
	}
	@ModelAttribute("commandTypes")
	public Collection<CommandType> populateCommandType(){
		return Arrays.asList(CommandType.class.getEnumConstants());
	}

	@ModelAttribute("etats")
	public Collection<Etat> populateEtats() {
		return Arrays.asList(Etat.class.getEnumConstants());
	}
}
