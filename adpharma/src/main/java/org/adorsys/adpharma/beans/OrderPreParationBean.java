package org.adorsys.adpharma.beans;

import java.math.BigInteger;
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
		TypedQuery<Produit> preparation = findProductForPreparation(filiale, rayon, beginBy, endBy, beginDate, endDate, modeSelection, minStock);
		 if(productSelectionQuantity!=null) preparation.setMaxResults(productSelectionQuantity.intValue());
		 return preparation.getResultList();
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
		System.out.println(produit.getPlafondStock());
		System.out.println(produit.getQuantiteEnStock());
		BigInteger subtract = produit.getPlafondStock().subtract(produit.getQuantiteEnStock());
		if(subtract.intValue()>0)orderItems.setQuantiteCommande(subtract);
		return orderItems ;
		
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











