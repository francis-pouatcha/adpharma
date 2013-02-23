package org.adorsys.adpharma.beans.importExport.ubipharm.wrapper;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

/**
 * 
 * @author w2b
 * in the send file.	
 */
public class CommandTypeLigne extends AbstractUbipharmLigneWrapper {
	private static final String LIGNE_IDENTIFIER_STRING_VALUE = "C";
	private UbipharmCommandStringSequence commandType ;
	private UbipharmCommandStringSequence separator ;
	private UbipharmCommandStringSequence commandReference ;//could also be commandId :)
	
	public CommandTypeLigne(int startIndex, int endIndex) {
		super(startIndex, endIndex);
		setLigneIdentifier(new UbipharmCommandStringSequence(1, 1, false, LIGNE_IDENTIFIER_STRING_VALUE));
	}

	public UbipharmCommandStringSequence getCommandType() {
		return commandType;
	}

	public void setCommandType(UbipharmCommandStringSequence commandType) {
		this.commandType = commandType;
	}

	public UbipharmCommandStringSequence getSeparator() {
		return separator;
	}

	public void setSeparator(UbipharmCommandStringSequence separator) {
		this.separator = separator;
	}

	public UbipharmCommandStringSequence getCommandReference() {
		return commandReference;
	}

	public void setCommandReference(UbipharmCommandStringSequence commandReference) {
		this.commandReference = commandReference;
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
	@Override
	public String toString() {
		return "CommandTypeLigne [commandType=" + commandType + ", separator="
				+ separator + ", commandReference=" + commandReference
				+ ", toString()=" + super.toString() + "]";
	}
	
}
