package es.onebox.mgmt.datasources.integration.dispatcher.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import es.onebox.mgmt.datasources.integration.dispatcher.enums.Status;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;

public class StatusDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4831324661574178650L;

    private Status status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String reason;

    public StatusDTO() {
    }

    public StatusDTO(Status status) {
        this.status = status;
    }

    public StatusDTO(Status status, String reason) {
        this.status = status;
        this.reason = reason;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
