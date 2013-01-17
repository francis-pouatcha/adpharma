package org.adorsys.adpharma.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PostPersist;
import javax.persistence.PreRemove;
import javax.persistence.Query;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.utils.NumberGenerator;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.ui.Model;
import org.springframework.roo.addon.json.RooJson;

@RooJavaBean
@RooToString
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "CommandeClient", finders = { "findCommandeClientsByDateCreationBetween", "findCommandeClientsByStatusAndDateCreationBetween" })
@RooJson
public class CommandeClient extends AdPharmaBaseEntity {

	private String cmdNumber;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy hh:mm")
	private Date dateCreation;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy hh:mm")
	private Date dateAnullation;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy hh:mm")
	private Date dateRestoration;

	public Date getDateAnullation() {
		return dateAnullation;
	}

	public void setDateAnullation(Date dateAnullation) {
		this.dateAnullation = dateAnullation;
	}

	public Date getDateRestoration() {
		return dateRestoration;
	}

	public void setDateRestoration(Date dateRestoration) {
		this.dateRestoration = dateRestoration;
	}

	@NotNull
	@ManyToOne
	private Client client;

	@NotNull
	@ManyToOne
	private PharmaUser vendeur;

	@Enumerated
	private Etat status = Etat.EN_COUR;

	@Value("false")
	private Boolean encaisse;

	@Value("false")
	private Boolean annuler;

	private BigDecimal montantHt = BigDecimal.ZERO;

	private BigDecimal montantTva = BigDecimal.ZERO;

	private BigDecimal montantTtc = BigDecimal.ZERO;

	private BigDecimal remise = BigDecimal.ZERO;

	private BigDecimal otherRemise = BigDecimal.ZERO;

	private String anullerPar ;

	public String getAnullerPar() {
		return anullerPar;
	}

	public void setAnullerPar(String anullerPar) {
		this.anullerPar = anullerPar;
	}

	public BigDecimal getOtherRemise() {
		return otherRemise;
	}

	public void setOtherRemise(BigDecimal otherRemise) {
		this.otherRemise = otherRemise;
	}

	private Long factureId;

	@Enumerated
	private TypeCommande typeCommande;

