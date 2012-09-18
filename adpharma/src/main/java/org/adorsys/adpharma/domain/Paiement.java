package org.adorsys.adpharma.domain;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PostPersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.adorsys.adpharma.domain.Site;
import javax.persistence.ManyToOne;
import org.adorsys.adpharma.domain.PharmaUser;
import org.adorsys.adpharma.domain.Caisse;
import org.adorsys.adpharma.domain.Facture;
import org.adorsys.adpharma.domain.TypePaiement;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.utils.NumberGenerator;
import org.adorsys.adpharma.utils.PharmaDateUtil;

import javax.persistence.Enumerated;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.annotation.Value;
import org.adorsys.adpharma.domain.QuiPaye;
import org.apache.commons.lang.StringUtils;

import javax.validation.constraints.Size;

@RooJavaBean
@RooToString
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "Paiement", finders = { "findPaiementsByTypePaiementAndCaisse", "findPaiementsByCaisse", "findPaiementsByDateSaisieBetween" })
public class Paiement extends AdPharmaBaseEntity {

    private String paiementNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private Date datePaiement;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private Date dateSaisie;

    private BigDecimal montant = BigDecimal.ZERO;

    private BigDecimal sommeRecue;

    private BigDecimal difference;

    @ManyToOne
    private Site site;

    @ManyToOne
    private PharmaUser cassier;

    @ManyToOne
    private Caisse caisse;

    @ManyToOne
    private Facture facture;

    @Enumerated
    private TypePaiement typePaiement;

    @Value("false")
    private Boolean ticketImprimer;

    private String payerPar;

    @Enumerated
    private QuiPaye quiPaye = QuiPaye.CLIENT;

    private String nomClient;

    @Size(max = 256)
    private String informations;

    private String numeroBon;

    private BigDecimal montantBon;
     
    @Value("true")
    private transient Boolean reduction;
    
    private transient String caisses;
    
    public String getCaisses() {
		return caisses;
	}

	public void setCaisses(String caisses) {
		this.caisses = caisses;
	}

	public Boolean getReduction() {
		return reduction;
	}

	public void setReduction(Boolean reduction) {
		this.reduction = reduction;
	}

	@Value("false")
    private  Boolean compenser;

    public Boolean getCompenser() {
		return compenser;
	}

	public void setCompenser(Boolean compenser) {
		this.compenser = compenser;
	}

	public Paiement getPaiementClientPayeur() {
        BigInteger partPayeur = facture.getNetPayer().subtract(montant.toBigInteger());
        if (partPayeur.intValue() == 0) return null;
        Paiement paiement = new Paiement();
        paiement.setCaisse(caisse);
        paiement.setCassier(cassier);
        paiement.setDateSaisie(dateSaisie);
        paiement.setDifference(BigDecimal.ZERO);
        paiement.setFacture(facture);
        paiement.setMontant(new BigDecimal(partPayeur));
        paiement.setNomClient(facture.getCommande().getClientPaiyeur().getNomComplet());
        paiement.setSommeRecue(new BigDecimal(partPayeur));
        paiement.setTypePaiement(TypePaiement.BON_CMD);
        paiement.setNomClient(getFacture().getCommande().getClientPaiyeur().displayName());
        return paiement;
    }
	
	public BigInteger getAvancePartClient(){
		return sommeRecue.intValue()< montant.intValue()?sommeRecue.toBigInteger():montant.toBigInteger();
	}
	public BigInteger getRestePartClient(){
		System.out.println(montant);
		return sommeRecue.intValue()< montant.intValue()?(montant.subtract(montant)).toBigInteger():BigInteger.ZERO;
	}

    @PostPersist
    public void postPersit() {
        paiementNumber = NumberGenerator.getNumber("PAY-", getId(), 4);
    }

    public Paiement(BigDecimal amount, Paiement paiement) {
        montant = amount;
        sommeRecue = amount;
        difference = BigDecimal.ZERO;
        quiPaye = paiement.getQuiPaye();
        informations = paiement.getInformations() + " PAIEMENT GROUPE";
        typePaiement = paiement.getTypePaiement();
    }

    public Paiement() {
    }

    @Override
    protected void internalPrePersist() {
        dateSaisie = new Date();
        if (!typePaiement.equals(TypePaiement.PROFORMAT)) {
            cassier = SecurityUtil.getPharmaUser();
        }
        site = cassier.getOffice();
        if (datePaiement == null) {
            datePaiement = new Date();
        }
    }

    public void definirPayeur() {
        if (quiPaye.equals(QuiPaye.CLIENT_PAYEUR)) {
            payerPar = getFacture().getCommande().getClientPaiyeur().toString();
        } else {
            payerPar = getFacture().getCommande().getClient().toString();
        }
        nomClient = getFacture().getCommande().getClient().toString();
    }

