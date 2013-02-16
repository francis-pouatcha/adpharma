/**
 * 
 */
package org.adorsys.adpharma.tests;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.adorsys.adpharma.beans.importExport.ubipharm.CommandJob;
import org.adorsys.adpharma.beans.importExport.ubipharm.CsvImportExportUtil;
import org.adorsys.adpharma.beans.importExport.ubipharm.FileSystemScanner;
import org.adorsys.adpharma.beans.importExport.ubipharm.wrapper.AbstractUbipharmLigneWrapper;
import org.adorsys.adpharma.beans.importExport.ubipharm.wrapper.DistributorLigne;
import org.adorsys.adpharma.beans.importExport.ubipharm.wrapper.UbipharmCommandStringSequence;
import org.apache.commons.lang.ArrayUtils;
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
/**
@RunWith(value=SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:META-INF/spring/applicationContext*.xml"})
@Transactional*/
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
				JobDetail anoteherJobDetail = JobBuilder.newJob(CommandJob.class)
						.withIdentity(new JobKey("fileReceivingOne", "fileReceiving"))
						.build();
				Trigger second = TriggerBuilder
						.newTrigger()
						.withIdentity(new TriggerKey("fileReceivingJobTriggerTwo","fileReceiving"))
						.withSchedule(systemScanner.REPEAT_SECONDLY_FOREVER)
						.startNow().build();
				systemScanner.scheduler.deleteJob(systemScanner.jobDetail.getKey());
				systemScanner.scheduler.scheduleJob(anoteherJobDetail, second);
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
		importExportUtil.exportCommandsToUbipharmCsv();
	}
	private List<AbstractUbipharmLigneWrapper> constructLigneToExport(){
		List<AbstractUbipharmLigneWrapper> lignesToExport = new ArrayList<AbstractUbipharmLigneWrapper>();
		DistributorLigne distributorLigne = new DistributorLigne(1, 50);
		distributorLigne.setLigneIdentifier(new UbipharmCommandStringSequence(1, 1, false, "R"));
		distributorLigne.setRepartitor(new UbipharmCommandStringSequence(2, 49, true, "Rep171"));
		distributorLigne.joinAnotherString(distributorLigne.getLigneIdentifier()).joinAnotherString(distributorLigne.getRepartitor());
		//...construct more lignes to export
		lignesToExport.add(distributorLigne);
		//add more lignes to export
		return lignesToExport;
	}
}
