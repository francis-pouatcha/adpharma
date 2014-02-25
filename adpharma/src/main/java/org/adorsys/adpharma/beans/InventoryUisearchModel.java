package org.adorsys.adpharma.beans;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.Rayon;
import org.apache.commons.lang.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

public class InventoryUisearchModel {

	private Rayon productRow ;

	private String beginName ;

	private String endName ;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	private Date beginOrderedDate;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	private Date endOrderedDate ;

	private Boolean includeArchivedLine =Boolean.FALSE ;




	public Rayon getProductRow() {
		return productRow;
	}




	public void setProductRow(Rayon productRow) {
		this.productRow = productRow;
	}




	public String getBeginName() {
		return beginName;
	}




	public void setBeginName(String beginName) {
		this.beginName = beginName;
	}




	public String getEndName() {
		return endName;
	}




	public void setEndName(String endName) {
		this.endName = endName;
	}




	public Date getBeginOrderedDate() {
		return beginOrderedDate;
	}




	public void setBeginOrderedDate(Date beginOrderedDate) {
		this.beginOrderedDate = beginOrderedDate;
	}




	public Date getEndOrderedDate() {
		return endOrderedDate;
	}




	public void setEndOrderedDate(Date endOrderedDate) {
		this.endOrderedDate = endOrderedDate;
	}




	public Boolean getIncludeArchivedLine() {
		return includeArchivedLine;
	}




	public void setIncludeArchivedLine(Boolean includeArchivedLine) {
		this.includeArchivedLine = includeArchivedLine;
	}




	public  List<LigneApprovisionement> seach() {
		StringBuilder searchQuery = new StringBuilder("SELECT o FROM LigneApprovisionement AS o WHERE o.quantieEnStock <> :quantieEnStock ");
		if (this.productRow !=null) {
			searchQuery.append(" AND  o.produit.rayon = :productRow ");	
		} 
		if (StringUtils.isNotBlank(beginName)) {
			searchQuery.append(" AND  o.produit.designation >= :beginName ");
		}
		if (StringUtils.isNotBlank(endName)) {
			searchQuery.append(" AND  o.produit.designation <= :endName ");
		}
		if (beginOrderedDate!=null) {
			searchQuery.append(" AND  o.dateSaisie >= :beginOrderedDate ");
		}
		if (endOrderedDate!=null) {
			searchQuery.append(" AND  o.dateSaisie <= :endOrderedDate ");
		}

		TypedQuery<LigneApprovisionement> q = LigneApprovisionement.entityManager().createQuery(searchQuery.append(" AND ( o.archived IS NULL OR o.archived = :archived ) ORDER BY o.produit.designation ").toString(), LigneApprovisionement.class);
		if (this.productRow !=null) {
			q.setParameter("productRow", productRow);
		}
		if (StringUtils.isNotBlank(beginName)) {
			q.setParameter("beginName", beginName);
		}
		if (StringUtils.isNotBlank(endName)) {
			q.setParameter("endName", endName);
		}
		if (beginOrderedDate!=null) {
			q.setParameter("beginOrderedDate", beginOrderedDate);
		}
		if (endOrderedDate!=null) {
			q.setParameter("endOrderedDate", endOrderedDate);
		}
		q.setParameter("archived", includeArchivedLine);
		q.setParameter("quantieEnStock", BigInteger.ZERO);
		return q.getResultList(); 
	}

}

