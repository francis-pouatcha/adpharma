package org.adorsys.adpharma.domain;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.TypedQuery;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import org.springframework.roo.addon.json.RooJson;

@RooJavaBean
@RooToString
@RooJson
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "TransformationProduit", finders = { "findTransformationProduitsByProduitOrigine", "findTransformationProduitsByProduitOrigineAndProduitCible" })
public class TransformationProduit extends AdPharmaBaseEntity {

    @OneToOne
    private Produit produitOrigine;

    @OneToOne
    private Produit produitCible;

    @NotNull
    @Min(1L)
    private BigInteger qteCible;

    @NotNull
    @Min(0L)
    private BigDecimal prixVente;

    private transient Long origineId;

    public Long getOrigineId() {
        return origineId;
    }

    public void setOrigineId(Long origineId) {
        this.origineId = origineId;
    }

    public String getOrigineName() {
        return origineName;
    }

    public void setOrigineName(String origineName) {
        this.origineName = origineName;
    }

    public Long getCibleId() {
        return cibleId;
    }

    public TransformationProduit clone() {
        TransformationProduit transformationProduit = new TransformationProduit();
        transformationProduit.setCibleId(produitCible.getId());
        transformationProduit.setOrigineId(produitOrigine.getId());
        transformationProduit.setCibleName(produitCible.getDesignation());
        transformationProduit.setOrigineName(produitOrigine.getDesignation());
        transformationProduit.setId(id);
        transformationProduit.setPrixVente(getPrixVente());
        transformationProduit.setQteCible(getQteCible());
        transformationProduit.setVersion(getVersion());
        transformationProduit.setActif(getActif());
        return transformationProduit;
    }

    public void protectSomeField() {
        TransformationProduit transf = TransformationProduit.findTransformationProduit(getId());
        footPrint = transf.getFootPrint();
    }

    public void setCibleId(Long cibleId) {
        this.cibleId = cibleId;
    }

    public String getCibleName() {
        return cibleName;
    }

    public void setCibleName(String cibleName) {
        this.cibleName = cibleName;
    }

    private transient String origineName;

    private transient Long cibleId;

    private transient String cibleName;

    @Value("true")
    private Boolean actif;

    public void validate(BindingResult bindingResult) {
        if (cibleId.intValue() == origineId.intValue()) {
            ObjectError error = new ObjectError("produitCible", "le Produit cible doit etre different du produit origine !");
            bindingResult.addError(error);
        }
        if (produitCible == null) {
            ObjectError error = new ObjectError("produitCible", "le Produit cible doit etre defini !");
            bindingResult.addError(error);
        }
        if (produitOrigine == null) {
            ObjectError error = new ObjectError("produitOrigine", "le Produit origine doit etre defini !");
            bindingResult.addError(error);
        }
        if (produitCible != null && produitOrigine != null) {
            List<TransformationProduit> resultList = TransformationProduit.findTransformationProduitsByProduitOrigineAndProduitCible(produitOrigine, produitCible).getResultList();
            List<TransformationProduit> resultList2 = TransformationProduit.findTransformationProduitsByProduitOrigineAndProduitCible(produitCible, produitOrigine).getResultList();            
            if (!resultList.isEmpty()) {
                TransformationProduit next = resultList.iterator().next();
                if (!next.getId().equals(id)) {
                    ObjectError error = new ObjectError("relation", "Une relation avec ces produits existe deja !");
                    bindingResult.addError(error);
                }
            }
            if (!resultList2.isEmpty()) {
                TransformationProduit next = resultList2.iterator().next();
                if (!next.getId().equals(id)) {
                    ObjectError error = new ObjectError("relation", "Une relation avec ces produits existe deja !");
                    bindingResult.addError(error);
                }
            }
        }
        
        if (produitCible != null ) {
            List<TransformationProduit> resultList = TransformationProduit.findTransformationProduitsByProduitOrigine(produitOrigine).getResultList();
            if (!resultList.isEmpty()) {
                TransformationProduit next = resultList.iterator().next();
                if (!next.getId().equals(id)) {
                    ObjectError error = new ObjectError("relation", "Une relation avec Le Produit Origine existe deja  !");
                    bindingResult.addError(error);
                }
            }
        }
    }
    
   

    @Override
    protected void internalPrePersist() {
        Produit produitOrigine = getProduitOrigine();
        produitOrigine.setProduitCompose(true);
        produitOrigine.merge();
    }

    protected void internalPreUpdate() {
        TransformationProduit transformationProduit = TransformationProduit.findTransformationProduit(getId());
        Produit produitOrigine2 = transformationProduit.getProduitOrigine();
        if (produitOrigine2.equals(produitOrigine)) {
        } else {
            produitOrigine2.setProduitCompose(false);
            produitOrigine.setProduitCompose(true);
            produitOrigine.merge();
            produitOrigine2.merge();
        }
    }
    
    public static TypedQuery<TransformationProduit>  search(Produit produitOrigine, Produit produitCible,String origineName, String cibleName,  Boolean actif) {
        StringBuilder searchQuery = new StringBuilder("SELECT o FROM TransformationProduit AS o WHERE o.actif IS :actif ");
        actif = actif == null ? Boolean.TRUE : actif;
        
        
        if (produitOrigine!=null) {
                searchQuery.append(" AND  o.produitOrigine = :produitOrigine ");
          }else {
			if (StringUtils.isNotBlank(origineName)) {
				origineName = origineName + "%";

	            searchQuery.append(" AND  LOWER(o.produitOrigine.designation) LIKE LOWER(:origineName) ");
			}
		}
            if (produitCible != null) {
                searchQuery.append(" AND o.produitCible = :produitCible ");
            }else {
            	
            	if (StringUtils.isNotBlank(cibleName)) {
            		cibleName = cibleName + "%";
    	            searchQuery.append(" AND  LOWER(o.produitCible.designation) LIKE LOWER(:cibleName) ");
    			}
				
			}
            
          
            TypedQuery<TransformationProduit> q = entityManager().createQuery(searchQuery.toString(), TransformationProduit.class);
          
            if (produitOrigine != null) {
                q.setParameter("produitOrigine", produitOrigine);
            }else {
            	if (StringUtils.isNotBlank(origineName)) {
            		q.setParameter("origineName", origineName);    			}
			}
            
            if (produitCible != null) {
                q.setParameter("produitCible", produitCible);
            }else {
            	if (StringUtils.isNotBlank(cibleName)) {
            		q.setParameter("cibleName", cibleName);   
			}
            }
            q.setParameter("actif", actif);
            return q;
        }

}
