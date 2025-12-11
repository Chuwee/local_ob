package es.onebox.mgmt.channels.gateways.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class UpdateChannelGateway implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("gateway_sid")
    private String gatewaySid;
    @JsonProperty("configuration_sid")
    private String configurationSid;
    private Boolean active;
    @JsonProperty("default")
    private Boolean defaultGateway;

    public String getGatewaySid() {
        return gatewaySid;
    }

    public void setGatewaySid(String gatewaySid) {
        this.gatewaySid = gatewaySid;
    }

    public String getConfigurationSid() {
        return configurationSid;
    }

    public void setConfigurationSid(String configurationSid) {
        this.configurationSid = configurationSid;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getDefaultGateway() {
        return defaultGateway;
    }

    public void setDefaultGateway(Boolean defaultGateway) {
        this.defaultGateway = defaultGateway;
    }

    @JsonProperty("send_additional_data")


    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
