package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class SessionRefundConditionsDTO extends SessionRefundConditionsBaseDTO {

    private static final long serialVersionUID = 8483926054025796825L;

    @JsonProperty("session_pack_refund_conditions")
    private List<SessionPackRefundConditionsDTO> sessionPackRefundConditions;

    public List<SessionPackRefundConditionsDTO> getSessionPackRefundConditions() {
        return sessionPackRefundConditions;
    }

    public void setSessionPackRefundConditions(List<SessionPackRefundConditionsDTO> sessionPackRefundConditions) {
        this.sessionPackRefundConditions = sessionPackRefundConditions;
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
