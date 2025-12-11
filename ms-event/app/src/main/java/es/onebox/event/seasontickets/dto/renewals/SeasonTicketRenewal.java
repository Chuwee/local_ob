package es.onebox.event.seasontickets.dto.renewals;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Map;

public class SeasonTicketRenewal implements Serializable {

    @Serial
    private static final long serialVersionUID = -5083810816453509198L;

    private ZonedDateTime renewalStartingDate;
    private ZonedDateTime renewalEndDate;
    private Boolean renewalEnabled;
    private Boolean isRenewalInProcess;
    private Boolean autoRenewal;
    private Boolean autoRenewalMandatory;
    private RenewalType renewalType;
    private Long bankAccountId;
    private Boolean groupByReference;

    public ZonedDateTime getRenewalStartingDate() {
        return renewalStartingDate;
    }

    public void setRenewalStartingDate(ZonedDateTime renewalStartingDate) {
        this.renewalStartingDate = renewalStartingDate;
    }

    public ZonedDateTime getRenewalEndDate() {
        return renewalEndDate;
    }

    public void setRenewalEndDate(ZonedDateTime renewalEndDate) {
        this.renewalEndDate = renewalEndDate;
    }

    public Boolean getRenewalEnabled() {
        return renewalEnabled;
    }

    public void setRenewalEnabled(Boolean renewalEnabled) {
        this.renewalEnabled = renewalEnabled;
    }

    public Boolean getRenewalInProcess() {
        return isRenewalInProcess;
    }

    public void setRenewalInProcess(Boolean renewalInProcess) {
        isRenewalInProcess = renewalInProcess;
    }

    public Boolean getAutoRenewal() {
        return autoRenewal;
    }

    public void setAutoRenewal(Boolean autoRenewal) {
        this.autoRenewal = autoRenewal;
    }

    public Boolean getAutoRenewalMandatory() {
        return autoRenewalMandatory;
    }

    public void setAutoRenewalMandatory(Boolean autoRenewalMandatory) {
        this.autoRenewalMandatory = autoRenewalMandatory;
    }

    public RenewalType getRenewalType() {
        return renewalType;
    }

    public void setRenewalType(RenewalType renewalType) {
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

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
