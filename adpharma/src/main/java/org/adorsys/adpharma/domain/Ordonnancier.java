package org.adorsys.adpharma.domain;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

import javax.persistence.PostPersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.format.annotation.DateTimeFormat;
import org.adorsys.adpharma.domain.CommandeClient;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.utils.NumberGenerator;
import javax.persistence.OneToOne;

@RooJavaBean
@RooToString
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "Ordonnancier", finders = { "findOrdonnanciersByCommande", "findOrdonnanciersByOrdNumberEquals" })
public class Ordonnancier extends AdPharmaBaseEntity {

    @NotNull
    private String Prescripteur;

    private String hospital;

    private String nomDuPatient;

    private String vendeur;

    private String ordNumber;

    @OneToOne
    private CommandeClient commande;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date datePrescription;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date dateSaisie;

    @Override
    protected void internalPrePersist() {
        vendeur = SecurityUtil.getPharmaUser().displayName();
        dateSaisie = new Date();
    }

    @PostPersist
    public void postPersit() {
        ordNumber = NumberGenerator.getNumber("ORD-", getId(), 4);
    }

    public void protectSomeField() {
        Ordonnancier ordonnance = Ordonnancier.findOrdonnancier(id);
        commande = ordonnance.getCommande();
        dateSaisie = ordonnance.getDateSaisie();
        vendeur = ordonnance.getVendeur();
        ordNumber = ordonnance.getOrdNumber();
        footPrint = ordonnance.getFootPrint() ;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getOrdNumber()).append(", ");
        sb.append("Prescripteur: ").append(getPrescripteur());
        return sb.toString();
    }
    
   
    
    public static List<Ordonnancier> findOrdonnancierEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Ordonnancier o ORDER BY o.id DESC ", Ordonnancier.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
}
