package org.adorsys.adpharma.beans.chart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.TypeCourbeGraphique;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.apache.commons.lang.StringUtils;

import flexjson.JSONSerializer;

/**
 * class ChartDataViewModelaViewModel 
 * @author clovisgakam
 * 
 */
public class ChartDataViewModel {
	public TypeCourbeGraphique typeOfChart ;
	public int firstYear = 2012;
	public int secondYear = 2012;
	public int minMonth = 1 ;
	public int maxMonth = 12 ;
	public String productCip;
	public List<String> xAxisValues = new ArrayList<String>() ;
	public List<Long> firstSerieValues = new ArrayList<Long>() ;
	public List<Long> secondSerieValues = new ArrayList<Long>() ;
	public String chartTitle ;
	
	
	
	public String toJson() {
		return new JSONSerializer().include("firstYear","secondYear","minMonth","maxMonth","productCip","xAxisValues","firstSerieValues","secondSerieValues","chartTitle").exclude("*","*.class").serialize(this);
	}
	
	public static String toJsonArray(Collection<XYChartData> collection) {
		return new JSONSerializer().include("firstYear","secondYear","minMonth","maxMonth","productCip","xAxisValues","firstSerieValues","secondSerieValues","chartTitle").exclude("*","*.class").serialize(collection);
	}
	
	
	public boolean isOnlyForOneCip(){
		return !StringUtils.isEmpty(productCip);
	}
	
    public boolean isForSameYears(){
    	return firstYear == secondYear ;
    }
    
    public void generateXaxisValues(){
    	if(isForSameYears()){
    		xAxisValues.add(PharmaDateUtil.parseToFrenchMonth(minMonth));
    		xAxisValues.add(PharmaDateUtil.parseToFrenchMonth(maxMonth));
    		
    	}else {
    		for (int i = minMonth; i <= maxMonth; i++) {
    			xAxisValues.add(PharmaDateUtil.parseToFrenchMonth(i));
    		}
		}
    	
    }
    public void generateFirstSerieValues(List<XYChartData> chartDatas){
    	if(isForSameYears()){
    		for (XYChartData xyChartData : chartDatas) {
    			firstSerieValues.add(xyChartData.getyAxisValue());
			}
    		
    	}else {
    		for (int i = minMonth; i <= maxMonth; i++) {
    			XYChartData xyChartByMonth = getXYChartByMonth(chartDatas, i);
    			firstSerieValues.add(xyChartByMonth.getyAxisValue());
    		}
		}
    }
    public void generateSecondSerieValues(List<XYChartData> chartDatas){
    	if(isForSameYears()){
    		for (XYChartData xyChartData : chartDatas) {
    			secondSerieValues.add(xyChartData.getyAxisValue());
			}
    		
    	}else {
    		for (int i = minMonth; i <= maxMonth; i++) {
    			XYChartData xyChartByMonth = getXYChartByMonth(chartDatas, i);
    			secondSerieValues.add(xyChartByMonth.getyAxisValue());
    		}
		}
    	
    }
    
    public void generateChartTitle(){
    	 chartTitle = "Graphes Compares Des "+typeOfChart.name() ;
    	 if(isOnlyForOneCip()) chartTitle = chartTitle+" ( "+getProductName()+" )";
    }
    
    public XYChartData getXYChartByMonth(List<XYChartData> chartDatas ,int month){
    	XYChartData xyChartData2 = new XYChartData(Long.valueOf(month).toString(), Long.valueOf(0));
    	for (XYChartData xyChartData : chartDatas) {
    		if(Long.valueOf(xyChartData.getxAxisValue()).intValue() == month) return xyChartData ;
    		if(Long.valueOf(xyChartData.getxAxisValue()).intValue() > month) return xyChartData2 ;
		}
    	return xyChartData2;
    }
    
    public String getProductName(){
    	List<Produit> resultList = Produit.findProduitsByCip(productCip).getResultList();
    	if(!resultList.isEmpty()) return resultList.iterator().next().getDesignation();
    	return "";
    }

	public TypeCourbeGraphique getTypeOfChart() {
		return typeOfChart;
	}

	public void setTypeOfChart(TypeCourbeGraphique typeOfChart) {
		this.typeOfChart = typeOfChart;
	}

	public int getFirstYear() {
		return firstYear;
	}

	public void setFirstYear(int firstYear) {
		this.firstYear = firstYear;
	}

	public int getSecondYear() {
		return secondYear;
	}

	public void setSecondYear(int secondYear) {
		this.secondYear = secondYear;
	}

	public int getMinMonth() {
		return minMonth;
	}

	public void setMinMonth(int minMonth) {
		this.minMonth = minMonth;
	}

	public int getMaxMonth() {
		return maxMonth;
	}

	public void setMaxMonth(int maxMonth) {
		this.maxMonth = maxMonth;
	}

	public String getProductCip() {
		return productCip;
	}

	public void setProductCip(String productCip) {
		this.productCip = productCip;
	}

	public List<String> getxAxisValues() {
		return xAxisValues;
	}

	public void setxAxisValues(List<String> xAxisValues) {
		this.xAxisValues = xAxisValues;
	}

	public List<Long> getFirstSerieValues() {
		return firstSerieValues;
	}

	public void setFirstSerieValues(List<Long> firstSerieValues) {
		this.firstSerieValues = firstSerieValues;
	}

	public List<Long> getSecondSerieValues() {
		return secondSerieValues;
	}

	public void setSecondSerieValues(List<Long> secondSerieValues) {
		this.secondSerieValues = secondSerieValues;
	}

	public String getChartTitle() {
		return chartTitle;
	}

	public void setChartTitle(String chartTitle) {
		this.chartTitle = chartTitle;
	}
    
    
}
