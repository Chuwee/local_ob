package es.onebox.common.datasources.dispatcher.dto;

import es.onebox.common.datasources.avetconfig.dto.Status;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class CheckStatusResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Status status;
    private String reason;

    public CheckStatusResponse() {
    }

    public CheckStatusResponse(Status status, String reason) {
        this.status = status;
        this.reason = reason;
    }

    public CheckStatusResponse(String status, String reason) {
        this.status = Status.valueOf(status);
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

}
