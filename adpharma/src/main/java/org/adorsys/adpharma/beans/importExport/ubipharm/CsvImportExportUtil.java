/**
 * 
 */
package org.adorsys.adpharma.beans.importExport.ubipharm;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;

import org.adorsys.adpharma.beans.importExport.ubipharm.wrapper.AbstractUbipharmLigneWrapper;
import org.adorsys.adpharma.beans.importExport.ubipharm.wrapper.CommandTypeLigne;
import org.adorsys.adpharma.beans.importExport.ubipharm.wrapper.CommentLigne;
import org.adorsys.adpharma.beans.importExport.ubipharm.wrapper.DeliveryDateLigne;
import org.adorsys.adpharma.beans.importExport.ubipharm.wrapper.DistributorLigne;
import org.adorsys.adpharma.beans.importExport.ubipharm.wrapper.EndOfCommandLigne;
import org.adorsys.adpharma.beans.importExport.ubipharm.wrapper.ProductItemLigne;
import org.adorsys.adpharma.beans.importExport.ubipharm.wrapper.UbipharmCommandStringSequence;
import org.adorsys.adpharma.beans.importExport.ubipharm.wrapper.WorkTypeLigne;
import org.adorsys.adpharma.domain.CipType;
import org.adorsys.adpharma.domain.CommandeFournisseur;
import org.adorsys.adpharma.domain.LigneCmdFournisseur;
import org.adorsys.adpharma.utils.CipMgenerator;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;

/**
 * @author w2b
 *
 */
public class CsvImportExportUtil {
	private static Logger LOG = LoggerFactory.getLogger(CsvImportExportUtil.class);
	private static  String RECEPTION_FOLDER_PATH = "file:///tools/ubipharm/receptions";
	private static 	String SENDING_FOLDER_PATH = "/tools/ubipharm/";
	private List<AbstractUbipharmLigneWrapper> lignesToExport= new ArrayList<AbstractUbipharmLigneWrapper>();
	private Long cmdId = null;
	private int numberOfEncodedLignes;
	private int numberOfClearLignes;
	public CsvImportExportUtil() {
	}
	
	public void readCsvFile(){
		Reader reader;
		String receptionFileName= RECEPTION_FOLDER_PATH+"reception.csv";
		try {
			reader = new FileReader(receptionFileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to find the file : "+receptionFileName);
		}
		ColumnPositionMappingStrategy<CommandeFournisseur> columnPositionMappingStrategy = new ColumnPositionMappingStrategy<CommandeFournisseur>();
		columnPositionMappingStrategy.setType(CommandeFournisseur.class);
		String [] columnStrategy = new String[]{"",""};
		CSVReader csvReader = new CSVReader(reader, CSVParser.NULL_CHARACTER);
	}
	public void exportCommandsToUbipharmCsv(){
		Writer writer;
		try {
			writer = new FileWriter(SENDING_FOLDER_PATH+loadCommandeFournisseur().getId()+".csv");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to  find the file :"+SENDING_FOLDER_PATH);
		}
		CSVWriter csvWriter = new CSVWriter(writer,CSVWriter.NO_QUOTE_CHARACTER,CSVWriter.NO_ESCAPE_CHARACTER);
		List<String[]> formattedLigneWrapperForCsv = this.getFormattedLigneWrapperForCsv();
		csvWriter.writeAll(formattedLigneWrapperForCsv);
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		LOG.debug("Done");
	}
	public boolean checkIfNewlyReceivedCommand(){
		FileSystemScanner fileSystemScanner = new FileSystemScanner();
		try {
			fileSystemScanner.startScan();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return false;
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
		List<String[]> stringLines = new ArrayList<String[]>(this.lignesToExport.size());
		if(this.lignesToExport.isEmpty()) return stringLines;
		for (AbstractUbipharmLigneWrapper ligneWrapper : this.lignesToExport) {
			stringLines.add(ligneWrapper.arrayRepresentation());
		}
		return stringLines;
	}

	public List<AbstractUbipharmLigneWrapper> constructLigneToExport(){
		List<AbstractUbipharmLigneWrapper> lignesToExport = new ArrayList<AbstractUbipharmLigneWrapper>();
		DistributorLigne distributorLigne = new DistributorLigne(1, 50);
		distributorLigne.setLigneIdentifier(new UbipharmCommandStringSequence(1, 1, false, "R"));
		distributorLigne.setRepartitor(new UbipharmCommandStringSequence(2, 49, true, "Rep171"));
		distributorLigne.joinAnotherString(distributorLigne.getLigneIdentifier()).joinAnotherString(distributorLigne.getRepartitor());
		
		WorkTypeLigne workTypeLigne = new WorkTypeLigne(1, 2);
		workTypeLigne.setWorkType(new UbipharmCommandStringSequence(2, 2, "C"));
		workTypeLigne.joinString(workTypeLigne.getLigneIdentifier(),workTypeLigne.getWorkType());
		
		CommandeFournisseur nextCommandeFournisseur = loadCommandeFournisseur();
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
		commandTypeLigne.setCommandType(new UbipharmCommandStringSequence(2, 4, "001"));
		commandTypeLigne.setSeparator(new UbipharmCommandStringSequence(5, 5, "R"));
		commandTypeLigne.setCommandReference(new UbipharmCommandStringSequence(6, 25, true, commandeFournisseur.getCmdNumber()));
		return commandTypeLigne;
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
}

