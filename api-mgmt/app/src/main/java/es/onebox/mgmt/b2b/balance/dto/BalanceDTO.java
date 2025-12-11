package es.onebox.mgmt.b2b.balance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class BalanceDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Double balance;
    @JsonProperty("credit_limit")
    private Double creditLimit;
    private Double debt;
    @JsonProperty("total_available")
    private Double totalAvailable;
    @JsonProperty("currency_code")
    private String currencyCode;
    @JsonProperty("currencies_balance")
    private List<CurrencyBalanceDTO> currenciesBalance;

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Double creditLimit) {
        this.creditLimit = creditLimit;
    }

    public Double getDebt() {
        return debt;
    }

    public void setDebt(Double debt) {
        this.debt = debt;
    }

    public Double getTotalAvailable() {
        return totalAvailable;
    }

    public void setTotalAvailable(Double totalAvailable) {
        this.totalAvailable = totalAvailable;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public List<CurrencyBalanceDTO> getCurrenciesBalance() {
        return currenciesBalance;
    }

    public void setCurrenciesBalance(List<CurrencyBalanceDTO> currenciesBalance) {
        this.currenciesBalance = currenciesBalance;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
