package org.adorsys.adpharma.beans.process;

import java.math.BigDecimal;
import java.util.List;

import org.adorsys.adpharma.domain.AvoirClient;
import org.adorsys.adpharma.domain.Caisse;
import org.adorsys.adpharma.domain.TypeDecaissement;
import org.adorsys.adpharma.security.SecurityUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.ui.Model;
public class CashDisbursementBean {

	public String orderBy;
	public String giveTo ;
	public TypeDecaissement raison ;
	public BigDecimal amount ;
	public String haveNumber;
	public BigDecimal haveAmount ;
	public String note ;


	public boolean isHaveDisbursement(){
		return StringUtils.isNotBlank(haveNumber);
	}

	public AvoirClient getConcernHave(){
		List<AvoirClient> haves = AvoirClient.findAvoirClientsByNumeroEquals(haveNumber).getResultList();
		if(!haves.isEmpty()) return haves.iterator().next();
		return null;
	}
    public CashDisbursementBean() {
		orderBy = SecurityUtil.getUserName();
	}
	//getters and setters 
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public String getGiveTo() {
		return giveTo;
	}
	public void setGiveTo(String giveTo) {
		this.giveTo = giveTo;
	}
	public TypeDecaissement getRaison() {
		return raison;
	}
	public void setRaison(TypeDecaissement raison) {
		this.raison = raison;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getHaveNumber() {
		return haveNumber;
	}

	public void setHaveNumber(String haveNumber) {
		this.haveNumber = haveNumber;
	}

	public BigDecimal getHaveAmount() {
		return haveAmount;
	}


	public void setHaveAmount(BigDecimal haveAmount) {
		this.haveAmount = haveAmount;
	}

	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}

	public boolean isValid(Model uiModel, Caisse caisse) {
		if(isHaveDisbursement()){

			if(haveAmount==null){
				uiModel.addAttribute("apMessage", "Preciser le montant de l'avoir !");
				return false;
			}
			amount = haveAmount ;
			AvoirClient concernHave = getConcernHave();
			if(concernHave == null){
				uiModel.addAttribute("apMessage", "Desole cet Avoir est Introuvable !");
				return false;
			}
			if(concernHave.getAnnuler()){
				uiModel.addAttribute("apMessage", "Desole cet Avoir a ete Anulle !");
				return false;
			}
			if(concernHave.getsolder()){
				uiModel.addAttribute("apMessage", "Desole cet Avoir est Deja Solde !");
				return false;
			}
			if(!isValidHaveInformation(concernHave)){
				uiModel.addAttribute("apMessage", "Desole Information avoir Incorrect !");
				return false;
			}
		}
		if(!caisse.canDoDisbursement(amount)){
			uiModel.addAttribute("apMessage", "Desole Votre caisse n'est pas assez fournie !");
			return false;
		}
		return true ;

	}

	public boolean isValidHaveInformation(AvoirClient avoirClient){
		return (StringUtils.equals(haveNumber, avoirClient.getNumero()) && avoirClient.getReste().compareTo(haveAmount) == 0);
	}

	@Override
	public String toString() {
		return "CashDisbursementBean [orderBy=" + orderBy + ", giveTo="
				+ giveTo + ", raison=" + raison + ", amount=" + amount
				+ ", haveNumber=" + haveNumber + ", haveAmount=" + haveAmount
				+ ", note=" + note + "]";
	}




}
