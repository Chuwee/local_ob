package es.onebox.mgmt.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class GatewayConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("gateway_sid")
    private String gatewaySid;
    private String name;
    private String description;
    private boolean synchronous;
    private boolean refund;
    private boolean retry;
    private Integer retries;
    private boolean live;
    private Boolean waller;
    @JsonProperty("available_gateway_asociation")
    private List<String> availableGatewayAsociation;
    private Boolean wallet;

    public String getGatewaySid() {
        return gatewaySid;
    }

    public void setGatewaySid(String gatewaySid) {
        this.gatewaySid = gatewaySid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSynchronous() {
        return synchronous;
    }

    public void setSynchronous(boolean synchronous) {
        this.synchronous = synchronous;
    }

    public boolean isRefund() {
        return refund;
    }

    public void setRefund(boolean refund) {
        this.refund = refund;
    }

    public boolean isRetry() {
        return retry;
    }

    public void setRetry(boolean retry) {
        this.retry = retry;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public Boolean getWaller() {
        return waller;
    }

    public void setWaller(Boolean waller) {
        this.waller = waller;
    }

    public List<String> getAvailableGatewayAsociation() {
        return availableGatewayAsociation;
    }

    public void setAvailableGatewayAsociation(List<String> availableGatewayAsociation) {
        this.availableGatewayAsociation = availableGatewayAsociation;
    }

    public Boolean getWallet() {
        return wallet;
    }

    public void setWallet(Boolean wallet) {
        this.wallet = wallet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GatewayConfigDTO that = (GatewayConfigDTO) o;
        return synchronous == that.synchronous &&
                refund == that.refund &&
                retry == that.retry &&
                live == that.live &&
                Objects.equals(gatewaySid, that.gatewaySid) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(retries, that.retries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gatewaySid, name, description, synchronous, refund, retry, retries, live);
    }
}
