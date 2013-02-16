/**
 * 
 */
package org.adorsys.adpharma.tests;

import junit.framework.Assert;

import org.adorsys.adpharma.beans.importExport.ubipharm.CommandJob;
import org.adorsys.adpharma.beans.importExport.ubipharm.FileSystemScanner;
import org.adorsys.adpharma.beans.importExport.ubipharm.wrapper.DistributorLigne;
import org.adorsys.adpharma.beans.importExport.ubipharm.wrapper.UbipharmCommandStringSequence;
import org.adorsys.adpharma.beans.importExport.ubipharm.wrapper.WorkTypeLigne;
import org.junit.Test;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author w2b
 *
 */
public class UbipharmTests {
	public SimpleScheduleBuilder REPEAT_SECONDLY_FOREVER = SimpleScheduleBuilder.repeatSecondlyForever(10);
	public Logger LOG = LoggerFactory.getLogger(FileSystemScanner.class);
	public Scheduler scheduler = null;
	public JobDetail jobDetail;
	public void startScan() throws SchedulerException {
		StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();
		 scheduler = schedulerFactory.getScheduler();
		jobDetail = JobBuilder.newJob(CommandJob.class)
				.withIdentity(new JobKey("fileReceivingOne", "fileReceiving"))
				.build();
		Trigger trigger = TriggerBuilder
				.newTrigger()
				.withIdentity(new TriggerKey("fileReceivingJobTrigger","fileReceiving"))
				.withSchedule(REPEAT_SECONDLY_FOREVER)
				.startNow().build();
		scheduler.scheduleJob(jobDetail, trigger);
		scheduler.start();
	}
	@Test
	public void testScan() throws SchedulerException {
		System.out.println("Starting ...");
		this.startScan();
		boolean boucle = true;
		while(boucle){
			if(CommandJob.numberOfTime == 6){
				System.out.println("quota riched");
				REPEAT_SECONDLY_FOREVER = SimpleScheduleBuilder.repeatSecondlyForever(1);
				JobDetail anoteherJobDetail = JobBuilder.newJob(CommandJob.class)
						.withIdentity(new JobKey("fileReceivingOne", "fileReceiving"))
						.build();
				Trigger second = TriggerBuilder
						.newTrigger()
						.withIdentity(new TriggerKey("fileReceivingJobTriggerTwo","fileReceiving"))
						.withSchedule(REPEAT_SECONDLY_FOREVER)
						.startNow().build();
				scheduler.deleteJob(jobDetail.getKey());
				scheduler.scheduleJob(anoteherJobDetail, second);
				scheduler.start();
				System.out.println("The second scheduler has started");
				boucle = false;
			}
		}
	}
	@Test
	public void testDistributorLigneGeneration(){
		DistributorLigne distributorLigne = new DistributorLigne(1, 50);
		distributorLigne.setLigneIdentifier(new UbipharmCommandStringSequence(1, 1, false, "R"));
		distributorLigne.setRepartitor(new UbipharmCommandStringSequence(2, 49, true, "Rep171"));
		distributorLigne.joinAnotherString(distributorLigne.getLigneIdentifier()).joinAnotherString(distributorLigne.getRepartitor());
		
		Assert.assertEquals(49, distributorLigne.getStringValue().length());
	}
}
