package es.onebox.mgmt.datasources.ms.channel.salerequests.dto;

import es.onebox.mgmt.datasources.common.dto.TimeZone;
import es.onebox.mgmt.datasources.ms.channel.salerequests.enums.SessionStatus;
import es.onebox.mgmt.datasources.ms.channel.salerequests.enums.SessionType;

import java.io.Serializable;

public class MsSessionSaleRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private SessionType type;
    private SessionStatus status;
    private MsSessionDate date;
    private Boolean published;
    private String externalReference;
    private TimeZone timeZone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SessionType getType() {
        return type;
    }

    public void setType(SessionType type) {
        this.type = type;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public MsSessionDate getDate() {
        return date;
    }

    public void setDate(MsSessionDate date) {
        this.date = date;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }
}
