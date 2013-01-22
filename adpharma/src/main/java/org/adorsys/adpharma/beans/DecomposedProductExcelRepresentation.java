package org.adorsys.adpharma.beans;

import com.ibm.icu.math.BigDecimal;

public class DecomposedProductExcelRepresentation {
	private String cipMaison ;
	private String designation ;
	private BigDecimal salePrice ;
	private String provider ; //in this particular, the provider is the drugstore itself
	private String site ; //in this particular, the site will be the magasin.
	
	
	public DecomposedProductExcelRepresentation() {
		super();
	}

	public DecomposedProductExcelRepresentation(String cipMaison,
			String designation, String provider, String site) {
		super();
		this.cipMaison = cipMaison;
		this.designation = designation;
		this.provider = provider;
		this.site = site;
	}


	public String getCipMaison() {
		return cipMaison;
	}


	public void setCipMaison(String cipMaison) {
		this.cipMaison = cipMaison;
	}


	public String getDesignation() {
		return designation;
	}


	public void setDesignation(String designation) {
		this.designation = designation;
	}


	public BigDecimal getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(BigDecimal salePrice) {
		this.salePrice = salePrice;
	}
	
	public String getSalePriceAsCFA() {
		return this.salePrice.doubleValue()+" CFA";
	}
	public String getProvider() {
		return provider;
	}


	public void setProvider(String provider) {
		this.provider = provider;
	}
	
	
	
	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	@Override
	public String toString() {
		return "DecomposedProductExcelRepresentation [cipMaison=" + cipMaison
				+ ", designation=" + designation + ", provider=" + provider
				+ ", rayon=" + site + "]";
	}
	
	
}
