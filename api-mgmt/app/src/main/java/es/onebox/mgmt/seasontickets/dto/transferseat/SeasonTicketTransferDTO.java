package es.onebox.mgmt.seasontickets.dto.transferseat;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.entities.customertypes.dto.CustomerTypeDTO;
import es.onebox.mgmt.seasontickets.enums.TransferPolicyDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class SeasonTicketTransferDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("transfer_policy")
    private TransferPolicyDTO transferPolicy;
    @JsonProperty("enable_transfer_delay")
    private Boolean enableTransferDelay;
    @JsonProperty("transfer_ticket_max_delay_time")
    private Integer transferTicketMaxDelayTime;
    @JsonProperty("transfer_ticket_min_delay_time")
    private Integer transferTicketMinDelayTime;
    @JsonProperty("enable_recovery_delay")
    private Boolean enableRecoveryDelay;
    @JsonProperty("recovery_ticket_max_delay_time")
    private Integer recoveryTicketMaxDelayTime;
    @JsonProperty("enable_max_ticket_transfers")
    private Boolean enableMaxTicketTransfers;
    @JsonProperty("max_ticket_transfers")
    private Integer maxTicketTransfers;
    @JsonProperty("enable_bulk")
    private Boolean enableBulk;
    @JsonProperty("bulk_customer_types")
    private List<CustomerTypeDTO> bulkCustomerTypes;
    @JsonProperty("excluded_sessions")
    private List<Long> excludedSessions;



    public TransferPolicyDTO getTransferPolicy() {
        return transferPolicy;
    }

    public void setTransferPolicy(TransferPolicyDTO transferPolicy) {
        this.transferPolicy = transferPolicy;
    }

    public Boolean getEnableTransferDelay() {
        return enableTransferDelay;
    }

    public void setEnableTransferDelay(Boolean enableTransferDelay) {
        this.enableTransferDelay = enableTransferDelay;
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

    public Boolean getEnableRecoveryDelay() {
        return enableRecoveryDelay;
    }

    public void setEnableRecoveryDelay(Boolean enableRecoveryDelay) {
        this.enableRecoveryDelay = enableRecoveryDelay;
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

    public List<CustomerTypeDTO> getBulkCustomerTypes() {
        return bulkCustomerTypes;
    }

    public void setBulkCustomerTypes(List<CustomerTypeDTO> bulkCustomerTypes) {
        this.bulkCustomerTypes = bulkCustomerTypes;
    }

    public List<Long> getExcludedSessions() {
        return excludedSessions;
    }

    public void setExcludedSessions(List<Long> excludedSessions) {
        this.excludedSessions = excludedSessions;
    }
}
