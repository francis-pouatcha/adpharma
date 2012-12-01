package org.adorsys.adpharma.platform.rest;

import org.adorsys.adpharma.platform.rest.exchanges.ExchangeData;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

/**
 * @author adorsys-clovis
 *
 */
public interface PlatformRestService {
	
	public String getBaseUrl();
	
	public RestTemplate preparedRestTemplate();
	
	public ExchangeData postData(ExchangeData data , String uri,MediaType mediaType) ;
	
	public ExchangeData getData( String exchangeDataKey, String uri) ;
	
	
	

}
