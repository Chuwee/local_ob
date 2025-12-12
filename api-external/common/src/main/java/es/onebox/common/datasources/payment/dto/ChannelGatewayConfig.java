package es.onebox.common.datasources.payment.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ChannelGatewayConfig implements Serializable {

    private String gatewaySid;
    private String confSid;
    private boolean active;
    private Map<String, String> fieldsValues = new HashMap<>();


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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Map<String, String> getFieldsValues() {
        return fieldsValues;
    }

    public void setFieldsValues(Map<String, String> fieldsValues) {
        this.fieldsValues = fieldsValues;
    }
}
