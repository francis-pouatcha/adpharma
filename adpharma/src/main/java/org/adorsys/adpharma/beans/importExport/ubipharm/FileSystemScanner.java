/**
 * 
 */
package org.adorsys.adpharma.beans.importExport.ubipharm;

import java.util.ArrayList;
import java.util.List;

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
	public SimpleScheduleBuilder REPEAT_SECONDLY_FOREVER = SimpleScheduleBuilder.repeatSecondlyForever(18000);
	public Logger LOG = LoggerFactory.getLogger(FileSystemScanner.class);
	public Scheduler scheduler = null;
	public JobDetail jobDetail;
	public static boolean FIST_SCAN_STARTED ;

	public static List<String> oldFiles = new ArrayList<String>();
	
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
}
