package es.onebox.mgmt.events.dto.channel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class UpdateEventChannelDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private EventChannelSettingsDTO settings;
    @JsonProperty("use_all_quotas")
    private Boolean useAllQuotas;
    @JsonProperty("quotas")
    private List<Long> quotas;
    @JsonProperty("provider_plan_settings")
    private ProviderPlanSettingsDTO providerPlanSettings;

    public EventChannelSettingsDTO getSettings() {
        return settings;
    }

    public void setSettings(EventChannelSettingsDTO settings) {
        this.settings = settings;
    }

    public Boolean getUseAllQuotas() {
        return useAllQuotas;
    }

    public void setUseAllQuotas(Boolean useAllQuotas) {
        this.useAllQuotas = useAllQuotas;
    }

    public List<Long> getQuotas() {
        return quotas;
    }

    public void setQuotas(List<Long> quotas) {
        this.quotas = quotas;
    }

    public ProviderPlanSettingsDTO getProviderPlanSettings() {
        return providerPlanSettings;
    }

    public void setProviderPlanSettings(ProviderPlanSettingsDTO providerPlanSettings) {
        this.providerPlanSettings = providerPlanSettings;
    }
}
