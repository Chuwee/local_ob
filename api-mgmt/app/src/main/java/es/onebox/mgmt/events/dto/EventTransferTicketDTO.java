package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.events.enums.TransferPolicy;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class EventTransferTicketDTO implements Serializable {
    private static final long serialVersionUID = -5083810816453509198L;

    private boolean enabled;
    @JsonProperty("transfer_policy")
    private TransferPolicy transferPolicy;
    @JsonProperty("transfer_ticket_max_delay_time")
    private Integer transferTicketMaxDelayTime;
    @JsonProperty("recovery_ticket_max_delay_time")
    private Integer recoveryTicketMaxDelayTime;
    @JsonProperty("enable_max_ticket_transfers")
    private Boolean enableMaxTicketTransfers;
    @JsonProperty("enable_multiple_transfers")
    private Boolean enableMultipleTransfers;
    @JsonProperty("max_ticket_transfers")
    private Integer maxTicketTransfers;
    @JsonProperty("transfer_ticket_min_delay_time")
    private Integer transferTicketMinDelayTime;
    @JsonProperty("restrict_transfer_by_sessions")
    private Boolean restrictTransferBySessions;
    @JsonProperty("allowed_transfer_sessions")
    private List<Long> allowedTransferSessions;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getTransferTicketMaxDelayTime() {
        return transferTicketMaxDelayTime;
    }

    public void setTransferTicketMaxDelayTime(Integer transferTicketMaxDelayTime) {
        this.transferTicketMaxDelayTime = transferTicketMaxDelayTime;
    }

    public Integer getRecoveryTicketMaxDelayTime() {
        return recoveryTicketMaxDelayTime;
    }

    public void setRecoveryTicketMaxDelayTime(Integer recoveryTicketMaxDelayTime) {
        this.recoveryTicketMaxDelayTime = recoveryTicketMaxDelayTime;
    }

    public Boolean getEnableMaxTicketTransfers() {
        return enableMaxTicketTransfers;
    }

    public void setEnableMaxTicketTransfers(Boolean enableMaxTicketTransfers) {
        this.enableMaxTicketTransfers = enableMaxTicketTransfers;
    }

    public Integer getMaxTicketTransfers() {
        return maxTicketTransfers;
    }

    public void setMaxTicketTransfers(Integer maxTicketTransfers) {
        this.maxTicketTransfers = maxTicketTransfers;
    }

    public Integer getTransferTicketMinDelayTime() { return transferTicketMinDelayTime; }

    public void setTransferTicketMinDelayTime(Integer transferTicketMinDelayTime) {
        this.transferTicketMinDelayTime = transferTicketMinDelayTime;
    }

    public TransferPolicy getTransferPolicy() {
        return transferPolicy;
    }

    public void setTransferPolicy(TransferPolicy transferPolicy) {
        this.transferPolicy = transferPolicy;
    }

    public Boolean getRestrictTransferBySessions() {
        return restrictTransferBySessions;
    }

    public void setRestrictTransferBySessions(Boolean restrictTransferBySessions) {
        this.restrictTransferBySessions = restrictTransferBySessions;
    }

    public List<Long> getAllowedTransferSessions() {
        return allowedTransferSessions;
    }

    public void setAllowedTransferSessions(List<Long> allowedTransferSessions) {
        this.allowedTransferSessions = allowedTransferSessions;
    }

    public Boolean getEnableMultipleTransfers() {
        return enableMultipleTransfers;
    }

    public void setEnableMultipleTransfers(Boolean enableMultipleTransfers) {
        this.enableMultipleTransfers = enableMultipleTransfers;
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
