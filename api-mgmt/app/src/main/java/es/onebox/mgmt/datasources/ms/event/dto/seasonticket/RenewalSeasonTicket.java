package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import java.io.Serializable;
import java.util.List;

public class RenewalSeasonTicket implements Serializable {

    private static final long serialVersionUID = -1990625723418990274L;

    private Long renewalSeasonTicket;

    private Long renewalExternalEvent;

    private Boolean isExternalEvent;

    private Boolean includeAllEntities;

    private List<RelatedRate> rates;

    private Boolean includeBalance;

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

    public List<RelatedRate> getRates() {
        return rates;
    }

    public void setRates(List<RelatedRate> rates) {
        this.rates = rates;
    }

    public Boolean getIncludeBalance() {
        return includeBalance;
    }

    public void setIncludeBalance(Boolean includeBalance) {
        this.includeBalance = includeBalance;
    }
}