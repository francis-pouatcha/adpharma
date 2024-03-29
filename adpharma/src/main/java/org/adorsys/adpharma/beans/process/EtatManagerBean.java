package org.adorsys.adpharma.beans.process;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.adorsys.adpharma.domain.Filiale;
import org.adorsys.adpharma.domain.PharmaUser;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.Rayon;
import org.adorsys.adpharma.domain.TypeDecaissement;
import org.adorsys.adpharma.domain.TypeMouvement;
import org.adorsys.adpharma.domain.TypeOpCaisse;
import org.adorsys.adpharma.domain.TypeSortieProduit;
import org.springframework.format.annotation.DateTimeFormat;


public class EtatManagerBean {

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	private Date dateDebut ;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	private Date dateFin ;

	private PharmaUser user ;

	private Rayon rayon ;

	private Filiale filiale ;

	private BigInteger  value ;

	private TypeSortieProduit typeSortieProduit ;


	/**
	 * Attributs pour l'etat des mouvements de stock par cip.
	 */
	private String cip;

	private String designation;


	TypeMouvement typeMouvement ;

	TypeOpCaisse typeOperation;

	private Produit produit ;


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

	public BigInteger getValue() {
		return value;
	}

	public void setValue(BigInteger value) {
		this.value = value;
	}

	public PharmaUser getUser() {
		return user;
	}

	public void setUser(PharmaUser user) {
		this.user = user;
	}

	public Rayon getRayon() {
		return rayon;
	}

	public void setRayon(Rayon rayon) {
		this.rayon = rayon;
	}

	public Filiale getFiliale() {
		return filiale;
	}

	public void setFiliale(Filiale filiale) {
		this.filiale = filiale;
	}

	public TypeMouvement getTypeMouvement() {
		return typeMouvement;
	}

	public void setTypeMouvement(TypeMouvement typeMouvement) {
		this.typeMouvement = typeMouvement;
	}

	public Produit getProduit() {
		return produit;
	}

	public void setProduit(Produit produit) {
		this.produit = produit;
	}

	public String getCip() {
		return cip;
	}

	public String getDesignation() {
		return designation;
	}

	public void setCip(String cip) {
		this.cip = cip;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public TypeOpCaisse getTypeOperation() {
		return typeOperation;
	}

	public void setTypeOperation(TypeOpCaisse typeOperation) {
		this.typeOperation = typeOperation;
	}

	public TypeSortieProduit getTypeSortieProduit() {
		return typeSortieProduit;
	}

	public void setTypeSortieProduit(TypeSortieProduit typeSortieProduit) {
		this.typeSortieProduit = typeSortieProduit;
	}



}
