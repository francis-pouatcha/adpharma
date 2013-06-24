package org.adorsys.adpharma.beans.chart;

import org.apache.commons.lang.text.StrBuilder;

import com.mchange.v2.async.StrandedTaskReporting;

/**
 * @author clovisgakam
 * utility class use for build chart sql query 
 *
 */
public class ChartDataQueryBuilder {
	
	
	
	/**
	 * provide sql query for sale chart 
	 * @param year
	 * @param beginMonth
	 * @param endMonth
	 */
	
	public  static String buildQueryForSaleChart(){
		StrBuilder queryBuilder = new StrBuilder();
		queryBuilder.append("SELECT MONTH(o.dateSaisie) AS xaxis , SUM((o.quantiteCommande - o.quantiteRetourne)*(o.prixUnitaire - o.remise)) AS yaxis FROM LigneCmdClient AS o ") ;
		queryBuilder.append(" WHERE YEAR(o.dateSaisie) = :y AND o.commande.encaisse IS :isCash AND  MONTH(o.dateSaisie) BETWEEN :beginMonth AND :endMonth ") ;
		queryBuilder.append(" GROUP BY  MONTH(o.dateSaisie) ") ;
		queryBuilder.append(" ORDER BY xaxis ") ;
		return queryBuilder.toString();
	}
	
	public  static String buildQueryForSaleChartByMonthAndYear(){
		StrBuilder queryBuilder = new StrBuilder();
		queryBuilder.append("SELECT MONTH(o.dateSaisie) AS xaxis , SUM((o.quantiteCommande - o.quantiteRetourne)*(o.prixUnitaire - o.remise)) AS yaxis FROM LigneCmdClient AS o ") ;
		queryBuilder.append(" WHERE YEAR(o.dateSaisie) = :y AND o.commande.encaisse IS :isCash AND  MONTH(o.dateSaisie) =:mth ") ;
		queryBuilder.append(" GROUP BY  MONTH(o.dateSaisie) ") ;
		queryBuilder.append(" ORDER BY xaxis ") ;
		return queryBuilder.toString();
	}
	public  static String buildQueryForSaleChartByYearsAndCipAndMonthBetween(){
		StrBuilder queryBuilder = new StrBuilder();
		queryBuilder.append("SELECT MONTH(o.dateSaisie) AS xaxis , SUM((o.quantiteCommande - o.quantiteRetourne)*(o.prixUnitaire - o.remise)) AS yaxis FROM LigneCmdClient AS o ") ;
		queryBuilder.append(" WHERE YEAR(o.dateSaisie) = :y AND o.commande.encaisse IS :isCash AND  MONTH(o.dateSaisie) BETWEEN :beginMonth AND :endMonth AND o.cip =:cip") ;
		queryBuilder.append(" GROUP BY  MONTH(o.dateSaisie) ") ;
		queryBuilder.append(" ORDER BY xaxis ") ;
		return queryBuilder.toString();
	}
	public  static String buildQueryForSaleChartByYearsAndCipAndMonth(){
		StrBuilder queryBuilder = new StrBuilder();
		queryBuilder.append("SELECT MONTH(o.dateSaisie) AS xaxis , SUM((o.quantiteCommande - o.quantiteRetourne)*(o.prixUnitaire - o.remise)) AS yaxis FROM LigneCmdClient AS o ") ;
		queryBuilder.append(" WHERE YEAR(o.dateSaisie) = :y AND o.commande.encaisse IS :isCash AND  MONTH(o.dateSaisie) =:mth  AND o.cip =:cip") ;
		queryBuilder.append(" GROUP BY  MONTH(o.dateSaisie) ") ;
		queryBuilder.append(" ORDER BY xaxis ") ;
		return queryBuilder.toString();
	}
	
	public static String buildQueryForSaleProductQteByYearsAndMonthBetween(){
		StrBuilder queryBuilder = new StrBuilder();
		queryBuilder.append("SELECT MONTH(o.dateSaisie) AS xaxis , SUM(o.quantiteCommande - o.quantiteRetourne) AS yaxis FROM LigneCmdClient AS o ") ;
		queryBuilder.append(" WHERE YEAR(o.dateSaisie) = :y AND o.commande.encaisse IS :isCash AND  MONTH(o.dateSaisie) BETWEEN :beginMonth AND :endMonth") ;
		queryBuilder.append(" GROUP BY  MONTH(o.dateSaisie) ") ;
		queryBuilder.append(" ORDER BY xaxis ") ;
		return queryBuilder.toString();
	}
	public static String buildQueryForSaleProductQteByYearsAndMonth(){
		StrBuilder queryBuilder = new StrBuilder();
		queryBuilder.append("SELECT MONTH(o.dateSaisie) AS xaxis , SUM(o.quantiteCommande - o.quantiteRetourne) AS yaxis FROM LigneCmdClient AS o ") ;
		queryBuilder.append(" WHERE YEAR(o.dateSaisie) = :y AND o.commande.encaisse IS :isCash AND  MONTH(o.dateSaisie) =:mth ") ;
		queryBuilder.append(" GROUP BY  MONTH(o.dateSaisie) ") ;
		queryBuilder.append(" ORDER BY xaxis ") ;
		return queryBuilder.toString();
	}
	
