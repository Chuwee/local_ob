package es.onebox.mgmt.datasources.ms.channel.dto.donations;

import java.io.Serial;
import java.io.Serializable;

public class DonationsConfig implements Serializable {
    @Serial
    private static final long serialVersionUID = 6962630932982885211L;

    private Boolean enabled;
    private DonationProvider provider;
    private DonationSettings settings;
    private Campaign campaign;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public DonationProvider getProvider() {
        return provider;
    }

    public void setProvider(DonationProvider provider) {
        this.provider = provider;
    }

    public DonationSettings getSettings() {
        return settings;
    }

    public void setSettings(DonationSettings settings) {
        this.settings = settings;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }
}
