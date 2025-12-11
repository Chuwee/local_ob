package es.onebox.mgmt.datasources.ms.event.dto.event;

import es.onebox.mgmt.events.enums.TransferPolicy;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class EventTransferTicket implements Serializable {
    private static final long serialVersionUID = -5083810816453509198L;

    private TransferPolicy transferPolicy;
    private Integer transferTicketMaxDelayTime;
    private Integer recoveryTicketMaxDelayTime;
    private Boolean enableMaxTicketTransfers;
    private Integer maxTicketTransfers;
    private Integer transferTicketMinDelayTime;
    private Boolean restrictTransferBySessions;
    private Boolean allowMultipleTransfers;
    private List<Long> allowedTransferSessions;

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

    public Boolean getAllowMultipleTransfers() {
        return allowMultipleTransfers;
    }

    public void setAllowMultipleTransfers(Boolean allowMultipleTransfers) {
        this.allowMultipleTransfers = allowMultipleTransfers;
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
