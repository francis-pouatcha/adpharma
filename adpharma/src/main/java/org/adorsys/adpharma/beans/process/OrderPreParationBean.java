package org.adorsys.adpharma.beans.process;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.adorsys.adpharma.domain.CommandeFournisseur;
import org.adorsys.adpharma.domain.Etat;
import org.adorsys.adpharma.domain.Filiale;
import org.adorsys.adpharma.domain.Fournisseur;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.LigneCmdFournisseur;
import org.adorsys.adpharma.domain.ModeSelection;
import org.adorsys.adpharma.domain.MouvementStock;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.Rayon;
import org.adorsys.adpharma.domain.Site;
import org.apache.commons.lang.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.ModelMap;

/**
 * use for hold all neccessary information during order preparation !
 * @author gakam clovis
 */
public class OrderPreParationBean {


	private CommandeFournisseur commandeFournisseur ;

	private Filiale filiale ;

	private Rayon rayon ;

	/**
	 *  the max quantity of product to retun 
	 */
	private BigInteger  productSelectionQuantity ;

	/**
	 *  
	 */
	private BigInteger minStock ;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy hh:mm")
	private Date beginDate;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy hh:mm")
	private Date deliveryDate;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy hh:mm")
	private Date endDate;

	private Fournisseur fournisseur ;

	private ModeSelection modeSelection;

	private Site magasin ;

	private String beginBy ;

	private String  endBy;

	public OrderPreParationBean(CommandeFournisseur commandeFournisseur) {
		this.commandeFournisseur = commandeFournisseur;
	}

	public OrderPreParationBean() {
	}


	// getters and setter
	public CommandeFournisseur getCommandeFournisseur() {
		return commandeFournisseur;
	}

	public void setCommandeFournisseur(CommandeFournisseur commandeFournisseur) {
		this.commandeFournisseur = commandeFournisseur;
	}

	public Filiale getFiliale() {
		return filiale;
	}

	public void setFiliale(Filiale filiale) {
		this.filiale = filiale;
	}

	public Rayon getRayon() {
		return rayon;
	}

	public void setRayon(Rayon rayon) {
		this.rayon = rayon;
	}

	public BigInteger getProductSelectionQuantity() {
		return productSelectionQuantity;
	}

	public void setProductSelectionQuantity(BigInteger productSelectionQuantity) {
		this.productSelectionQuantity = productSelectionQuantity;
	}

	public BigInteger getMinStock() {
		return minStock;
	}

