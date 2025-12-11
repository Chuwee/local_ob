package es.onebox.mgmt.channels.dto.adminchannels;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.channels.dto.ChannelDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class AdminChannelDTO extends ChannelDTO {

    private static final long serialVersionUID = 1L;
    @JsonProperty("whitelabel_settings")
    private AdminChannelWhitelabelSettings whitelabelSettings;

    public AdminChannelWhitelabelSettings getWhitelabelSettings() {
        return whitelabelSettings;
    }

    public void setWhitelabelSettings(AdminChannelWhitelabelSettings whitelabelSettings) {
        this.whitelabelSettings = whitelabelSettings;
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
