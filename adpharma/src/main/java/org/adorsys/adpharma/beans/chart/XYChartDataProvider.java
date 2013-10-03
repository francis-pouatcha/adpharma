package org.adorsys.adpharma.beans.chart;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;

import org.adorsys.adpharma.domain.LigneCmdClient;
import org.adorsys.adpharma.domain.LigneCmdFournisseur;
import org.adorsys.adpharma.domain.TypeCourbeGraphique;
import org.apache.commons.lang.text.StrBuilder;
import org.springframework.stereotype.Component;

import com.ibm.icu.math.BigDecimal;

/**
 * @author clovisgakam
 * use  for provide ChartData information 
 *
 */
@Component
public class XYChartDataProvider implements ChartDataProvider {

	
	private EntityManager em ;

	public XYChartDataProvider(){
		//em =LigneCmdClient.entityManager();
	}

	public EntityManager getEm() {
		return em;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}

	/* (non-Javadoc)
	 * @see org.adorsys.adpharma.beans.chart.ChartDataProvider#findChartByYearsAndMonthBetween(int, int, int, org.adorsys.adpharma.beans.chart.TypeOfChart)
	 */
	@Override
	public List<XYChartData> findChartByYearsAndMonthBetween(int year,
			int beginMonth, int endMonth, TypeCourbeGraphique typeOfChart) {
		ArrayList<XYChartData> xyChartDatas = new ArrayList<XYChartData>();
		String buildQueryForSaleChart ="";

		if(TypeCourbeGraphique.CHIFFRE_AFFAIRE == typeOfChart){
			buildQueryForSaleChart = ChartDataQueryBuilder.buildQueryForSaleChart();
		}
		if(TypeCourbeGraphique.VENTE_EN_QUANTITE == typeOfChart){
			buildQueryForSaleChart = ChartDataQueryBuilder.buildQueryForSaleProductQteByYearsAndMonthBetween();
		}
		if(TypeCourbeGraphique.NOMBRE_CLIENTS == typeOfChart){
			 buildQueryForSaleChart = ChartDataQueryBuilder.buildQueryForCustomerNumberByYearsAndMonthBetween();
		}
		if(TypeCourbeGraphique.COMMANDE_EN_QUANTITE == typeOfChart){
			 buildQueryForSaleChart = ChartDataQueryBuilder.buildQueryForOrderQteByYearsAndMonthBetween();
		}
		Query query  = em.createQuery(buildQueryForSaleChart);
		query.setParameter("y", year);
		query.setParameter("beginMonth", beginMonth);
		query.setParameter("endMonth", endMonth);
		query.setParameter("isCash", Boolean.TRUE);
		List<Object[]> resultList = query.getResultList();
		for (Object[] objects : resultList) {
			xyChartDatas.add(new XYChartData(""+objects[0], new BigDecimal(""+objects[1]).longValue()));
		}
		return xyChartDatas ;
	}

	@Override
	public List<XYChartData> findChartByYearsAndMonth(int year, int beginMonth,
			TypeCourbeGraphique typeOfChart) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<XYChartData> findChartByMonthAndDayBetween(int year,
			int beginDay, int endDay, TypeCourbeGraphique typeOfChart) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<XYChartData> findChartByYearsAndCipAndMonthBetween(int year,
			int beginMonth, int endMonth,String cip, TypeCourbeGraphique typeOfChart) {
		ArrayList<XYChartData> xyChartDatas = new ArrayList<XYChartData>();
		String buildQuery ="";
		if(TypeCourbeGraphique.CHIFFRE_AFFAIRE == typeOfChart){
			buildQuery = ChartDataQueryBuilder.buildQueryForSaleChartByYearsAndCipAndMonthBetween();
		}
		if(TypeCourbeGraphique.VENTE_EN_QUANTITE.equals(typeOfChart)){
			buildQuery = ChartDataQueryBuilder.buildQueryForSaleProductQteByCipAndYearsAndMonthBetween();
		}
		if(TypeCourbeGraphique.NOMBRE_CLIENTS == typeOfChart){
			buildQuery = ChartDataQueryBuilder.buildQueryForCustomerNumberByYearsAndMonthBetween();
		}
		if(TypeCourbeGraphique.COMMANDE_EN_QUANTITE == typeOfChart){
			buildQuery = ChartDataQueryBuilder.buildQueryForOrderQteByCipAndYearsAndMonthBetween();
		}
		

		Query query  = em.createQuery(buildQuery);
		query.setParameter("y", year);
		query.setParameter("beginMonth", beginMonth);
		query.setParameter("endMonth", endMonth);
		query.setParameter("isCash", Boolean.TRUE);
		query.setParameter("cip", cip);
		List<Object[]> resultList = query.getResultList();
		for (Object[] objects : resultList) {
			xyChartDatas.add(new XYChartData(""+objects[0], new BigDecimal(""+objects[1]).longValue()));
		}

		return xyChartDatas ;
	}

