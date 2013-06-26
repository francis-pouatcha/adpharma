package org.adorsys.adpharma.domain;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.adorsys.adpharma.domain.CommandeClient;
import javax.validation.constraints.NotNull;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.PostPersist;
import javax.persistence.Query;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.utils.CipMgenerator;
import org.adorsys.adpharma.utils.NumberGenerator;
import org.springframework.roo.addon.json.RooJson;

import flexjson.JSONSerializer;

@RooJavaBean
@RooToString
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "LigneCmdClient", finders = { "findLigneCmdClientsByCommande", "findLigneCmdClientsByCipMEqualsAndCommande", "findLigneCmdClientsByCipEquals" })
@RooJson
public class LigneCmdClient extends AdPharmaBaseEntity {

	@NotNull
	@ManyToOne
	private CommandeClient commande;

	private BigInteger quantiteCommande;

	private BigInteger quantiteRetourne = BigInteger.ZERO;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date dateSaisie;

	private BigDecimal prixUnitaire;

	private BigDecimal remise = BigDecimal.ZERO;

	private BigDecimal totalRemise = BigDecimal.ZERO;

	private BigDecimal prixTotal;

	private String designation;

	private String cip;

	private String agentSaisie;

	@ManyToOne
	private LigneApprovisionement produit;

	private String cipM;

	public LigneCmdClient clone() {
		LigneCmdClient ligneCmdClient = new LigneCmdClient();
		ligneCmdClient.setCip(cip);
		ligneCmdClient.setCipM(cipM);
		ligneCmdClient.setDesignation(designation);
		ligneCmdClient.setQuantiteRetourne(BigInteger.ONE);
		ligneCmdClient.setPrixUnitaire(prixUnitaire);
		ligneCmdClient.setQuantiteCommande(quantiteCommande);
		ligneCmdClient.setQuantiteRetourne(quantiteRetourne);
		ligneCmdClient.setId(id);
		return ligneCmdClient;
	}

	//solution provisiore pr  palier au pb de json 
	public String getStringFormat(){
		StringBuilder builder = new StringBuilder();
		builder.append(cip+"#");
		builder.append(cipM+"#");
		builder.append(designation+"#");
		builder.append(prixUnitaire+"#");
		builder.append(quantiteCommande+"#");
		builder.append(quantiteRetourne+"#");
		builder.append(id);
		return builder.toString();


	}

	public BigInteger getQteToAdd() {
		BigInteger qteStock = produit.getQuantieEnStock();
		BigInteger qteCmd = getQuantiteCommande();
		return qteStock.intValue() >= qteCmd.intValue() ? qteStock.subtract(qteCmd) : BigInteger.ZERO;
	}

	@Override
	protected void internalPrePersist() {
		agentSaisie = SecurityUtil.getUserName();
		dateSaisie = new Date();
	}

	public void calculPrixTotal() {
		prixTotal = BigDecimal.valueOf(prixUnitaire.multiply(BigDecimal.valueOf(quantiteCommande.longValue())).longValue());
	}

	public void calculremiseTotal() {
		totalRemise = BigDecimal.valueOf(remise.multiply(BigDecimal.valueOf(quantiteCommande.longValue())).longValue());
	}

	@Override
	public void  internalPreUpdate(){
		calculremiseTotal() ;
		calculPrixTotal();
	}

	public void increaseQteRetounee(BigInteger qte) {
		if (quantiteRetourne == null) {
			setQuantiteRetourne(qte);
		} else {
			quantiteRetourne = quantiteRetourne.add(qte);
		}
	}

	public LigneFacture convertToLigneFacture() {
		LigneFacture ligneFacture = new LigneFacture();
		ligneFacture.setCip(cip);
		ligneFacture.setCipM(cipM);
		ligneFacture.setDesignation(designation);
		ligneFacture.setPrixTotal(BigInteger.valueOf(prixTotal.longValue()));
		ligneFacture.setPrixUnitaire(BigInteger.valueOf(prixUnitaire.longValue()));
		ligneFacture.setQteAchete(quantiteCommande);
		ligneFacture.setRemise(BigInteger.valueOf(totalRemise.longValue()));
		return ligneFacture;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((cipM == null) ? 0 : cipM.hashCode());
		return result;
	}

	public void increaseCmdQte(BigInteger qte, BigDecimal remise) {
		quantiteCommande = quantiteCommande.add(qte);
		this.remise = remise;
		calculremiseTotal();
		calculPrixTotal();
		merge();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		LigneCmdClient other = (LigneCmdClient) obj;
		if (cipM == null) {
			if (other.cipM != null) return false;
		} else if (!cipM.equals(other.cipM)) return false;
		return true;
	}

	public static TypedQuery<LigneCmdClient> findLigneCmdClientsByCommande(CommandeClient commande) {
		if (commande == null) throw new IllegalArgumentException("The commande argument is required");
		EntityManager em = LigneCmdClient.entityManager();
		TypedQuery<LigneCmdClient> q = em.createQuery("SELECT o FROM LigneCmdClient AS o WHERE o.commande = :commande ORDER BY o.id DESC", LigneCmdClient.class);
		q.setParameter("commande", commande);
		return q;
	}

	public static List<BigDecimal> findMontantHtAndRemise(Long comdId) {
		// a refaire
		if (comdId == null  ) throw new IllegalArgumentException("The id  arguments are required");
		EntityManager em = LigneCmdClient.entityManager();
		Query q = em.createQuery("SELECT SUM(o.prixTotal), SUM(o.totalRemise) FROM LigneCmdClient AS o WHERE o.commande.id = :id ");
		q.setParameter("id", comdId);
		List<Object> resultList = q.getResultList();
		ArrayList<BigDecimal> arrayList = new ArrayList<BigDecimal>();
		BigDecimal prixtotal = BigDecimal.ZERO;
		BigDecimal remiset = BigDecimal.ZERO;
		if(!resultList.isEmpty()){
			prixtotal =  resultList.get(0) != null ?BigDecimal.valueOf( (Long)resultList.get(0)) : BigDecimal.ZERO;
			remiset =  resultList.get(1) != null ?BigDecimal.valueOf( (Long)resultList.get(1)) : BigDecimal.ZERO;
		}
		arrayList.add(prixtotal);
		arrayList.add(remiset);
		return arrayList ;
	}

	public String toDeepJson() {
		return new JSONSerializer().include("id","cipM","designation","quantiteCommande","prixTotal","remise").exclude("*.class","*").serialize(this);
	}
	public static String toDeepJsonArray(Collection<LigneCmdClient> collection) {
		return new JSONSerializer().include("id","cipM","designation","quantiteCommande","prixTotal","remise").exclude("*.class","*").serialize(collection);
	}
	
	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append(this.designation);
		builder.append("|").append(this.cip);
		builder.append("|").append(this.cipM);
		builder.append("|").append(this.quantiteCommande);
		builder.append("|").append(this.quantiteRetourne);
		return builder.toString();
	}
	
	
}
