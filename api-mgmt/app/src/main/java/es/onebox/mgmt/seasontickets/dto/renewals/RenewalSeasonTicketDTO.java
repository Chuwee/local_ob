package es.onebox.mgmt.seasontickets.dto.renewals;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.List;

public class RenewalSeasonTicketDTO implements Serializable {
    private static final long serialVersionUID = -3352664269076509367L;

    @JsonProperty("renewal_season_ticket")
    private Long renewalSeasonTicket;

    @JsonProperty("renewal_external_event")
    private Long renewalExternalEvent;

    @JsonProperty("is_external_event")
    private Boolean isExternalEvent;

    @JsonProperty("include_all_entities")
    private Boolean includeAllEntities;

    @JsonProperty("include_balance")
    private Boolean includeBalance;

    @NotNull(message = "at least a rate is required")
    @Size(min = 1)
    @Valid
    private List<RelatedRateDTO> rates;

    public Long getRenewalSeasonTicket() {
        return renewalSeasonTicket;
    }

    public void setRenewalSeasonTicket(Long renewalSeasonTicket) {
        this.renewalSeasonTicket = renewalSeasonTicket;
    }

    public Long getRenewalExternalEvent() {
        return renewalExternalEvent;
    }

    public void setRenewalExternalEvent(Long renewalExternalEvent) {
        this.renewalExternalEvent = renewalExternalEvent;
    }

    public Boolean getExternalEvent() {
        return isExternalEvent;
    }

    public void setExternalEvent(Boolean externalEvent) {
        isExternalEvent = externalEvent;
    }

    public Boolean getIncludeAllEntities() {
        return includeAllEntities;
    }

    public void setIncludeAllEntities(Boolean includeAllEntities) {
        this.includeAllEntities = includeAllEntities;
    }

    public List<RelatedRateDTO> getRates() {
        return rates;
    }

    public void setRates(List<RelatedRateDTO> rates) {
        this.rates = rates;
    }

    public Boolean getIncludeBalance() {
        return includeBalance;
    }

    public void setIncludeBalance(Boolean includeBalance) {
        this.includeBalance = includeBalance;
    }
}
