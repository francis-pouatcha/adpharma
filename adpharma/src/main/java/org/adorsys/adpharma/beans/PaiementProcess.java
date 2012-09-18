package org.adorsys.adpharma.beans;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.adorsys.adpharma.domain.Caisse;
import org.adorsys.adpharma.domain.Client;
import org.adorsys.adpharma.domain.DetteClient;
import org.adorsys.adpharma.domain.Facture;
import org.adorsys.adpharma.domain.PharmaUser;
import org.adorsys.adpharma.domain.TypeCommande;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.springframework.roo.addon.javabean.RooJavaBean;
@RooJavaBean
public class PaiementProcess {

	private boolean showPostForm ;

	private boolean showPutForm ;

	private boolean showDetailForm ;

	private String cassier ;

	private Client client ;

	private String caisseNumber ;

	private String dateOuvertureCaisse ;

	private BigDecimal fondCaisse ;

	private Long factureId ;

	private Facture factureSelected ;

	private	BigInteger detteclient ;

	private BigInteger dettePayeur ;

	private List<Facture>  factureResult = new ArrayList<Facture>();



	public PaiementProcess( Caisse caisse ) {
		cassier = caisse.getCaissier().getDisplayName() ;
		caisseNumber = caisse.getCaisseNumber();
		dateOuvertureCaisse = PharmaDateUtil.format(caisse.getDateOuverture(), "dd-MM-yyyy HH:mm");
		fondCaisse = caisse.getFondCaisse();
	}


	public PaiementProcess( Caisse caisse , Facture facture ) {
		cassier = caisse.getCaissier().getDisplayName() ;
		caisseNumber = caisse.getCaisseNumber();
		dateOuvertureCaisse = PharmaDateUtil.format(caisse.getDateOuverture(), "dd-MM-yyyy HH:mm");
		fondCaisse = caisse.getFondCaisse();
		factureId= facture.getId();
		this.factureSelected = facture ;
		if (facture.getCommande().getTypeCommande().equals(TypeCommande.VENTE_A_CREDIT)) {
			BigDecimal amount = new BigDecimal(facture.getNetPayer());
			amount=amount.multiply(BigDecimal.valueOf(facture.getCommande().getTauxCouverture())).divide(BigDecimal.valueOf(100));
			dettePayeur = amount.toBigInteger();
			detteclient = facture.getNetPayer().subtract(dettePayeur);
		}



	}

	public static Caisse getMyOpenCaisse(PharmaUser caissier){
		List<Caisse> list = Caisse.findCaissesByCaisseOuverteNotAndCaissier(Boolean.FALSE, caissier).getResultList();
		Caisse openCaisse = null ;
		if (!list.isEmpty()) {
			for (Caisse caisse : list) {
				openCaisse = caisse ;
				break ;
			}
		}
		return openCaisse ;	
	}

	public static Caisse getOpenCaisse(){
		List<Caisse> list = Caisse.findCaissesByCaisseOuverteNot(Boolean.FALSE).getResultList();
		Caisse openCaisse = null ;
		if (!list.isEmpty()) {
			for (Caisse caisse : list) {
				openCaisse = caisse ;
				break ;
			}
		}
		return openCaisse ;	
	}


}
