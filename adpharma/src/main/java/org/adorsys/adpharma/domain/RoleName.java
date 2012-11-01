package org.adorsys.adpharma.domain;


public enum RoleName {

    ROLE_ADMIN("ADMINISTRATEUR"," Gere des Utilisateurs"),
    ROLE_STOCKER("MAGASINIER" ,"Gere des aprovisionnements , du catalogue de produits , les mouvements de stocks"),
    ROLE_CASHIER("CAISSIER","Gere les Encaissements "), 
    ROLE_SITE_MANAGER("MANAGER" ,"Responsable du site a tous les profiles exclu l administration"), 
    ROLE_VENDEUR("VENDEUR" ," gere les Ventes "),
    ROLE_SUPER_ADMIN("SUPER ADMINISTRATEUR","Retabli le systeme en cas de bloquage") , 
    ROLE_OPEN_SALE_SESSION("SESSION DE VENTE" ,"gere les Ventes par des utilisateur differents"),
    ROLE_INVENTAIRE("GESTION INVENTAIRE","Effectue les Inventaires"),
    ROLE_GESTION_DETTE("GESTION DE DETTE","Edite les Etats de dette des Clients"),
    ROLE_RETOUR_PRODUIT("RETOUR DE PRODUITS","Effectue Les Retours De produits")   ;


	private String nameFr ;
	private String description ;

	RoleName(String name ,String description){
		this.nameFr = name ;
		this.description = description ;
	}
	
	@Override
	public String toString(){
		 String desc = description.toLowerCase();
		return nameFr +"( "+desc+" )";
	}
	
	
	public String getNameFr(){
		return nameFr ;
	}
	
	public String getDescription(){
		return description ;
	}
	

	public void setNameFr( String nameFr){
		this.nameFr =nameFr ;
	}
	
	public void setDescription( String description){
		this .description = description ;
	}

}
