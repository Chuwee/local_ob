package es.onebox.event.sessions.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class GenerationStatusSessionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private SessionStatus status;
    private SessionGenerationStatus sessionGenerationStatus;

    public SessionGenerationStatus getSessionGenerationStatus() {
        return sessionGenerationStatus;
    }

    public void setSessionGenerationStatus(SessionGenerationStatus sessionGenerationStatus) {
        this.sessionGenerationStatus = sessionGenerationStatus;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
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
