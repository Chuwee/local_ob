package es.onebox.common.datasources.ms.event.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class SeasonTicketRenewalConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4859779005032697534L;

    private String renewalType;
    private Boolean groupByReference;
    private Long bankAccountId;

    public String getRenewalType() {
        return renewalType;
    }

    public void setRenewalType(String renewalType) { this.renewalType = renewalType; }

    public Boolean getGroupByReference() {
        return groupByReference;
    }

    public void setGroupByReference(Boolean groupByReference) {
        this.groupByReference = groupByReference;
    }

    public Long getBankAccountId() {
        return bankAccountId;
    }

    public void setBankAccountId(Long bankAccountId) {
        this.bankAccountId = bankAccountId;
    }
}