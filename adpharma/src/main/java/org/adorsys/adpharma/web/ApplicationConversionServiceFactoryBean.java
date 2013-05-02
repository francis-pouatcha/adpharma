package org.adorsys.adpharma.web;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.adorsys.adpharma.domain.Approvisionement;
import org.adorsys.adpharma.domain.Caisse;
import org.adorsys.adpharma.domain.CategorieClient;
import org.adorsys.adpharma.domain.Client;
import org.adorsys.adpharma.domain.CommandeClient;
import org.adorsys.adpharma.domain.CommandeFournisseur;
import org.adorsys.adpharma.domain.DetteClient;
import org.adorsys.adpharma.domain.Devise;
import org.adorsys.adpharma.domain.Facture;
import org.adorsys.adpharma.domain.FamilleProduit;
import org.adorsys.adpharma.domain.Filiale;
import org.adorsys.adpharma.domain.FootPrint;
import org.adorsys.adpharma.domain.Fournisseur;
import org.adorsys.adpharma.domain.ModeConditionement;
import org.adorsys.adpharma.domain.OperationCaisse;
import org.adorsys.adpharma.domain.PharmaUser;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.Rayon;
import org.adorsys.adpharma.domain.Site;
import org.adorsys.adpharma.domain.SousFamilleProduit;
import org.adorsys.adpharma.domain.TVA;
import org.adorsys.adpharma.domain.TauxMarge;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.hibernate.ejb.EntityManagerFactoryImpl;
import org.hibernate.ejb.internal.EntityManagerFactoryRegistry;
import org.hibernate.engine.spi.PersistenceContext;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.roo.addon.web.mvc.controller.RooConversionService;

/**
 * A central place to register application converters and formatters. 
 */
@RooConversionService
public class ApplicationConversionServiceFactoryBean extends FormattingConversionServiceFactoryBean {
	@Override
	protected void installFormatters(FormatterRegistry registry) {
		super.installFormatters(registry);
		// Register application converters and formatters
        registry.addConverter(new ModeConditionementConverter());
        registry.addConverter(new FootPrintConverter());
        registry.addConverter(new CommandeFournisseurConverter());
        registry.addConverter(new ApprovisionementConverter());
        registry.addConverter(new DeviseConverter());
        registry.addConverter(new FamilleProduitConverter());
        registry.addConverter(new FournisseurConverter());
        registry.addConverter(new PharmaUserConverter());
        registry.addConverter(new ProduitConverter());
        registry.addConverter(new RayonConverter());
        registry.addConverter(new SiteConverter());
        registry.addConverter(new SousFamilleProduitConverter());
        registry.addConverter(new TVAConverter());
        registry.addConverter(new TauxMargeConverter());
        registry.addConverter(new CaisseConverter());
        registry.addConverter(new CategorieClientConverter());
        registry.addConverter(new ClientConverter());
        registry.addConverter(new OperationCaisseConverter());
        registry.addConverter(new CommandeClientConverter());
        registry.addConverter(new DetteClientConverter());
        registry.addConverter(new FactureConverter());
        registry.addConverter(new FilialeConverter());

       
	}
	 static class FilialeConverter implements Converter<Filiale, String> {
	        public String convert(Filiale filiale) {
	            return  filiale.toString();
	        }
	        
	    }
	
	static class CommandeClientConverter implements Converter<CommandeClient, String> {
        public String convert(CommandeClient commandeClient) {
            return new StringBuilder().append(commandeClient.getCmdNumber()).append(" Du ").append(PharmaDateUtil.format(commandeClient.getDateCreation(),"dd-MM-yyyy HH:mm") ).toString();
        }
        
    }
    
    static class DetteClientConverter implements Converter<DetteClient, String> {
        public String convert(DetteClient detteClient) {
            return new StringBuilder().append(detteClient.getFactureId()).append(" ").append(detteClient.getClientId()).append(" ").append(detteClient.getClientName()).append(" ").append(detteClient.getDateCreation()).toString();
        }
        
    }
    
