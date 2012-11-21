package org.adorsys.adpharma.platformHelper.api;

/**
 * 
 * 
 * @author adorsys-clovis
 *
 */
public interface PlatformToApharmaBeansConverter<A,P> {
	
	public A fromPlatform(P platformBean) ;
	
	public P toPlatform(A adpharmaBean) ;
	
	public int compareBean(A adpharmaBean , P platformBean) ;
	 
	

}
