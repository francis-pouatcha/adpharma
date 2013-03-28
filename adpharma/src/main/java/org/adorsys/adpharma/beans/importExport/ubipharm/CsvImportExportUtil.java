/**
 * 
 */
package org.adorsys.adpharma.beans.importExport.ubipharm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.persistence.TypedQuery;

import org.adorsys.adpharma.beans.importExport.ubipharm.wrapper.AbstractUbipharmLigneWrapper;
import org.adorsys.adpharma.beans.importExport.ubipharm.wrapper.CommandTypeLigne;
import org.adorsys.adpharma.beans.importExport.ubipharm.wrapper.CommentLigne;
import org.adorsys.adpharma.beans.importExport.ubipharm.wrapper.DeliveryDateLigne;
import org.adorsys.adpharma.beans.importExport.ubipharm.wrapper.DistributorLigne;
import org.adorsys.adpharma.beans.importExport.ubipharm.wrapper.EndOfCommandLigne;
import org.adorsys.adpharma.beans.importExport.ubipharm.wrapper.ProductItemLigne;
import org.adorsys.adpharma.beans.importExport.ubipharm.wrapper.ResponseCommandRowReference;
import org.adorsys.adpharma.beans.importExport.ubipharm.wrapper.ResponseErrorDetailRow;
import org.adorsys.adpharma.beans.importExport.ubipharm.wrapper.ResponseProductItemRow;
import org.adorsys.adpharma.beans.importExport.ubipharm.wrapper.UbipharmCommandStringSequence;
import org.adorsys.adpharma.beans.importExport.ubipharm.wrapper.WorkTypeLigne;
import org.adorsys.adpharma.domain.CipType;
import org.adorsys.adpharma.domain.CommandType;
import org.adorsys.adpharma.domain.CommandeFournisseur;
import org.adorsys.adpharma.domain.Configuration;
import org.adorsys.adpharma.domain.Etat;
import org.adorsys.adpharma.domain.LigneCmdFournisseur;
import org.adorsys.adpharma.utils.CipMgenerator;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.aspectj.util.FileUtil;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * @author w2b
 *
 */
public class CsvImportExportUtil {
	private static Logger LOG = LoggerFactory.getLogger(CsvImportExportUtil.class);
	public static  String RECEPTION_FOLDER_PATH = "/tools/ubipharm/receptions/";
	private static 	String SENDING_FOLDER_PATH = "/tools/ubipharm/exports/";
	private List<AbstractUbipharmLigneWrapper> lignesToExport= new ArrayList<AbstractUbipharmLigneWrapper>();
	private Long cmdId = null;
	private int numberOfEncodedLignes;
	private int numberOfClearLignes;
	private static final String ERROR_CODE_FROM_SENDED_FILE= "E";
	private static final String ERROR_CODE_FROM_UBIPHARM_SERVER= "ERR";

	Map<LigneCmdFournisseur,BigInteger> productAndQtyDeliveredBalance = new HashMap<LigneCmdFournisseur, BigInteger>();
	
	public CsvImportExportUtil() {
	}
	
	public void readCsvFile(String fileName) throws Exception{
			List<String> receptionLines = loadReceptionLines(fileName);
			DistributorLigne readDistributor = readDistributor(receptionLines);
			WorkTypeLigne readWorTypeLigne = readWorTypeLigne(receptionLines);
			ResponseCommandRowReference readResponseCommandReferences = readResponseCommandReferences(receptionLines);
			ResponseErrorDetailRow readResponseErrorDetail = readResponseErrorDetail(receptionLines);
			String responseErrorCode = readWorTypeLigne.getWorkType().getStringValue();
			if(ERROR_CODE_FROM_SENDED_FILE.equals(responseErrorCode)||ERROR_CODE_FROM_UBIPHARM_SERVER.equals(responseErrorCode)){
				LOG.error("This is an error file");
				saveError(readResponseErrorDetail, readResponseCommandReferences);
				return ;
			}
			List<CommandeFournisseur> commandFournisseurs = CommandeFournisseur.findCommandeFournisseursByCmdNumberEquals(readResponseCommandReferences.
					getCustomerCommandKey().getStringValue()).getResultList();
			if(commandFournisseurs.isEmpty()){
				LOG.error("Command Not Found, Hug !");
			}else {
				CommandeFournisseur commandeFournisseur = commandFournisseurs.iterator().next();
				List<ResponseProductItemRow> readResponseProductLignes = readResponseProductLignes(receptionLines);
				saveLigneCmdFournisseurs(readResponseProductLignes,commandeFournisseur);
				commandeFournisseur.setEtatCmd(Etat.RECEIVED);
				commandeFournisseur.merge().flush();
			}
	}
	
