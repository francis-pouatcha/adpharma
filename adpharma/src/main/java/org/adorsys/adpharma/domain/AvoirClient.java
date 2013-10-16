package org.adorsys.adpharma.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Enumerated;
import javax.persistence.PostPersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.utils.NumberGenerator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "AvoirClient", finders = { "findAvoirClientsByNumeroEquals" })
public class AvoirClient extends AdPharmaBaseEntity {

	private String numero = " ";

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	private Date dateEdition = new Date();

	@Value("0")
	private BigDecimal montant;

	private String clientName;

	private String clientNumber;

	private Boolean annuler = Boolean.FALSE;

	private String agent = SecurityUtil.getUserName();

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date dateDernierOp;

	@Enumerated
	private TypeBon typeBon;

	private BigDecimal montantUtilise = BigDecimal.ZERO;

	@Value("false")
	private Boolean solder;

	private BigDecimal reste = BigDecimal.ZERO;

	@Value("false")
	private Boolean imprimer;

	@Override
	protected void internalPrePersist() {
		agent = SecurityUtil.getUserName();
		dateEdition = new Date();
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public Date getDateEdition() {
		return dateEdition;
	}

	public void setDateEdition(Date dateEdition) {
		this.dateEdition = dateEdition;
	}

	public BigDecimal getMontant() {
		return montant;
	}

	public void setMontant(BigDecimal montant) {
		this.montant = montant;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getClientNumber() {
		return clientNumber;
	}

	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
	}

	public Boolean getAnnuler() {
		return annuler;
	}

	public void setAnnuler(Boolean annuler) {
		this.annuler = annuler;
	}

	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public Date getDateDernierOp() {
		return dateDernierOp;
	}

	public void setDateDernierOp(Date dateDernierOp) {
		this.dateDernierOp = dateDernierOp;
	}

	public TypeBon getTypeBon() {
		return typeBon;
	}

	public void setTypeBon(TypeBon typeBon) {
		this.typeBon = typeBon;
	}

	public BigDecimal getMontantUtilise() {
		return montantUtilise;
	}

	public void setMontantUtilise(BigDecimal montantUtilise) {
		this.montantUtilise = montantUtilise;
	}

	public Boolean getsolder() {
		return solder;
	}

	public void setsolder(Boolean solder) {
		this.solder = solder;
	}

	public AvoirClient() {
	}

	;

	public AvoirClient(CommandeClient commandeClient,BigDecimal amount ) {
		clientNumber = commandeClient.getClient().getClientNumber();
		clientName = commandeClient.getClient().getNomComplet();
		setMontant(amount);
		reste = montant;
		montantUtilise=BigDecimal.ZERO;
		solder=estSolder();
	}

	@PostPersist
	public void postPersit() {
		numero = NumberGenerator.getNumber("", getId(), 4);
	}

	public void protectSomeField() {
		AvoirClient avoir = AvoirClient.findAvoirClient(getId());
		footPrint = avoir.getFootPrint();
		numero = avoir.getNumero();
		agent = avoir.getAgent();
		dateEdition = avoir.getDateEdition();
		montant = avoir.getMontant();
		clientName = avoir.getClientName();
		clientNumber = avoir.getClientNumber();
		typeBon = avoir.getTypeBon();
	}

	public void increaseAmount(BigDecimal amount) {
		montant = montant.add(amount);
		defineReste();
	}

	public void decreaseAmount(BigDecimal amount) {
		montant = montant.subtract(amount);
		defineReste();
	}

	public void defineReste() {
		reste = montant.subtract(montantUtilise);
		solder = estSolder();
	}

	public void avancer(BigDecimal amount) {
		montantUtilise = montantUtilise.add(amount);
		reste = montant.subtract(montantUtilise);
		solder = estSolder();
		dateDernierOp = new Date();
	}

	public boolean estSolder() {
		return reste.intValue() == 0;
	}

	public static List<AvoirClient> findAllAvoirClients() {
		return entityManager().createQuery("SELECT o FROM AvoirClient  o ORDER BY o.id DESC", AvoirClient.class).getResultList();
	}
	public static List<AvoirClient> findAvoirClientEntries(int firstResult, int maxResults) {
		return entityManager().createQuery("SELECT o FROM AvoirClient o ORDER BY o.id DESC", AvoirClient.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
	}

	public static TypedQuery<AvoirClient> search(Date dateEdition,String numero, String clientName, String clientNumber, TypeBon typeBon, Boolean annuler, Boolean solder) {
		StringBuilder searchQuery = new StringBuilder("SELECT o FROM AvoirClient AS o WHERE o.annuler IS :annuler ");
		annuler = annuler != null ? annuler : Boolean.FALSE;
		if (typeBon != null) {
			searchQuery.append(" AND  o.typeBon = :typeBon ");
		}
		if (solder != null) {
			searchQuery.append(" AND  o.solder IS :solder ");
		}

		if (StringUtils.isNotBlank(numero)) {
			searchQuery.append(" AND  o.numero = :numero ");
		}

		if (dateEdition !=null) {
			searchQuery.append(" AND  o.dateEdition >= :dateEdition ");
		}
		if (StringUtils.isNotBlank(clientNumber)) {
			searchQuery.append(" AND o.clientNumber = :clientNumber ");
		} else {
			if (StringUtils.isNotBlank(clientName)) {
				clientName = "%" + clientName + "%";
				searchQuery.append(" AND  LOWER(o.clientName) LIKE LOWER(:clientName) ");
			}
		}
		TypedQuery<AvoirClient> q = entityManager().createQuery(searchQuery.append(" ORDER BY o.id DESC").toString(), AvoirClient.class);
		if (typeBon != null) {
			q.setParameter("typeBon", typeBon);
		}
		if (dateEdition !=null) {
			q.setParameter("dateEdition", dateEdition);

		}
		if (StringUtils.isNotBlank(numero)) {
			q.setParameter("numero", numero);
		}
		if (solder != null) {
			q.setParameter("solder", solder);
		}
		if (StringUtils.isNotBlank(clientNumber)) {
			q.setParameter("clientNumber", clientNumber);
		} else {
			if (StringUtils.isNotBlank(clientName)) {
				q.setParameter("clientName", clientName);
			}
		}
		q.setParameter("annuler", annuler);
		return q;
	}



}
