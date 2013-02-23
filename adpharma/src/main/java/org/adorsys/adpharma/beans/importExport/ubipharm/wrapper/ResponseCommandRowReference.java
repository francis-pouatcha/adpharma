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
public class ResponseCommandRowReference extends AbstractUbipharmLigneWrapper {

	private static final String LIGNE_IDENTIFIER_STRING_VALUE = "C";
	private static final String SEPARATOR_STRING_VALUE = "R";
	public ResponseCommandRowReference(int startIndex, int endIndex) {
		super(startIndex, endIndex);
	}
	public ResponseCommandRowReference(int startIndex, int endIndex,
			String csvLigneRepresentation) {
		super(startIndex, endIndex);
		this.contains = csvLigneRepresentation;
		loadData();
	}
	private UbipharmCommandStringSequence customerCommandKey ;
	private UbipharmCommandStringSequence separator;
	private UbipharmCommandStringSequence providerCommandKey;
	
	public static boolean assertThisIsAvalidCommandRow(String line) {
		return line.startsWith(LIGNE_IDENTIFIER_STRING_VALUE);
	}

	private UbipharmCommandStringSequence readLigneIdentifier(){
		return new UbipharmCommandStringSequence(1, 1, super.readValue(getStringValue(),1,1));
	}
	
	private UbipharmCommandStringSequence readCustomerCommandReference(){
		int separatorIndex = getSeparatorIndex(getStringValue());
		String customerCommandReference = readValue(getStringValue(), 2, separatorIndex);
		int endIndex2 = (customerCommandReference.length()+2)-1;
		UbipharmCommandStringSequence customerCommandKey = new UbipharmCommandStringSequence(2, endIndex2, customerCommandReference);
		return customerCommandKey;
	}
	private UbipharmCommandStringSequence readSeparator(){
		int separatorIndex = getSeparatorIndex(getStringValue());
		return new UbipharmCommandStringSequence(separatorIndex, separatorIndex, SEPARATOR_STRING_VALUE);
	}
	private UbipharmCommandStringSequence readProviderCommandReference(){
		int separatorIndex = getSeparatorIndex(getStringValue());
		int providerCommandReferenceIndexStart = separatorIndex+1;
		String providerCommandReference = readValue(getStringValue(), providerCommandReferenceIndexStart, getStringValue().length());
		int providerCommandReferenceIndexEnd = getStringValue().length() +1;
		return  new UbipharmCommandStringSequence(providerCommandReferenceIndexStart, providerCommandReferenceIndexEnd,providerCommandReference);
	}
	private void loadData(){
		if(false == assertThisIsAvalidCommandRow(getStringValue())){
			throw new IllegalArgumentException("Invalid Ligne Identifier, this might not be a Command Row Reference");
		}
		setLigneIdentifier(readLigneIdentifier());
		customerCommandKey = this.readCustomerCommandReference();
		separator = readSeparator();
		providerCommandKey = readProviderCommandReference();
	}
	
	private int getSeparatorIndex(String rowStringValue){
		//I'd increment (+1) here, because the ubipharm csv file is starting at 1, not at zero like StringUtils
		//StringUtils and String
		return StringUtils.indexOf(rowStringValue, SEPARATOR_STRING_VALUE) + 1;
	}
	public UbipharmCommandStringSequence getCustomerCommandKey() {
		return customerCommandKey;
	}
	public UbipharmCommandStringSequence getSeparator() {
		return separator;
	}
	public UbipharmCommandStringSequence getProviderCommandKey() {
		return providerCommandKey;
	}
	
	
}
