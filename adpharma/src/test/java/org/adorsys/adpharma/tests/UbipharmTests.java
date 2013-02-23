/**
 * 
 */
package org.adorsys.adpharma.tests;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.adorsys.adpharma.beans.importExport.ubipharm.CommandJob;
import org.adorsys.adpharma.beans.importExport.ubipharm.CsvImportExportUtil;
import org.adorsys.adpharma.beans.importExport.ubipharm.FileSystemScanner;
import org.adorsys.adpharma.beans.importExport.ubipharm.wrapper.AbstractUbipharmLigneWrapper;
import org.adorsys.adpharma.beans.importExport.ubipharm.wrapper.CommandTypeLigne;
import org.adorsys.adpharma.beans.importExport.ubipharm.wrapper.DistributorLigne;
import org.adorsys.adpharma.beans.importExport.ubipharm.wrapper.ProductItemLigne;
import org.adorsys.adpharma.beans.importExport.ubipharm.wrapper.UbipharmCommandStringSequence;
import org.adorsys.adpharma.domain.CipType;
import org.adorsys.adpharma.domain.CommandeFournisseur;
import org.adorsys.adpharma.domain.LigneCmdFournisseur;
import org.adorsys.adpharma.utils.CipMgenerator;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author w2b
 *
 */
@RunWith(value=SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:META-INF/spring/applicationContext*.xml"})
@Transactional
public class UbipharmTests {
	@Test
	public void testScan() throws SchedulerException {
		FileSystemScanner systemScanner = new FileSystemScanner();
		System.out.println("Starting ...");
		systemScanner.startScan();
		boolean boucle = true;
		while(boucle){
			if(CommandJob.numberOfTime == 6){
				System.out.println("quota riched");
				systemScanner.REPEAT_SECONDLY_FOREVER = SimpleScheduleBuilder.repeatSecondlyForever(1);
				JobDetail anotherJobDetail = JobBuilder.newJob(CommandJob.class)
						.withIdentity(new JobKey("fileReceivingOne", "fileReceiving"))
						.build();
				Trigger second = TriggerBuilder
						.newTrigger()
						.withIdentity(new TriggerKey("fileReceivingJobTriggerTwo","fileReceiving"))
						.withSchedule(systemScanner.REPEAT_SECONDLY_FOREVER)
						.startNow().build();
				systemScanner.scheduler.deleteJob(systemScanner.jobDetail.getKey());
				systemScanner.scheduler.scheduleJob(anotherJobDetail, second);
				systemScanner.scheduler.start();
				System.out.println("The second scheduler has started");
				boucle = false;
			}
		}
	}
	@Test
	public void testDistributorLigneGeneration(){
		DistributorLigne distributorLigne = new DistributorLigne(1, 50);
		distributorLigne.setLigneIdentifier(new UbipharmCommandStringSequence(1, 1, false, "R"));
		distributorLigne.setRepartitor(new UbipharmCommandStringSequence(2, 49, false, "Rep171"));
		distributorLigne.joinAnotherString(distributorLigne.getLigneIdentifier()).joinAnotherString(distributorLigne.getRepartitor());
		Assert.assertEquals(7, distributorLigne.getStringValue().length());
		String[] expected = new String[]{"RRep171"};
		Assert.assertEquals(ArrayUtils.toString(expected), ArrayUtils.toString(distributorLigne.arrayRepresentation()));
	}
	
	@Test
	public void testArrayRepresentamtion(){
		UbipharmCommandStringSequence ubipharmCommandStringSequence = new UbipharmCommandStringSequence(2, 49, false, "Rep171");
		String[] expected = new String[]{"Rep171"};
		Assert.assertEquals(ArrayUtils.toString(expected), ArrayUtils.toString(ubipharmCommandStringSequence.arrayRepresentation()));
	}
	@Test
	public void testExport(){
		CsvImportExportUtil importExportUtil = new CsvImportExportUtil();
		List<AbstractUbipharmLigneWrapper> lignesToExport = constructLigneToExport();
		importExportUtil.setLignesToExport(lignesToExport);
		System.out.println(lignesToExport);
		importExportUtil.exportCommandsToUbipharmCsv();
	}
	@Test
	public void testDateFormat(){
		String format = PharmaDateUtil.format(loadCommandeFournisseur().getDateLimiteLivraison()	,"yyyy-MM-dd");
		System.out.println("Date : "+format);
	}
	@Test
	public void mockAResponseFile() throws IOException{
		String source = "responseMock"+RandomStringUtils.randomNumeric(3)+".csv";
		FileWriter fileWriter = new FileWriter(source);
		String backLine = "\n";
		fileWriter.write("R"+RandomStringUtils.randomAlphabetic(8)+backLine);
		fileWriter.write("PC" +backLine);
		fileWriter.write("C"+RandomStringUtils.randomNumeric(4)+"R"+RandomStringUtils.randomNumeric(7)+backLine);
		for (int i = 0; i < 4; i++) {
			String str = "K"+CipMgenerator.formatNumber(""+RandomStringUtils.randomNumeric(2), 4)+"C1"+""
						+StringUtils.leftPad(RandomStringUtils.randomAlphabetic(30),50 )+"R"+RandomStringUtils.randomAlphanumeric(4)+""
						+"C1"+StringUtils.leftPad(RandomStringUtils.randomAlphanumeric(12), 49)+""+backLine;
			System.out.println("size : "+str.length());
			fileWriter.write(str);
		}
		fileWriter.write("E"+RandomStringUtils.randomAlphanumeric(4)+"D"+new String("Toute l erreure ici")+backLine);
		fileWriter.close();
		copyFile(source, "/tools/ubipharm/receptions");
		deleteFile(source);
	}
	@Test
	public void testImport() throws IOException{
		CsvImportExportUtil csvImportExportUtil = new CsvImportExportUtil();
		csvImportExportUtil.readCsvFile("/tools/ubipharm/receptions/responseMock062.csv");
	}

	@Test
	public void loadReceptionLines() throws IOException{
		String fileName = "/tools/ubipharm/receptions/responseMock062.csv";
		File file = new File(fileName);
		List readLines = FileUtils.readLines(file);
		System.out.println(readLines);
		System.out.println(readLines.size());
	}
	public void copyFile(String source, String destination) throws IOException{
		FileUtils.copyFileToDirectory(new File(source), new File(destination));
	}
	public void deleteFile(String fileName){
		new File(fileName).delete();
	}
	public List<AbstractUbipharmLigneWrapper> constructLigneToExport(){
		List<AbstractUbipharmLigneWrapper> lignesToExport = new ArrayList<AbstractUbipharmLigneWrapper>();
		DistributorLigne distributorLigne = new DistributorLigne(1, 50);
		distributorLigne.setLigneIdentifier(new UbipharmCommandStringSequence(1, 1, false, "R"));
		distributorLigne.setRepartitor(new UbipharmCommandStringSequence(2, 49, true, "Rep171"));
		distributorLigne.joinAnotherString(distributorLigne.getLigneIdentifier()).joinAnotherString(distributorLigne.getRepartitor());
		System.out.println("here");
		CommandeFournisseur nextCommandeFournisseur = loadCommandeFournisseur();
		System.out.println("and here  "+nextCommandeFournisseur.toString());
		CommandTypeLigne commandTypeLigne = convertCommand(nextCommandeFournisseur);
		commandTypeLigne.joinString(commandTypeLigne.getLigneIdentifier(),commandTypeLigne.getCommandType(),commandTypeLigne.getSeparator(),commandTypeLigne.getCommandReference());
		List<LigneCmdFournisseur> ligneCmdFournisseurs = loadLigneCommandFournisseurs(nextCommandeFournisseur);
		List<ProductItemLigne> productLigneItems = constructProductItemLignes(ligneCmdFournisseurs);
		
		lignesToExport.add(distributorLigne);
		lignesToExport.add(commandTypeLigne);
		lignesToExport.addAll(productLigneItems);
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
		List<CommandeFournisseur> commandeFournisseurs = CommandeFournisseur.findAllCommandeFournisseurs();
		if(commandeFournisseurs == null) throw new NullPointerException("Invalid command value, null value not required");
		CommandeFournisseur nextCommandeFournisseur = commandeFournisseurs.iterator().next();
		return nextCommandeFournisseur;
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
		}else if(CipType.CLAIR.equals(cipType)){
			cipTypeStringRepresentation = "C3";
		}if(CipType.EAN13.equals(cipType)){
			cipTypeStringRepresentation = "C2";
		}
		return cipTypeStringRepresentation;
	}
}

