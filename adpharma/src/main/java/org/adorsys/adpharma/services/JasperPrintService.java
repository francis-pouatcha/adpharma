package org.adorsys.adpharma.services;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import org.adorsys.adpharma.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;

@Service
public class JasperPrintService {
	@Autowired
	DataSource dataSource ;

	/**
	 * use to build pdf document trow jasper report 
	 * @param parameters map which content all parameters need to build document 
	 * @param response HttpServletResponse to which the response will be send 
	 * @param jasperFile the .jrxml file path to use 
	 * @throws Exception
	 */
	public void printDocument( Map parameters,HttpServletResponse response ,String jasperFile) throws Exception {
		if(jasperFile.isEmpty() || jasperFile == null || response == null) throw new RuntimeException("jasperFile or response may not be null ! ");
		Connection connection = DataSourceUtils.getConnection(dataSource);
		JasperReport jasperReport;
		JasperPrint jasperPrint;
		parameters.put("PrintBy", SecurityUtil.getUserName());
		parameters.put("user", SecurityUtil.getUserName());
		jasperReport = JasperCompileManager.compileReport(jasperFile);
		jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,connection);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, baos);  
		response.setContentLength(baos.size());
		ServletOutputStream out1 = response.getOutputStream();
		baos.writeTo(out1);
		out1.flush();
	}
	
	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
