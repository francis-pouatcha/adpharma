package org.adorsys.adpharma.beans.importExport.ubipharm;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SimpleScheduleBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandJob implements Job {
	private static Logger LOG = LoggerFactory.getLogger(CommandJob.class);
	public  static int numberOfTime;
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println("Running for ... "+numberOfTime);
		numberOfTime = numberOfTime  + 1 ;
	}
}