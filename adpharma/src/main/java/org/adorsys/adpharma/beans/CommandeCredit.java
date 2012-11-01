package org.adorsys.adpharma.beans;

import java.math.BigInteger;

import org.adorsys.adpharma.domain.Client;
import org.adorsys.adpharma.domain.CommandeClient;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

@RooJavaBean
public class CommandeCredit {

	public String getNumeroBon() {
		return numeroBon;
	}


	public void setNumeroBon(String numeroBon) {
		this.numeroBon = numeroBon;
	}

	private Long clientId ;

	private Long payeurId ;

	private String clientName ;

	private String payeurName ;
	
	private String numeroBon ;
	
	private boolean ventePartiel = true ;

	private BigInteger taux = BigInteger.valueOf(100) ;

	


	public CommandeCredit(CommandeClient commandeClient){
		clientId = commandeClient.getClient().getId();
		payeurId = commandeClient.getClientPaiyeur().getId();
		clientName = commandeClient.getClient().getNomComplet();
		payeurName = commandeClient.getClientPaiyeur().getNomComplet();
		taux = BigInteger.valueOf(commandeClient.getTauxCouverture());
		numeroBon=  commandeClient.getNumeroBon();
	}

	public CommandeCredit(){

	}
	
	
	public CommandeCredit(Client client){
		clientId = client.getId();
		payeurId = client.getClientPayeur().getId();
		clientName = client.getNomComplet();
		payeurName = client.getClientPayeur().getNomComplet();
		taux = client.getTauxCouverture().toBigInteger();
	}

	public boolean valider(Model uiModel){
		Client client = Client.findClient(getClientId());
		Client payeur = Client.findClient(getPayeurId());
		if (!client.estCredible(BigInteger.ZERO)) {
			uiModel.addAttribute("msgClient", " Vente Impossible ! ce client  n'est pas credible ! veuillez contacter le manager Pour plus d'informations ");
			return false ;
		}
		if (!payeur.estCredible(BigInteger.ZERO)) {
			uiModel.addAttribute("msgPayeur", " Vente Impossible ! le client payeur n'est pas  credible ! veuillez contacter le manager Pour plus d'informations ");
			return false ;
		}
		if (taux ==null || taux.intValue()>100) {
			uiModel.addAttribute("msgTaux", " Vente Impossible ! le Taux doit etre Inferieur a 100!");
			return false ;
		} 

		return true ;


	}

}
