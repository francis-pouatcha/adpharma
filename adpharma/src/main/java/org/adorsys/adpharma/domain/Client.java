package org.adorsys.adpharma.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.PostPersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.adorsys.adpharma.utils.NumberGenerator;
import org.apache.commons.lang.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import antlr.CodeGenerator;
import flexjson.JSONSerializer;

@RooJavaBean
@RooToString
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "Client", finders = { "findClientsByNomCompletLike", "findClientsByClientNumberEquals", "findClientsByNomLike", "findClientsByNomEquals" })
@RooJson
public class Client extends AdPharmaBaseEntity {

    private String clientNumber;

    private String nom;

    private String prenom;

    private String nomComplet;

    private String telephoneFixe;

    private String telephoneMobile;

    private String fax;

    private String email;

    private Boolean creditAutorise = Boolean.TRUE;

    private Boolean remiseAutorise = Boolean.TRUE;

    private BigDecimal plafondCredit = BigDecimal.ZERO;

    private String employeur;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date dateNaissance;

    @Enumerated
    private Genre sexe = Genre.Neutre;

    @NotNull(message="Veuillez entrer le taux de couverture")
    private BigDecimal tauxCouverture = BigDecimal.valueOf(100);

    @Size(max = 256)
    private String note;

    @ManyToOne
    private Client clientPayeur;

    private String clientPayeurNumber;

    @ManyToOne
    private CategorieClient categorie ;

    private BigInteger totalDette = BigInteger.ZERO;

    @Enumerated
    private TypeClient typeClient = TypeClient.PHYSIQUE;

    private String matricule;
    
    @Override
    protected void internalPrePersist() {
        nomComplet = displayName();
        if (categorie == null) {
            categorie = CategorieClient.findCategorieClient(Long.valueOf(1));
        }
    }
 // use to translate to json format 
    
    public String toJson() {
		return new JSONSerializer().include("id","nom","clientPayeur.nomComplet","clientPayeur.id","tauxCouverture","clientPayeurNumber","categorie.categorieNumber","typeClient","clientNumber","nomComplet","telephoneFixe","telephoneMobile","employeur","totalDette","creditAutorise").exclude("*.class","*").serialize(this);

    }
    
    public static String toJsonArray(Collection<Client> collection) {
		return new JSONSerializer().include("id","nom","clientPayeur.nomComplet","clientPayeur.id","tauxCouverture","clientPayeurNumber","categorie.categorieNumber","typeClient","clientNumber","nomComplet","telephoneFixe","telephoneMobile","employeur","totalDette","creditAutorise").exclude("*.class","*").serialize(collection);

    }
    @Override
    protected void internalPreUpdate() {
    }

    @PostPersist
    public void postPersit() {
    	plafondCredit = plafondCredit ==null?BigDecimal.ZERO:plafondCredit;
        clientNumber = NumberGenerator.getNumber("CL-", getId(), 4);
        if (clientPayeur == null) {
            clientPayeur = this;
            clientPayeurNumber = clientNumber;
            tauxCouverture = BigDecimal.valueOf(100);
            
        }
    }
    @PreUpdate
    public void preUpdate() {
    	plafondCredit = plafondCredit ==null?BigDecimal.ZERO:plafondCredit;
    }

    