	public static String buildQueryForSaleProductQteByCipAndYearsAndMonthBetween(){
		StrBuilder queryBuilder = new StrBuilder();
		queryBuilder.append("SELECT MONTH(o.dateSaisie) AS xaxis , SUM(o.quantiteCommande - o.quantiteRetourne) AS yaxis FROM LigneCmdClient AS o ") ;
		queryBuilder.append(" WHERE YEAR(o.dateSaisie) = :y AND o.commande.encaisse IS :isCash AND  MONTH(o.dateSaisie) BETWEEN :beginMonth AND :endMonth AND o.cip =:cip") ;
		queryBuilder.append(" GROUP BY  MONTH(o.dateSaisie) ") ;
		queryBuilder.append(" ORDER BY xaxis ") ;
		return queryBuilder.toString();
	}
	
	public static String buildQueryForSaleProductQteByCipAndYearsAndMonth(){
		StrBuilder queryBuilder = new StrBuilder();
		queryBuilder.append("SELECT MONTH(o.dateSaisie) AS xaxis , SUM(o.quantiteCommande - o.quantiteRetourne) AS yaxis FROM LigneCmdClient AS o ") ;
		queryBuilder.append(" WHERE YEAR(o.dateSaisie) = :y AND o.commande.encaisse IS :isCash AND  MONTH(o.dateSaisie) =:mth AND o.cip =:cip ") ;
		queryBuilder.append(" GROUP BY  MONTH(o.dateSaisie) ") ;
		queryBuilder.append(" ORDER BY xaxis ") ;
		return queryBuilder.toString();
	}
	
	
	
	public static String buildQueryForCustomerNumberByYearsAndMonthBetween(){
		StrBuilder queryBuilder = new StrBuilder();
		queryBuilder.append("SELECT MONTH(o.dateCreation) AS xaxis , COUNT(o) AS yaxis FROM CommandeClient AS o ") ;
		queryBuilder.append(" WHERE YEAR(o.dateCreation) = :y AND o.encaisse IS :isCash AND  MONTH(o.dateCreation)  BETWEEN :beginMonth AND :endMonth  ") ;
		queryBuilder.append(" GROUP BY  MONTH(o.dateCreation) ") ;
		queryBuilder.append(" ORDER BY xaxis ")  ;
		return queryBuilder.toString();
	}
	
	public static String buildQueryForCustomerNumberByYearsAndMonth(){
		StrBuilder queryBuilder = new StrBuilder();
		queryBuilder.append("SELECT MONTH(o.dateCreation) AS xaxis , COUNT(o) AS yaxis FROM CommandeClient AS o ") ;
		queryBuilder.append(" WHERE YEAR(o.dateCreation) = :y AND o.encaisse IS :isCash AND  MONTH(o.dateCreation) =:mth  ") ;
		queryBuilder.append(" GROUP BY  MONTH(o.dateCreation) ") ;
		queryBuilder.append(" ORDER BY xaxis ") ;
		return queryBuilder.toString();
	}
	
	public static String buildQueryForOrderQteByYearsAndMonth(){
		StrBuilder queryBuilder = new StrBuilder();
		queryBuilder.append("SELECT MONTH(o.dateSaisie) AS xaxis , SUM(o.quantiteAprovisione) AS yaxis FROM LigneApprovisionement AS o ") ;
		queryBuilder.append(" WHERE YEAR(o.dateSaisie) = :y AND o.approvisionement.cloturer IS :isCash AND  MONTH(o.dateSaisie) =:mth  ") ;
		queryBuilder.append(" GROUP BY  MONTH(o.dateSaisie) ") ;
		queryBuilder.append(" ORDER BY xaxis ") ;
		return queryBuilder.toString();
	}
	public static String buildQueryForOrderQteByYearsAndMonthBetween(){
		StrBuilder queryBuilder = new StrBuilder();
		queryBuilder.append("SELECT MONTH(o.dateSaisie) AS xaxis , SUM(o.quantiteAprovisione) AS yaxis FROM LigneApprovisionement AS o ") ;
		queryBuilder.append(" WHERE YEAR(o.dateSaisie) = :y AND o.approvisionement.cloturer IS :isCash AND  MONTH(o.dateSaisie) BETWEEN :beginMonth AND :endMonth  ") ;
		queryBuilder.append(" GROUP BY  MONTH(o.dateSaisie) ") ;
		queryBuilder.append(" ORDER BY xaxis ") ;
		return queryBuilder.toString();
	}
	
	public static String buildQueryForOrderQteByCipAndYearsAndMonth(){
		StrBuilder queryBuilder = new StrBuilder();
		queryBuilder.append("SELECT MONTH(o.dateSaisie) AS xaxis , SUM(o.quantiteAprovisione) AS yaxis FROM LigneApprovisionement AS o ") ;
		queryBuilder.append(" WHERE YEAR(o.dateSaisie) = :y AND o.approvisionement.cloturer IS :isCash AND  MONTH(o.dateSaisie) =:mth  AND o.cip =:cip") ;
		queryBuilder.append(" GROUP BY  MONTH(o.dateSaisie) ") ;
		queryBuilder.append(" ORDER BY xaxis ") ;
		return queryBuilder.toString();
	}
	public static String buildQueryForOrderQteByCipAndYearsAndMonthBetween(){
		StrBuilder queryBuilder = new StrBuilder();
		queryBuilder.append("SELECT MONTH(o.dateSaisie) AS xaxis , SUM(o.quantiteAprovisione) AS yaxis FROM LigneApprovisionement AS o ") ;
		queryBuilder.append(" WHERE YEAR(o.dateSaisie) = :y AND o.approvisionement.cloturer IS :isCash AND  MONTH(o.dateSaisie) BETWEEN :beginMonth AND :endMonth AND o.cip =:cip ") ;
		queryBuilder.append(" GROUP BY  MONTH(o.dateSaisie) ") ;
		queryBuilder.append(" ORDER BY xaxis ") ;
		return queryBuilder.toString();
	}
	
	
	

	
	
	

}
