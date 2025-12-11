package es.onebox.event.catalog.elasticsearch.dto.seasonticket;

import java.io.Serial;
import java.io.Serializable;

public class TransferConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = -8740630340597281163L;

    private Boolean enabled;
    private String transferPolicy;
    private Integer transferMaxDelayTime;
    private Integer transferMinDelayTime;
    private Integer recoveryMaxDelayTime;
    private Integer maxTransfers;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getTransferPolicy() {
        return transferPolicy;
    }

    public void setTransferPolicy(String transferPolicy) {
        this.transferPolicy = transferPolicy;
    }

    public Integer getTransferMaxDelayTime() {
        return transferMaxDelayTime;
    }

    public void setTransferMaxDelayTime(Integer transferMaxDelayTime) {
        this.transferMaxDelayTime = transferMaxDelayTime;
    }

    public Integer getTransferMinDelayTime() {
        return transferMinDelayTime;
    }

    public void setTransferMinDelayTime(Integer transferMinDelayTime) {
        this.transferMinDelayTime = transferMinDelayTime;
    }

    public Integer getRecoveryMaxDelayTime() {
        return recoveryMaxDelayTime;
    }

    public void setRecoveryMaxDelayTime(Integer recoveryMaxDelayTime) {
        this.recoveryMaxDelayTime = recoveryMaxDelayTime;
    }

    public Integer getMaxTransfers() {
        return maxTransfers;
    }

    public void setMaxTransfers(Integer maxTransfers) {
        this.maxTransfers = maxTransfers;
    }
}