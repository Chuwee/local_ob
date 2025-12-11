package es.onebox.mgmt.datasources.ms.event.dto.b2b;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class EventChannelB2BAssignation extends IdDTO {

    private static final long serialVersionUID = 1L;

    private Boolean allClients;
    private IdNameDTO quota;
    private List<Long> clients;

    public Boolean getAllClients() {
        return allClients;
    }

    public void setAllClients(Boolean allClients) {
        this.allClients = allClients;
    }

    public IdNameDTO getQuota() {
        return quota;
    }

    public void setQuota(IdNameDTO quota) {
        this.quota = quota;
    }

    public List<Long> getClients() {
        return clients;
    }

    public void setClients(List<Long> clients) {
        this.clients = clients;
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
