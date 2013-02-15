/**
 * 
 */
package org.adorsys.adpharma.beans.importExport.ubipharm;

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
public class FileSystemScanner {
	public static SimpleScheduleBuilder REPEAT_SECONDLY_FOREVER = SimpleScheduleBuilder.repeatSecondlyForever(10);
	public static Logger LOG = LoggerFactory.getLogger(FileSystemScanner.class);
	public static Scheduler scheduler = null;
	public static JobDetail jobDetail;
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

	public static void main(String... args) throws SchedulerException {
		System.out.println("Starting ...");
		FileSystemScanner systemScanner = new FileSystemScanner();
		systemScanner.startScan();
		boolean boucle = true;
		while(boucle){
			if(CommandJob.numberOfTime == 6){
				System.out.println("quota riched");
//				scheduler.shutdown(true);
				FileSystemScanner.REPEAT_SECONDLY_FOREVER = SimpleScheduleBuilder.repeatSecondlyForever(1);
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
}