	public void setMinStock(BigInteger minStock) {
		this.minStock = minStock;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getBeginBy() {
		return beginBy;
	}

	public void setBeginBy(String beginBy) {
		this.beginBy = beginBy;
	}

	public String getEndBy() {
		return endBy;
	}

	public void setEndBy(String endBy) {
		this.endBy = endBy;
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public Fournisseur getFournisseur() {
		return fournisseur;
	}

	public void setFournisseur(Fournisseur fournisseur) {
		this.fournisseur = fournisseur;
	}

	public ModeSelection getModeSelection() {
		return modeSelection;
	}

	public void setModeSelection(ModeSelection modeSelection) {
		this.modeSelection = modeSelection;
	}

	public Site getMagasin() {
		return magasin;
	}

	public void setMagasin(Site magasin) {
		this.magasin = magasin;
	}


	public  List<Produit> getPreparedProductList(){
		if(modeSelection.equals(ModeSelection.RUPTURE_STOCK)){
			TypedQuery<Produit> search = Produit.search(null, null, null, null, beginBy, endBy, rayon, filiale, null,BigInteger.ZERO);
			if(productSelectionQuantity!=null)search.setMaxResults(productSelectionQuantity.intValue()) ;
			return search.getResultList();
		}
		/*
		 if(modeSelection.equals(ModeSelection.PLUS_VENDU)){
			List<Object[]> produitAndQuantiteVendue = MouvementStock.findProduitAndQuantiteVendue(null, null, beginBy, endBy, beginDate, endDate, rayon, filiale);
			if(!produitAndQuantiteVendue.isEmpty()){
				ArrayList<Produit> arrayList = new ArrayList<Produit>();
				for (Object[] objects : produitAndQuantiteVendue) {
					arrayList.add((Produit)objects[0]);
				}
				return arrayList ;
			}
		}*/
		return new ArrayList<Produit>();
	}


	public CommandeFournisseur generateOrder(){
		CommandeFournisseur order = new CommandeFournisseur();
		order.setDateLimiteLivraison(getDeliveryDate());
		order.setEtatCmd(Etat.EN_COUR);
		order.setFournisseur(getFournisseur());
		order.setModeDeSelection(getModeSelection());
		order.setSite(getMagasin());
		order.setLivre(Boolean.FALSE);
		return order;
	}


	public static LigneCmdFournisseur  getOrderItemm(Produit produit, Fournisseur fournisseur,ModeSelection modeSelection,BigInteger minStock){
		produit.getFournisseurPrice(fournisseur);
		LigneCmdFournisseur orderItems = new LigneCmdFournisseur();
		orderItems.setPrixAchatMin(produit.getPrixAchatSTock());
		orderItems.setPrixAVenteMin(produit.getPrixVenteStock());
		orderItems.setProduit(produit);
		orderItems.calculPrixTotal();
		BigInteger plafond = produit.getPlafondStock()==null ? BigInteger.valueOf(50):produit.getPlafondStock();
		BigInteger subtract = plafond.subtract(produit.getQuantiteEnStock());
		if(subtract.intValue()>0)orderItems.setQuantiteCommande(subtract);
		return orderItems ;

	}
	
	public static List<LigneCmdFournisseur>  getOrderItemm(CommandeFournisseur order,List<Object[]> itemifos , ModeSelection modeSelection){
		 ArrayList<LigneCmdFournisseur> arrayList = new ArrayList<LigneCmdFournisseur>();
		if (itemifos == null) return arrayList ;
		
//		if(itemifos.isEmpty() ) return arrayList ;
		for (Object[] objects : itemifos) {
			Produit prd = (Produit) objects[0];
			BigInteger qte = (BigInteger) objects[1];
			BigDecimal pa = (BigDecimal) objects[2];
			BigDecimal pv = (BigDecimal) objects[3];
			LigneCmdFournisseur orderItems = new LigneCmdFournisseur();
			orderItems.setPrixAchatMin(pa);
			orderItems.setPrixAVenteMin(pv);
			orderItems.setProduit(prd);
			orderItems.calculPrixTotal();
			if (modeSelection.equals(ModeSelection.PLUS_VENDU)) {
				orderItems.setQuantiteCommande(qte);
			}else {
				BigInteger plafond = prd.getPlafondStock()==null ? BigInteger.valueOf(50):prd.getPlafondStock();
				BigInteger subtract = plafond.subtract(prd.getQuantiteEnStock());
				if(subtract.intValue()>0)orderItems.setQuantiteCommande(subtract);
			}
			orderItems.setCommande(order);
			orderItems.persist();
			arrayList.add(orderItems) ;
		}
		return arrayList ;
		

	}


	public static TypedQuery<Produit> findProductForPreparation(Filiale filiale ,Rayon rayon, String beginBy,String endBy,Date beginDate,
			Date endDate,ModeSelection modeSelection ,BigInteger qteLimit){
		if(qteLimit==null) qteLimit=BigInteger.ONE;
		StringBuilder searchQuery = new StringBuilder("SELECT o FROM Produit AS o WHERE o.quantiteEnStock  <= :qteLimit AND  o.quantiteEnStock >= :min");

		if (StringUtils.isNotBlank(endBy)) {
			endBy = endBy + "%";
			searchQuery.append(" AND  LOWER(o.designation) <= LOWER(:endBy) ");
		}

		if (StringUtils.isNotBlank(beginBy)) {
			beginBy = beginBy + "%";
			searchQuery.append(" AND  LOWER(o.designation) >= LOWER(:beginBy) ");
		}

		if (rayon != null) {
			searchQuery.append(" AND o.rayon = :rayon ");
		}
		if (filiale != null) {
			searchQuery.append(" AND o.filiale = :filiale ");
		}
		EntityManager em = Produit.entityManager();
		TypedQuery<Produit> q = em.createQuery(searchQuery.append(" ORDER BY o.designation ASC").toString(), Produit.class);

		if (StringUtils.isNotBlank(endBy)) {
			q.setParameter("endBy", endBy);

		}

		if (StringUtils.isNotBlank(beginBy)) {
			q.setParameter("beginBy", beginBy);

		}

		if (rayon != null) {
			q.setParameter("rayon", rayon);
		}

		if (filiale != null) {
			q.setParameter("filiale", filiale);
		}
		q.setParameter("qteLimit", qteLimit);
		q.setParameter("min", BigInteger.ZERO);

		return q;
	}

}











