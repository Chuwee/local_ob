package es.onebox.mgmt.channels.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class DonationsConfigDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 5903175506574459047L;

    @JsonProperty("enabled")
    private Boolean enabled;
    @JsonProperty("provider")
    private DonationProviderDTO provider;
    @JsonProperty("settings")
    private DonationSettingsDTO settings;
    @JsonProperty("campaign")
    private CampaignDTO campaign;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public DonationProviderDTO getProvider() {
        return provider;
    }

    public void setProvider(DonationProviderDTO provider) {
        this.provider = provider;
    }

    public DonationSettingsDTO getSettings() {
        return settings;
    }

    public void setSettings(DonationSettingsDTO settings) {
        this.settings = settings;
    }

    public CampaignDTO getCampaign() {
        return campaign;
    }

    public void setCampaign(CampaignDTO campaign) {
        this.campaign = campaign;
    }
}
