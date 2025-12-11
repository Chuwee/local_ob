package es.onebox.mgmt.datasources.ms.entity.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CreateOperatorRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private String shortName;

    private String currencyCode;
    private String olsonId;
    private String languageCode;
    private String shard;
    private List<String> gateways = new ArrayList<>();
    private CreateOperatorCurrenciesDTO currencies;
    private List<WalletConfigDTO> wallets;


    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getOlsonId() {
        return olsonId;
    }

    public void setOlsonId(String olsonId) {
        this.olsonId = olsonId;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getShard() {
        return shard;
    }

    public void setShard(String shard) {
        this.shard = shard;
    }

    public List<String> getGateways() {
        return gateways;
    }

    public void setGateways(List<String> gateways) {
        this.gateways = gateways;
    }

    public CreateOperatorCurrenciesDTO getCurrencies() { return currencies; }

    public void setCurrencies(CreateOperatorCurrenciesDTO currencies) { this.currencies = currencies; }

    public List<WalletConfigDTO> getWallets() {
        return wallets;
    }

    public void setWallets(List<WalletConfigDTO> wallets) {
        this.wallets = wallets;
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
