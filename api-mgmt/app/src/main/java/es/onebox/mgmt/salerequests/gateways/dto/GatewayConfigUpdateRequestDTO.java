package es.onebox.mgmt.salerequests.gateways.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.channels.gateways.dto.BaseChannelGatewayDTO;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;

public class GatewayConfigUpdateRequestDTO implements Serializable {

    private static final long serialVersionUID = 8965957128320561824L;

    @NotNull
    private Boolean custom;

    @JsonProperty("channel_gateways")
    private List<BaseChannelGatewayDTO> channelGateways;

    public Boolean getCustom() {
        return custom;
    }

    public void setCustom(Boolean custom) {
        this.custom = custom;
    }

    public List<BaseChannelGatewayDTO> getChannelGateways() {
        return channelGateways;
    }

    public void setChannelGateways(List<BaseChannelGatewayDTO> channelGateways) {
        this.channelGateways = channelGateways;
    }
}
