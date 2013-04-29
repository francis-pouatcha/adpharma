package org.adorsys.adpharma.beans.importExport.ubipharm.wrapper;
/**
 * 
 * @author w2b
 *
 */
public class CommentLigne extends AbstractUbipharmLigneWrapper {
	
	private UbipharmCommandStringSequence comment;
	public CommentLigne(int startIndex, int endIndex) {
		super(startIndex, endIndex);
	}
	public UbipharmCommandStringSequence getComment() {
		return comment;
	}
	public void setComment(UbipharmCommandStringSequence comment) {
		this.comment = comment;
	}
	@Override
	public String toString() {
		return "CommentLigne [comment=" + comment + ", toString()="
				+ super.toString() + "]";
	}
	
}
