/**
 * 
 */
package org.adorsys.adpharma.beans.importExport.ubipharm.wrapper;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

/**
 * @author w2b
 *
 */
public class ResponseErrorDetailRow extends AbstractUbipharmLigneWrapper {

	private static final String LIGNE_IDENTIFIER_STRING_VALUE = "E";
	
	private UbipharmCommandStringSequence errorStatus;
	private UbipharmCommandStringSequence errorReturningCode;
	private UbipharmCommandStringSequence separator;
	private UbipharmCommandStringSequence detail;
	
	public ResponseErrorDetailRow(int startIndex, int endIndex) {
		super(startIndex, endIndex);
	}

	public ResponseErrorDetailRow(int startIndex, int endIndex,String csvFileRepresentation) {
		super(startIndex, endIndex);
		this.contains = csvFileRepresentation;
		this.loadDataFromRow();
	}
	
	public static boolean assertItisAvalidErrorDetailRow(String line){
		return line.startsWith(LIGNE_IDENTIFIER_STRING_VALUE);
	}
	public UbipharmCommandStringSequence readErrorStatus(){
		return new UbipharmCommandStringSequence(1, 1, super.readValue(getStringValue(), 1, 1));
	}
	public UbipharmCommandStringSequence readErrorReturningKey(){
		return new UbipharmCommandStringSequence(2, 5, super.readValue(getStringValue(), 2, 5));
	}
	public UbipharmCommandStringSequence readSeparator(){
		UbipharmCommandStringSequence separatorCommandStringSequence = new UbipharmCommandStringSequence(6, 6, super.readValue(getStringValue(), 6, 6));
		if("D".equalsIgnoreCase(separatorCommandStringSequence.getStringValue()))throw new IllegalArgumentException("Invalid Separator");
		return separatorCommandStringSequence;
	}
	public UbipharmCommandStringSequence readDetail(){
		String errorDetail = super.readValue(getStringValue(), 7, getStringValue().length());
		return new UbipharmCommandStringSequence(7, (errorDetail.length()+7) -1, errorDetail);
	}
	public UbipharmCommandStringSequence readUbipharmServerError(){
		String errorDetail = super.readValue(getStringValue(), 2, getStringValue().length());
		return new UbipharmCommandStringSequence(7, (errorDetail.length()+2) -1, errorDetail);
	}
	public void loadDataFromRow(){
		if(false == assertItisAvalidErrorDetailRow(getStringValue())) 
			throw new IllegalArgumentException("Invalid Ligne Identifier, this might not be a valid Product Item Row");
		this.setLigneIdentifier(readLigneIdentifier());
		if(isErrorFromSendedFile()){
			errorStatus = readErrorStatus();
			errorReturningCode = readErrorReturningKey();
			detail = readDetail();
		}else {
			detail = readUbipharmServerError();
		}
	}

	private boolean isErrorFromSendedFile() {
		try {
			separator = readSeparator();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return true;
		}
		return false;
	}

	private UbipharmCommandStringSequence readLigneIdentifier(){
		return new UbipharmCommandStringSequence(1, 1, super.readValue(getStringValue(),1,1));
	}

	@Override
	public boolean isCorrectLigne(String ligneWrapperFromCsvFile) {
		Assert.notNull(ligneWrapperFromCsvFile, "Null Value Not Required");
		return StringUtils.startsWith(ligneWrapperFromCsvFile, LIGNE_IDENTIFIER_STRING_VALUE);
	}
	@Override
	public AbstractUbipharmLigneWrapper loadAbstractLigneFromCsvFileLigne(
			String csvLigneRepresentation) {
		if(isCorrectLigne(csvLigneRepresentation) == false) throw new IllegalArgumentException("This is not a CommandType Ligne");
		CommandTypeLigne commandTypeLigne = new CommandTypeLigne(1, csvLigneRepresentation.length());
//		new UbipharmCommandStringSequence(2, 4,readValue(csvLigneRepresentation, startIndex, endIndex))
		return null;
	}

	public UbipharmCommandStringSequence getErrorStatus() {
		return errorStatus;
	}

	public UbipharmCommandStringSequence getErrorReturningCode() {
		return errorReturningCode;
	}

	public UbipharmCommandStringSequence getSeparator() {
		return separator;
	}

	public UbipharmCommandStringSequence getDetail() {
		return detail;
	}
}
