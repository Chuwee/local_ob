package es.onebox.event.events.domain.eventconfig;


import es.onebox.event.events.dto.TransferPolicy;

import java.io.Serializable;
import java.util.List;

public class EventTransferTicketConfig implements Serializable {

    private TransferPolicy transferPolicy;
    private Boolean allowTransferTicket;
    private Integer transferTicketMaxDelayTime;
    private Integer recoveryTicketMaxDelayTime;
    private Boolean enableMaxTicketTransfers;
    private Integer maxTicketTransfers;
    private Integer transferTicketMinDelayTime;
    private Boolean restrictTransferBySessions;
    private Boolean allowMultipleTransfers;
    private List<Long> allowedTransferSessions;

    public Boolean getAllowChangeSeat() {
        return allowTransferTicket;
    }

    public void setAllowChangeSeat(Boolean allowTransferTicket) {
        this.allowTransferTicket = allowTransferTicket;
    }

    public TransferPolicy getTransferPolicy() {
        return transferPolicy;
    }

    public void setTransferPolicy(TransferPolicy transferPolicy) {
        this.transferPolicy = transferPolicy;
    }

    public Boolean getAllowTransferTicket() {
        return allowTransferTicket;
    }

    public void setAllowTransferTicket(Boolean allowTransferTicket) {
        this.allowTransferTicket = allowTransferTicket;
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

    public Integer getTransferTicketMinDelayTime() {
        return transferTicketMinDelayTime;
    }

    public void setTransferTicketMinDelayTime(Integer transferTicketMinDelayTime) {
        this.transferTicketMinDelayTime = transferTicketMinDelayTime;
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
}
