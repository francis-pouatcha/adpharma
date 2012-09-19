package org.adorsys.adpharma.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.ManyToOne;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.adorsys.adpharma.beans.DetteComparator;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.utils.NumberGenerator;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

@RooJavaBean
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "EtatCredits")
public class EtatCredits extends AdPharmaBaseEntity {

	private String etatNumber;

	@ManyToOne
	private Client client;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy hh:mm")
	private Date dateEdition;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy hh:mm")
	private Date datePaiement;

	private transient Long clientId;

	private transient String clientName;

	@Value("0")
	private BigDecimal montantInitial;

	@Value("0")
	private BigDecimal avance;

	@Value("0")
	private BigDecimal reste;

	@Value("false")
	private Boolean solder;

	@Value("0")
	private BigDecimal montantAvoir;

	@Value("false")
	private Boolean annuler;

	@Value("false")
	private Boolean consommerAvoir;

	private transient List<DetteClient> listeDettes = new ArrayList<DetteClient>();

	@javax.persistence.PostLoad
	public void PostLoad() {

		// listeDettes = DetteClient.search(null, this, null, null).getResultList();
		//sortListeDettes();
	}

	public void sortListeDettes(){
		Collections.sort(listeDettes , new DetteComparator());
	}

	public void initListeDettes(){

		listeDettes = DetteClient.search(null, this, null, null).getResultList();
		new Thread(new Runnable() {
			@Override
			public void run() {
				sortListeDettes();

			}
		}).start();

	}

	public void avancer(BigDecimal amount){
		avance = avance.add(amount);
		reste = montantInitial.subtract(avance).subtract(montantAvoir);
		solder = reste.intValue() == 0 ;
		setEncaisser(Boolean.TRUE);
	}

	public void encaisser(Paiement paiement){
		Facture facture = new Facture();
		BigDecimal amount = BigDecimal.ZERO;
		if (paiement.getMontant().intValue() <= paiement.getSommeRecue().intValue()) {
			amount = paiement.getMontant();
		}else {
			amount = paiement.getSommeRecue();
		}
		avancer(amount);
		listeDettes = DetteClient.search(null, this, null, null).getResultList();
		for (DetteClient dette : listeDettes) {
			if (!dette.getAnnuler()) {
				facture =   Facture.findFacturesByFactureNumberEquals(dette.getFactureNo()).getResultList().iterator().next();
				if (dette.getReste().intValue() <= amount.intValue()) {
					amount = amount.subtract(BigDecimal.valueOf(dette.getReste().longValue()));
					dette.avancer(dette.getReste());
					facture.avancerPaiement(dette.getReste());
					dette.merge();
					facture.merge();
				} else {
					dette.avancer(amount.toBigInteger());
					facture.avancerPaiement(amount.toBigInteger());
					facture.merge();
					dette.merge();
					break;
				}
			}
		}


	}


	public void validate(BindingResult bindingResult) {
		List<DetteClient> listDettes = DetteClient.search(getClient().getId(), null, Boolean.FALSE, Boolean.FALSE).getResultList();
		if (listDettes.isEmpty()) {
			ObjectError error = new ObjectError("listDettes", "ce Client de possede Aucune Dette Non Facturee");
			bindingResult.addError(error);
		} else {
			getListeDettes().addAll(listDettes);
		}
	}

	private String agent;

	@Value("false")
	private Boolean encaisser;

	public void protectSomeField() {
		EtatCredits etatCredits = EtatCredits.findEtatCredits(getId());
		footPrint = etatCredits.getFootPrint();
		montantAvoir = etatCredits.getMontantAvoir();
		dateEdition = etatCredits.getDateEdition();
		datePaiement = datePaiement==null? etatCredits.getDatePaiement():datePaiement;
		client = etatCredits.getClient();
		agent = etatCredits.getAgent();
		etatNumber = etatCredits.getEtatNumber();


	}

	@PrePersist
	public void prePersist() {
		dateEdition = new Date();
		agent = SecurityUtil.getUserName();
	}

	@PostPersist
	public void postPersit() {
		etatNumber = NumberGenerator.getNumber("ET-", getId(), 4);
	}
	public static List<EtatCredits> findAllEtatCreditses() {
		return entityManager().createQuery("SELECT o FROM EtatCredits o ORDER BY o.dateEdition DESC", EtatCredits.class).getResultList();
	}

