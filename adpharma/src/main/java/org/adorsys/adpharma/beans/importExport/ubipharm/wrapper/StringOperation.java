/**
 * 
 */
package org.adorsys.adpharma.beans.importExport.ubipharm.wrapper;

/**
 * @author w2b
 *
 */
public interface StringOperation {
	public int getStartIndex();
	public int getEndIndex();
	public String getStringValue();
	public StringOperation joinAnotherString(StringOperation stringOperation);
	public void clean();
}
