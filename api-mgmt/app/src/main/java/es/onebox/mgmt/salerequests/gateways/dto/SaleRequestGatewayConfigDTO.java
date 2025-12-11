package es.onebox.mgmt.salerequests.gateways.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.channels.gateways.dto.ChannelGatewayDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class SaleRequestGatewayConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7788864273682147604L;

    private Boolean custom;
    private Boolean benefits;
    @JsonProperty("channel_gateways")
    private List<ChannelGatewayDTO> channelGateways;

    public Boolean getCustom() {
        return custom;
    }

    public void setCustom(Boolean custom) {
        this.custom = custom;
    }

    public Boolean getBenefits() {
        return benefits;
    }

    public void setBenefits(Boolean benefits) {
        this.benefits = benefits;
    }

    public List<ChannelGatewayDTO> getChannelGateways() {
        return channelGateways;
    }

    public void setChannelGateways(List<ChannelGatewayDTO> channelGateways) {
        this.channelGateways = channelGateways;
    }
}
