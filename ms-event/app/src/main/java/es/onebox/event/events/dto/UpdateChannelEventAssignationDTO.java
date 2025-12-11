package es.onebox.event.events.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class UpdateChannelEventAssignationDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean allClients;
    private Long quotaId;
    private List<Long> clients;

    public Boolean getAllClients() {
        return allClients;
    }

    public void setAllClients(Boolean allClients) {
        this.allClients = allClients;
    }

    public Long getQuotaId() {
        return quotaId;
    }

    public void setQuotaId(Long quotaId) {
        this.quotaId = quotaId;
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
