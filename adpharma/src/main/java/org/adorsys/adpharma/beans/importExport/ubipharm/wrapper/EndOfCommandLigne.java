/**
 * 
 */
package org.adorsys.adpharma.beans.importExport.ubipharm.wrapper;

/**
 * @author w2b
 *
 */
public class EndOfCommandLigne extends AbstractUbipharmLigneWrapper {
	
	private UbipharmCommandStringSequence numberOfEncodedLignes;
	
	private UbipharmCommandStringSequence numberOfClearLignes;
	
	public EndOfCommandLigne(int startIndex, int endIndex) {
		super(startIndex, endIndex);
	}

	public UbipharmCommandStringSequence getNumberOfEncodedLignes() {
		return numberOfEncodedLignes;
	}

	public void setNumberOfEncodedLignes(
			UbipharmCommandStringSequence numberOfEncodedLignes) {
		this.numberOfEncodedLignes = numberOfEncodedLignes;
	}

	public UbipharmCommandStringSequence getNumberOfClearLignes() {
		return numberOfClearLignes;
	}

	public void setNumberOfClearLignes(
			UbipharmCommandStringSequence numberOfClearLignes) {
		this.numberOfClearLignes = numberOfClearLignes;
	}

	@Override
	public String toString() {
		return "EndOfCommandLigne [numberOfEncodedLignes="
				+ numberOfEncodedLignes + ", numberOfClearLignes="
				+ numberOfClearLignes + ", toString()=" + super.toString()
				+ "]";
	}
	
}
