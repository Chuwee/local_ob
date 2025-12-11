package es.onebox.event.seasontickets.dto.renewals;

import java.io.Serial;
import java.io.Serializable;

public class UpdateSeasonTicketRenewalsConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8277741725633449928L;

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
