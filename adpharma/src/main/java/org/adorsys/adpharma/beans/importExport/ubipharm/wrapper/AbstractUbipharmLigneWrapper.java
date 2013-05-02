/**
 * 
 */
package org.adorsys.adpharma.beans.importExport.ubipharm.wrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * @author w2b
 *
 */
public abstract class AbstractUbipharmLigneWrapper extends UbipharmStringOperation {
	static final Logger LOGGER = LoggerFactory.getLogger(AbstractUbipharmLigneWrapper.class);
	
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
	public boolean isCorrectLigne(String ligneWrapperFromCsvFile){
		Assert.notNull(ligneWrapperFromCsvFile, "Null Argument Not Required");
		return ligneWrapperFromCsvFile.startsWith(getStringValue());
	}
	public AbstractUbipharmLigneWrapper loadAbstractLigneFromCsvFileLigne(String csvLigneRepresentation){
		return null;
	}
	public String readValue(String stringValue,int start,int end){
		start --;
//		end--;
		String result = null;
		try {
			result = stringValue.substring(start, end);
		} catch (IndexOutOfBoundsException e) {
			result = "";
//			LOGGER.error(e.getMessage(), e);
		}
		return result ;
	}
}
