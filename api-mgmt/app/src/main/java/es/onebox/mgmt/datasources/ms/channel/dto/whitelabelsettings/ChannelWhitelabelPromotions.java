package es.onebox.mgmt.datasources.ms.channel.dto.whitelabelsettings;

import es.onebox.mgmt.channels.enums.PromotionApplicationConfig;
import es.onebox.mgmt.channels.enums.PromotionsLocation;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

public class ChannelWhitelabelPromotions implements Serializable {

    @Serial
    private static final long serialVersionUID = -8433639264999715511L;

    private Set<PromotionsLocation> locations;
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
