package es.onebox.mgmt.datasources.ms.ticket.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class SessionWithQuotasDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    public SessionWithQuotasDTO() {
    }

    public SessionWithQuotasDTO(Long sessionId, Long quotaId) {
        this.sessionId = sessionId;
        this.quotas = quotaId != null ? Collections.singletonList(quotaId) : null;
    }

    private Long sessionId;

    private List<Long> quotas;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public List<Long> getQuotas() {
        return quotas;
    }

    public void setQuotas(List<Long> quotas) {
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
