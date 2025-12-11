package es.onebox.mgmt.secondarymarket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SeasonTicketSecondaryMarketConfigDTO extends SecondaryMarketBaseConfigDTO {

    @JsonProperty("num_sessions")
    private Integer numSessions;

    @JsonProperty("additional_settings")
    private AdditionalSettingsDTO additionalSettings;

    @JsonProperty("sale_type")
    private SaleType saleType;


    public Integer getNumSessions() {
        return numSessions;
    }

    public void setNumSessions(Integer numSessions) {
        this.numSessions = numSessions;
    }

    public AdditionalSettingsDTO getAdditionalSettings() {
        return additionalSettings;
    }

    public void setAdditionalSettings(AdditionalSettingsDTO additionalSettings) {
        this.additionalSettings = additionalSettings;
    }

    public SaleType getSaleType() {
        return saleType;
    }

    public void setSaleType(SaleType saleType) {
        this.saleType = saleType;
    }
}
