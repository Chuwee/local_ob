package es.onebox.mgmt.datasources.ms.entity.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UpdateOperatorRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private String name;
    private String languageCode;
    private String currencyCode;
    private String olsonId;
    private List<String> gateways = new ArrayList<>();
    private List<WalletConfigDTO> wallets;
    private Boolean allowFeverZone;
    private Boolean allowGatewayBenefits;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
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

    public List<String> getGateways() {
        return gateways;
    }

    public void setGateways(List<String> gateways) {
        this.gateways = gateways;
    }

    public List<WalletConfigDTO> getWallets() {
        return wallets;
    }

    public void setWallets(List<WalletConfigDTO> wallets) {
        this.wallets = wallets;
    }

    public Boolean getAllowFeverZone() {
        return allowFeverZone;
    }

    public void setAllowFeverZone(Boolean allowFeverZone) {
        this.allowFeverZone = allowFeverZone;
    }

    public Boolean getAllowGatewayBenefits() {
        return allowGatewayBenefits;
    }

    public void setAllowGatewayBenefits(Boolean allowGatewayBenefits) {
        this.allowGatewayBenefits = allowGatewayBenefits;
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
