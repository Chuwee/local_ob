package es.onebox.common.datasources.ms.event.dto;

import java.io.Serial;
import java.io.Serializable;

public class SeasonTicketDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -1949382005934651311L;

    private Long id;
    private Long entityId;
    private Long sessionId;
    private Boolean allowRenewal;
    private SeasonTicketRenewalInfoDTO renewal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Boolean getAllowRenewal() {
        return allowRenewal;
    }

    public void setAllowRenewal(Boolean allowRenewal) {
        this.allowRenewal = allowRenewal;
    }

    public SeasonTicketRenewalInfoDTO getRenewal() {
        return renewal;
    }

    public void setRenewal(SeasonTicketRenewalInfoDTO renewal) {
        this.renewal = renewal;
    }
}