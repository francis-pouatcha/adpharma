package org.adorsys.adpharma.platformHelper;

public interface BeansConverter<T,P>{
 
	
	public T fromTagerBean(P targetBean);
	
	public P toTargetBean(T bean);
	
}
