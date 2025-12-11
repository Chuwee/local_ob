package es.onebox.mgmt.salerequests.gateways.benefit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class GatewayBenefitsConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1471956897802374624L;

    @JsonProperty("channel_id")
    private Long channelId;
    @JsonProperty("gateway_sid")
    private String gatewaySid;
    @JsonProperty("conf_sid")
    private String confSid;
    @JsonProperty("event_id")
    private Long eventId;
    private List<BenefitDTO> benefits;

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
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

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public List<BenefitDTO> getBenefits() {
        return benefits;
    }

    public void setBenefits(List<BenefitDTO> benefits) {
        this.benefits = benefits;
    }
}
