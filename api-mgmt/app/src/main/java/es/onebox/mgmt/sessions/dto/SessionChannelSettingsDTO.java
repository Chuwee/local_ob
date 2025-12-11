package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class SessionChannelSettingsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("show_date")
    private Boolean enableShowDateInChannels;
    @JsonProperty("show_time")
    private Boolean enableShowTimeInChannels;
    @JsonProperty("show_unconfirmed_date")
    private Boolean enableShowUnconfirmedDateInChannels;

    public Boolean getEnableShowDateInChannels() {
        return enableShowDateInChannels;
    }

    public void setEnableShowDateInChannels(Boolean enableShowDateInChannels) {
        this.enableShowDateInChannels = enableShowDateInChannels;
    }

    public Boolean getEnableShowTimeInChannels() {
        return enableShowTimeInChannels;
    }

    public void setEnableShowTimeInChannels(Boolean enableShowTimeInChannels) {
        this.enableShowTimeInChannels = enableShowTimeInChannels;
    }

    public Boolean getEnableShowUnconfirmedDateInChannels() {
        return enableShowUnconfirmedDateInChannels;
    }

    public void setEnableShowUnconfirmedDateInChannels(Boolean enableShowUnconfirmedDateInChannels) {
        this.enableShowUnconfirmedDateInChannels = enableShowUnconfirmedDateInChannels;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
