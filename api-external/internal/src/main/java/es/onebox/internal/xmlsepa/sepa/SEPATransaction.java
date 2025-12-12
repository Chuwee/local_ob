package es.onebox.internal.xmlsepa.sepa;

import es.onebox.internal.xmlsepa.sepa.enums.Currency;

import java.math.BigDecimal;
import java.util.Date;

public class SEPATransaction {
	private SEPABankAccount bankAccount;
	private String id;
	private String subject;
	private BigDecimal value;
	private Date date;
	private Integer endToEndId;
	private String mandatReference;
	private Date mandatReferenceDate;
	private Currency currency;

	public SEPABankAccount getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(SEPABankAccount bankAccount) {
		this.bankAccount = bankAccount;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getEndToEndId() {
		return endToEndId;
	}

	public void setEndToEndId(Integer endToEndId) {
		this.endToEndId = endToEndId;
	}

	public String getMandatReference() {
		return mandatReference;
	}

	public void setMandatReference(String mandatReference) {
		this.mandatReference = mandatReference;
	}

	public Date getMandatReferenceDate() {
		return mandatReferenceDate;
	}

	public void setMandatReferenceDate(Date mandatReferenceDate) {
		this.mandatReferenceDate = mandatReferenceDate;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
}