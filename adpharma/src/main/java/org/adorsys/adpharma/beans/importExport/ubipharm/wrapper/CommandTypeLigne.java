package org.adorsys.adpharma.beans.importExport.ubipharm.wrapper;
/**
 * 
 * @author w2b
 *
 */
public class CommandTypeLigne extends AbstractUbipharmLigneWrapper {
	private UbipharmCommandStringSequence commandType ;
	private UbipharmCommandStringSequence separator ;
	private UbipharmCommandStringSequence commandReference ;//could also be commandId :)
	
	public CommandTypeLigne(int startIndex, int endIndex) {
		super(startIndex, endIndex);
		setLigneIdentifier(new UbipharmCommandStringSequence(1, 1, false, "C"));
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
	public String toString() {
		return "CommandTypeLigne [commandType=" + commandType + ", separator="
				+ separator + ", commandReference=" + commandReference
				+ ", toString()=" + super.toString() + "]";
	}
	
}
