package es.onebox.mgmt.sessions.dto;

import java.io.Serializable;

public class IntegrationEventEntityDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer eventId;
    private Boolean state;
    private Integer integrationId;


    public Integer getEventId() {
        return this.eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Boolean getState() {
        return this.state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public Integer getIntegrationId() {
        return this.integrationId;
    }

    public void setIntegrationId(Integer integrationId) {
        this.integrationId = integrationId;
    }
}