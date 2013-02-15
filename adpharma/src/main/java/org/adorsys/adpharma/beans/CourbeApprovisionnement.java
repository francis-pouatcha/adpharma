package org.adorsys.adpharma.beans;

import java.math.BigInteger;

import org.springframework.roo.addon.javabean.RooJavaBean;


@RooJavaBean
public class CourbeApprovisionnement {
	
	private BigInteger paTotal;
	
	private Integer mois;
	
	private Integer annee;
	
	
	public CourbeApprovisionnement(){
		paTotal= BigInteger.ZERO;
	}
	
	public CourbeApprovisionnement(BigInteger paTotal, Integer mois, Integer annee){
		if(paTotal==null){
			this.paTotal= BigInteger.ZERO;
		}else {
			this.paTotal= paTotal;
		}
		this.mois= mois;
		this.annee=annee;
	}
	
	@Override
	public String toString(){
		return "["+paTotal+ ","+mois+","+annee+"]";
	}
	

}
