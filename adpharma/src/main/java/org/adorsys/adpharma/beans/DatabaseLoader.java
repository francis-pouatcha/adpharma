package org.adorsys.adpharma.beans;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.adorsys.adpharma.domain.Approvisionement;
import org.adorsys.adpharma.domain.CategorieClient;
import org.adorsys.adpharma.domain.Client;
import org.adorsys.adpharma.domain.CommandeClient;
import org.adorsys.adpharma.domain.Devise;
import org.adorsys.adpharma.domain.Filiale;
import org.adorsys.adpharma.domain.Fournisseur;
import org.adorsys.adpharma.domain.Genre;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.Rayon;
import org.adorsys.adpharma.domain.Site;
import org.adorsys.adpharma.domain.TypeClient;
import org.adorsys.adpharma.domain.virtualtable.ClientV;
import org.adorsys.adpharma.domain.virtualtable.ProduitV;
import org.apache.commons.lang.NumberUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;

public class DatabaseLoader {

	public static int loadRayons( ){
		List<String> allRayons = ProduitV.findAllRayons();
		int i = 0 ;
		if (!allRayons.isEmpty()) {
			System.out.println("[loading  rayons fromcvs file begin ]");
			for (String rayonV : allRayons) {
		if (StringUtils.isNotBlank(rayonV)) {
				Rayon rayon = new Rayon();
				rayon.setMagasin(Site.findSite(Long.valueOf(1)));
			   String 	substring =  getRayonName(rayonV);
				rayon.setName(substring);

				if (Rayon.findRayonsByNameEquals(rayon.getName()).getResultList().isEmpty()) {
					i++ ;
					rayon.persist();
					System.out.println("rayon "+i);
				}


		}
			}
			System.out.println("[loading  rayons from  cvs file end ]");




		}
		return i;

	}
	
	public static int loadClients( ){
		 List<ClientV> clientVs = ClientV.findAllClientVs();
		int i = 0 ;
		if (!clientVs.isEmpty()) {
			System.out.println("[loading  rayons fromcvs file begin ]");
			for (ClientV clientV : clientVs) {
				Client client = new Client();
				client.setNom(clientV.getName());
				client.setTypeClient(TypeClient.PHYSIQUE);
				client.setEmployeur(clientV.getEmployeur());
				client.setCategorie(CategorieClient.findCategorieClient(new Long(i)));
				client.setCreditAutorise(Boolean.TRUE);
				client.setSexe(Genre.Neutre);
				client.setRemiseAutorise(true);
                client.persist();
                i++;
			}




		}
		return i;

	}

	
	public static String getRayonName( String rayonV){
		String substring = "";

		if (rayonV.contains(":")) {
			 substring = rayonV.substring(rayonV.indexOf(":")+1, rayonV.length());
		}else {
			substring = rayonV ;
		}
		
		return substring ;
	}
	
	


	public static BigDecimal getPrice( String price){
		String	replace = "";
	    if (StringUtils.isNotBlank(price)) {
	    	for (int i = 0; i < price.length(); i++) {
				if (NumberUtils.isNumber(""+price.charAt(i))) {
				replace =	replace.concat(""+price.charAt(i));
				}
			}
		}else {
			replace = "1";
		}
	
	    
			System.out.println(replace.trim());
		if (StringUtils.isNotBlank(replace)) {
			return	 new BigDecimal(replace.trim());
		}else {
			return BigDecimal.ZERO ;
		}

	}

	public static void correctNullLine(){
		List<LigneApprovisionement> resultList = findNullDesLine().getResultList();
		if (!resultList.isEmpty()) {
			for (LigneApprovisionement ligneApprovisionement : resultList) {
				ligneApprovisionement.merge();
				
			}
			
			
		}
		CommandeClient findCommandeClient = CommandeClient.findCommandeClient(new Long(451));
		CommandeClient findCommandeClient2 = CommandeClient.findCommandeClient(new Long(455));
       findCommandeClient2.setEncaisse(Boolean.FALSE);
       findCommandeClient.setEncaisse(Boolean.FALSE);
       findCommandeClient.merge();
       findCommandeClient2.merge();
		
	}

