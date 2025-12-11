package es.onebox.event.seasontickets.dto.renewals;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

public class RenewalSeasonTicketDTO implements Serializable {

    private static final long serialVersionUID = 2196053132424468873L;

    @JsonProperty("renewalSeasonTicket")
    private Long originSeasonTicketId;

    @JsonProperty("renewalExternalEvent")
    private Long originRenewalExternalEvent;

    private Boolean isExternalEvent;

    private Boolean includeAllEntities;

    private Boolean includeBalance;

    @NotNull
    @Size(min = 1)
    @Valid
    private List<RelatedRateDTO> rates;

    public Long getOriginSeasonTicketId() {
        return originSeasonTicketId;
    }

    public void setOriginSeasonTicketId(Long originSeasonTicketId) {
        this.originSeasonTicketId = originSeasonTicketId;
    }

    public Long getOriginRenewalExternalEvent() {
        return originRenewalExternalEvent;
    }

    public void setOriginRenewalExternalEvent(Long originRenewalExternalEvent) {
        this.originRenewalExternalEvent = originRenewalExternalEvent;
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
