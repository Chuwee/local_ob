package es.onebox.mgmt.seasontickets.dto.channels;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class UpdateSeasonTicketChannelDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private SeasonTicketChannelSettingsDTO settings;
    @JsonProperty("use_all_quotas")
    private Boolean useAllQuotas;
    @JsonProperty("quotas")
    private List<Long> quotas;

    public SeasonTicketChannelSettingsDTO getSettings() {
        return settings;
    }

    public void setSettings(SeasonTicketChannelSettingsDTO settings) {
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
}
