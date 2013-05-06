package org.adorsys.adpharma.domain;


public enum RoleName {

	 ROLE_ADMIN("ADMINISTRATEUR", " Gere des Utilisateurs"),
     ROLE_STOCKER("MAGASINIER", "Gere des aprovisionnements , du catalogue de produits , les mouvements de stocks"),
     ROLE_CASHIER("CAISSIER", "Gere les Encaissements "),
     ROLE_SITE_MANAGER("MANAGER", "Responsable du site a tous les profiles exclu l administration"),
     ROLE_VENDEUR("VENDEUR", " gere les Ventes "),
     ROLE_SUPER_ADMIN("SUPER ADMINISTRATEUR", "Retabli le systeme en cas de bloquage"),
     ROLE_OPEN_SALE_SESSION("SESSION DE VENTE", "gere les Ventes par des utilisateur differents"),
     ROLE_INVENTAIRE("GESTION INVENTAIRE", "Effectue les Inventaires"),
     ROLE_GESTION_DETTE("GESTION DE DETTE", "Edite les Etats de dette des Clients"),
     ROLE_RETOUR_PRODUIT("RETOUR DE PRODUITS", "Effectue Les Retours De produits"),
     ROLE_SAISIE_PRODUIT("SAISIE DE PRODUITS", "Effectue Les entrees De produits"),
     ROLE_DECOMPOSE_ENTREE("DECOMPOSER LES ENTREES", "Effectue la decoposition des entrees de produits "),
     ROLE_SORTIE_ENTREE("SORTIR DES PRODUITS", "Effectue la sortie des  produits du stock"),
     ROLE_PREPARATION_CMD("PREPARATION DES COMMANDE", "Effectue la preparation des commandes"),
     ROLE_RAYON("GESTION  DES RAYONS", "Cree modifie des rayons"),
     ROLE_FOURNISSEUR("GESTION  DES FOURNISSEURS", "Cree modifie les fournisseurs"),
     ROLE_MOUVEMENTS("GESTION  DES MOUVEMENTS", "Recherche visualise les mouvements de stock"),
     ROLE_ETATS("GESTION  DES ETATS", "Permet l'edition de different etats "),
     ROLE_VENTE_COMPTANT("VENTE AU COMPTANT", "Permet d'effectuer des ventes au comptant "),
     ROLE_VENTE_CREDIT("VENTE A CREDIT", "Permet d'effectuer des ventes a credit "),
     ROLE_VENTE_PROFORMAT("EDITION PROFORMAT", "Permet d'editer les proformats "),
     ROLE_VENTE_VISU("VISUALISATION VENTE", "Permet de visualiser des ventes  "),
     ROLE_GESTION_CATALOGUE("GESTION DU CATALOGUE", "Permet de creer modifier les produits du catalogues "),
     ROLE_RELATION_TRANSFORMATION("RELATION TRANSFORMATION ", "permet de gerer les relations de transformations  "),
	 ROLE_VENTE_FORCEE(" VENTE FORCEE ", "permet de gerer d'effectuer les ventes forcees"),
	 ROLE_IMPRESSION_CODE_BARE("IMPRESSION CODE BARE ", "permet d'effectuer les impression de code barre "),
	 ROLE_VENTE_DESIGNATION("VENTE PAR DESIGNATION ", "permet d'effectuer les ventes par designation "),
	 ROLE_RECHERCHE_PAR_CIPM("RECHERCHE PRD PAR CIPM ", "permet d'effectuer la recherche de produit par cipm "),
	 ROLE_CHANGER_PRIX_VENTE("CHANGER LE PRIX D'UN PRODUIT A LA VENTE", "permet de changer le prix d'un produit a la vente"),  
	 ROLE_FUSIONNER_CIP("FUSIONNER DES CIP", "permet de fusionner le stock de produit different "),
	 ROLE_CASH_CONTROLLER("CASH CONTROLLER", "la gestion des caisses ") ,
	 ROLE_SUIVI_CLIENT("SUIVIE CLIENT", "permet la gestion et suivi des clients ")  ,
	 ROLE_MODIFIER_PRIX_DATE("MODIFIER PRIX DATE", "permet de modifier le prix et la date de premption d'un produit ")  ;
	 
   

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
