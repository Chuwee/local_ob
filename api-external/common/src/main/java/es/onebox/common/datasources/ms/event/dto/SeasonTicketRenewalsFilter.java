package es.onebox.common.datasources.ms.event.dto;

import es.onebox.common.datasources.orders.dto.BaseRequestFilter;

import java.io.Serial;
import java.util.Collection;

public class SeasonTicketRenewalsFilter extends BaseRequestFilter {

    @Serial
    private static final long serialVersionUID = -5173989581617262157L;

    private Collection<String> renewalIds;
    private String state;
    private SeatRenewalStatus renewalStatus;
    private Boolean autoRenewal;
    private String renewalSubstatus;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Collection<String> getRenewalIds() {
        return renewalIds;
    }

    public void setRenewalIds(Collection<String> renewalIds) {
        this.renewalIds = renewalIds;
    }

    public SeatRenewalStatus getRenewalStatus() { return renewalStatus; }

    public void setRenewalStatus(SeatRenewalStatus renewalStatus) { this.renewalStatus = renewalStatus; }

    public Boolean getAutoRenewal() {
        return autoRenewal;
    }

    public void setAutoRenewal(Boolean autoRenewal) {
        this.autoRenewal = autoRenewal;
    }

    public String getRenewalSubstatus() {
        return renewalSubstatus;
    }

    public void setRenewalSubstatus(String renewalSubstatus) {
        this.renewalSubstatus = renewalSubstatus;
    }
}