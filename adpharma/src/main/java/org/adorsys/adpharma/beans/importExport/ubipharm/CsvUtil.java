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

import org.adorsys.adpharma.domain.CommandeFournisseur;
import org.adorsys.adpharma.domain.LigneCmdFournisseur;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;
import au.com.bytecode.opencsv.bean.CsvToBean;

/**
 * @author w2b
 *
 */
public class CsvUtil {
	private static Logger LOG = LoggerFactory.getLogger(CsvUtil.class);
	private static final String RECEPTION_FOLDER_PATH = "file:///tools/ubipharm/receptions";
	private static final String SENDING_FOLDER_PATH = "file:///tools/ubipharm/sends";
	
	private CommandeFournisseur commandeFournisseur ;
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
	public void exportCommandsToUbipharmCsv(List<CommandeFournisseur> commandeFournisseurs){
		if(commandeFournisseurs == null || commandeFournisseurs.isEmpty()) throw new IllegalArgumentException("Invalid Parameters");
		Writer writer;
		String sendingFileName = SENDING_FOLDER_PATH+"sending.csv";
		try {
			writer = new FileWriter(sendingFileName);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to  find the file :"+sendingFileName);
		}
		
		CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR);
		csvWriter.writeNext(constructFirstRow());
		csvWriter.writeNext(constructSecondRow());
		csvWriter.writeNext(constructThirdRow());
		List<LigneCmdFournisseur> ligneCmdFournisseurs = loadLigneCmdFournisseur(commandeFournisseur);
		csvWriter.writeAll(getManyLigneCommandeFournisseurCsvRepresentation(ligneCmdFournisseurs));
		List<String[]> allLines = null;
		csvWriter.writeAll(allLines);
		LOG.debug("Done");
	}
	public boolean checkIfNewlyReceivedCommand(){
		//check if new command/file is received/added to this Directory
		return false;
	}
	/*
	 * Construct specifics rows to match ubipharm csv format.
	*/
	public String[] constructFirstRow(){
		return new String []{""};
	}
	public String[] constructSecondRow(){
		return new String []{""};
	}
	public String[] constructThirdRow(){
		return new String []{""};
	}
	public String[] readFirstRow(CSVReader csvReader){
		return new String []{""};
	}
	public String[] readSecondRow(CSVReader csvReader){
		return new String []{""};
	}
	public String[] readThirdRow(CSVReader csvReader){
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
}
