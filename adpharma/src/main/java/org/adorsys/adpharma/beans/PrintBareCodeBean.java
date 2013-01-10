package org.adorsys.adpharma.beans;

import java.math.BigInteger;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.Max;

import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.Produit;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

public class PrintBareCodeBean {

	private String bareCode;

	@Min(0L)
	@Max(12L)
	private int emptyLine;

	@Min(0L)
	@Max(8L)
	private int emptyColumn;

	@Value("false")
	private Boolean printAll;

	private String cmdNumber;

	private String minBarecode;

	private String maxBarecode;


	private BigInteger nombre;

	public String getBareCode() {
		return bareCode;
	}

	public void setBareCode(String bareCode) {
		this.bareCode = bareCode;
	}

	public int getEmptyLine() {
		return emptyLine;
	}

	public void setEmptyLine(int emptyLine) {
		this.emptyLine = emptyLine;
	}

	public int getEmptyColumn() {
		return emptyColumn;
	}

	public void setEmptyColumn(int emptyColumn) {
		this.emptyColumn = emptyColumn;
	}

	public Boolean getPrintAll() {
		return printAll;
	}

	public void setPrintAll(Boolean printAll) {
		this.printAll = printAll;
	}

	public String getCmdNumber() {
		return cmdNumber;
	}

	public void setCmdNumber(String cmdNumber) {
		this.cmdNumber = cmdNumber;
	}

	public String getMinBarecode() {
		return minBarecode;
	}

	public void setMinBarecode(String minBarecode) {
		this.minBarecode = minBarecode;
	}

	public String getMaxBarecode() {
		return maxBarecode;
	}

	public void setMaxBarecode(String maxBarecode) {
		this.maxBarecode = maxBarecode;
	}

	public BigInteger getNombre() {
		return nombre;
	}

	public void setNombre(BigInteger nombre) {
		this.nombre = nombre;
	}

	public void validate(BindingResult bindingResult) {
		if (StringUtils.isBlank(bareCode)) {
			ObjectError error = new ObjectError("bareCode", "Saisir le code bare");
			bindingResult.addError(error);
		}
		if (StringUtils.isNotBlank(bareCode)) {
			List<LigneApprovisionement> resultList = LigneApprovisionement.findLigneApprovisionementsByCipMaisonEquals(bareCode).getResultList();
			if (resultList.isEmpty()) {
				ObjectError error = new ObjectError("bareCode", "AuCun Produit Avec Ce CIPM");
				bindingResult.addError(error);
			}

		}


	}

}
