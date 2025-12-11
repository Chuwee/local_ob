package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class SessionSettingsLimitsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Valid
    @JsonProperty("tickets")
    private SessionSettingsLimitsTicketsDTO ticketsLimit;

    @Valid
    @JsonProperty("members_logins")
    private SessionSettingsLimitsMembersLoginsDTO membersLoginsLimit;

    public SessionSettingsLimitsTicketsDTO getTicketsLimit() {
        return ticketsLimit;
    }

    public void setTicketsLimit(SessionSettingsLimitsTicketsDTO ticketsLimit) {
        this.ticketsLimit = ticketsLimit;
    }

    public SessionSettingsLimitsMembersLoginsDTO getMembersLoginsLimit() {
        return membersLoginsLimit;
    }

    public void setMembersLoginsLimit(SessionSettingsLimitsMembersLoginsDTO membersLoginsLimit) {
        this.membersLoginsLimit = membersLoginsLimit;
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