    static class FactureConverter implements Converter<Facture, String> {
        public String convert(Facture facture) {
            return new StringBuilder().append(facture.getFactureNumber()).append(" du: ").append(PharmaDateUtil.format(facture.getDateCreation(),"dd-MM-yyyy HH:mm") ).append(" ").append(facture.getMontantTotal()).append(" ").append(facture.getMontantRemise()).toString();
        }
        
    }
	
	  static class OperationCaisseConverter implements Converter<OperationCaisse, String> {
	        public String convert(OperationCaisse operationCaisse) {
	            return new StringBuilder().append(operationCaisse.getOpNumber()).append(" ").append(operationCaisse.getDateOperation()).append(" ").append(operationCaisse.getMontant()).append(" ").append(operationCaisse.getRaisonOperation()).toString();
	        }
	        
	    }
	 static class ModeConditionementConverter implements Converter<ModeConditionement, String> {
	        public String convert(ModeConditionement modeConditionement) {
	            return new StringBuilder().append(modeConditionement.getLibelle()).toString();
	        }
	        
	    }
	
	 static class ClientConverter implements Converter<Client, String> {
	        public String convert(Client client) {
	            return new StringBuilder().append(client.getNomComplet()).append(" , ").append(client.getClientNumber()).toString();
	        }
	        
	    }
	    
	
	 static class CategorieClientConverter implements Converter<CategorieClient, String> {
	        public String convert(CategorieClient categorieClient) {
	            return new StringBuilder().append(categorieClient.getLibelle()).toString();
	        }
	        
	    }
	
	static class CaisseConverter implements Converter<Caisse, String> {
        public String convert(Caisse caisse) {
            return new StringBuilder().append(caisse.getCaisseNumber()).append(" , ").append(caisse.getCaissier().getFullName()).toString();
        }
        
    }
	
	 static class FootPrintConverter implements Converter<FootPrint, String> {
	        public String convert(FootPrint footPrint) {
	            return footPrint.toString();
	        }
	    }

	    static class CommandeFournisseurConverter implements Converter<CommandeFournisseur, String> {
	        public String convert(CommandeFournisseur commandeFournisseur) {
	            return commandeFournisseur.displayName() ;        
	    }
}

		 static class ApprovisionementConverter implements Converter<Approvisionement, String> {
		        public String convert(Approvisionement approvisionement) {
		            return approvisionement.displayName();
		        }
		    }
		 
		 static class DeviseConverter implements Converter<Devise, String> {
		        public String convert(Devise devise) {
		            return devise.getLibelle();
		        }
		    }
		 static class FamilleProduitConverter implements Converter<FamilleProduit, String> {
		        public String convert(FamilleProduit familleProduit) {
		            return familleProduit.getLibelleFamille();
		        }
		    }
		 static class FournisseurConverter implements Converter<Fournisseur, String> {
		        public String convert(Fournisseur fournisseur) {
		            return fournisseur.displayName();
		        }
		    }
		
		 static class   PharmaUserConverter implements Converter< PharmaUser,  String> {
		        public String convert(PharmaUser pharmaUser) {
		            return  pharmaUser.displayName();
		        }
		    }
		 static class    ProduitConverter implements Converter< Produit,  String> {
		        public String convert( Produit  produit) {
		            return   produit.displayName();
		        }
		    }
		 static class   RayonConverter implements Converter<Rayon,  String> {
		        public String convert( Rayon rayon) {
		            return  rayon.displayName();
		        }
		    }
		 
		 static class   SiteConverter implements Converter<Site,  String> {
		        public String convert(Site site) {
		            return  site.displayName();
		        }
		    }
		 
		 static class   SousFamilleProduitConverter implements Converter<SousFamilleProduit,  String> {
		        public String convert(SousFamilleProduit sousFamilleProduit) {
		            return  sousFamilleProduit.displayName();
		        }
		    }
		 static class    TVAConverter implements Converter<TVA,  String> {
		        public String convert(TVA tVA) {
		            return tVA.displayName();
		        }
		    }
		 static class  TauxMargeConverter implements Converter<TauxMarge,  String> {
		        public String convert(TauxMarge tauxMarge) {
		            return tauxMarge.displayName();
		        }
		    }
		 
		
		 
		
}