	@Override
	public XYChartData findChartByYearsAnCipAndMonth(int year,
			int beginMonth,String cip, TypeCourbeGraphique typeOfChart) {
		String buildQueryForSaleChart = "";
		XYChartData chartData =new XYChartData(""+beginMonth, Long.valueOf(0));

		if(TypeCourbeGraphique.CHIFFRE_AFFAIRE == typeOfChart){
			buildQueryForSaleChart = ChartDataQueryBuilder.buildQueryForSaleChartByYearsAndCipAndMonth();

		}
		if(TypeCourbeGraphique.VENTE_EN_QUANTITE == typeOfChart){
			buildQueryForSaleChart = ChartDataQueryBuilder.buildQueryForSaleProductQteByCipAndYearsAndMonth();

		}
		if(TypeCourbeGraphique.NOMBRE_CLIENTS == typeOfChart){
			 buildQueryForSaleChart = ChartDataQueryBuilder.buildQueryForCustomerNumberByYearsAndMonth();
			
		}
		if(TypeCourbeGraphique.COMMANDE_EN_QUANTITE == typeOfChart){
			buildQueryForSaleChart = ChartDataQueryBuilder.buildQueryForOrderQteByCipAndYearsAndMonth();
		}
		Query query  = em.createQuery(buildQueryForSaleChart);
		query.setParameter("y", year);
		query.setParameter("mth", beginMonth);
		query.setParameter("isCash", Boolean.TRUE);
		query.setParameter("cip", cip);
		List<Object[]> resultList = query.getResultList();
		if(!resultList.isEmpty()){
			chartData = new  XYChartData(""+resultList.get(0)[0], new BigDecimal(""+resultList.get(0)[1]).longValue());
		}
		return chartData ;

	}

	@Override
	public List<XYChartData> findChartByMonthAndCipAndDayBetween(int year,
			int beginDay, int endDay, TypeCourbeGraphique typeOfChart) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XYChartData findChartByMonthAndYear(int year, int month,TypeCourbeGraphique typeOfChart) {
		XYChartData chartData =new XYChartData(""+month, Long.valueOf(0));
		String buildQueryForSaleChart = "";
		if(TypeCourbeGraphique.CHIFFRE_AFFAIRE == typeOfChart){
			 buildQueryForSaleChart = ChartDataQueryBuilder.buildQueryForSaleChartByMonthAndYear();
			
		}
		if(TypeCourbeGraphique.VENTE_EN_QUANTITE == typeOfChart){
			 buildQueryForSaleChart = ChartDataQueryBuilder.buildQueryForSaleProductQteByYearsAndMonth();
			
		}
		if(TypeCourbeGraphique.NOMBRE_CLIENTS == typeOfChart){
			 buildQueryForSaleChart = ChartDataQueryBuilder.buildQueryForCustomerNumberByYearsAndMonth();
			
		}
		if(TypeCourbeGraphique.COMMANDE_EN_QUANTITE == typeOfChart){
			buildQueryForSaleChart = ChartDataQueryBuilder.buildQueryForOrderQteByYearsAndMonth();
		}
		Query query  = em.createQuery(buildQueryForSaleChart);
		query.setParameter("y", year);
		query.setParameter("mth", month);
		query.setParameter("isCash", Boolean.TRUE);
		List<Object[]> resultList = query.getResultList();
		if(!resultList.isEmpty()){
			chartData = new  XYChartData(""+resultList.get(0)[0], new BigDecimal(""+resultList.get(0)[1]).longValue());
		}
		return chartData ;
	}

}
