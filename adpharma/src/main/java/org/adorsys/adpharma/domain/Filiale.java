package org.adorsys.adpharma.domain;

import java.util.List;
import javax.persistence.PostPersist;
import javax.validation.constraints.Size;
import org.adorsys.adpharma.utils.NumberGenerator;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.beans.factory.annotation.Value;

@RooJavaBean
@RooToString
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "Filiale")
public class Filiale extends AdPharmaBaseEntity {

    private String filialeNumber;

    private String libelle;

    @Size(max = 256)
    private String description;

    @Value("true")
    private Boolean actif;

    @PostPersist
    public void postPersist() {
        filialeNumber = NumberGenerator.getNumber("FIL-", getId(), 4);
    }

    public void protectSomeField() {
        Filiale filiale = Filiale.findFiliale(getId());
        filialeNumber = filiale.getFilialeNumber();
        footPrint = filiale.getFootPrint();
    }

    public static void initFiliale() {
        if (Filiale.countFiliales() <= 0) {
            System.out.println("[ initialisation des filiales  ]");
            for (int i = 0; i <= 4; i++) {
                Filiale filiale = new Filiale();
                filiale.setLibelle("CLASSE(" + i + ")");
                filiale.persist();
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getLibelle());
        return sb.toString();
    }

    public static List<Filiale> findAllActifFiliales() {
        return entityManager().createQuery("SELECT o FROM Filiale o WHERE o.actif IS :actif", Filiale.class).setParameter("actif", Boolean.TRUE).getResultList();
    }
    
    public static List<Filiale> findAllFiliales() {
    	return findAllActifFiliales();
      //  return entityManager().createQuery("SELECT o FROM Filiale o ", Filiale.class).getResultList();
    }
}