	@OrderBy("id DESC")
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "commande")
	private Set<LigneCmdClient> lineCommande = new HashSet<LigneCmdClient>();

	@ManyToOne
	private Client clientPaiyeur;

	@Value("100")
	@Min(0L)
	@Max(100L)
	private int tauxCouverture;

	@Value("false")
	private Boolean ventePartiel;

	private String avoirNumber;

	private String numeroBon;

	public Facture generateFacture() {
		Facture facture = new Facture();
		facture.setClient(client);
		facture.setCommande(this);
		facture.setMontantRemise(BigInteger.valueOf(remise.longValue()));
		facture.setMontantTotal(BigInteger.valueOf(montantHt.longValue()));
		facture.setVendeur(SecurityUtil.getPharmaUser());
		facture.setNetPayer(BigInteger.valueOf(montantTtc.longValue()));
		return facture;
	}

	public AvoirClient getBonClient() {
		if (avoirNumber != null) {
			return AvoirClient.findAvoirClientsByNumeroEquals(avoirNumber).getSingleResult();
		} else {
			return null;
		}
	}

	public String getBonNumber() {
		if (numeroBon != null) {
			return numeroBon;
		} else {
			return "";
		}
	}
	
	public Boolean isAllItemToSale(LigneApprovisionement line){
	    	LigneCmdClient sameCipM = this.getSameCipM(line.getCipMaison());
	    	if(sameCipM !=null){
	    		if(sameCipM.getQuantiteCommande().equals(line.getQuantieEnStock())){
	    			return true ;
	    		}else {
					return false ;
				}
	    	}
	    	return false ;
	}
	
	

	public BigInteger getPartClient() {
		if (typeCommande.equals(TypeCommande.VENTE_A_CREDIT)) {
			BigDecimal partPayeur = montantTtc.multiply(BigDecimal.valueOf(tauxCouverture)).divide(BigDecimal.valueOf(100));
			return montantTtc.subtract(partPayeur).toBigInteger();
		} else {
			return BigInteger.ZERO;
		}
	}

	public boolean validaterCmd(Model uiModel) {
		boolean valider = true;
		BigDecimal partPayeur = montantTtc.multiply(BigDecimal.valueOf(tauxCouverture)).divide(BigDecimal.valueOf(100));
		BigDecimal parclient = montantTtc.subtract(partPayeur);
		Set<LigneCmdClient> lineCommande2 = getLineCommande();
		if (lineCommande2.isEmpty()) {
			uiModel.addAttribute("apMessage", "Impossible de cloturer ! La commande ne contient Aucun Produits .");
			valider = false;
		}
		if (typeCommande.equals(TypeCommande.VENTE_A_CREDIT)) {
			if (!client.estCredible(parclient.toBigInteger())) {
				uiModel.addAttribute("apMessage", "Impossible de cloturer !  Le client " + getClient().displayName() + " n'est plus credible son  plafond actuel  est : " + client.getMontantCredible() + "fcfa  < " + parclient);
				valider = false;
			}
			if (!clientPaiyeur.estCredible(partPayeur.toBigInteger())) {
				uiModel.addAttribute("apMessage", "Impossible de cloturer ! Le client Payeur " + getClientPaiyeur().displayName() + " n'est plus credible son  plafond actuel est : " + clientPaiyeur.getMontantCredible() + "fcfa <" + partPayeur);
				valider = false;
			}
		}
		return valider;
	}


	public boolean validaterRemiseGlobale(Model uiModel, BigDecimal remise) {
		boolean valider = true;
		BigDecimal actualRemise = remise.add(getRemise());
		BigDecimal remiseAdmicible = BigDecimal.ZERO;
		if (client.getRemiseAutorise()) remiseAdmicible = (getMontantHt().multiply(getClient().getCategorie().getTauxRemiseMax())).divide(BigDecimal.valueOf(100));
		if (remiseAdmicible.doubleValue() == 0) {
			uiModel.addAttribute("apMessage", "Ce client n'est pas autorise a obtenir une remise ! ");
			return false;
		}

		if (remiseAdmicible.doubleValue() < actualRemise.doubleValue()) {
			uiModel.addAttribute("apMessage", "la remise pour  ce client  doit etre inferieur a : " + remiseAdmicible.doubleValue());
			return false ;
		}
		return valider;
	}

	@Override
	protected void internalPrePersist() {
		dateCreation = new Date();
		vendeur = SecurityUtil.getPharmaUser();
	}

	@PostPersist
	public void postPersit() {
		cmdNumber = NumberGenerator.getNumber("CMD-", getId(), 4);
	}

	@PreRemove
	public void preRemove() {
		List<Ordonnancier> ordonnancier = Ordonnancier.findOrdonnanciersByCommande(this).getResultList();
		if (!ordonnancier.isEmpty()) {
			ordonnancier.iterator().next().remove();
		}
	}

	public void protectSomeField() {
		CommandeClient commande = CommandeClient.findCommandeClient(getId());
		cmdNumber = commande.getCmdNumber();
		setDateCreation(commande.getDateCreation());
		footPrint = commande.getFootPrint();
		footPrint.setModifyingUser(SecurityUtil.getUserName());
		factureId = commande.getFactureId();
	}

	public LigneCmdClient getLineProduct(Produit produit) {
		if (lineCommande.isEmpty()) {
			return null;
		} else {
			for (LigneCmdClient ligneCmd : lineCommande) {
				if (ligneCmd.getProduit().equals(produit)) {
					return ligneCmd;
				}
			}
		}
		return null;
	}


	public void annulerCommande(String userName) {
		if (factureId != null) {
			Facture facture = Facture.findFacture(factureId);
			facture.remove();
		}
		this.setAnnuler(true);
		setFactureId(null);
		setAnullerPar(userName);
		dateAnullation = new Date();
		dateRestoration = null;
		merge();
	}

	public void restorerCommande() {
		this.setAnnuler(false);
		setStatus(Etat.EN_COUR);
		setAnullerPar("");
		dateRestoration = new Date();
		merge();
	}

	public boolean contientSameCipM(String cipM) {
		if (lineCommande.isEmpty()) {
			return false;
		} else {
			for (LigneCmdClient ligneCmd : lineCommande) {
				if (ligneCmd.getCipM().equals(cipM)) {
					return true;
				}
			}
		}
		return false;
	}

	public LigneCmdClient getSameCipM(String cipM) {
		if (lineCommande.isEmpty()) {
			return null;
		} else {
			for (LigneCmdClient ligneCmd : lineCommande) {
				if (ligneCmd.getCipM().equals(cipM)) {
					return ligneCmd;
				}
			}
		}
		return null;
	}

	public List<LigneCmdClient> getAllSameCip(String cip) {
		ArrayList<LigneCmdClient> listOfSameCip = new ArrayList<LigneCmdClient>();
		if (!lineCommande.isEmpty()) {
			for (LigneCmdClient ligneCmd : lineCommande) {
				if (ligneCmd.getCip().equals(cip)) {
					listOfSameCip.add(ligneCmd);
				}
			}
		}
		return listOfSameCip;
	}
	
	public LigneCmdClient getItemHasSameCipm(String cipm) {
		if (!lineCommande.isEmpty()) {
			for (LigneCmdClient ligneCmd : lineCommande) {
				if (StringUtils.equals(ligneCmd.getCipM(),cipm)) return ligneCmd ;
			}
		}
		return null ;
	}
	public Facture getFacture(){
		List<Facture> resultList = Facture.findFacturesByCommande(this).getResultList();
		if (!resultList.isEmpty()) return resultList.iterator().next();
		return null;
	}

	public Paiement getPaiements(){
		Facture facture = getFacture();
		if (facture!=null){
			List<Paiement> resultList = Paiement.findByFactures(facture).getResultList();
			if(!resultList.isEmpty()) return resultList.iterator().next();
		}
		return null;
	}
	/**
	 * use for return the lineAprovisionnemnt of all same cipM
	 * @param cipM
	 * @return
	 */
	public List<LigneApprovisionement> getAllLineApForSameCip(String cip) {
		ArrayList<LigneApprovisionement> lineApForSameCip = new ArrayList<LigneApprovisionement>();
		if (!lineCommande.isEmpty()) {
			for (LigneCmdClient ligneCmd : lineCommande) {
				if (ligneCmd.getCip().equals(cip)) {
					lineApForSameCip.add(ligneCmd.getProduit());
				}
			}
		}
		return lineApForSameCip;
	}

	/**
	 * use to update the qte of all cipm in commmande
	 * @param qte to update 
	 * @param cipM of line
	 */
	public BigInteger updateQteOfItem(BigInteger qte, BigDecimal remise, String cip) {
		List<LigneCmdClient> allSameCip = getAllSameCip(cip);
		BigInteger qteAfterUpdate = qte;
		if (!allSameCip.isEmpty()) {
			for (LigneCmdClient line : allSameCip) {
				BigInteger qteToAdd = line.getQteToAdd();
				if (qteToAdd.intValue() != 0) {
					if (qteToAdd.intValue() <= qteAfterUpdate.intValue()) {
						line.setQuantiteCommande(line.getQuantiteCommande().add(qteToAdd));
						qteAfterUpdate = qteAfterUpdate.subtract(qteToAdd);
					} else {
						line.setQuantiteCommande(line.getQuantiteCommande().add(qteAfterUpdate));
						qteAfterUpdate = qteAfterUpdate.subtract(qteAfterUpdate);
						break;
					}
					line.setRemise(remise);
					line.calculPrixTotal();
					line.calculremiseTotal();
					line.merge();
				}
			}
		}
		return qteAfterUpdate;
	}

	public String displayName() {
		return new StringBuilder().append(getCmdNumber()).append(" du ").append(getDateCreation()).toString();
	}

	public boolean containCipm(String cipM) {
		return getSameCipM(cipM) == null ? false : true;
	}

	public void calculPrixHtAndRemise() {
		if(otherRemise ==null) otherRemise = BigDecimal.ZERO;
		montantHt = BigDecimal.ZERO;
		remise = BigDecimal.ZERO;
		List<LigneCmdClient> line = LigneCmdClient.findLigneCmdClientsByCommande(this).getResultList();
		if (!line.isEmpty()) {
			for (LigneCmdClient ligneCmd : line) {
				montantHt = montantHt.add(ligneCmd.getPrixTotal());
				remise = remise.add(ligneCmd.getTotalRemise());
			}
		}
		remise = remise.add(otherRemise);
		/* List<BigDecimal> montantHtAndRemise = LigneCmdClient.findMontantHtAndRemise(getId());
          montantHt = montantHtAndRemise.get(0);
          remise = montantHtAndRemise.get(1);*/

	}

	public void addOtherRemise(BigDecimal other){
		if(otherRemise==null) otherRemise = BigDecimal.ZERO;
		if(other != null) otherRemise = otherRemise.add(other);
		if(otherRemise.intValue() < 0)  otherRemise = BigDecimal.ZERO;
	}

	public void calculNetApayer() {
		montantTtc = BigDecimal.valueOf(montantHt.subtract(remise).longValue());
	}

	public TypeFacture getTypeFacture() {
		TypeFacture type = TypeFacture.CAISSE;
		if (typeCommande.equals(TypeCommande.VENTE_PROFORMAT)) {
			type = TypeFacture.PROFORMAT;
		}
		return type;
	}

	public TypePaiement getTypePaiement() {
		TypePaiement type = TypePaiement.CASH;
		if (typeCommande.equals(TypeCommande.VENTE_A_CREDIT)) {
			type = TypePaiement.CREDIT;
		}
		if (typeCommande.equals(TypeCommande.VENTE_PROFORMAT)) {
			type = TypePaiement.PROFORMAT;
		}
		return type;
	}

	public String toString() {
		return new StringBuilder().append(" " + cmdNumber).append("  , Du: ").append(PharmaDateUtil.format(getDateCreation(), "dd-MM-yyyy HH:mm")).append("  , ").append(getClient().toString()).append("   ,     " + typeCommande).toString();
	}

	public static TypedQuery<CommandeClient> findCommandeClientsByDateCreationBetweenNotEncaisser(Date minDateCreation, Date maxDateCreation) {
		if (minDateCreation == null) throw new IllegalArgumentException("The minDateCreation argument is required");
		if (maxDateCreation == null) throw new IllegalArgumentException("The maxDateCreation argument is required");
		EntityManager em = CommandeClient.entityManager();
		TypedQuery<CommandeClient> q = em.createQuery("SELECT o FROM CommandeClient AS o WHERE o.dateCreation BETWEEN :minDateCreation AND :maxDateCreation AND o.encaisse IS :encaisse", CommandeClient.class);
		q.setParameter("minDateCreation", minDateCreation);
		q.setParameter("maxDateCreation", maxDateCreation);
		q.setParameter("encaisse", Boolean.FALSE);
		return q;
	}

	public static TypedQuery<CommandeClient> findCommandeClientsByStatusAndDateCreationBetweenNotEncaisser(Etat status, Date minDateCreation, Date maxDateCreation) {
		if (status == null) throw new IllegalArgumentException("The status argument is required");
		if (minDateCreation == null) throw new IllegalArgumentException("The minDateCreation argument is required");
		if (maxDateCreation == null) throw new IllegalArgumentException("The maxDateCreation argument is required");
		EntityManager em = CommandeClient.entityManager();
		TypedQuery<CommandeClient> q = em.createQuery("SELECT o FROM CommandeClient AS o WHERE o.status = :status AND o.dateCreation BETWEEN :minDateCreation AND :maxDateCreation AND o.encaisse IS :encaisse", CommandeClient.class);
		q.setParameter("status", status);
		q.setParameter("minDateCreation", minDateCreation);
		q.setParameter("maxDateCreation", maxDateCreation);
		q.setParameter("encaisse", Boolean.FALSE);
		return q;
	}


	public static List<Long> findUnpayCloseSales(Etat status , Boolean encaisser, Boolean annuler,TypeCommande typeCommande) {
		EntityManager em = CommandeClient.entityManager();
		Query q = em.createQuery("SELECT  COUNT(distinct o.id) FROM CommandeClient AS o WHERE o.status = :status AND o.encaisse IS :encaisse  AND o.annuler IS :annuler AND  o.typeCommande != :typeCommande   ");
		q.setParameter("status", status);
		q.setParameter("encaisse", encaisser);
		q.setParameter("annuler", annuler);
		q.setParameter("typeCommande", typeCommande);
		return q.getResultList();
	}


	public static List<CommandeClient> findCommandeClientEntries(int firstResult, int maxResults) {
		return entityManager().createQuery("SELECT o FROM CommandeClient o ORDER BY o.id DESC ", CommandeClient.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
	}

	public static List<CommandeClient> search(String cmdNumber, Etat status, Date minDate, Date maxDate, Client client, TypeCommande typeCommande) {
		StringBuilder searchQuery = new StringBuilder("SELECT o FROM CommandeClient AS o WHERE o.dateCreation BETWEEN :minDateCreation AND :maxDateCreation ");
		minDate = minDate != null ? minDate : PharmaDateUtil.parse("10-10-2010 00:00", PharmaDateUtil.DATETIME_PATTERN_LONG);
		maxDate = maxDate != null ? maxDate : PharmaDateUtil.parse("10-10-2050 00:00", PharmaDateUtil.DATETIME_PATTERN_LONG);
		if (StringUtils.isNotBlank(cmdNumber)) {
			return entityManager().createQuery("SELECT o FROM CommandeClient AS o WHERE  o.cmdNumber = :cmdNumber ", CommandeClient.class).setParameter("cmdNumber", "CMD-" + cmdNumber).getResultList();
		} else {
			if (!typeCommande.equals(TypeCommande.ALL)) {
				searchQuery.append(" AND o.typeCommande = :typeCommande ");
			}
			if (!status.equals(Etat.ALL)) {
				searchQuery.append(" AND o.status = :status ");
			}
			if (client != null) {
				searchQuery.append(" AND o.client = :client ");
			}
			TypedQuery<CommandeClient> q = entityManager().createQuery(searchQuery.append(" ORDER BY o.id DESC").toString(), CommandeClient.class);
			if (!status.equals(Etat.ALL)) {
				q.setParameter("status", status);
			}
			if (client != null) {
				q.setParameter("client", client);
			}
			if (!typeCommande.equals(TypeCommande.ALL)) {
				q.setParameter("typeCommande", typeCommande);
			}
			q.setParameter("minDateCreation", minDate);
			q.setParameter("maxDateCreation", maxDate);
			return q.getResultList();
		}
	}

	public static TypedQuery<CommandeClient> searchTypeQuery(String cmdNumber, Etat status, Date minDate, Date maxDate, Client client, TypeCommande typeCommande) {
		StringBuilder searchQuery = new StringBuilder("SELECT o FROM CommandeClient AS o WHERE o.dateCreation BETWEEN :minDateCreation AND :maxDateCreation ");
		minDate = minDate != null ? minDate : PharmaDateUtil.parse("10-10-2010 00:00", PharmaDateUtil.DATETIME_PATTERN_LONG);
		maxDate = maxDate != null ? maxDate : PharmaDateUtil.parse("10-10-2050 00:00", PharmaDateUtil.DATETIME_PATTERN_LONG);
		if (StringUtils.isNotBlank(cmdNumber)) {
			return entityManager().createQuery("SELECT o FROM CommandeClient AS o WHERE  o.cmdNumber = :cmdNumber ", CommandeClient.class).setParameter("cmdNumber", "CMD-" + cmdNumber);
		} else {
			if (!typeCommande.equals(TypeCommande.ALL)) {
				searchQuery.append(" AND o.typeCommande = :typeCommande ");
			}
			if (!status.equals(Etat.ALL)) {
				searchQuery.append(" AND o.status = :status ");
			}
			if (client != null) {
				searchQuery.append(" AND o.client = :client ");
			}
			TypedQuery<CommandeClient> q = entityManager().createQuery(searchQuery.append(" ORDER BY o.id DESC").toString(), CommandeClient.class);
			if (!status.equals(Etat.ALL)) {
				q.setParameter("status", status);
			}
			if (client != null) {
				q.setParameter("client", client);
			}
			if (!typeCommande.equals(TypeCommande.ALL)) {
				q.setParameter("typeCommande", typeCommande);
			}
			q.setParameter("minDateCreation", minDate);
			q.setParameter("maxDateCreation", maxDate);
			return q ;
		}
	}
}
