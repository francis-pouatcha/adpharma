package org.adorsys.adpharma.beans.importExport.ubipharm.wrapper;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

/**
 * 
 * @author w2b
 *
 */
public class ResponseProductItemRow extends AbstractUbipharmLigneWrapper {
	private static final String LIGNE_IDENTIFIER_STRING_VALUE = "K";
	private UbipharmCommandStringSequence quantityDelivered;
	private UbipharmCommandStringSequence productCodificationType ;
	private UbipharmCommandStringSequence orderingProductKey;
	private UbipharmCommandStringSequence separator;
	private UbipharmCommandStringSequence returningProductKey;
	private UbipharmCommandStringSequence productDeliveryCodificationType;
	private UbipharmCommandStringSequence deliveredProductKey;
	
	public ResponseProductItemRow(int startIndex, int endIndex) {
		super(startIndex, endIndex);
	}
	public ResponseProductItemRow(int startIndex, int endIndex,String csvFileRepresentation) {
		super(startIndex, endIndex);
		this.contains = csvFileRepresentation;
		this.loadData();
	}
	
	public static boolean assertThisIsAvalidResponseProductItemRow(String line) {
		return line.startsWith(LIGNE_IDENTIFIER_STRING_VALUE);
	}

	private UbipharmCommandStringSequence readLigneIdentifier(){
		return new UbipharmCommandStringSequence(1, 1, super.readValue(getStringValue(),1,1));
	}
	public UbipharmCommandStringSequence readQuantityDelivered(){
		return new UbipharmCommandStringSequence(2, 5,super.readValue(getStringValue(), 2, 5));
	}
	public UbipharmCommandStringSequence readProductCodificationType(){
		return new UbipharmCommandStringSequence(6,7,super.readValue(getStringValue(), 6, 7));
	}
	public UbipharmCommandStringSequence readOrderingProductKey(){
		return new UbipharmCommandStringSequence(8, 57, super.readValue(getStringValue(), 8, 57));
	}
	
	public UbipharmCommandStringSequence readSeparator(){
		return new UbipharmCommandStringSequence(58, 58,super.readValue(getStringValue(), 58, 58));
	}
	
	public UbipharmCommandStringSequence readReturningKey(){
		return new UbipharmCommandStringSequence(59, 62, super.readValue(getStringValue(), 59, 62));
	}
	
	public UbipharmCommandStringSequence readProductDeliveryCodificationType(){
		return new UbipharmCommandStringSequence(63, 64, super.readValue(getStringValue(), 63, 64));
	}
	private String fillValuedWithDefaultContent(String valueToFill,int maxLength){
		if(StringUtils.isEmpty(valueToFill) || valueToFill.length() < maxLength){
			valueToFill = StringUtils.leftPad(valueToFill, maxLength);
		}
		return valueToFill;
	}
	public UbipharmCommandStringSequence readDeliveryProductKey(){
		return new UbipharmCommandStringSequence(64, 113, super.readValue(getStringValue(), 65, getStringValue().length()));
	}
	/**
	 * Example
	 * 	ResponseProductItemRow responseProductItemRownew = ResponseProductItemRow(startIndex,endIndex,csvStringRepresentation);
	 * 	BigInteger quantityDelivered = new BigInteger(responseProductItemRow.quantityDelivered.getStringValue());
	 */
	private void loadData(){
		if(false == assertThisIsAvalidResponseProductItemRow(getStringValue())){
			throw new IllegalArgumentException("Invalid Ligne Identifier, this might not be a valid Product Item Row");
		}
		setLigneIdentifier(readLigneIdentifier());
		quantityDelivered = readQuantityDelivered();
		productCodificationType = readProductCodificationType();
		orderingProductKey = readOrderingProductKey();
		separator = readSeparator();
		returningProductKey = readReturningKey();
		productDeliveryCodificationType = readProductDeliveryCodificationType();
		deliveredProductKey = readDeliveryProductKey();
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
		return null;
	}
	public UbipharmCommandStringSequence getQuantityDelivered() {
		return quantityDelivered;
	}
	public UbipharmCommandStringSequence getProductCodificationType() {
		return productCodificationType;
	}
	public UbipharmCommandStringSequence getOrderingProductKey() {
		return orderingProductKey;
	}
	public UbipharmCommandStringSequence getSeparator() {
		return separator;
	}
	public UbipharmCommandStringSequence getReturningProductKey() {
		return returningProductKey;
	}
	public UbipharmCommandStringSequence getProductDeliveryCodificationType() {
		return productDeliveryCodificationType;
	}
	public UbipharmCommandStringSequence getDeliveredProductKey() {
		return deliveredProductKey;
	}
	
}
