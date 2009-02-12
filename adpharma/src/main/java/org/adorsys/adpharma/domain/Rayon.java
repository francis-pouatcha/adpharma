package org.adorsys.adpharma.domain;

import java.util.List;
import javax.persistence.ManyToOne;
import javax.persistence.PostPersist;
import javax.validation.constraints.NotNull;
import org.adorsys.adpharma.domain.virtualtable.ProduitV;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.utils.NumberGenerator;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "Rayon", finders = { "findRayonsByDisplayNameEquals", "findRayonsByNameEquals" })
public class Rayon extends AdPharmaBaseEntity {

    private String codeRayon;

    @NotNull
    private String name;

    private String displayName;
    
    private String emplacement;
    
    private String codeGeo;

    private String note;

    @NotNull
    @ManyToOne
    private Site magasin;

    protected void internalPrePersist() {
    }
    
    

    @PostPersist
    public void postPersist() {
        codeRayon = NumberGenerator.getNumber("RAY-", getId(), 4);
    }

    protected void internalPostPersit() {
    }

    public void protectSomeField() {
        Rayon rayon = Rayon.findRayon(getId());
        codeRayon = rayon.getCodeRayon();
        footPrint = rayon.getFootPrint();
    }

    public String toString() {
        return displayName();
    }

    public String displayName() {
        return new StringBuilder().append(getName()).toString();
    }

    protected String getBusinessKey() {
        return codeRayon;
    }
    
    public String getEmplacement() {
		return emplacement;
	}

	public void setEmplacement(String emplacement) {
		this.emplacement = emplacement;
	}

	public String getCodeGeo() {
		return codeGeo;
	}

	public void setCodeGeo(String codeGeo) {
		this.codeGeo = codeGeo;
	}
	
	public boolean existe(){
		return !Rayon.findRayonsByNameEquals(name).getResultList().isEmpty();
	}

	public static List<Rayon> findAllRayons() {
        return entityManager().createQuery("SELECT o FROM Rayon o ORDER BY o.name ASC ", Rayon.class).getResultList();
    }
}