package org.adorsys.adpharma.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.adorsys.adpharma.security.SecurityUtil;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

import antlr.collections.List;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@RooJavaBean
@RooToString
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "LigneCmdFournisseur", finders = { "findLigneCmdFournisseursByCommande" })
public class LigneCmdFournisseur extends AdPharmaBaseEntity {


    private int indexLine ;
    
    private String cip;

    public String getCip() {
		return cip;
	}

	public void setCip(String cip) {
		this.cip = cip;
	}

	@ManyToOne
    private Produit produit;

	 @Temporal(TemporalType.TIMESTAMP)
	    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	    private Date dateSaisie = new Date();

    private BigInteger quantiteCommande = BigInteger.ONE;

    @NotNull
    private String agentSaisie;
    
    
    private String designation;

    public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	@ManyToOne
    private CommandeFournisseur commande;

    private BigDecimal prixAchatMin;
    
    private BigDecimal prixAVenteMin;
    
    private BigDecimal prixAchatTotal;

    @Override
    protected void internalPrePersist() {
        agentSaisie = SecurityUtil.getUserName();
        cip = produit.getCip();
        designation = produit.getDesignation();
        dateSaisie= new Date();
        calculPrixTotal();
        
    }

	protected void internalPreUpdate() {
        agentSaisie = SecurityUtil.getUserName();
        dateSaisie = new Date();
        calculPrixTotal();
	}
    @PostPersist
    public void postPersist() {
    }
    
    @PostLoad
    public void postLoad() {
        designation = produit.getDesignation();
    }

    public void protectSomeField() {
        LigneCmdFournisseur ligneCmdFournisseur = LigneCmdFournisseur.findLigneCmdFournisseur(getId());
        produit = ligneCmdFournisseur.getProduit();
        commande = ligneCmdFournisseur.getCommande();
        footPrint = ligneCmdFournisseur.getFootPrint();
    }

   

    public void IncreaseQte() {
        setQuantiteCommande(quantiteCommande.add(BigInteger.ONE));
    }
    
    public void calculPrixTotal(){
    	prixAchatTotal =  prixAchatMin.multiply(BigDecimal.valueOf(quantiteCommande.longValue()));  	
    	
    	
    }

	public BigDecimal getPrixAVenteMin() {
		return prixAVenteMin;
	}

	public void setPrixAVenteMin(BigDecimal prixAVenteMin) {
		this.prixAVenteMin = prixAVenteMin;
	}
	
	
	 public String toJson() {
	        return new JSONSerializer().exclude("*.class").serialize(this);
	    }
	    
	    public static LigneCmdFournisseur fromJsonToProduit(String json) {
	        return new JSONDeserializer<LigneCmdFournisseur>().use(null, Produit.class).deserialize(json);
	    }
	    
	    public static String toJsonArray(Collection<LigneCmdFournisseur> collection) {
	        return new JSONSerializer().exclude("*.class").serialize(collection);
	    }
	    
	   
    
    
    
}
