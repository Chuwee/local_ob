package es.onebox.mgmt.channels.sharing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class BookingCheckoutPaymentSettingsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -2124953858036186295L;

    @JsonProperty("active")
    private Boolean isActive;
    @JsonProperty("default")
    private Boolean isDefault;
    @JsonProperty("gateway_sid")
    private String gatewaySid;
    @JsonProperty("conf_sid")
    private String confSid;


    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public String getGatewaySid() {
        return gatewaySid;
    }

    public void setGatewaySid(String gatewaySid) {
        this.gatewaySid = gatewaySid;
    }

    public String getConfSid() {
        return confSid;
    }

    public void setConfSid(String confSid) {
        this.confSid = confSid;
    }
}