	public static List<EtatCredits> findEtatCreditsEntries(int firstResult, int maxResults) {
		return entityManager().createQuery("SELECT o FROM EtatCredits o ORDER BY o.dateEdition DESC", EtatCredits.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
	}
	public void consommerAvoir() {
		List<AvoirClient> resultList = AvoirClient.search(null,null, null, getClient().getClientNumber(), null, Boolean.FALSE, Boolean.FALSE).getResultList();
		if (!resultList.isEmpty()) {
			for (AvoirClient avoir : resultList) {
				BigDecimal amount = montantInitial;
				if (amount.subtract(avoir.getReste()).intValue() >= 0) {
					amount = amount.subtract(avoir.getReste());
					montantAvoir = montantAvoir.add(avoir.getReste());
					avoir.avancer(avoir.getReste());
					avoir.merge();
				} else {
					montantAvoir = montantAvoir.add(amount);
					avoir.avancer(amount);
					avoir.merge();
					break;
				}
			}
		}
	}

	public List<DetteClient> getListeDettes() {
		return listeDettes;
	}

	public void setListeDettes(List<DetteClient> listeDettes) {
		this.listeDettes = listeDettes;
	}

	public void computeAmount() {
		if (!listeDettes.isEmpty()) {
			montantInitial = BigDecimal.ZERO;
			avance = BigDecimal.ZERO;
			reste = BigDecimal.ZERO;
			for (DetteClient dette : listeDettes) {
				if (!dette.getAnnuler()) {
					montantInitial = montantInitial.add(BigDecimal.valueOf(dette.getMontantInitial().intValue()));
					avance = avance.add(BigDecimal.valueOf(dette.getAvance().intValue()));
					reste = reste.add(BigDecimal.valueOf(dette.getReste().intValue()));
					if (consommerAvoir) {
						reste = reste.subtract(montantAvoir);
					}
				}
			}
		}
	}
	
	 public static TypedQuery<EtatCredits> search(String clientName,String etatNumber,  Date dateEdition, Date datePaiement, Boolean solder, Boolean anuller,Boolean encaisser) {
	        StringBuilder searchQuery = new StringBuilder("SELECT o FROM EtatCredits AS o WHERE   o.id IS NOT NULL ");
	        if (StringUtils.isNotBlank(clientName)) {
	        	clientName = clientName+"% " ;
	            searchQuery.append(" AND LOWER (o.client.nom ) LIKE LOWER (:clientName) ");
	        }
	        if (StringUtils.isNotBlank(etatNumber)) {
	            searchQuery.append(" AND o.etatNumber = :etatNumber ");
	        }
	        if (dateEdition!=null) {
	            searchQuery.append("AND o.dateEdition > :dateEdition ");
	        }
	        if (datePaiement!=null) {
	            searchQuery.append("AND o.datePaiement > :datePaiement ");
	        }
	        if (solder!=null) {
	            searchQuery.append("AND o.solder IS :solder ");
	        }
	        if (anuller!=null) {
	            searchQuery.append("AND o.annuler IS :anuller ");
	        }
	        if (encaisser!=null) {
	            searchQuery.append("AND o.encaisser IS :encaisser ");
	        }
	        
	        
	       
	        TypedQuery<EtatCredits> q = entityManager().createQuery(searchQuery.toString(), EtatCredits.class);
	        if (StringUtils.isNotBlank(clientName)) {
	            q.setParameter("clientName", clientName);
	        }
	        if (StringUtils.isNotBlank(etatNumber)) {
	            q.setParameter("etatNumber", etatNumber);
	        }
	        if (dateEdition!=null) {
	        	q.setParameter("dateEdition", dateEdition);
	        }
	        if (datePaiement!=null) {
	            q.setParameter("datePaiement", datePaiement);
	        }
	        if (solder!=null) {
	        	q.setParameter("solder", solder);
	        }
	        if (anuller!=null) {
	        	q.setParameter("anuller", anuller);
	        }
	        if (encaisser!=null) {
	        	q.setParameter("encaisser", encaisser);
	        }
	        
	        return q;
	    }

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public Boolean getSolder() {
		return solder;
	}

	public void setSolder(Boolean solder) {
		this.solder = solder;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getEtatNumber()).append(" DU :").append(PharmaDateUtil.format(dateEdition, "dd-MM-yyyy"));
		return sb.toString();
	}
}
