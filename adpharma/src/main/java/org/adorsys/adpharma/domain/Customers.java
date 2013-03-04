package org.adorsys.adpharma.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

public class Customers {
	
	private Long id;
	
	private String numero;
	
	private String nomClient;
	
	private BigDecimal montant;
	
	private BigDecimal avance;
	
	private BigDecimal reste;
	
	@Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd-MM-yyyy hh:mm")
	private Date dateCreation;
	
	
	public Customers(){
		montant= BigDecimal.ZERO;
		avance= BigDecimal.ZERO;
		reste= BigDecimal.ZERO;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getNumero() {
		return numero;
	}


	public void setNumero(String numero) {
		this.numero = numero;
	}


	public String getNomClient() {
		return nomClient;
	}


	public void setNomClient(String nomClient) {
		this.nomClient = nomClient;
	}


	public BigDecimal getMontant() {
		return montant;
	}


	public void setMontant(BigDecimal montant) {
		this.montant = montant;
	}


	public BigDecimal getAvance() {
		return avance;
	}


	public void setAvance(BigDecimal avance) {
		this.avance = avance;
	}


	public BigDecimal getReste() {
		return reste;
	}


	public void setReste(BigDecimal reste) {
		this.reste = reste;
	}


	public Date getDateCreation() {
		return dateCreation;
	}


	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}
	
	
	
	
	
	

}
