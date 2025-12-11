package es.onebox.mgmt.datasources.ms.channel.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ChannelCorsSettings implements Serializable {

    @Serial
    private static final long serialVersionUID = 6452314528195643403L;

    private Boolean enabled;
    private List<String> allowedOrigins;

    public ChannelCorsSettings() {
    }

    public ChannelCorsSettings(Boolean enabled, List<String> allowedOrigins) {
        this.enabled = enabled;
        this.allowedOrigins = allowedOrigins;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }
}
