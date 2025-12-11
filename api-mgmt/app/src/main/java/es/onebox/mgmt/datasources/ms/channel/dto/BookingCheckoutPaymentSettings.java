package es.onebox.mgmt.datasources.ms.channel.dto;

import java.io.Serial;
import java.io.Serializable;

public class BookingCheckoutPaymentSettings implements Serializable {

    @Serial
    private static final long serialVersionUID = -46328187891444724L;

    private Boolean isActive;
    private Boolean isDefault;
    private String gatewaySid;
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
