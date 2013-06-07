package org.adorsys.adpharma.domain;

import java.util.Date;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.adorsys.adpharma.utils.DateConfig;
import org.springframework.format.annotation.DateTimeFormat;

public class Statistic {

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	private Date dateDebut ;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	private Date dateFin ;
	private String statisticOfSale ;
	private String cip ;

	@Enumerated(EnumType.STRING)
	private Periode periode;

	@Enumerated(EnumType.STRING)
	private TypeCourbeGraphique typeCourbe;

	public Date getDateDebut() {
		return dateDebut;
	}

	public void setDateDebut(Date dateDebut) {
		this.dateDebut = dateDebut;
	}

	public Date getDateFin() {
		return dateFin;
	}

	public void setDateFin(Date dateFin) {
		this.dateFin = dateFin;
	}

	public Periode getPeriode() {
		return periode;
	}

	public void setPeriode(Periode periode) {
		this.periode = periode;
	}

	public TypeCourbeGraphique getTypeCourbe() {
		return typeCourbe;
	}

	public void setTypeCourbe(TypeCourbeGraphique typeCourbe) {
		this.typeCourbe = typeCourbe;
	}

	public String getStatisticOfSale() {
		return statisticOfSale;
	}

	public void setStatisticOfSale(String statisticOfSale) {
		this.statisticOfSale = statisticOfSale;
		Periode annuel = Periode.ANNUEL;
	}

	public String getCip() {
		return cip;
	}

	public void setCip(String cip) {
		this.cip = cip;
	}

	public  Date getNextDate(Date date){
		if(Periode.JOURNALIER.equals(periode))return DateConfig.getNextDay(date);
		if(Periode.HEBDOMADAIRE.equals(periode))return DateConfig.getNextWeek(date);
		if(Periode.MENSUEL.equals(periode))return DateConfig.getNextMonth(date);
		if(Periode.ANNUEL.equals(periode))return DateConfig.getNextYear(date);
		return date ;
	}

}
