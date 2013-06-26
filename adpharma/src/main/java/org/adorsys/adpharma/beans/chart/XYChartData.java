package org.adorsys.adpharma.beans.chart;

import java.util.Collection;

import org.adorsys.adpharma.domain.LigneApprovisionement;

import flexjson.JSONSerializer;

/**
 * @author clovisgakam
 *
 */
public class XYChartData {
	
	private String xAxisValue ;
	
	private Long yAxisValue ;

	public String getxAxisValue() {
		return xAxisValue;
	}

	public void setxAxisValue(String xAxisValue) {
		this.xAxisValue = xAxisValue;
	}

	public Long getyAxisValue() {
		return yAxisValue;
	}

	public void setyAxisValue(Long yAxisValue) {
		this.yAxisValue = yAxisValue;
	}

	public XYChartData(String xAxisValue, Long yAxisValue) {
		super();
		this.xAxisValue = xAxisValue;
		this.yAxisValue = yAxisValue;
	}

	@Override
	public String toString() {
		return "XYChartData [xAxisValue=" + xAxisValue + ", yAxisValue="
				+ yAxisValue + "]";
	}
	
	
	public String toJson() {
		return new JSONSerializer().include("xAxisValue","yAxisValue").exclude("*","*.class").serialize(this);
	}
	
	public static String toJsonArray(Collection<XYChartData> collection) {
		return new JSONSerializer().include("xAxisValue","yAxisValue").exclude("*","*.class").serialize(collection);
	}
	

}
