package org.adorsys.adpharma.domain;


public enum RoleName {

	 ROLE_ADMIN("label_org_adorsys_adpharma_domain_rolename_admin"),
     ROLE_STOCKER("label_org_adorsys_adpharma_domain_rolename_stocker"),
     ROLE_CASHIER("label_org_adorsys_adpharma_domain_rolename_cashier"),
     ROLE_SITE_MANAGER("label_org_adorsys_adpharma_domain_rolename_sitemanager"),
     ROLE_VENDEUR("label_org_adorsys_adpharma_domain_rolename_vendeur"),
     ROLE_SUPER_ADMIN("label_org_adorsys_adpharma_domain_rolename_superadmin"),
     ROLE_OPEN_SALE_SESSION("label_org_adorsys_adpharma_domain_rolename_opensale_session"),
     ROLE_INVENTAIRE("label_org_adorsys_adpharma_domain_rolename_inventaire"),
     ROLE_GESTION_DETTE("label_org_adorsys_adpharma_domain_rolename_gestion_dettes"),
     ROLE_RETOUR_PRODUIT("label_org_adorsys_adpharma_domain_rolename_retour_produit"),
     ROLE_SAISIE_PRODUIT("label_org_adorsys_adpharma_domain_rolename_saisie_produit"),
     ROLE_DECOMPOSE_ENTREE("label_org_adorsys_adpharma_domain_rolename_decompose_entree"),
     ROLE_SORTIE_ENTREE("label_org_adorsys_adpharma_domain_rolename_sortie_entree"),
     ROLE_PREPARATION_CMD("label_org_adorsys_adpharma_domain_rolename_preparation_cmd"),
     ROLE_RAYON("label_org_adorsys_adpharma_domain_rolename_rayon"),
     ROLE_FOURNISSEUR("label_org_adorsys_adpharma_domain_rolename_fournisseur"),
     ROLE_MOUVEMENTS("label_org_adorsys_adpharma_domain_rolename_mouvements"),
     ROLE_ETATS("label_org_adorsys_adpharma_domain_rolename_etats"),
     ROLE_VENTE_COMPTANT("label_org_adorsys_adpharma_domain_rolename_vente_comptant"),
     ROLE_VENTE_CREDIT("label_org_adorsys_adpharma_domain_rolename_vente_credit"),
     ROLE_VENTE_PROFORMAT("label_org_adorsys_adpharma_domain_rolename_vente_proformat"),
     ROLE_VENTE_VISU("label_org_adorsys_adpharma_domain_rolename_vente_visu"),
     ROLE_GESTION_CATALOGUE("label_org_adorsys_adpharma_domain_rolename_gestion_catalogue"),
     ROLE_RELATION_TRANSFORMATION("label_org_adorsys_adpharma_domain_rolename_relation_transformation"),
	 ROLE_VENTE_FORCEE("label_org_adorsys_adpharma_domain_rolename_vente_forcee"),
	 ROLE_IMPRESSION_CODE_BARE("label_org_adorsys_adpharma_domain_rolename_impression_codebar"),
	 ROLE_VENTE_DESIGNATION("label_org_adorsys_adpharma_domain_rolename_vente_designation"),
	 ROLE_RECHERCHE_PAR_CIPM("label_org_adorsys_adpharma_domain_rolename_recherche_cipm"),
	 ROLE_CHANGER_PRIX_VENTE("label_org_adorsys_adpharma_domain_rolename_change_prixvente"),  
	 ROLE_FUSIONNER_CIP("label_org_adorsys_adpharma_domain_rolename_fusionner_cip"),
	 ROLE_CASH_CONTROLLER("label_org_adorsys_adpharma_domain_rolename_cash_controller") ,
	 ROLE_SUIVI_CLIENT("label_org_adorsys_adpharma_domain_rolename_suivi_client")  ,
	 ROLE_MODIFIER_PRIX_DATE("label_org_adorsys_adpharma_domain_rolename_modifier_prixdate")  ,
	 ROLE_MODIFIER_CIP("label_org_adorsys_adpharma_domain_rolename_modifier_cip") ,
	 ROLE_MODIFIER_LIGNEAP("label_org_adorsys_adpharma_domain_rolename_modifier_ligneapp"),
	 ROLE_CASH_DETTE("label_org_adorsys_adpharma_domain_rolename_cash_dette"),
	 ROLE_CHIFFRE_AFFAIRE_VARIABLE("label_org_adorsys_adpharma_domain_rolename_chiffre_affaire_variable");
	 
   

	
	private String description ;

	RoleName(String description){
		this.description = description ;
	}
	
	@Override
	public String toString(){
		 String desc = description.toLowerCase();
		return name() +"( "+desc+" )";
	}
	
	
	
	
	public String getDescription(){
		return description ;
	}
	

	
	
	public void setDescription( String description){
		this .description = description ;
	}

}