	private void saveError(ResponseErrorDetailRow readResponseErrorDetail,
			ResponseCommandRowReference readResponseCommandReferences) {

		String errorMessageValue = readResponseErrorDetail.getDetail().getStringValue();
		TypedQuery<CommandeFournisseur> typedQuery = CommandeFournisseur.findCommandeFournisseursByCmdNumberEquals(readResponseCommandReferences.getCustomerCommandKey().getStringValue());
		if(!typedQuery.getResultList().isEmpty()){
			CommandeFournisseur commandeFournisseur = typedQuery.getResultList().iterator().next();
			commandeFournisseur.setLastestUbipharmError(errorMessageValue);
			commandeFournisseur.merge().flush();
		}
	}
	private void saveLigneCmdFournisseurs(List<ResponseProductItemRow> productItemRows,CommandeFournisseur commandeFournisseur) {
		Assert.notNull(productItemRows, "Null argment not required");
		for (ResponseProductItemRow responseProductItemRow : productItemRows) {
			String productKey = StringUtils.trim(responseProductItemRow.getOrderingProductKey().getStringValue());
//			LigneCmdFournisseur ligneCmdFournisseur=LigneCmdFournisseur.findLigneCmdFournisseursByCip(productKey).getSingleResult();
			LigneCmdFournisseur ligneCmdFournisseur = LigneCmdFournisseur.findLigneCmdFournisseurByCipAndComFournisseur(productKey, commandeFournisseur).getSingleResult();
			
			ligneCmdFournisseur.setQuantiteFournie(new BigInteger(responseProductItemRow.getQuantityDelivered().getStringValue()));
			ligneCmdFournisseur.merge().flush();
			productAndQtyDeliveredBalance.put(ligneCmdFournisseur, ligneCmdFournisseur.getQuantiteCommande().subtract(ligneCmdFournisseur.getQuantiteFournie()));
			
			LOG.warn("Merging Command Item : CIP = "+ligneCmdFournisseur.getCip());
		}
	}
	public boolean responseHasError(ResponseErrorDetailRow errorDetailRow){
		return errorDetailRow != null;
	}

