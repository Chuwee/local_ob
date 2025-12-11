package es.onebox.mgmt.operators.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdCodeDTO;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.WalletConfigDTO;
import es.onebox.mgmt.entities.dto.EntitySettingsDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class OperatorDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    @JsonProperty("short_name")
    private String shortName;
    private IdNameCodeDTO language;
    private String shard;
    private IdCodeDTO currency;
    private OperatorCurrenciesDTO currencies;
    private IdCodeDTO timezone;
    private List<String> gateways;
    private EntitySettingsDTO settings;
    @JsonProperty("allow_fever_zone")
    private Boolean allowFeverZone;
    private List<WalletConfigDTO> wallets;
    @JsonProperty("allow_gateway_benefits")
    private Boolean allowGatewayBenefits;

    public OperatorDTO() {
    }

    public OperatorDTO(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public IdNameCodeDTO getLanguage() {
        return language;
    }

    public void setLanguage(IdNameCodeDTO language) {
        this.language = language;
    }

    public String getShard() {
        return shard;
    }

    public void setShard(String shard) {
        this.shard = shard;
    }

    public IdCodeDTO getCurrency() {
        return currency;
    }

    public void setCurrency(IdCodeDTO currency) {
        this.currency = currency;
    }

    public IdCodeDTO getTimezone() {
        return timezone;
    }

    public void setTimezone(IdCodeDTO timezone) {
        this.timezone = timezone;
    }

    public List<String> getGateways() {
        return gateways;
    }

    public void setGateways(List<String> gateways) {
        this.gateways = gateways;
    }

    public EntitySettingsDTO getSettings() {
        return settings;
    }

    public void setSettings(EntitySettingsDTO settings) {
        this.settings = settings;
    }

    public OperatorCurrenciesDTO getCurrencies() { return currencies; }

    public void setCurrencies(OperatorCurrenciesDTO currencies) { this.currencies = currencies; }

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
