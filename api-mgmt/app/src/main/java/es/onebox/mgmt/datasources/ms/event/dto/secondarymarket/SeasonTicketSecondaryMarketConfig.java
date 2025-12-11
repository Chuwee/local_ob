package es.onebox.mgmt.datasources.ms.event.dto.secondarymarket;

import es.onebox.mgmt.secondarymarket.dto.SaleType;

public class SeasonTicketSecondaryMarketConfig extends SecondaryMarketConfig {

    private Boolean isSeasonTicket;

    private Integer numSessions;

    private SaleType saleType;

    private AdditionalSettings additionalSettings;

    public Boolean getIsSeasonTicket() {
        return isSeasonTicket;
    }

    public void setIsSeasonTicket(Boolean isSeasonTicket) {
        this.isSeasonTicket = isSeasonTicket;
    }

    public Integer getNumSessions() {
        return numSessions;
    }

    public void setNumSessions(Integer numSessions) {
        this.numSessions = numSessions;
    }

    public AdditionalSettings getAdditionalSettings() {
        return additionalSettings;
    }

    public void setAdditionalSettings(AdditionalSettings additionalSettings) {
        this.additionalSettings = additionalSettings;
    }

    public SaleType getSaleType() {
        return saleType;
    }

    public void setSaleType(SaleType saleType) {
        this.saleType = saleType;
    }
}
