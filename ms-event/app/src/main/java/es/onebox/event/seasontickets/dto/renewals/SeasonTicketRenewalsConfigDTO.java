package es.onebox.event.seasontickets.dto.renewals;

import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class SeasonTicketRenewalsConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -9041342989778782349L;

    @NotNull
    private SeasonTicketRenewalType renewalType;
    private Long bankAccountId;
    private Boolean groupByReference;

    public SeasonTicketRenewalType getRenewalType() {
        return renewalType;
    }

    public void setRenewalType(SeasonTicketRenewalType renewalType) {
        this.renewalType = renewalType;
    }

    public Long getBankAccountId() {
        return bankAccountId;
    }

    public void setBankAccountId(Long bankAccountId) {
        this.bankAccountId = bankAccountId;
    }

    public Boolean getGroupByReference() {
        return groupByReference;
    }

    public void setGroupByReference(Boolean groupByReference) {
        this.groupByReference = groupByReference;
    }
}
