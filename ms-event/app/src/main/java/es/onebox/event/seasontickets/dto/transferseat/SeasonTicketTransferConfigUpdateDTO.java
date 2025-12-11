package es.onebox.event.seasontickets.dto.transferseat;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class SeasonTicketTransferConfigUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private TransferPolicy transferPolicy;
    private Integer transferTicketMaxDelayTime;
    private Integer transferTicketMinDelayTime;
    private Integer recoveryTicketMaxDelayTime;
    private Boolean enableMaxTicketTransfers;
    private Integer maxTicketTransfers;
    private Boolean enableBulk;
    private List<Long> bulkCustomerTypes;
    private List<Long> excludedSessions;


    public TransferPolicy getTransferPolicy() {
        return transferPolicy;
    }

    public void setTransferPolicy(TransferPolicy transferPolicy) {
        this.transferPolicy = transferPolicy;
    }

    public Integer getTransferTicketMaxDelayTime() {
        return transferTicketMaxDelayTime;
    }

    public void setTransferTicketMaxDelayTime(Integer transferTicketMaxDelayTime) {
        this.transferTicketMaxDelayTime = transferTicketMaxDelayTime;
    }

    public Integer getTransferTicketMinDelayTime() {
        return transferTicketMinDelayTime;
    }

    public void setTransferTicketMinDelayTime(Integer transferTicketMinDelayTime) {
        this.transferTicketMinDelayTime = transferTicketMinDelayTime;
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

    public Boolean getEnableBulk() {
        return enableBulk;
    }

    public void setEnableBulk(Boolean enableBulk) {
        this.enableBulk = enableBulk;
    }

    public List<Long> getBulkCustomerTypes() {
        return bulkCustomerTypes;
    }

    public void setBulkCustomerTypes(List<Long> bulkCustomerTypes) {
        this.bulkCustomerTypes = bulkCustomerTypes;
    }

    public List<Long> getExcludedSessions() {
        return excludedSessions;
    }

    public void setExcludedSessions(List<Long> excludedSessions) {
        this.excludedSessions = excludedSessions;
    }
}
