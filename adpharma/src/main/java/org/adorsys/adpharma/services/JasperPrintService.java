package org.adorsys.adpharma.services;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;

import org.adorsys.adpharma.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;

@Service
public class JasperPrintService
{

	@Autowired
    DataSource dataSource;

    public JasperPrintService()
    {
    }

    public void printDocument(Map parameters, HttpServletResponse response, String jasperFile)
        throws Exception
    {
        if(jasperFile.isEmpty() || jasperFile == null || response == null)
        {
            throw new RuntimeException("jasperFile or response may not be null ! ");
        } else
        {
            java.sql.Connection connection = DataSourceUtils.doGetConnection(dataSource);
            parameters.put("PrintBy", SecurityUtil.getUserName());
            parameters.put("user", SecurityUtil.getUserName());
            net.sf.jasperreports.engine.JasperReport jasperReport = JasperCompileManager.compileReport(jasperFile);
            net.sf.jasperreports.engine.JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, baos);
            response.setContentLength(baos.size());
            ServletOutputStream out1 = response.getOutputStream();
            baos.writeTo(out1);
            out1.flush();
            DataSourceUtils.doReleaseConnection(connection, dataSource);
            return;
        }
    }

    public DataSource getDataSource()
    {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }
}