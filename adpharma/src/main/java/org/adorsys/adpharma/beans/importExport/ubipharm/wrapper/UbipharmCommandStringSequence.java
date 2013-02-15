/**
 * 
 */
package org.adorsys.adpharma.beans.importExport.ubipharm.wrapper;

/**
 * @author w2b
 *
 */
public class UbipharmCommandStringSequence extends UbipharmStringOperation{
	private boolean isFilledWithSpace = false;
	public UbipharmCommandStringSequence(int startIndex, int endIndex) {
		super(startIndex, endIndex);
	}
	public UbipharmCommandStringSequence(int startIndex, int endIndex, String contains){
		super(startIndex, endIndex);
		this.contains = contains;
	}
	public UbipharmCommandStringSequence(int startIndex, int endIndex,boolean isFilledWithSpace, String contains){
		super(startIndex, endIndex);
		this.contains = contains;
		this.isFilledWithSpace = isFilledWithSpace;
	}
	public void setContains(String value){
		if(this.isFilledWithSpace){
			
		}
		this.contains = value;
	}
	public boolean isFilledWithSpace() {
		return isFilledWithSpace;
	}
	public void setFilledWithSpace(boolean isFilledWithSpace) {
		this.isFilledWithSpace = isFilledWithSpace;
	}
}
