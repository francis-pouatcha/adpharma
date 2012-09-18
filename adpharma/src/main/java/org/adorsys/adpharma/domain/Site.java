package org.adorsys.adpharma.domain;

import javax.persistence.Column;
import javax.persistence.PostPersist;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.adorsys.adpharma.utils.NumberGenerator;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.beans.factory.annotation.Value;

@RooJavaBean
@RooToString
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "Site")
public class Site extends AdPharmaBaseEntity {

    @NotNull
    private String displayName;

    @Size(max = 256)
    private String adresse;

    private String ville;

    private String Pays;

    @Column(unique = true)
    private String siteNumber;

    private String phone;

    @NotNull
    private String siteManager;

    private String email;

    private String siteInternet;

    private String mobile;

    private String fax;
    
    private String numeroRegistre;
    
    @Size(max = 256)
    @Value("bonne guerison")
    private String messageTiket;

    public void protectSomeField() {
        Site site = Site.findSite(getId());
        siteNumber = site.getSiteNumber();
    }

    @PostPersist
    public void postPersist() {
        siteNumber = NumberGenerator.getNumber("S-", getId(), 4);
    }

    public static void initSite() {
        if (Site.countSites() <= 0) {
            System.out.println("[ initialisation des Sites ]");
            Site site = new Site();
            site.setAdresse("Sise CAMP YABASSI B.P 11526 DLA");
            site.setDisplayName("PHARMACIE DE L'ALLIANCE ");
            site.setSiteManager("DR TIENGOUE PULCHERIE");
            site.setEmail("phciealliance@yahoo.ca");
            site.setAdresse("Sise CAMP YABASSI B.P 11526 DLA");
            site.setPhone("(237) - 342.99.15");
            site.setFax("33436313");
            site.setNumeroRegistre("901/019695");
            site.setMessageTiket("Votre Pharmacien a votre Ecoute,MERCI DE VOTRE CONFIANCE...,BONNE JOURNEE BONNE GUERISON," +
            		"Les produits Vendus ne sont ni CHANGES ni RETOURNES");
            site.persist();
        }
    }

    public String displayName() {
        return new StringBuilder().append(getDisplayName()).append(" , ").append(getSiteNumber()).toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getDisplayName()).append(", ");
        sb.append("Phone: ").append(getPhone()).append(", ");
        sb.append("SiteManager: ").append(getSiteManager()).append(", ");
        return sb.toString();
    }
}
