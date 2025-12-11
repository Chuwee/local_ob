package es.onebox.mgmt.datasources.ms.payment.dto;

import java.io.Serializable;

public class Key implements Serializable {

    private static final long serialVersionUID = -7906714471047397243L;

    private Integer channelId;
    private String gatewaySid;
    private String confSid;

    public Key() {
    }

    public Key(Integer channelId, String gatewaySid, String confSid) {
        this.channelId = channelId;
        this.gatewaySid = gatewaySid;
        this.confSid = confSid;
    }

    public Integer getChannelId() {
        return this.channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getGatewaySid() {
        return this.gatewaySid;
    }

    public void setGatewaySid(String gatewaySid) {
        this.gatewaySid = gatewaySid;
    }

    public String getConfSid() {
        return this.confSid;
    }

    public void setConfSid(String confSid) {
        this.confSid = confSid;
    }
}
