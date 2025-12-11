package es.onebox.mgmt.datasources.api.accounting.dto;

import java.io.Serializable;
import java.util.List;

public class ProviderClient implements Serializable {

    private Integer providerId;
    private Integer clientId;
    private boolean active;
    private Integer balance;
    private Integer usedCredit;
    private Integer maxCredit;
    private String currencyCode;
    private List<CurrencyBalance> currenciesBalance;


    public Integer getProviderId() {
        return providerId;
    }

    public void setProviderId(Integer providerId) {
        this.providerId = providerId;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

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

    public List<CurrencyBalance> getCurrenciesBalance() {
        return currenciesBalance;
    }

    public void setCurrenciesBalance(List<CurrencyBalance> currenciesBalance) {
        this.currenciesBalance = currenciesBalance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ProviderClient that = (ProviderClient) o;

        if (active != that.active) {
            return false;
        }
        if (providerId != null ? !providerId.equals(that.providerId) : that.providerId != null) {
            return false;
        }
        if (clientId != null ? !clientId.equals(that.clientId) : that.clientId != null) {
            return false;
        }
        if (balance != null ? !balance.equals(that.balance) : that.balance != null) {
            return false;
        }
        if (usedCredit != null ? !usedCredit.equals(that.usedCredit) : that.usedCredit != null) {
            return false;
        }
        if (maxCredit != null ? !maxCredit.equals(that.maxCredit) : that.maxCredit != null) {
            return false;
        }
        if (currenciesBalance != null ? !currenciesBalance.equals(that.currenciesBalance) : that.currenciesBalance != null) {
            return false;
        }
        return currencyCode != null ? currencyCode.equals(that.currencyCode) : that.currencyCode == null;

    }

    @Override
    public int hashCode() {
        int result = providerId != null ? providerId.hashCode() : 0;
        result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
        result = 31 * result + (active ? 1 : 0);
        result = 31 * result + (balance != null ? balance.hashCode() : 0);
        result = 31 * result + (usedCredit != null ? usedCredit.hashCode() : 0);
        result = 31 * result + (maxCredit != null ? maxCredit.hashCode() : 0);
        result = 31 * result + (currenciesBalance != null ? currenciesBalance.hashCode() : 0);
        result = 31 * result + (currencyCode != null ? currencyCode.hashCode() : 0);
        return result;
    }


}
