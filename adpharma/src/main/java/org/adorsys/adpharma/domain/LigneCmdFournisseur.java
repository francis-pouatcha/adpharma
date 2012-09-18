package org.adorsys.adpharma.domain;

import java.math.BigInteger;
import java.util.Date;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.utils.CipMgenerator;
import org.adorsys.adpharma.utils.Contract;
import org.adorsys.adpharma.utils.NumberGenerator;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import antlr.collections.List;
import java.math.BigDecimal;

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
    
    private BigDecimal prixAchatTotal;

    @Override
    protected void internalPrePersist() {
        agentSaisie = SecurityUtil.getUserName();
        cip = produit.getCip();
        designation = produit.getDesignation();
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
    
}