	public List<String> loadReceptionLines(String fileName) throws IOException{
		File file = new File(fileName);
		List<String> readLines = FileUtils.readLines(file);
		return readLines;
	}
	private DistributorLigne readDistributor(List<String> lines){
		DistributorLigne distributorLigne = null ;
		for (String lineContent : lines) {
			if(!lineContent.startsWith("R"))
				continue;
			distributorLigne = new DistributorLigne(1, lineContent.length());
			distributorLigne.setLigneIdentifier(new UbipharmCommandStringSequence(1, 1, "R"));
			distributorLigne.setRepartitor(new UbipharmCommandStringSequence(2, lineContent.length()+1,
					distributorLigne.readValue(lineContent, 2, lineContent.length() + 1)));
			break;
		}
		return distributorLigne;
	}
	private WorkTypeLigne readWorTypeLigne(List<String> lines){
		WorkTypeLigne workTypeLigne = null;
		for (String lineContent: lines) {
			if(!lineContent.startsWith("P")){
				continue;
			}
			workTypeLigne = new WorkTypeLigne(1, lineContent.length()+1);
			int workTypeStartIndex = 2;
			workTypeLigne.setWorkType(new UbipharmCommandStringSequence(2, lineContent.length(),
					workTypeLigne.readValue(lineContent, workTypeStartIndex, lineContent.length() +1)));
			break;
		}
		return workTypeLigne;
	}
	private ResponseCommandRowReference readResponseCommandReferences(List<String> lines){
		ResponseCommandRowReference responseCommandRowReference = null;
		for (String lineContent : lines) {
			if(ResponseCommandRowReference.assertThisIsAvalidCommandRow(lineContent) == false){
				continue;
			}
			responseCommandRowReference = new ResponseCommandRowReference(1, lineContent.length(), lineContent);
			break ;
		}
		return responseCommandRowReference;
	}
	private List<ResponseProductItemRow> readResponseProductLignes(List<String> lines){
		ResponseProductItemRow responseProductItemRow = null;
		List<ResponseProductItemRow> results = new ArrayList<ResponseProductItemRow>();
		for (String lineContent: lines) {
			if(ResponseProductItemRow.assertThisIsAvalidResponseProductItemRow(lineContent) == false){
				continue ;
			}
			responseProductItemRow = new ResponseProductItemRow(1, 113, lineContent);
			results.add(responseProductItemRow);
		}
		return results;
	}
	private ResponseErrorDetailRow readResponseErrorDetail(List<String> lines){
		ResponseErrorDetailRow responseErrorDetailRow = null;
		for (String lineContent: lines) {
			if(ResponseErrorDetailRow.assertItisAvalidErrorDetailRow(lineContent) == false){
				continue ;
			}
			responseErrorDetailRow = new ResponseErrorDetailRow(1, lineContent.length(), lineContent);
			break;
		}
		return responseErrorDetailRow;
	}
	public void exportCommandsToUbipharmCsv() throws Exception{
		Writer writer;
		CommandeFournisseur commandeFournisseur = loadCommandeFournisseur();
		writer = new FileWriter(getSendFolder()+getFileName(commandeFournisseur,"csv"));
		CSVWriter csvWriter = new CSVWriter(writer,CSVWriter.NO_QUOTE_CHARACTER,CSVWriter.NO_ESCAPE_CHARACTER);
		List<String[]> formattedLigneWrapperForCsv = this.getFormattedLigneWrapperForCsv();
		csvWriter.writeAll(formattedLigneWrapperForCsv);
		writer.close(
				);
		LOG.debug("Done");
		checkIfCommandFileIsTransferred(commandeFournisseur);
	}
	public void exportCommandsToUbipharmTxt() throws Exception{
			CommandeFournisseur commandeFournisseur = loadCommandeFournisseur();
			String fileName = getFileName(commandeFournisseur, "txt");
			File fileToSend = new File(getSendFolder()+""+fileName);
			FileUtils.writeLines(fileToSend, convertAbstractLinesToLines(getLignesToExport()));
			checkIfCommandFileIsTransferred(commandeFournisseur);
	}

	private void checkIfCommandFileIsTransferred(CommandeFournisseur commandeFournisseur)
			throws InterruptedException, Exception {
		java.util.logging.Logger.getGlobal().log(Level.INFO, "Execution Blocked for 15000 ms, To wait ubipharm to transfert the file");
		Thread.sleep(15000);
		java.util.logging.Logger.getGlobal().log(Level.INFO, "Scanning The ../exports Directory, to check if the file is transferred");
		String[] listFiles = FileUtil.listFiles(new File(getSendFolder()));
		if(listFiles.length > 0){
			java.util.logging.Logger.getGlobal().log(Level.INFO, "File Not Transferred ! " +
					"Please check If Ubipharm Module is Running on This Computer !");
			throw new Exception("Command File Not Transfered! Please check If Ubipharm Module is Running on This Computer !");
		}else {
			updateCommandToSended(commandeFournisseur);					
		}
	}

	private void updateCommandToSended(CommandeFournisseur commandeFournisseur) {
		commandeFournisseur.setEtatCmd(Etat.SENDED_TO_PROVIDER);
		commandeFournisseur.merge().flush();
	}
	public List<String> convertAbstractLinesToLines(List<AbstractUbipharmLigneWrapper> rows){
		List<String> lines = new ArrayList<String>();
		for (AbstractUbipharmLigneWrapper abstractUbipharmLigneWrapper : rows) {
			lines.add(abstractUbipharmLigneWrapper.getStringValue());
		}
		return lines;
	}
	private String getFileName(CommandeFournisseur loadCommandeFournisseur,String extension) {
		return "Command-"+loadCommandeFournisseur.getId()+"."+extension;
	}

