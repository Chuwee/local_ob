package es.onebox.mgmt.channels.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.channels.enums.PromotionApplicationConfig;
import es.onebox.mgmt.channels.enums.PromotionsLocation;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

public class ChannelWhitelabelPromotionsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -8433639264999715511L;

    private Set<PromotionsLocation> locations;
    @JsonProperty("application_config")
    private PromotionApplicationConfig applicationConfig;

    public Set<PromotionsLocation> getLocations() {
        return locations;
    }
    public void setLocations(Set<PromotionsLocation> locations) {
        this.locations = locations;
    }

    public PromotionApplicationConfig getApplicationConfig() { return applicationConfig; }
    public void setApplicationConfig(PromotionApplicationConfig applicationConfig) { this.applicationConfig = applicationConfig; }
}
