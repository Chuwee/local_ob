package es.onebox.mgmt.datasources.ms.channel.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class BookingCheckoutSettings implements Serializable {

    @Serial
    private static final long serialVersionUID = -7200056960815487349L;

    private Boolean enabled;
    private Long channelId;
    private String channelName;
    private List<BookingCheckoutPaymentSettings> paymentSettings;


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

    public List<BookingCheckoutPaymentSettings> getPaymentSettings() {
        return paymentSettings;
    }

    public void setPaymentSettings(List<BookingCheckoutPaymentSettings> paymentSettings) {
        this.paymentSettings = paymentSettings;
    }
}
