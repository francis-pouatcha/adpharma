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
import org.adorsys.adpharma.domain.CommandeFournisseur;
import org.adorsys.adpharma.domain.LigneCmdFournisseur;
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
	private static 	String SENDING_FOLDER_PATH = "sending.csv";
	private List<AbstractUbipharmLigneWrapper> lignesToExport= new ArrayList<AbstractUbipharmLigneWrapper>();
	
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
			writer = new FileWriter(SENDING_FOLDER_PATH);
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
}
