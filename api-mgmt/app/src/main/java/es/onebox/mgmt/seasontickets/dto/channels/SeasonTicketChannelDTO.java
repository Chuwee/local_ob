package es.onebox.mgmt.seasontickets.dto.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.utils.dto.DateConvertible;

import java.util.List;

public class SeasonTicketChannelDTO extends BaseSeasonTicketChannelDTO implements DateConvertible {

    private static final long serialVersionUID = 1L;

    @JsonProperty("use_all_quotas")
    private Boolean useAllQuotas;
    @JsonProperty("quotas")
    private List<SeasonTicketChannelQuotaDTO> quotas;

    public Boolean getUseAllQuotas() {
        return useAllQuotas;
    }

    public void setUseAllQuotas(Boolean useAllQuotas) {
        this.useAllQuotas = useAllQuotas;
    }

    public List<SeasonTicketChannelQuotaDTO> getQuotas() {
        return quotas;
    }

    public void setQuotas(List<SeasonTicketChannelQuotaDTO> quotas) {
        this.quotas = quotas;
    }

}
