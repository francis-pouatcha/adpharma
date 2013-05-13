package org.adorsys.adpharma.platform.rest.exchanges;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;


/**
 * Bean use for manage data transfer between
 * Apharma and Platform
 * @author adorsys-clovis
 *
 */
@RooJavaBean
@RooToString
@RooJson
public class ExchangeData {

	@NotNull
	private String orderKey;

	@NotNull
	private String providerKey;

	@NotNull
	private String drugStoreKey;

	private String commercialKey;
	
	private String remoteMessage ;


	@Enumerated
	private ExchangeBeanState exchangeBeanState;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy hh:mm:ss")
	private Date submitionDate;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy hh:mm:ss")
	private Date traitmentDate;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy hh:mm:ss")
	private Date validationDate;
    
	@JsonSerialize
	@ElementCollection(fetch = FetchType.EAGER)
	private List<PlatformRestException> platformRestException = new ArrayList<PlatformRestException>();

	private List<OrderItems> commandItems;

	public String toJson() {
		return new JSONSerializer().include("commandItems").exclude("*.class").serialize(this);
	}

	public static ExchangeData fromJsonToExchangeData(String json) {
		return new JSONDeserializer<ExchangeData>().use(null, ExchangeData.class).deserialize(json);
	}

	public static String toJsonArray(Collection<ExchangeData> collection) {
		return new JSONSerializer().exclude("*.class").serialize(collection);
	}

	public static Collection<ExchangeData> fromJsonArrayToExchangeDatas(String json) {
		return new JSONDeserializer<List<ExchangeData>>().use(null, ArrayList.class).use("values", ExchangeData.class).deserialize(json);
	}
	
	public boolean hasExceptionOccured(){
		return !platformRestException.isEmpty() ;
	}
	
	public void addException(PlatformRestException exception){
		getPlatformRestException().add(exception);
	}

	// getters and setters
	public String getOrderKey() {
		return orderKey;
	}

	public void setOrderKey(String orderKey) {
		this.orderKey = orderKey;
	}

	public String getProviderKey() {
		return providerKey;
	}

	public void setProviderKey(String providerKey) {
		this.providerKey = providerKey;
	}

	public String getDrugStoreKey() {
		return drugStoreKey;
	}

	public void setDrugStoreKey(String drugStoreKey) {
		this.drugStoreKey = drugStoreKey;
	}

	public String getCommercialKey() {
		return commercialKey;
	}

	public void setCommercialKey(String commercialKey) {
		this.commercialKey = commercialKey;
	}

	public ExchangeBeanState getExchangeBeanState() {
		return exchangeBeanState;
	}

	public void setExchangeBeanState(ExchangeBeanState exchangeBeanState) {
		this.exchangeBeanState = exchangeBeanState;
	}

	public Date getSubmitionDate() {
		return submitionDate;
	}

	public void setSubmitionDate(Date submitionDate) {
		this.submitionDate = submitionDate;
	}

	public Date getTraitmentDate() {
		return traitmentDate;
	}

	public void setTraitmentDate(Date traitmentDate) {
		this.traitmentDate = traitmentDate;
	}

	public Date getValidationDate() {
		return validationDate;
	}

	public void setValidationDate(Date validationDate) {
		this.validationDate = validationDate;
	}

	public List<PlatformRestException> getPlatformRestException() {
		return platformRestException;
	}

	public void setPlatformRestException(List<PlatformRestException> platformRestException) {
		this.platformRestException = platformRestException;
	}

	public List<OrderItems> getCommandItems() {
		return commandItems;
	}

	public void setCommandItems(List<OrderItems> commandItems) {
		this.commandItems = commandItems;
	}

	public String getRemoteMessage() {
		return remoteMessage;
	}

	public void setRemoteMessage(String remoteMessage) {
		this.remoteMessage = remoteMessage;
	}

	@Override
	public String toString() {
		return "ExchangeData [orderKey=" + orderKey + ", providerKey="
				+ providerKey + ", drugStoreKey=" + drugStoreKey
				+ ", commercialKey=" + commercialKey + ", remoteMessage="
				+ remoteMessage + ", exchangeBeanState=" + exchangeBeanState
				+ ", submitionDate=" + submitionDate + ", traitmentDate="
				+ traitmentDate + ", validationDate=" + validationDate
				+ ", platformRestException=" + platformRestException
				+ ", commandItems=" + commandItems + "]";
	}





}
