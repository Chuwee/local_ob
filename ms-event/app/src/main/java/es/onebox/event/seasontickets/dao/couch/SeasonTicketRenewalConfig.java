package es.onebox.event.seasontickets.dao.couch;

import es.onebox.couchbase.annotations.CouchDocument;
import es.onebox.couchbase.annotations.Id;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@CouchDocument
public class SeasonTicketRenewalConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 98948063605916153L;

    @Id
    private Long seasonTicketId;
    private RenewalType renewalType;
    private AutomaticRenewalStatus automaticRenewalStatus;
    private Long bankAccountId;
    private Boolean groupByReference;
    private Boolean autoRenewalMandatory;

    public Long getSeasonTicketId() {
        return seasonTicketId;
    }

    public void setSeasonTicketId(Long seasonTicketId) {
        this.seasonTicketId = seasonTicketId;
    }

    public RenewalType getRenewalType() {
        return renewalType;
    }

    public void setRenewalType(RenewalType renewalType) {
        this.renewalType = renewalType;
    }

    public AutomaticRenewalStatus getAutomaticRenewalStatus() {
        return automaticRenewalStatus;
    }

    public void setAutomaticRenewalStatus(AutomaticRenewalStatus automaticRenewalStatus) {
        this.automaticRenewalStatus = automaticRenewalStatus;
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

    public Boolean getAutoRenewalMandatory() {
        return autoRenewalMandatory;
    }

    public void setAutoRenewalMandatory(Boolean autoRenewalMandatory) {
        this.autoRenewalMandatory = autoRenewalMandatory;
    }
}