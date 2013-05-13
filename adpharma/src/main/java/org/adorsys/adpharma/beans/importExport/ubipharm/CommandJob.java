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
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		File fileDir = new File(CsvImportExportUtil.getReceptionFolder());
		String[] fileNames = FileUtil.listFiles(fileDir);
		for (String fileName : fileNames) {
			if(!FileSystemScanner.oldFiles.contains(fileName)){
				CsvImportExportUtil csvImportExportUtil = new CsvImportExportUtil();
				try {
					LOG.warn("Start Import Of : "+fileName+" ...");
					csvImportExportUtil.readCsvFile(CsvImportExportUtil.getReceptionFolder()+""+fileName);
					FileSystemScanner.oldFiles.add(fileName);
				} catch (Exception e) {
					LOG.error("",e);
					FileSystemScanner.oldFiles.add(fileName);
					e.printStackTrace();
				}
				LOG.warn("... "+fileName+", Import Finished");
			}
		}
	}
}