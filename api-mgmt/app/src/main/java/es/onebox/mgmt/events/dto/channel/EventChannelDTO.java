package es.onebox.mgmt.events.dto.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class EventChannelDTO extends BaseEventChannelDTO {

    private static final long serialVersionUID = 1L;

    @JsonProperty("use_all_quotas")
    private Boolean useAllQuotas;
    @JsonProperty("quotas")
    private List<EventChannelQuotaDTO> quotas;

    public Boolean getUseAllQuotas() {
        return useAllQuotas;
    }

    public void setUseAllQuotas(Boolean useAllQuotas) {
        this.useAllQuotas = useAllQuotas;
    }

    public List<EventChannelQuotaDTO> getQuotas() {
        return quotas;
    }

    public void setQuotas(List<EventChannelQuotaDTO> quotas) {
        this.quotas = quotas;
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
