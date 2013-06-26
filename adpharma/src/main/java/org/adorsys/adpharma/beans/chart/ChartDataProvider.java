package org.adorsys.adpharma.beans.chart;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.adorsys.adpharma.domain.TypeCourbeGraphique;

/**
 * @author clovisgakam
 * 
 */
public interface ChartDataProvider {
	
	public List<XYChartData> findChartByYearsAndMonthBetween(int year , int beginMonth ,int endMonth , TypeCourbeGraphique typeOfChart);
	
	public List<XYChartData> findChartByYearsAndMonth(int year , int beginMonth, TypeCourbeGraphique typeOfChart);
	
	public List<XYChartData> findChartByMonthAndDayBetween(int year , int beginDay ,int endDay , TypeCourbeGraphique typeOfChart);
	
    public List<XYChartData> findChartByYearsAndCipAndMonthBetween(int year , int beginMonth ,int endMonth ,String cip, TypeCourbeGraphique typeOfChart);
	
	public XYChartData findChartByYearsAnCipAndMonth(int year , int beginMonth,String cip, TypeCourbeGraphique typeOfChart);
	
	public List<XYChartData> findChartByMonthAndCipAndDayBetween(int year , int beginDay ,int endDay , TypeCourbeGraphique typeOfChart);
	
	public XYChartData findChartByMonthAndYear(int year , int month ,TypeCourbeGraphique typeOfChart);
	
	
	
	

}
