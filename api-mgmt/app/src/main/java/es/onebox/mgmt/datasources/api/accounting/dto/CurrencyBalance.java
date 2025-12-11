package es.onebox.mgmt.datasources.api.accounting.dto;

import java.io.Serializable;

public class CurrencyBalance implements Serializable {

    private Integer balance;
    private Integer usedCredit;
    private Integer maxCredit;
    private String currencyCode;

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Integer getUsedCredit() {
        return usedCredit;
    }

    public void setUsedCredit(Integer usedCredit) {
        this.usedCredit = usedCredit;
    }

    public Integer getMaxCredit() {
        return maxCredit;
    }

    public void setMaxCredit(Integer maxCredit) {
        this.maxCredit = maxCredit;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CurrencyBalance that = (CurrencyBalance) o;

        if (balance != null ? !balance.equals(that.balance) : that.balance != null) return false;
        if (usedCredit != null ? !usedCredit.equals(that.usedCredit) : that.usedCredit != null) return false;
        if (maxCredit != null ? !maxCredit.equals(that.maxCredit) : that.maxCredit != null) return false;
        return currencyCode != null ? currencyCode.equals(that.currencyCode) : that.currencyCode == null;

    }

    @Override
    public int hashCode() {
        int result = balance != null ? balance.hashCode() : 0;
        result = 31 * result + (usedCredit != null ? usedCredit.hashCode() : 0);
        result = 31 * result + (maxCredit != null ? maxCredit.hashCode() : 0);
        result = 31 * result + (currencyCode != null ? currencyCode.hashCode() : 0);
        return result;
    }


}