    public boolean estCredible(BigInteger amount) {
        if (creditAutorise) {
        	plafondCredit = plafondCredit ==null ?BigDecimal.ZERO:plafondCredit;
        			if (plafondCredit.intValue() > 0) {
                calculeTotalDette();
                merge();
                return totalDette.add(amount).intValue() < plafondCredit.intValue();
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public BigInteger getMontantCredible() {
        calculeTotalDette();
        merge();
        return plafondCredit.toBigInteger().subtract(totalDette);
    }

    public void calculeTotalDette() {
       totalDette = DetteClient.getTotalDetteAvailable(getId());
    }

    public void protectSomeField() {
        Client client = Client.findClient(getId());
        clientNumber = client.getClientNumber();
        footPrint = client.getFootPrint();
        nomComplet = displayName();
        if (StringUtils.isBlank(clientPayeurNumber)) {
            clientPayeur = client;
            clientPayeurNumber = clientNumber;
            tauxCouverture = BigDecimal.valueOf(100);
        }
        if (plafondCredit == null) {
            plafondCredit = BigDecimal.ZERO;
        }
    }

    public void validate(BindingResult bindingResult) {
        if (!StringUtils.isBlank(clientPayeurNumber)) {
            List<Client> client = Client.findClientsByClientNumberEquals(clientPayeurNumber).getResultList();
            if (!client.isEmpty()) {
                clientPayeur = client.iterator().next();
            } else {
                ObjectError error = new ObjectError("clientPayeurNumber", "Aucun client payeur avec le numero  : " + clientPayeurNumber);
                bindingResult.addError(error);
            }
        }
    }

    public static void initClient() {
        if (Client.countClients() <= 0) {
            System.out.println("[ initialisation des clients ]");
            Client client = new Client();
            client.setSexe(Genre.Neutre);
            client.setNom("CLIENT");
            client.setPrenom("DIVERS");
            client.setTelephoneMobile("7");
            client.setCategorie(CategorieClient.findAllCategorieClients().iterator().next());
            client.setTelephoneMobile("-");
            client.persist();
        }
    }

    @Override
    public Client clone() {
        Client clt = new Client();
        clt.setNom(nom);
        clt.setSexe(sexe);
        clt.setPrenom(prenom);
        clt.setEmployeur(employeur);
        return clt;
    }

    public String toString() {
        return displayName() + "," + getClientNumber();
    }

    public String displayName() {
        String toString = "" ;
    	if(sexe != null)toString= new  StringBuilder().append(sexe.getSalutation()).append(" ").append(nom).append(" ").append(prenom).toString();
        return  toString ;
    }
    
    public String displayNameShot() {
        return new StringBuilder().append(nom).append(" ").append(prenom).toString();
    }

    public static List<Client> findClientEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Client o ORDER BY o.nom ASC", Client.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public static TypedQuery<Client> findClientsByNomLike(String nom) {
        if (nom == null || nom.length() == 0) throw new IllegalArgumentException("The nom argument is required");
        nom ="%"+nom + "%";
        EntityManager em = Client.entityManager();
        TypedQuery<Client> q = em.createQuery("SELECT o FROM Client AS o WHERE LOWER(o.nom) LIKE LOWER(:nom) ORDER BY o.nom ASC", Client.class);
        q.setParameter("nom", nom);
        return q;
    }
    
    public static TypedQuery<Client> findClientsByNomLikeAndPayeurLike(String nom) {
        if (nom == null || nom.length() == 0) throw new IllegalArgumentException("The nom argument is required");
        nom ="%"+nom + "%";
        EntityManager em = Client.entityManager();
        TypedQuery<Client> q = em.createQuery("SELECT o FROM Client AS o WHERE LOWER(o.nom) LIKE LOWER(:nom) OR LOWER(o.clientPayeur.nom) LIKE LOWER(:nom) ORDER BY o.nom ASC", Client.class);
        q.setParameter("nom", nom);
        return q;
    }
    
    public static TypedQuery<Client> findNextClients(Long id ) {
        if (id == null ) throw new IllegalArgumentException("The id argument is required");
        EntityManager em = Client.entityManager();
        TypedQuery<Client> q = em.createQuery("SELECT o FROM Client AS o WHERE o.id > :id ORDER BY o.id ", Client.class);
        q.setParameter("id", id);
        return q;
    }
 public static TypedQuery<Client> findPreviousClients(Long id ) {
        if (id == null ) throw new IllegalArgumentException("The id argument is required");
        EntityManager em = Client.entityManager();
        TypedQuery<Client> q = em.createQuery("SELECT o FROM Client AS o WHERE o.id < :id ORDER BY o.id DESC", Client.class);
        q.setParameter("id", id);
        return q;
    }
    
   
    public static List<Client> search(String clientNumber, String nom, String employeur, Client clientPayeur, CategorieClient categorie, TypeClient typeClient ,BigInteger totalDette) {
        StringBuilder searchQuery = new StringBuilder("SELECT o FROM Client AS o WHERE o.totalDette >= :totalDette ");
        if (StringUtils.isNotBlank(clientNumber)) {
            return entityManager().createQuery("SELECT o FROM Client AS o WHERE  o.clientNumber = :clientNumber ", Client.class).setParameter("clientNumber", clientNumber).getResultList();
        }
        
        totalDette = totalDette!=null ?totalDette:BigInteger.ZERO;
        if (StringUtils.isNotBlank(nom)) {
        	nom =  "%"+nom + "%";
            searchQuery.append(" AND  LOWER(o.nom) LIKE LOWER(:nom) ");
        } 
        
        if (StringUtils.isNotBlank(employeur)) {
        	employeur = "%"+employeur + "%";
        	 searchQuery.append(" AND  LOWER(o.employeur) LIKE LOWER(:employeur) ");
        }
        
        if (clientPayeur != null) {
            searchQuery.append(" AND o.clientPayeur = :clientPayeur ");
        }
        if (categorie != null) {
            searchQuery.append(" AND o.categorie = :categorie ");
        }
        
        if (typeClient != null) {
            searchQuery.append(" AND o.typeClient = :typeClient ");
        }
        TypedQuery<Client> q = entityManager().createQuery(searchQuery.append(" ORDER BY o.nom ASC").toString(), Client.class);
        if (StringUtils.isNotBlank(nom)) {
            q.setParameter("nom", nom);
        } 
        if (StringUtils.isNotBlank(employeur)) {
            q.setParameter("employeur", employeur);

        }
       
        if (typeClient != null) {
            q.setParameter("typeClient", typeClient);
        }
        if (categorie != null) {
            q.setParameter("categorie", categorie);
        }
        
        if (clientPayeur != null) {
            q.setParameter("clientPayeur", clientPayeur);
        }
            q.setParameter("totalDette", totalDette);
        return q.getResultList();
    }

    
}