	public void checkIfNewlyReceivedCommand(){
		FileSystemScanner fileSystemScanner = new FileSystemScanner();
		if(FileSystemScanner.FIST_SCAN_STARTED == true) {
			return ;
		}else  {
			try {
				FileSystemScanner.FIST_SCAN_STARTED = true ;
				fileSystemScanner.startScan();
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		}
	}
	/*
	 * Construct specifics rows to match ubipharm csv format.
	*/
	public String[] constructFirstRow(){
		return new String []{""};
	}
	public String[] getLigneCommandeFournisseurCsvRepresentation(LigneCmdFournisseur ligneCmdFournisseur){
		if(ligneCmdFournisseur == null) throw new IllegalArgumentException("Invalid Argument, must not be null. ");
		List<String> stringItems = new ArrayList<String>();
		stringItems.add(ligneCmdFournisseur.getDesignation());
		stringItems.add(ligneCmdFournisseur.getCip());
		stringItems.add(ligneCmdFournisseur.getQuantiteCommande().toString());
		stringItems.add(ligneCmdFournisseur.getPrixAchatMin().toString());
		return stringItems.toArray(new String[stringItems.size()]);
	}
	public List<String[]> getManyLigneCommandeFournisseurCsvRepresentation(List<LigneCmdFournisseur> ligneCmdFournisseurs){
		if(ligneCmdFournisseurs == null) throw new IllegalArgumentException("Invalid Argument, must not be null. ");
		List<String[]> results = new ArrayList<String[]>();
		for (LigneCmdFournisseur ligneCmdFournisseur : ligneCmdFournisseurs) {
			results.add(getLigneCommandeFournisseurCsvRepresentation(ligneCmdFournisseur));
		}
		return results;
	}
	public List<LigneCmdFournisseur> loadLigneCmdFournisseur(CommandeFournisseur commandeFournisseur){
		TypedQuery<LigneCmdFournisseur> ligneCmdFournisseursByCommande = LigneCmdFournisseur.findLigneCmdFournisseursByCommande(commandeFournisseur);
		return ligneCmdFournisseursByCommande.getResultList();
	}
	public List<AbstractUbipharmLigneWrapper> getLignesToExport() {
		return lignesToExport;
	}
	public void setLignesToExport(List<AbstractUbipharmLigneWrapper> lignesToExport) {
		this.lignesToExport = lignesToExport;
	}
	public List<String[]> getFormattedLigneWrapperForCsv(){
		if(this.lignesToExport == null || this.lignesToExport.isEmpty()){
			LOG.warn("no command to export");
			this.lignesToExport = new ArrayList<AbstractUbipharmLigneWrapper>();
		}
		List<String[]> stringLines = new ArrayList<String[]>(this.lignesToExport.size());
		if(this.lignesToExport.isEmpty()) return stringLines;
		for (AbstractUbipharmLigneWrapper ligneWrapper : this.lignesToExport) {
			stringLines.add(ligneWrapper.arrayRepresentation());
		}
		return stringLines;
	}

	public List<AbstractUbipharmLigneWrapper> constructLigneToExport(){
		List<AbstractUbipharmLigneWrapper> lignesToExport = new ArrayList<AbstractUbipharmLigneWrapper>();

		CommandeFournisseur nextCommandeFournisseur = loadCommandeFournisseur();
		
		DistributorLigne distributorLigne = new DistributorLigne(1, 50);
		distributorLigne.setLigneIdentifier(new UbipharmCommandStringSequence(1, 1, false, "R"));
		distributorLigne.setRepartitor(new UbipharmCommandStringSequence(2, 49, true,getDistributorKey(nextCommandeFournisseur)));
		distributorLigne.joinAnotherString(distributorLigne.getLigneIdentifier()).joinAnotherString(distributorLigne.getRepartitor());
		
		WorkTypeLigne workTypeLigne = new WorkTypeLigne(1, 2);
		workTypeLigne.setWorkType(new UbipharmCommandStringSequence(2, 2, "C"));
		workTypeLigne.joinString(workTypeLigne.getLigneIdentifier(),workTypeLigne.getWorkType());
		
		CommandTypeLigne commandTypeLigne = convertCommand(nextCommandeFournisseur);
		commandTypeLigne.joinString(commandTypeLigne.getLigneIdentifier(),commandTypeLigne.getCommandType(),commandTypeLigne.getSeparator(),commandTypeLigne.getCommandReference());

		DeliveryDateLigne deliveryDateLigne = new DeliveryDateLigne(1, 11);
		deliveryDateLigne.setDeliveryDate(new UbipharmCommandStringSequence(2, 11,PharmaDateUtil.format(loadCommandeFournisseur().getDateLimiteLivraison()	,"yyyy-MM-dd")));
		deliveryDateLigne.joinString(deliveryDateLigne.getLigneIdentifier(),deliveryDateLigne.getDeliveryDate());
		
		List<LigneCmdFournisseur> ligneCmdFournisseurs = loadLigneCommandFournisseurs(nextCommandeFournisseur);
		List<ProductItemLigne> productLigneItems = constructProductItemLignes(ligneCmdFournisseurs);
		
		CommentLigne commentLigne = new CommentLigne(1, 257);
		commentLigne.setLigneIdentifier(new UbipharmCommandStringSequence(1, 1, "W"));
		commentLigne.setComment(new UbipharmCommandStringSequence(2, 257, false, "No Comments"));
		commentLigne.joinString(commentLigne.getLigneIdentifier(),commentLigne.getComment());
		
		EndOfCommandLigne endOfCommandLigne = new EndOfCommandLigne(1, 9);
		endOfCommandLigne.setLigneIdentifier(new UbipharmCommandStringSequence(1, 1, "Z"));
		endOfCommandLigne.setNumberOfEncodedLignes(new UbipharmCommandStringSequence(6, 9, CipMgenerator.formatNumber(""+numberOfEncodedLignes, 4)));
		endOfCommandLigne.setNumberOfClearLignes(new UbipharmCommandStringSequence(2, 5, CipMgenerator.formatNumber(""+numberOfClearLignes, 4)));
		endOfCommandLigne.joinString(endOfCommandLigne.getLigneIdentifier(),endOfCommandLigne.getNumberOfEncodedLignes(),
				endOfCommandLigne.getNumberOfClearLignes());
		
		lignesToExport.add(distributorLigne);
		lignesToExport.add(workTypeLigne);
		lignesToExport.add(commandTypeLigne);
		lignesToExport.add(deliveryDateLigne);
		lignesToExport.addAll(productLigneItems);
		lignesToExport.add(commentLigne);
		lignesToExport.add(endOfCommandLigne);
		
		return lignesToExport;
	}
	
	private String getDistributorKey(CommandeFournisseur commandeFournisseur) {
		String distributorKey = commandeFournisseur.getFournisseur().getDistributorKey();
		if(StringUtils.isEmpty(distributorKey)){
			distributorKey = "GRS";
		}
		return distributorKey;
	}

	public void constuctLigneToImport(){
		
	}
	public List<ProductItemLigne> constructProductItemLignes(List<LigneCmdFournisseur> ligneCmdFournisseurs) {
		List<ProductItemLigne> productLigneItems = new ArrayList<ProductItemLigne>();
		int ligneNumber = 1;
		for (LigneCmdFournisseur ligneCmdFournisseur : ligneCmdFournisseurs) {
			productLigneItems.add(convertCommandItem(ligneCmdFournisseur, ligneNumber));
			ligneNumber ++;
		}
		for (ProductItemLigne productItemLigne : productLigneItems) {
			productItemLigne.joinString(productItemLigne.getLigneIdentifier(),productItemLigne.getQuantityOrdored(),
					productItemLigne.getCodificationType(),productItemLigne.getProductId(),productItemLigne.getIsPartialDelivery(),
					productItemLigne.getIsBalance(), productItemLigne.getIsEquivalentDelivery(), productItemLigne.getLigneNumber());
		}
		return productLigneItems;
	}
	public List<LigneCmdFournisseur> loadLigneCommandFournisseurs(
			CommandeFournisseur nextCommandeFournisseur) {
		return LigneCmdFournisseur.findLigneCmdFournisseursByCommande(nextCommandeFournisseur).getResultList();
	}
	public CommandeFournisseur loadCommandeFournisseur() {
		return CommandeFournisseur.findCommandeFournisseur(cmdId);
	}
	
	public CommandTypeLigne convertCommand(CommandeFournisseur commandeFournisseur){
		if(commandeFournisseur == null) throw new IllegalArgumentException("Invalid Argument ! Null values aren't required");
		CommandTypeLigne commandTypeLigne = new CommandTypeLigne(1, 25);
		String commandTypeValue =  getCommandTypeValue(commandeFournisseur);
		commandTypeLigne.setCommandType(new UbipharmCommandStringSequence(2, 4, commandTypeValue));
		commandTypeLigne.setSeparator(new UbipharmCommandStringSequence(5, 5, "R"));
		commandTypeLigne.setCommandReference(new UbipharmCommandStringSequence(6, 25, true, commandeFournisseur.getCmdNumber()));
		return commandTypeLigne;
	}

	private String getCommandTypeValue(CommandeFournisseur commandeFournisseur) {
		String result = null ;
		if(commandeFournisseur.getCommandType() == null || CommandType.NORMAL.equals(commandeFournisseur.getCommandType())){
			result = "000";
		}else if(CommandType.PACKAGED.equals(commandeFournisseur.getCommandType())){
			result = "001";
		}else if(CommandType.SPECIAL.equals(commandeFournisseur.getCommandType())){
			result = "002";
		}
		return result;
	}
	public ProductItemLigne convertCommandItem(LigneCmdFournisseur ligneCmdFournisseur,int ligneNumber){
		if(ligneCmdFournisseur == null) throw new IllegalArgumentException("Invalid Argument ! Null values aren't required");
		ProductItemLigne itemLigne = new ProductItemLigne(1, 64);
		CipType cipType = ligneCmdFournisseur.getProduit().getCipType();
		itemLigne.setCodificationType(new UbipharmCommandStringSequence(6, 7, true,getCipTypeStringRepresetation(cipType).toString()));
		itemLigne.setIsBalance(new UbipharmCommandStringSequence(59, 59, "N"));
		itemLigne.setIsEquivalentDelivery(new UbipharmCommandStringSequence(60, 60, "N"));
		itemLigne.setIsPartialDelivery(new UbipharmCommandStringSequence(58, 58, "N"));
		itemLigne.setLigneNumber(new UbipharmCommandStringSequence(61, 64, CipMgenerator.formatNumber(""+ligneNumber, 4)));
		itemLigne.setProductId(new UbipharmCommandStringSequence(8, 57,true,ligneCmdFournisseur.getCip()));
		String formattedNumber = CipMgenerator.formatNumber(""+ligneCmdFournisseur.getQuantiteCommande().toString(), 4);
		itemLigne.setQuantityOrdored(new UbipharmCommandStringSequence(2, 5,true, formattedNumber));
		return itemLigne;
	}
	public String getCipTypeStringRepresetation(CipType cipType){
		String cipTypeStringRepresentation  = null;
		if(CipType.CIP39.equals(cipType)){
			cipTypeStringRepresentation = "C1";
			this.numberOfEncodedLignes = numberOfEncodedLignes + 1;
		}else if(CipType.EAN13.equals(cipType)){
			cipTypeStringRepresentation = "C2";
			this.numberOfEncodedLignes = numberOfEncodedLignes + 1;
		}else if(CipType.CLAIR.equals(cipType)){
			cipTypeStringRepresentation = "C3";
			this.numberOfClearLignes = numberOfClearLignes + 1 ;
		}
		return cipTypeStringRepresentation;
	}
	public Long getCmdId() {
		return cmdId;
	}
	public void setCmdId(Long cmdId) {
		this.cmdId = cmdId;
	}
	public static String getSendFolder(){
		Configuration configuration = Configuration.findAllConfigurations().iterator().next();
		String sendFolder = configuration.getSendFolder();
		if(sendFolder == null || sendFolder.isEmpty()){
			sendFolder = SENDING_FOLDER_PATH;
		}
		return sendFolder;
	}
	public static String getReceptionFolder(){
		Configuration configuration = Configuration.findAllConfigurations().iterator().next();
		String receptionFolder = configuration.getReceptionFolder();
		if(receptionFolder == null || receptionFolder.isEmpty()){
			receptionFolder = RECEPTION_FOLDER_PATH;
		}
		return receptionFolder;
	}
	public String[] getReceivedFiles() {
		File fileDir = new File(getReceptionFolder());
		String[] fileNames = FileUtil.listFiles(fileDir);
		return fileNames;
	}
	public List<CommandeFournisseur> listCommandToImport(List<CommandeFournisseur> commandesFournisseurs,String[] fileNames){
		Assert.notNull(commandesFournisseurs, "Null Commands not required");
		Assert.notNull(fileNames, "Null files not required");
		List<CommandeFournisseur> commandeFournisseursToImports = new ArrayList<CommandeFournisseur>();
		for (CommandeFournisseur commandeFournisseur : commandesFournisseurs) {
			
			String cmdNumber = commandeFournisseur.getCmdNumber();
			for (String fileName : fileNames) {
				if(fileName.startsWith(cmdNumber)){
					commandeFournisseursToImports.add(commandeFournisseur);
					break ;
				}
			}
		}
		return commandeFournisseursToImports;
	}
}

