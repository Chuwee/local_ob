package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import es.onebox.mgmt.seasontickets.enums.TransferPolicyDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class SeasonTicketTransferTicket implements Serializable {
    private static final long serialVersionUID = -5083810816453509198L;

    private TransferPolicyDTO transferPolicy;
    private Integer transferTicketMaxDelayTime;
    private Integer recoveryTicketMaxDelayTime;
    private Boolean enableMaxTicketTransfers;
    private Integer maxTicketTransfers;
    private Integer transferTicketMinDelayTime;

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

    public TransferPolicyDTO getTransferPolicy() {
        return transferPolicy;
    }

    public void setTransferPolicy(TransferPolicyDTO transferPolicy) {
        this.transferPolicy = transferPolicy;
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
