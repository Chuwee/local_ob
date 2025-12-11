package es.onebox.event.sessions.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class GenerationStatusSessionRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private SessionGenerationStatus sessionGenerationStatus;

    public SessionGenerationStatus getSessionGenerationStatus() {
        return sessionGenerationStatus;
    }

    public void setSessionGenerationStatus(SessionGenerationStatus sessionGenerationStatus) {
        this.sessionGenerationStatus = sessionGenerationStatus;
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
