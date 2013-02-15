package org.adorsys.adpharma.beans.importExport.ubipharm.wrapper;
/**
 * 
 * @author w2b
 *
 */
public class ProductItemLigne extends AbstractUbipharmLigneWrapper {
	
	private UbipharmCommandStringSequence quantityOrdored; 
	private UbipharmCommandStringSequence codificationType ;
	private UbipharmCommandStringSequence productId ;//could be product's cip
	private UbipharmCommandStringSequence isPartialDelivery ;
	private UbipharmCommandStringSequence isBalance; //stand for "reliquats", or rest (ou balance)
	private UbipharmCommandStringSequence isEquivalentDelivery ;
	private UbipharmCommandStringSequence ligneNumber;
	public ProductItemLigne(int startIndex, int endIndex) {
		super(startIndex, endIndex);
	}
	public UbipharmCommandStringSequence getQuantityOrdored() {
		return quantityOrdored;
	}
	public void setQuantityOrdored(UbipharmCommandStringSequence quantityOrdored) {
		this.quantityOrdored = quantityOrdored;
	}
	public UbipharmCommandStringSequence getCodificationType() {
		return codificationType;
	}
	public void setCodificationType(UbipharmCommandStringSequence codificationType) {
		this.codificationType = codificationType;
	}
	public UbipharmCommandStringSequence getProductId() {
		return productId;
	}
	public void setProductId(UbipharmCommandStringSequence productId) {
		this.productId = productId;
	}
	public UbipharmCommandStringSequence getIsPartialDelivery() {
		return isPartialDelivery;
	}
	public void setIsPartialDelivery(UbipharmCommandStringSequence isPartialDelivery) {
		this.isPartialDelivery = isPartialDelivery;
	}
	public UbipharmCommandStringSequence getIsBalance() {
		return isBalance;
	}
	public void setIsBalance(UbipharmCommandStringSequence isBalance) {
		this.isBalance = isBalance;
	}
	public UbipharmCommandStringSequence getIsEquivalentDelivery() {
		return isEquivalentDelivery;
	}
	public void setIsEquivalentDelivery(
			UbipharmCommandStringSequence isEquivalentDelivery) {
		this.isEquivalentDelivery = isEquivalentDelivery;
	}
	public UbipharmCommandStringSequence getLigneNumber() {
		return ligneNumber;
	}
	public void setLigneNumber(UbipharmCommandStringSequence ligneNumber) {
		this.ligneNumber = ligneNumber;
	}
	@Override
	public String toString() {
		return "ProductItemLigne [quantityOrdored=" + quantityOrdored
				+ ", codificationType=" + codificationType + ", productId="
				+ productId + ", isPartialDelivery=" + isPartialDelivery
				+ ", isBalance=" + isBalance + ", isEquivalentDelivery="
				+ isEquivalentDelivery + ", ligneNumber=" + ligneNumber
				+ ", toString()=" + super.toString() + "]";
	}
	
}
