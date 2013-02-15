/**
 * 
 */
package org.adorsys.adpharma.beans.importExport.ubipharm.wrapper;


/**
 * @author w2b
 * Ligne du Repartiteur, correspondant a la ligne R
 */
public class DistributorLigne extends UbipharmStringOperation{
	private UbipharmStringOperation ligneIdentifier ;
	private UbipharmStringOperation repartitor ;
	public DistributorLigne() {
		super(0, 64);
	}
	public DistributorLigne(int startIndex, int endIndex) {
		super(startIndex, endIndex);
	}
	public UbipharmStringOperation getLigneIdentifier() {
		return ligneIdentifier;
	}
	public void setLigneIdentifier(UbipharmStringOperation ligneIdentifier) {
		this.ligneIdentifier = ligneIdentifier;
	}
	public UbipharmStringOperation getRepartitor() {
		return repartitor;
	}
	public void setRepartitor(UbipharmStringOperation repartitor) {
		this.repartitor = repartitor;
	}
	@Override
	public String toString() {
		return "DistributorLigne [ligneIdentifier=" + ligneIdentifier
				+ ", repartitor=" + repartitor + ", toString()="
				+ super.toString() + "]";
	}
	
}
