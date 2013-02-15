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
	}

}