	 public static TypedQuery<LigneApprovisionement> findNullDesLine() {
	        EntityManager em = LigneApprovisionement.entityManager();
	        TypedQuery<LigneApprovisionement> q = em.createQuery("SELECT o FROM LigneApprovisionement AS o WHERE o.designation IS NULL ", LigneApprovisionement.class);
	        return q;
	    }
	
	@Transactional
	public static void loadApprovisionnement(){
		List<ProduitV> allProduitVs = ProduitV.findAllProduitVs();
		if (!allProduitVs.isEmpty()) {
	Approvisionement approvisionement = new Approvisionement();
			approvisionement.setFounisseur(Fournisseur.findFournisseur(Long.valueOf(1)));
			approvisionement.setMagasin(Site.findSite(Long.valueOf(1)));
			approvisionement.setDevise(Devise.findDevise(Long.valueOf(1)));
			approvisionement.setMontantHt(BigDecimal.ZERO);
			approvisionement.close() ;
			approvisionement.persist();
			
		//	Approvisionement approvisionement = Approvisionement.findApprovisionement(new Long(1));
			for (ProduitV produitV : allProduitVs) {
				List<Produit> resultList = Produit.findProduitsByCipEquals(produitV.getCip()).getResultList();
				System.out.println(resultList.isEmpty());
				if (!resultList.isEmpty()) {
					Produit prd = resultList.iterator().next();
				    String filiale =	produitV.getFiliale();
					 if (StringUtils.isNotBlank(filiale)) {
					    	prd.setFiliale(Filiale.findFiliale(DatabaseLoader.getPrice(filiale).longValue()));
					    	
						}
				List<Rayon> list=	 Rayon.findRayonsByNameEquals(getRayonName(produitV.getRayon())).getResultList();
				if (!list.isEmpty()) {
					prd.setRayon(list.iterator().next());
				}
					LigneApprovisionement ligneApprovisionement = new LigneApprovisionement();
					ligneApprovisionement.setApprovisionement(approvisionement);
					ligneApprovisionement.setPrixAchatUnitaire(DatabaseLoader.getPrice(produitV.getPachat()));
					ligneApprovisionement.setPrixVenteUnitaire(DatabaseLoader.getPrice(produitV.getPvente()));
					ligneApprovisionement.setCipMaison(produitV.getCipMaison());
					ligneApprovisionement.setProduit((Produit)prd.merge());
					ligneApprovisionement.setQuantiteAprovisione(DatabaseLoader.getPrice(produitV.getQte()).toBigInteger());
					ligneApprovisionement.CalculerMargeBrute();
					ligneApprovisionement.CalculeQteEnStock();
					ligneApprovisionement.persist();
					/*MouvementStock mouvementStock = new MouvementStock();
					mouvementStock.setAgentCreateur(SecurityUtil.getUserName());
					mouvementStock.setDestination(DestinationMvt.MAGASIN);
					mouvementStock.setNumeroBordereau(approvisionement.getBordereauNumber());
					mouvementStock.setNumeroTicket("-//-");
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
				*/	ligneApprovisionement.setQuantieEnStock(ligneApprovisionement.getQuantiteAprovisione());//initialisation de la quantite en stock de cette ligne 
					ligneApprovisionement.getProduit().setDateDerniereEntre(new Date());// mis a jour de la date de derniere entree
					ligneApprovisionement.setVenteAutorise(true);
					ligneApprovisionement.getProduit().addproduct(ligneApprovisionement.getQuantieEnStock());  // mis a jou du stock de produit 
				// mis a jou du stock de produit 
				//	mouvementStock.setQteFinale(ligneApprovisionement.getQuantiteAprovisione());
				//	mouvementStock.persist();
					approvisionement.increaseMontant(ligneApprovisionement.getPrixAchatTotal());
					ligneApprovisionement.getProduit().merge();
				}
				
				
				
					//	produitV.remove();
				}
			approvisionement.merge();
			}
			
			
		}

	
	
}
