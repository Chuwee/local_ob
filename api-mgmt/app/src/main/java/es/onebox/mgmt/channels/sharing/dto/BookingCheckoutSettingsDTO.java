package es.onebox.mgmt.channels.sharing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class BookingCheckoutSettingsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -7200056960815487349L;


    @JsonProperty("enabled")
    private Boolean enabled;
    @JsonProperty("channel_id")
    private Long channelId;
    @JsonProperty("channel_name")
    private String channelName;
    @JsonProperty("payment_settings")
    private List<BookingCheckoutPaymentSettingsDTO> paymentSettings;


    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public List<BookingCheckoutPaymentSettingsDTO> getPaymentSettings() {
        return paymentSettings;
    }

    public void setPaymentSettings(List<BookingCheckoutPaymentSettingsDTO> paymentSettings) {
        this.paymentSettings = paymentSettings;
    }
}
