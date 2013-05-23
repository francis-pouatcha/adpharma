package org.adorsys.adpharma.beans;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.adorsys.adpharma.domain.Approvisionement;
import org.adorsys.adpharma.domain.CommandeFournisseur;
import org.adorsys.adpharma.domain.DestinationMvt;
import org.adorsys.adpharma.domain.Etat;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.MouvementStock;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.TypeMouvement;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.services.InventoryService;
import org.adorsys.adpharma.utils.Contract;
import org.springframework.roo.addon.javabean.RooJavaBean;


@RooJavaBean
public class ApprovisonementProcess {

	private Produit produit ;

	private transient Etat etat = Etat.EN_COUR ;

	private Long apId ;

	private String displayName ;

	private transient BigDecimal taux = BigDecimal.ZERO;

	private LigneApprovisionement lineToUpdate ;

	private List<Produit> productResult = new ArrayList<Produit>();
	
	private CommandeFournisseur commande;
	
	
	// Variables pour la pagination dans la modifications des lignes d'approvisionement de la preparation
	private int size;
	
	private int index;
	
	public int getSize() {
		return size;
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	
	public CommandeFournisseur getCommande() {
		return commande;
	}
	
	public void setCommande(CommandeFournisseur commande) {
		this.commande = commande;
	}

	public Etat getEtat() {
		return etat;
	}


	public void setEtat(Etat etat) {
		this.etat = etat;
	}

	private List<CommandeFournisseur> commandeFournisseur = new ArrayList<CommandeFournisseur>();

	private List<LigneApprovisionement>  ligneApprovisionements = new ArrayList<LigneApprovisionement>();


	public ApprovisonementProcess(Long apId) {
		super();
		this.apId = apId;
		displayName = displayName();
	}


	public void closeApprovisionement(){

		Contract.notNull("apId", apId);
		Approvisionement approvisionement = Approvisionement.findApprovisionement(apId);
		List<LigneApprovisionement> lines = LigneApprovisionement.findLigneApprovisionementsByApprovisionement(approvisionement).getResultList();
		Contract.notNull("approvisionnement", approvisionement);
		Contract.notNull("ligne approvisionement ", lines);

		for (LigneApprovisionement ligneApprovisionement : lines) {
			// creation du mouvement de stock
			Produit produit = ligneApprovisionement.getProduit() ;
			MouvementStock mouvementStock = new MouvementStock();
			mouvementStock.setAgentCreateur(SecurityUtil.getUserName());
			mouvementStock.setDestination(DestinationMvt.MAGASIN);
			mouvementStock.setNumeroBordereau(approvisionement.getBordereauNumber());
			mouvementStock.setNumeroTicket("-");
			mouvementStock.setOrigine(DestinationMvt.FOURNISSEUR);
			mouvementStock.setSite(approvisionement.getMagasin());
			mouvementStock.setQteDeplace(ligneApprovisionement.getQuantiteAprovisione());
			mouvementStock.setCip(ligneApprovisionement.getProduit().getCip());
			mouvementStock.setCipM(ligneApprovisionement.getCipMaison());
			mouvementStock.setDesignation(ligneApprovisionement.getProduit().getDesignation());
			mouvementStock.setTypeMouvement(TypeMouvement.APPROVISIONEMENT);
			mouvementStock.setQteInitiale(BigInteger.ZERO);
			mouvementStock.setPAchatTotal(ligneApprovisionement.getPrixAchatTotal().toBigInteger());
			mouvementStock.setPVenteTotal(ligneApprovisionement.getPrixVenteUnitaire().toBigInteger().multiply(ligneApprovisionement.getQuantiteAprovisione()));
			ligneApprovisionement.setQuantieEnStock(ligneApprovisionement.getQuantiteAprovisione());//initialisation de la quantite en stock de cette ligne 
			ligneApprovisionement.getProduit().setDateDerniereEntre(new Date());// mis a jour de la date de derniere entree
			ligneApprovisionement.setVenteAutorise(produit.isVenteAutorise());
			//ligneApprovisionement.merge();
			produit.addproduct(ligneApprovisionement.getQuantieEnStock());  // mis a jou du stock de produit 
			ligneApprovisionement.compenserStock();
			produit.setQuantiteEnStock(InventoryService.stock(produit));
			produit.setPrixAchatU(ligneApprovisionement.getPrixAchatUnitaire());
			produit.setPrixVenteU(ligneApprovisionement.getPrixVenteUnitaire());
			produit.merge();
			mouvementStock.setQteFinale(mouvementStock.getQteInitiale().add(mouvementStock.getQteDeplace()));
			mouvementStock.persist();
			ligneApprovisionement.merge();
		}
		approvisionement.close();
		approvisionement.merge();




	}

	public static void specialDeleteLine(LigneApprovisionement ligne){
		Produit produit2 = ligne.getProduit();
		produit2.removeProduct(ligne.getQuantieEnStock());
		produit2.merge();
		ligne.remove();
	}

	public static void specialAddLine(LigneApprovisionement ligne){
		Produit produit2 = ligne.getProduit();
		produit2.removeProduct(ligne.getQuantieEnStock());
		produit2.merge();
		ligne.remove();
	}

	public String displayName(){
		return Approvisionement.findApprovisionement(apId).toString();
	}


	public BigDecimal getTaux() {
		return taux;
	}


	public void setTaux(BigDecimal taux) {
		this.taux = taux;
	}

	public static BigInteger getQteTOCompensate(String cip){
		return Produit.findQteToCompensate(cip, BigInteger.ZERO);

	}

	public static List<LigneApprovisionement> getLineToCompensate(String cip){
		return Produit.findByCipAndQteStockLessThan(cip, BigInteger.ZERO).getResultList();
	}

	public static void compensateProduct(String cip ,LigneApprovisionement lineIn){
		BigInteger qte = getQteTOCompensate(cip);
		if(qte .intValue() == 0 ) return ;
		List<LigneApprovisionement> lineToCompensate = getLineToCompensate(cip);
		if(lineToCompensate.isEmpty()) return ;
		BigInteger stockIn = lineIn.getQuantieEnStock();

	}

}
