package org.adorsys.adpharma.beans;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.adorsys.adpharma.domain.CategorieClient;
import org.adorsys.adpharma.domain.Client;
import org.adorsys.adpharma.domain.Devise;
import org.adorsys.adpharma.domain.Filiale;
import org.adorsys.adpharma.domain.Fournisseur;
import org.adorsys.adpharma.domain.PharmaUser;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.Rayon;
import org.adorsys.adpharma.domain.Site;
import org.adorsys.adpharma.domain.TVA;
import org.adorsys.adpharma.domain.TauxMarge;
import org.adorsys.adpharma.domain.virtualtable.ProduitV;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.OncePerRequestFilter;
@Service
@Transactional
public class AdPharmaInitFilter extends OncePerRequestFilter {
	boolean done=false;
	@Override
	protected void doFilterInternal(HttpServletRequest arg0,HttpServletResponse arg1, FilterChain arg2)
			throws ServletException, IOException {
		if(!done){
			Site.initSite();
			PharmaUser.initPharmaUser();
			TVA.initTva();
			Fournisseur.initFournisseur();
			TauxMarge.initTauxMarge();
			Devise.initDevise();
			CategorieClient.initCategorieClient();
			Client.initClient() ;
			Filiale.initFiliale();
			done=true;
		}
		
		arg2.doFilter(arg0, arg1);
	}

}
