/**
 * 
 */
package org.adorsys.adpharma.beans.importExport.ubipharm.wrapper;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		this.setContains(contains);
	}
	public UbipharmCommandStringSequence(int startIndex, int endIndex,boolean isFilledWithSpace, String contains){
		super(startIndex, endIndex);
		this.isFilledWithSpace = isFilledWithSpace;
		setContains(contains);
	}
	private void validateContent(String content){
		if(content == null) throw new IllegalArgumentException("Invalid Argument Exception ! Null Value aren't Accepted");
		if(content.length() > this.size) throw new IllegalArgumentException("The Item is larger than its size");
	}
	public void setContains(String value){
		validateContent(value);
		if(this.isFilledWithSpace){
			value = StringUtils.rightPad(value, this.size);
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