    public boolean validate(Model uiModel, BigInteger detteClient) {
        boolean valider = false;
        if (typePaiement.equals(TypePaiement.BON_AVOIR_CLIENT)) {
            sommeRecue = sommeRecue != null ? sommeRecue : BigDecimal.ZERO;
            if (montantBon.add(sommeRecue).intValue() < montant.intValue()) {
                uiModel.addAttribute("apMessage", " Modifier La somme Recue !");
                valider = true;
            }
        } else {
            if (montant.longValue() > facture.getReste().longValue()) {
                uiModel.addAttribute("apMessage", " Incorrect ! doit etre inferieur ou egal au reste A payer");
                valider = true;
            }
            if ((sommeRecue.longValue() < montant.longValue()) && !typePaiement.equals(TypePaiement.CREDIT)  ) {
                uiModel.addAttribute("apMessage", " Incorrect ! La somme Recue doit etre superieur ou egal au montant");
                valider = true;
            }
           /* if (typePaiement.equals(TypePaiement.CREDIT) && montant.intValue() != 0) {
                uiModel.addAttribute("apMessage", " Incorrect ! pour un paiement a credit le monatant doit etre null ");
                valider = true;
            }*/
        }
        return valider;
    }

    public boolean validatePaiementGlobal(Model uiModel, BigInteger detteClient) {
        boolean valider = false;
        return !valider;
    }

    public static TypedQuery<Paiement> findPaiementsByInvoiceNumberEquals(String factureNumber) {
        if (factureNumber == null || factureNumber.length() == 0) throw new IllegalArgumentException("The factureNumber argument is required");
        EntityManager em = Paiement.entityManager();
        TypedQuery<Paiement> q = em.createQuery("SELECT o FROM Paiement AS o WHERE o.facture.factureNumber = :factureNumber ORDER BY o.id DESC ", Paiement.class);
        q.setParameter("factureNumber", factureNumber);
        return q;
    }

    public static List<Paiement> findPaiementByCaisseEntries(int firstResult, int maxResults, Caisse caisse) {
        TypedQuery<Paiement> typedQuery = Paiement.findPaiementsByCaisse(caisse);
        return typedQuery.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public static List<Paiement> findPaiementEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Paiement o ORDER BY o.id DESC", Paiement.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public static List<Paiement> findAllPaiements() {
        return entityManager().createQuery("SELECT o FROM Paiement o ORDER BY o.id DESC", Paiement.class).getResultList();
    }
    

    public static TypedQuery<Paiement> findMyPaiements(Caisse caisse) {
        EntityManager em = Paiement.entityManager();
        TypedQuery<Paiement> q = em.createQuery("SELECT o FROM Paiement AS o WHERE o.caisse = :caisse ORDER BY o.id DESC ", Paiement.class);
        q.setParameter("caisse", caisse);
        return q;
    }
    public static TypedQuery<Paiement> findByFactures(Facture facture) {
        EntityManager em = Paiement.entityManager();
        TypedQuery<Paiement> q = em.createQuery("SELECT o FROM Paiement AS o WHERE o.facture = :facture ORDER BY o.id DESC ", Paiement.class);
        q.setParameter("facture", facture);
        return q;
    }

    public static TypedQuery<Paiement> findPaiementsByDateSaisieBetween(Date minDateSaisie, Date maxDateSaisie) {
        if (minDateSaisie == null) throw new IllegalArgumentException("The minDateSaisie argument is required");
        if (maxDateSaisie == null) throw new IllegalArgumentException("The maxDateSaisie argument is required");
        EntityManager em = Paiement.entityManager();
        TypedQuery<Paiement> q = em.createQuery("SELECT o FROM Paiement AS o WHERE o.dateSaisie BETWEEN :minDateSaisie AND :maxDateSaisie ORDER BY o.id DESC ", Paiement.class);
        q.setParameter("minDateSaisie", minDateSaisie);
        q.setParameter("maxDateSaisie", maxDateSaisie);
        return q;
    }
    
    
    public static TypedQuery<Paiement> search(PharmaUser cassier,Date dateSaisie, Caisse caisse,TypePaiement typePaiement) {
        StringBuilder searchQuery = new StringBuilder("SELECT o FROM Paiement AS o WHERE o.dateSaisie >= :dateSaisie ");
        dateSaisie = dateSaisie != null ? dateSaisie : PharmaDateUtil.parse("16-05-2012 07:00", PharmaDateUtil.DATETIME_PATTERN_LONG);
        if (cassier != null) {
            searchQuery.append(" AND  o.cassier = :cassier ");
        }
        
        if (typePaiement != null) {
        	  if (typePaiement != TypePaiement.ALL) {
                 searchQuery.append(" AND  o.typePaiement = :typePaiement ");
             }
        }
        if (caisse != null) {
            searchQuery.append(" AND  o.caisse = :caisse ");
        }
       
        TypedQuery<Paiement> q = entityManager().createQuery(searchQuery.toString(), Paiement.class);
        if (cassier != null) {
            q.setParameter("cassier", cassier);
        }
        if (caisse !=null) {
            q.setParameter("caisse", caisse);

        }
        if (typePaiement != null) {
      	  if (typePaiement != TypePaiement.ALL) {
              q.setParameter("typePaiement", typePaiement);
           }
      }
        q.setParameter("dateSaisie", dateSaisie);
        return q;
    }
    
    public void searchCaisse(){
    	if (StringUtils.isNotBlank(caisses)) {
    		caisses = "CA-"+StringUtils.removeStart(caisses, "CA-");
    		List<Caisse> resultList = Caisse.findCaissesByCaisseNumber(caisses).getResultList();
    		if (!resultList.isEmpty()) {
    			caisse = resultList.iterator().next();
				
			}else {
				caisse = null ;
			}
		}else {
			caisse = null ;
		}
    }
}
