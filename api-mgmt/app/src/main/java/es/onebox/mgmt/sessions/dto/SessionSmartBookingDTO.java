package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.sessions.enums.SessionSmartBookingType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class SessionSmartBookingDTO implements Serializable {


    @Serial
    private static final long serialVersionUID = -1227976175906495208L;

    @JsonProperty("type")
    private SessionSmartBookingType type;

    @JsonProperty("related_id")
    private Long relatedSessionId;

    public SessionSmartBookingType getType() {
        return type;
    }

    public void setType(SessionSmartBookingType type) {
        this.type = type;
    }

    public Long getRelatedSessionId() {
        return relatedSessionId;
    }

    public void setRelatedSessionId(Long relatedSessionId) {
        this.relatedSessionId = relatedSessionId;
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
