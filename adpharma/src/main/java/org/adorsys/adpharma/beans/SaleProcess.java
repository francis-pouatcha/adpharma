package org.adorsys.adpharma.beans;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.TypedQuery;

import org.adorsys.adpharma.domain.CommandeClient;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.LigneCmdClient;
import org.adorsys.adpharma.domain.Ordonnancier;
import org.adorsys.adpharma.domain.Site;
import org.adorsys.adpharma.domain.TypeCommande;
import org.adorsys.adpharma.services.SaleService;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.ui.Model;

/**
 * @author clovis gakam
 *
 */
@RooJavaBean
public class SaleProcess {
	
	private LigneApprovisionement produit ;
	
	private Long cmdId ;

	private List<CommandeClient> commmandes ;

	private LigneCmdClient lineToUpdate ;

	private List<LigneApprovisionement> productResult = new ArrayList<LigneApprovisionement>();

	private List<LigneCmdClient>  ligneCommande = new ArrayList<LigneCmdClient>();


	private Long clientId ;

	private String cmdNumber ;

	private String clientName ;

	private String typeCommande ;		

	private Date dateCommande ;

	private BigDecimal totalCommande ;

	private BigDecimal totalRemise ;

	private BigDecimal netApayer ;

	private String displayName ;

	private Ordonnancier ordonnancier;


	private void ordonne(Long cmdId, Model uiModel){

		List<Ordonnancier> ordonnanciers;
		try {
			ordonnanciers = Ordonnancier.findOrdonnanciersByCommande(CommandeClient.findCommandeClient(cmdId)).getResultList();
		} catch (Exception e) {
			ordonnanciers = new ArrayList<Ordonnancier>();
		}
		if (!ordonnanciers.isEmpty()) {
			this.ordonnancier = ordonnanciers.iterator().next();
			uiModel.addAttribute("ordonnancier", this.ordonnancier);

		}/*else { 
			this.ordonnancier = new Ordonnancier();
			uiModel.addAttribute("ordonnancier", new Ordonnancier());
		}*/

	}


	public SaleProcess(CommandeClient commande, Model uiModel) {
		super();
		cmdId = commande.getId() ;
		clientId = commande.getClient().getId();
		clientName = commande.getClient().getNomComplet() ;
		dateCommande = commande.getDateCreation();
		cmdNumber = commande.getCmdNumber() ;
		typeCommande = commande.getTypeCommande().name();
		displayName = displayName() ;
		if (commande.getTypeCommande().equals(TypeCommande.VENTE_A_CREDIT)) {
			displayName = displayName + ", PART CLIENT :"+commande.getPartClient().longValue()+" FCFA";
		}

		calculPrix(commande);
		ordonne(cmdId, uiModel);
	}

	public void calculPrix(CommandeClient commande){
		commande.calculPrixHtAndRemise();
		commande.calculNetApayer();
		commande.merge();
		this.setTotalCommande(commande.getMontantHt());
		this.setTotalRemise(commande.getRemise());
		this.setNetApayer(commande.getMontantTtc());

	}

	/**
	 * @param commande
	 * @param remise
	 * @param uiModel
	 */
	public SaleProcess(CommandeClient commande,BigDecimal remise, Model uiModel){
		cmdId = commande.getId() ;
		clientId = commande.getClient().getId();
		clientName = commande.getClient().getNomComplet() ;
		dateCommande = commande.getDateCreation();
		cmdNumber = commande.getCmdNumber() ;
		typeCommande = commande.getTypeCommande().name(); 
		displayName = displayName() ;
		if (commande.getTypeCommande().equals(TypeCommande.VENTE_A_CREDIT)) {
			displayName = displayName + " PART CLIENT :"+commande.getPartClient().longValue()+" FCFA";
		}
		commande.addOtherRemise(remise);
		commande.calculPrixHtAndRemise();
		//commande.setRemise(commande.getRemise().add(remise));
		commande.calculNetApayer();
		commande.merge();
		this.setTotalCommande(commande.getMontantHt());
		this.setTotalRemise(commande.getRemise());
		this.setNetApayer(commande.getMontantTtc());
		ordonne(cmdId, uiModel);

	}

	public SaleProcess(Long cmdId, List<LigneCmdClient> ligneCommande, Model uiModel) {
		this.cmdId = cmdId;
		this.ligneCommande = ligneCommande;
		typeCommande = CommandeClient.findCommandeClient(cmdId).getTypeCommande().name();
		ordonne(cmdId, uiModel);
	}
	public SaleProcess(Model uiModel) {
		ordonne(this.cmdId, uiModel);
	}


	public SaleProcess(Long cmdId, Model uiModel) {
		this.cmdId = cmdId;
		typeCommande = CommandeClient.findCommandeClient(cmdId).getTypeCommande().name();
		ordonne(cmdId, uiModel);

	}

	//genere les ligne de commande pour un cipm donne 

	public static  void addline(Long lineId , BigInteger quantite ,BigDecimal remise ,CommandeClient commandeClient){
		new SaleService().addline(lineId, quantite, remise, commandeClient);
	}

	public static void addlineForcer(Long lineId , BigInteger quantite ,BigDecimal remise ,CommandeClient commandeClient){
		
		new SaleService().addlineForcer(lineId, quantite, remise, commandeClient);
	}

	public static void upateLine(Long lineId , BigInteger quantite ,BigDecimal remise ,CommandeClient commandeClient){
		LigneCmdClient line =  LigneCmdClient.findLigneCmdClient(lineId);
		LigneApprovisionement ligneApp = line.getProduit();
		line.remove() ;
		addline(ligneApp.getId(), quantite, remise, commandeClient);

	}


	


	public List<LigneCmdClient> getLigneCommande() {
		return ligneCommande;
	}
	public void setLigneCommande(List<LigneCmdClient> ligneCommande) {
		this.ligneCommande = ligneCommande;

	}

	private String displayName(){
		return new StringBuilder().append(" "+cmdNumber).append("  , du: ").append(PharmaDateUtil.format(dateCommande, "dd-MM-yyyy HH:mm")).append("  , ").append(clientName).append("   ,     "+typeCommande).toString();
	}

}
