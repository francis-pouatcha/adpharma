package org.adorsys.adpharma.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.adorsys.adpharma.domain.Devise;
import org.adorsys.adpharma.domain.FamilleProduit;
import org.adorsys.adpharma.domain.Filiale;
import org.adorsys.adpharma.domain.Fournisseur;
import org.adorsys.adpharma.domain.Genre;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.PharmaUser;
import org.adorsys.adpharma.domain.Rayon;
import org.adorsys.adpharma.domain.RoleName;
import org.adorsys.adpharma.domain.SousFamilleProduit;
import org.adorsys.adpharma.security.SecurityUtil;
import org.springframework.ui.Model;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

public class ProcessHelper {

	public   static String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
		String enc = httpServletRequest.getCharacterEncoding();
		if (enc == null) {
			enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
		}
		try {
			pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
		}
		catch (UnsupportedEncodingException uee) {}
		return pathSegment;
	}

	public static void addDateTimeFormatPatterns(Model uiModel) {
		uiModel.addAttribute("commande_limiteddate_date_format", "dd-MM-yyyy HH:mm");
		uiModel.addAttribute("client_datenaissance_date_format", "dd-MM-yyyy HH:mm");
		uiModel.addAttribute("voyage_landingdate_date_format", "dd-MM-yyyy HH:mm");
		uiModel.addAttribute("default_date_format", "dd-MM-yyyy");
		uiModel.addAttribute("booking_simpledate_date_format", "dd-MM-yyyy");
		uiModel.addAttribute("facture_mindatecreation_date_format", "dd-MM-yyyy HH:mm");
		uiModel.addAttribute("facture_maxdatecreation_date_format", "dd-MM-yyyy HH:mm");
		uiModel.addAttribute("ordonnancier_dateprescription_date_format", "dd-Mm-yyyy");
		uiModel.addAttribute("ordonnancier_datesaisie_date_format", "dd-Mm-yyyy");
		uiModel.addAttribute("ticket_reservationdate_date_format", "yyyy-MM-dd hh:mm:ss");
		uiModel.addAttribute("ticket_maxreservationdate_date_format", "yyyy-MM-dd hh:mm:ss");
		uiModel.addAttribute("customer_minbirthdate_date_format", "dd-MM-yyyy");
		uiModel.addAttribute("ligneCmdFournisseur_datesaisie_date_format", "dd-MM-yyyy");
		uiModel.addAttribute("commandeFournisseur_datecreation_date_format", "dd-MM-yyyy HH:mm");
		uiModel.addAttribute("commandeFournisseur_datelimitelivraison_date_format", "dd-MM-yyyy HH:mm");
		uiModel.addAttribute("approvisionement_datebordereau_date_format", "dd-MM-yyyy");
		uiModel.addAttribute("approvisionement_datecommande_date_format", "dd-MM-yyyy");
		uiModel.addAttribute("approvisionement_datelivraison_date_format", "dd-MM-yyyy");
		uiModel.addAttribute("approvisionement_datereglement_date_format", "dd-MM-yyyy");
		uiModel.addAttribute("approvisionement_datecreation_date_format", "dd-MM-yyyy");
		uiModel.addAttribute("caisse_mindateouverture_date_format", "dd-MM-yyyy HH:mm");
		uiModel.addAttribute("caisse_maxdateouverture_date_format", "dd-MM-yyyy HH:mm");

	}

	public static BigDecimal convertMoneyToCfa(BigDecimal montant ,Devise devise) {
		return BigDecimal.valueOf(montant.multiply(devise.getEquivalenceCfa()).longValue()) ;
	}



	public static Collection<Rayon> populateRayon() {
		Rayon rayon = new Rayon();
		rayon.setName("ALL");
		rayon.setCodeRayon("RAY-0000");
		rayon.setId(Long.valueOf(0));
		ArrayList<Rayon> arrayList = new ArrayList<Rayon>();
		arrayList.add(rayon);
		arrayList.addAll(Rayon.findAllRayons());

		return arrayList;
	}  

	public static Collection<FamilleProduit> populateFamilleProduit() {
		FamilleProduit familleProduit = new FamilleProduit();
		familleProduit.setLibelleFamille("ALL FAMILLES");
		familleProduit.setId(Long.valueOf(0));
		ArrayList<FamilleProduit> arrayList = new ArrayList<FamilleProduit>();
		arrayList.add(familleProduit);
		arrayList.addAll(FamilleProduit.findAllFamilleProduits());

		return arrayList;
	} 
	public static Collection<SousFamilleProduit> populateSousFamilleProduit() {
		SousFamilleProduit sousFamilleProduit = new SousFamilleProduit();
		sousFamilleProduit.setLibelleSousFamille("ALL SOUS FAMILLES");
		sousFamilleProduit.setId(Long.valueOf(0));
		ArrayList<SousFamilleProduit> arrayList = new ArrayList<SousFamilleProduit>();
		arrayList.add(sousFamilleProduit);
		arrayList.addAll(SousFamilleProduit.findAllSousFamilleProduits());

		return arrayList;
	} 

	public static Collection<Filiale> populateFiliale() {
		Filiale filiale = new Filiale();
		filiale.setLibelle("ALL FILIALES");
		filiale.setFilialeNumber("F-0000");
		filiale.setId(Long.valueOf(0));
		ArrayList<Filiale> arrayList = new ArrayList<Filiale>();
		arrayList.add(filiale);
		arrayList.addAll(Filiale.findAllActifFiliales());

		return arrayList;
	}  

	public static Collection<Filiale> populateAllFiliale() {

		return Filiale.findAllActifFiliales();

	}  


	public static Collection<Fournisseur> populateFournisseur() {
		Fournisseur fournisseur = new Fournisseur();
		fournisseur.setName("ALL");
		fournisseur.setFournisseurNumber("F-0000");
		fournisseur.setId(Long.valueOf(0));
		ArrayList<Fournisseur> arrayList = new ArrayList<Fournisseur>();
		arrayList.add(fournisseur);
		arrayList.addAll(Fournisseur.findAllFournisseurs());

		return arrayList;
	}

	public static Collection<PharmaUser> populateUsers() {
		PharmaUser pharmaUser = new PharmaUser();
		pharmaUser.setUserName("ALL");
		pharmaUser.setGender(Genre.Neutre);
		pharmaUser.setFirstName("ALL");
		pharmaUser.setLastName("USERS");
		pharmaUser.setUserNumber("U-0000");
		pharmaUser.setId(Long.valueOf(0));
		ArrayList<PharmaUser> arrayList = new ArrayList<PharmaUser>();
		arrayList.add(pharmaUser);
		arrayList.addAll(PharmaUser.findAllPharmaUsers());

		return arrayList;
	}


	public static Collection<RoleName> populateRoleNames() {
		ArrayList<RoleName> arrayList = new ArrayList<RoleName>() ;
		arrayList.add(RoleName.ROLE_ADMIN);
		arrayList.add(RoleName.ROLE_CASHIER);
		arrayList.add(RoleName.ROLE_SITE_MANAGER);
		arrayList.add(RoleName.ROLE_STOCKER);
		arrayList.add(RoleName.ROLE_INVENTAIRE);
		arrayList.add(RoleName.ROLE_VENDEUR);
		arrayList.add(RoleName.ROLE_OPEN_SALE_SESSION);
		arrayList.add(RoleName.ROLE_GESTION_DETTE);
		arrayList.add(RoleName.ROLE_RETOUR_PRODUIT);

		return arrayList;
	}

	public static Long getRemise( LigneApprovisionement ligneApprovisionement){ 
		BigDecimal remise = BigDecimal.ZERO ;
		PharmaUser pharmaUser = SecurityUtil.getPharmaUser();
		if (ligneApprovisionement.getRemiseAutorise()) {
			if (pharmaUser == null) {
				return remise .longValue();
			}else{
				BigDecimal tauxRemise = pharmaUser.getTauxRemise();
				if (tauxRemise == null ) {
					return remise.longValue() ;
				}else {
					return (ligneApprovisionement.getPrixVenteUnitaire().multiply(tauxRemise.divide( BigDecimal.valueOf(100)))).longValue() ;
				}

			}
		}else {
			return remise .longValue();
		}




	}

	public static BigInteger roundMoney(BigInteger money){
		if(money == null ) throw new IllegalArgumentException("money argument may not be null");
		char c = money.toString().charAt(money.toString().length()-1);
		BigInteger moneyUnit = new BigInteger(""+c); 
		if (moneyUnit.intValue() < 5 && moneyUnit.intValue() > 0) return money.add(BigInteger.valueOf(5).subtract(moneyUnit));
		if (moneyUnit.intValue() > 5) return money.add(BigInteger.valueOf(10).subtract(moneyUnit));
		return money;

	}


}
