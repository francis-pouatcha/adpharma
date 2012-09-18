package org.adorsys.adpharma.domain.virtualtable;

import java.math.BigInteger;
import java.util.List;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import com.ibm.icu.math.BigDecimal;

@RooJavaBean
@RooToString
@RooEntity(finders = { "findProduitVsByCipEquals" })
public class ProduitV {

    private String cip;
    
    private String cipMaison;

    private String designation;

    private String qte;

    private String pachat;

    private String pvente;

    private String filiale;

    private String rayon;

    public static List<ProduitV> findAllProduitVs() {
        return entityManager().createQuery("SELECT  o FROM ProduitV o", ProduitV.class).getResultList();
    }

    public static List<String> findAllRayons() {
        return (List<String>) entityManager().createQuery("SELECT distinct o.rayon FROM ProduitV o").getResultList();
    }

    public static int deleteAllProduitVs() {
        System.out.println("delete ProduitVs begin ");
        int i = 0;
        List<ProduitV> produitVs = ProduitV.findAllProduitVs();
        if (!produitVs.isEmpty()) {
            for (ProduitV produitV : produitVs) {
                produitV.remove();
                System.out.println(i + " produitv remove ");
            }
            System.out.println("delete ProduitVs end ");
        } else {
            System.out.println("no produitvs found ");
        }
        return entityManager().createNativeQuery("DELETE FROM produitv ").executeUpdate();
    }
}
