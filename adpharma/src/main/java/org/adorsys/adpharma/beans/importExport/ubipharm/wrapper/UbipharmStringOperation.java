package org.adorsys.adpharma.beans.importExport.ubipharm.wrapper;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author w2b
 *
 */
public abstract class UbipharmStringOperation implements StringOperation {
	
	public UbipharmStringOperation (int startIndex , int endIndex){
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}
	int startIndex ;
	int endIndex ;
	String contains  = null;
	int size = this.endIndex -this.startIndex;
	@Override
	public int getStartIndex() {
		return startIndex;
	}

	@Override
	public int getEndIndex() {
		return endIndex;
	}
	@Override
	public String getStringValue() {
		if(contains == null) {
			contains = "";
		}
		return contains;
	}
	@Override
	public void joinAnotherString(StringOperation stringOperation) {
		if(stringOperation == null) throw new IllegalArgumentException("Invalid Argument, null value aren't accepted");
		
		this.getStringValue().concat(stringOperation.getStringValue());
		int startIndex2 = stringOperation.getStartIndex();
		if(isStartIndexCorrect(startIndex2) == false) 
			throw new IllegalArgumentException("The start index Is incorrect ! Must be ("+getStartIndex()+"+1) = "+getEndIndex()+1);
		int endIndex2 = stringOperation.getEndIndex();
		if(isEndIndexCorrect(endIndex2)==false) 
			throw new IllegalArgumentException("The end index Is incorrect ! Must be lower than"+getEndIndex());
		this.contains = this.getStringValue().concat(stringOperation.getStringValue());
	}
	public boolean isStartIndexCorrect(int startIndex){
		boolean isCorrect = false;
		int computedIndex = startIndex -1 ;
		String stringValue = this.getStringValue();
		//TODO : remove the trimOperation.
		String trimedStringValue = StringUtils.trimToEmpty(stringValue);
		int currentEndIndex = trimedStringValue.length();
		if(currentEndIndex == computedIndex) isCorrect = true;
		if(this.getStartIndex() == computedIndex) isCorrect = true;
		return isCorrect;
	}
	public boolean isEndIndexCorrect(int endIndex){
		return this.getEndIndex() <= endIndex;
	}
	@Override
	public void clean() {
	}
	
	public int getSize() {
		return size;
	}
	@Override
	public String toString() {
		return "UbipharmStringOperation [startIndex=" + startIndex
				+ ", endIndex=" + endIndex + ", contains=" + contains + "]";
	}
	

}

