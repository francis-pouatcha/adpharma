package org.adorsys.adpharma.beans.process;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.adorsys.adpharma.domain.AdPharmaBaseEntity;
import org.adorsys.adpharma.domain.Client;
import org.adorsys.adpharma.domain.CommandeClient;
import org.adorsys.adpharma.domain.Etat;
import org.adorsys.adpharma.domain.PharmaUser;
import org.adorsys.adpharma.domain.TypeCommande;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;

public class SearchSalesBean {

	transient EntityManager entityManager ;


	private String commandeNumber;

	private String cipm ;

	private String designation ;

	private Client client ;

	private TypeCommande typeCommande;

	private Etat etat ;
	
	@Value("false")
	private Boolean anuller ;
	
    
    public Boolean getAnuller() {
		return anuller;
	}


	public void setAnuller(Boolean anuller) {
		this.anuller = anuller;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	private Date dateCreation ;

	private PharmaUser pharmaUser;

	private List<CommandeClient> searchReasult = new ArrayList<CommandeClient>();

	public  List<CommandeClient> search() {
		 if (this.entityManager == null) this.entityManager = AdPharmaBaseEntity.entityManager();
		StringBuilder searchQuery = new StringBuilder("SELECT o FROM CommandeClient AS o WHERE o.dateCreation >= :minDateCreation ");
		dateCreation = dateCreation != null ? dateCreation : PharmaDateUtil.parse("10-10-2010 00:00", PharmaDateUtil.DATETIME_PATTERN_LONG);
		if (StringUtils.isNotBlank(commandeNumber))return entityManager.createQuery("SELECT o FROM CommandeClient AS o WHERE  o.cmdNumber = :cmdNumber ", CommandeClient.class).setParameter("cmdNumber", "CMD-" + commandeNumber).getResultList();
		if (StringUtils.isNotBlank(cipm) && StringUtils.isNotBlank(designation)){
			searchQuery.append(" AND o.id IN (SELECT l.commande.id FROM LigneCmdClient AS l where l.cipM = :cipm AND LOWER(l.designation) LIKE LOWER(:designation)  ) "); 
		}else if (StringUtils.isNotBlank(cipm)) {
			searchQuery.append(" AND o.id IN (SELECT l.commande.id FROM LigneCmdClient AS l where l.cipM = :cipm ) "); 
		}else if (StringUtils.isNotBlank(designation)) {
			searchQuery.append(" AND o.id IN (SELECT p.commande.id FROM LigneCmdClient AS p  where LOWER(p.designation) LIKE LOWER(:designation) ) ");
		}
		if (client != null)  searchQuery.append(" AND o.client = :client "); 
		if (!etat.equals(Etat.ALL))  searchQuery.append(" AND o.status = :status "); 
		if (pharmaUser != null) searchQuery.append(" AND o.vendeur = :pharmaUser "); 
		if (anuller != null) searchQuery.append(" AND o.annuler = :annuler "); 
		if (!typeCommande.equals(TypeCommande.ALL))  searchQuery.append(" AND o.typeCommande = :typeCommande "); 

		TypedQuery<CommandeClient> q = entityManager.createQuery(searchQuery.append(" ORDER BY o.id DESC").toString(), CommandeClient.class);
		if (StringUtils.isNotBlank(cipm) && StringUtils.isNotBlank(designation)){
			q.setParameter("cipm",cipm); 
			q.setParameter("designation",designation + "%"); 
		}else if (StringUtils.isNotBlank(cipm)) {
			q.setParameter("cipm",cipm); 
		}else if (StringUtils.isNotBlank(designation)) {
			q.setParameter("designation",designation + "%");  	
		}
		if (client != null)  q.setParameter("client",client);  
		if (!etat.equals(Etat.ALL))  q.setParameter("status",etat); 
		if (pharmaUser != null)  q.setParameter("pharmaUser",pharmaUser);
		if (anuller != null)  q.setParameter("annuler",anuller);
		if (!typeCommande.equals(TypeCommande.ALL))   q.setParameter("typeCommande",typeCommande); 
		q.setParameter("minDateCreation", dateCreation ,TemporalType.TIMESTAMP);
		System.out.println(q.toString());
		return q.getResultList();
		}
	
	
/**
 * 
 * getters and setters .
 */

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public String getCommandeNumber() {
		return commandeNumber;
	}

	public void setCommandeNumber(String commandeNumber) {
		this.commandeNumber = commandeNumber;
	}

	public String getCipm() {
		return cipm;
	}

	public void setCipm(String cipm) {
		this.cipm = cipm;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public TypeCommande getTypeCommande() {
		return typeCommande;
	}

	public void setTypeCommande(TypeCommande typeCommande) {
		this.typeCommande = typeCommande;
	}

	public Etat getEtat() {
		return etat;
	}

	public void setEtat(Etat etat) {
		this.etat = etat;
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	public PharmaUser getPharmaUser() {
		return pharmaUser;
	}

	public void setPharmaUser(PharmaUser pharmaUser) {
		this.pharmaUser = pharmaUser;
	}

	public List<CommandeClient> getSearchReasult() {
		return searchReasult;
	}

	public void setSearchReasult(List<CommandeClient> searchReasult) {
		this.searchReasult = searchReasult;
	}
	
	
	}





