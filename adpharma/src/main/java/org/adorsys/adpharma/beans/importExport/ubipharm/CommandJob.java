package org.adorsys.adpharma.beans.importExport.ubipharm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.aspectj.util.FileUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandJob implements Job {
	private static Logger LOG = LoggerFactory.getLogger(CommandJob.class);
	public  static int numberOfTime;
	private static List<String> oldFiles = new ArrayList<String>();
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		File fileDir = new File(CsvImportExportUtil.RECEPTION_FOLDER_PATH);
		String[] fileNames = FileUtil.listFiles(fileDir);
		for (String fileName : fileNames) {
			if(isNewFile(fileName) ){
				CsvImportExportUtil csvImportExportUtil = new CsvImportExportUtil();
				try {
					LOG.warn("Start Import Of : "+fileName+" ...");
					csvImportExportUtil.readCsvFile(CsvImportExportUtil.RECEPTION_FOLDER_PATH+""+fileName);
				} catch (Exception e) {
					e.printStackTrace();
				}
				oldFiles.add(fileName);
				LOG.warn("... "+fileName+", Import Finished");
			}
		}
	}
	private boolean isNewFile(String fileName) {
		return oldFiles.contains(fileName) == false;
	}
}