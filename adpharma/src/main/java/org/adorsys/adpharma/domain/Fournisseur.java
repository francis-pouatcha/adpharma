package org.adorsys.adpharma.domain;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.TypedQuery;
import javax.validation.constraints.Size;
import org.adorsys.adpharma.utils.NumberGenerator;
import org.apache.commons.lang.StringUtils;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;
import flexjson.JSONSerializer;

@RooJavaBean
@RooToString
@RooJson
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "Fournisseur", finders = { "findFournisseursByNameLike" })
public class Fournisseur extends AdPharmaBaseEntity {

    private String fournisseurNumber;

    private String providerKey;

    private String name;

    private String shortname;

    private String phone;

    private String mobile;

    private String fax;

    private String email;

    private String adresse;

    private String siteInternet;

    private String responsable;

    private BigDecimal chiffreAffaire;

    private String ville;

    private String pays;

    private String codePostal;

    private String ContribuableNumber;

    private String numRegistreComerce;
    
    private String distributorKey ;

    @Size(max = 256)
    private String note;

    public static void initFournisseur() {
        if (Fournisseur.countFournisseurs() <= 0) {
            System.out.println("[ initialisation des fournisseur ]");
            Fournisseur fournisseur = new Fournisseur();
            fournisseur.setName("FOURNISSEUR DIVERS");
            fournisseur.persist();
        }
    }

    public void protectSomeField() {
        Fournisseur fournisseur = Fournisseur.findFournisseur(getId());
        fournisseurNumber = fournisseur.getFournisseurNumber();
    }

    @PrePersist
    public void prePersit() {
    }

    @PostPersist
    public void postPersit() {
        fournisseurNumber = NumberGenerator.getNumber("F-", getId(), 4);
    }

    public String displayName() {
        return new StringBuilder().append(getName()).append(" , ").append(getFournisseurNumber()).toString();
    }

    public String getProviderKey() {
        return providerKey;
    }

    public void setProviderKey(String providerKey) {
        this.providerKey = providerKey;
    }

    public String displayShotName() {
        String name2 = getName();
        if (StringUtils.isBlank(name2)) name2 = "FOUR DIVER";
        if (name2.length() > 10) {
            name2 = name2.substring(0, 9);
        }
        return name2;
    }

    public String displayCodeName() {
        String name2 = getName();
        if (StringUtils.isBlank(name2)) name2 = "DAF";
        if (name2.length() > 4) {
            name2 = name2.substring(0, 3);
        }
        return ":" + name2 + ":";
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }
    
    

    public String getDistributorKey() {
		return distributorKey;
	}

	public void setDistributorKey(String distributorKey) {
		this.distributorKey = distributorKey;
	}

	public String toString() {
        return displayName();
    }

    public String toJson() {
        return new JSONSerializer().include("name", "id").exclude("*", "*.class").serialize(this);
    }

    public static String toJsonArray(Collection<Fournisseur> collection) {
        return new JSONSerializer().include("name", "id").exclude("*", "*.class").serialize(collection);
    }

    @Transactional(readOnly = true)
    public static List<Fournisseur> findAllFournisseurs() {
        return entityManager().createQuery("SELECT o FROM Fournisseur o ORDER BY o.name ASC ", Fournisseur.class).getResultList();
    }

    public static TypedQuery<Fournisseur> findFournisseurByProviderKey(String providerKey) {
        TypedQuery<Fournisseur> q = entityManager().createQuery("SELECT o FROM Fournisseur o WHERE o.providerKey =:providerKey ", Fournisseur.class);
        q.setParameter("providerKey", providerKey);
        return q;
    }
}
