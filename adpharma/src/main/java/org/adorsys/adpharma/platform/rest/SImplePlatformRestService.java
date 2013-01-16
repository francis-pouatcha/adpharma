package org.adorsys.adpharma.platform.rest;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.adorsys.adpharma.domain.Site;
import org.adorsys.adpharma.platform.rest.exchanges.ExchangeData;
import org.adorsys.adpharma.platform.rest.exchanges.PlatformRestException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SImplePlatformRestService implements PlatformRestService {

	public static final String POST_DATA_URI = "/platform/rest/exchangebeans/create";
	public String platformBaseUrl;
	private RestTemplate restTemplate;
	private List<HttpMessageConverter<?>> messageConverters;
	private static Logger logger = Logger
			.getLogger(SImplePlatformRestService.class);

	public SImplePlatformRestService(String platformBaseUrl) {
		this.platformBaseUrl = platformBaseUrl;
	}

	public SImplePlatformRestService() {
		getBaseUrl();
		preparedRestTemplate();
	}

	@Override
	public String getBaseUrl() {
		if (StringUtils.isNotBlank(platformBaseUrl))
			return platformBaseUrl;
		Site site = Site.findSite(new Long(1));
		if (site != null) {
			this.platformBaseUrl = site.getPlatformBaseUrl();
			return platformBaseUrl;
		}
		return "";
	}

	@Override
	public RestTemplate preparedRestTemplate() {
		this.restTemplate = new RestTemplate();
		messageConverters = new ArrayList<HttpMessageConverter<?>>();
		MappingJacksonHttpMessageConverter jacksonHttpMessageConverter = new MappingJacksonHttpMessageConverter();
		HttpMessageConverter<?> stringHttpMessageConverter = new StringHttpMessageConverter();
		messageConverters.add(jacksonHttpMessageConverter);
		messageConverters.add(stringHttpMessageConverter);
		restTemplate.setMessageConverters(messageConverters);
		return restTemplate;
	}

	@Override
	public ExchangeData postData(ExchangeData data, String uri,MediaType mediaType) {
		if (mediaType == null || StringUtils.isBlank(uri) || data == null)
			throw new IllegalArgumentException("data uri ,mediaType are all required !");
		String url = getBaseUrl() + uri;
		System.out.println("Base Url: "+getBaseUrl()); 
		System.out.println("sent To :" + url);
		HttpHeaders httpHeaders = new HttpHeaders();
		HttpEntity<ExchangeData> entity = new HttpEntity<ExchangeData>(data,httpHeaders);
		ResponseEntity<ExchangeData> responseEntity= null;
		try{
		 responseEntity = restTemplate.postForEntity(url,entity, ExchangeData.class);
		 System.out.println("Successfuly send...");
		}catch (Exception e) {
			System.out.println("Send error: " +e.getMessage());
			ExchangeData exchangeDataForException = createExchangeBeanWhenExceptionOccured();
			return exchangeDataForException;
		}
		return responseEntity.getBody();
	}

	private ExchangeData createExchangeBeanWhenExceptionOccured() {
		ExchangeData exchangeDataForException = new ExchangeData();
		List<PlatformRestException> postingExceptions = new ArrayList<PlatformRestException>();
		postingExceptions.add(PlatformRestException.UNKNOW_EXCEPTION);
		exchangeDataForException.setPlatformRestException(postingExceptions);
		return exchangeDataForException;
	}

	@Override
	public ExchangeData getData(String exchangeDataKey, String uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPlatformBaseUrl() {
		return platformBaseUrl;
	}

	public void setPlatformBaseUrl(String platformBaseUrl) {
		this.platformBaseUrl = platformBaseUrl;
	}

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public List<HttpMessageConverter<?>> getMessageConverters() {
		return messageConverters;
	}

	public void setMessageConverters(
			List<HttpMessageConverter<?>> messageConverters) {
		this.messageConverters = messageConverters;
	}

}
