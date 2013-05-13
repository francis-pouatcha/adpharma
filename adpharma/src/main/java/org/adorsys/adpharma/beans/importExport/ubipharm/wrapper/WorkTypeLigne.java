/**
 * 
 */
package org.adorsys.adpharma.beans.importExport.ubipharm.wrapper;

/**
 * @author w2b
 * Wrapper pour la ligne  : Type de Travail
 */
public class WorkTypeLigne extends AbstractUbipharmLigneWrapper {
	private UbipharmCommandStringSequence workType;
	public WorkTypeLigne(int startIndex, int endIndex) {
		super(startIndex, endIndex);
		setLigneIdentifier(new UbipharmCommandStringSequence(1, 1, false, "P"));
	}
	public UbipharmCommandStringSequence getWorkType() {
		return workType;
	}
	
	public void setWorkType(UbipharmCommandStringSequence workType) {
		this.workType = workType;
	}
	@Override
	public String toString() {
		return "WorkTypeLigne [workType=" + workType + ", toString()="
				+ super.toString() + "]";
	}
	
}
