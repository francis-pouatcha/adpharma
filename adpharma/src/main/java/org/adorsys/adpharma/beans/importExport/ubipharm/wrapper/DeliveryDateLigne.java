package org.adorsys.adpharma.beans.importExport.ubipharm.wrapper;
/**
 * 
 * @author w2b
 *
 */
public class DeliveryDateLigne extends AbstractUbipharmLigneWrapper{
	private UbipharmCommandStringSequence deliveryDate ;
	public DeliveryDateLigne(int startIndex, int endIndex) {
		super(startIndex, endIndex);
		setLigneIdentifier(new UbipharmCommandStringSequence(1, 1,"L"));
	}
	public UbipharmCommandStringSequence getDeliveryDate() {
		return deliveryDate;
	}
	public void setDeliveryDate(UbipharmCommandStringSequence deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	@Override
	public String toString() {
		return "DeliveryDateLigne [deliveryDate=" + deliveryDate
				+ ", toString()=" + super.toString() + "]";
	}
	
}
