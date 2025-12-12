package es.onebox.common.datasources.orderitems.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.datasources.orderitems.enums.AccessControlValidatonStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class AccessControlValidation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("session_id")
    private Long sessionId;
    private String user;
    private ZonedDateTime date;
    private AccessControlValidatonStatus status;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public AccessControlValidatonStatus getStatus() {
        return status;
    }

    public void setStatus(AccessControlValidatonStatus status) {
        this.status = status;
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
