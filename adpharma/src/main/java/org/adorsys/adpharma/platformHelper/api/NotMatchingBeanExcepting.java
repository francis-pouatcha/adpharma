package org.adorsys.adpharma.platformHelper.api;

public class NotMatchingBeanExcepting  extends RuntimeException{
	
	private Class adpharmaBeanType ;
	
	private Class platformBEanType ;

	public NotMatchingBeanExcepting(Class adpharmaBeanType ,Class platformBEanType) {
		super(adpharmaBeanType.getSimpleName()+" do not match with "+platformBEanType.getSimpleName());
		this.adpharmaBeanType = adpharmaBeanType;
		this.platformBEanType = platformBEanType;
	}
	
	
	

}
