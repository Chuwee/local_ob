package es.onebox.event.events.dto;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class ChannelEventB2BClientAssignationDTO extends IdDTO {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long client;
    private List<IdNameDTO> quotas;

    public List<IdNameDTO> getQuotas() {
        return quotas;
    }

    public void setQuotas(List<IdNameDTO> quotas) {
        this.quotas = quotas;
    }

    public Long getClient() {
        return client;
    }

    public void setClient(Long client) {
        this.client = client;
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
