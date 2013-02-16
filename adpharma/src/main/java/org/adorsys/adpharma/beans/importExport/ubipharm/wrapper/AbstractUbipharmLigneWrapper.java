/**
 * 
 */
package org.adorsys.adpharma.beans.importExport.ubipharm.wrapper;

/**
 * @author w2b
 *
 */
public abstract class AbstractUbipharmLigneWrapper extends UbipharmStringOperation {
	private UbipharmCommandStringSequence ligneIdentifier ;
	public AbstractUbipharmLigneWrapper(int startIndex, int endIndex) {
		super(startIndex, endIndex);
	}
	public UbipharmCommandStringSequence getLigneIdentifier() {
		return ligneIdentifier;
	}
	public void setLigneIdentifier(UbipharmCommandStringSequence ligneIdentifier) {
		this.ligneIdentifier = ligneIdentifier;
	}
	@Override
	public String toString() {
		return "AbstractUbipharmLigneWrapper [ligneIdentifier="
				+ ligneIdentifier + ", toString()=" + super.toString() + "]";
	}
}
