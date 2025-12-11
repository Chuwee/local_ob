package es.onebox.event.sessions.request;

import es.onebox.event.sessions.enums.SessionGroupType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import jakarta.validation.constraints.NotNull;

import java.io.Serial;


public class SessionsGroupsSearchFilter extends SessionSearchFilter {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "groupType must be not null")
    private SessionGroupType groupType;

    public SessionGroupType getGroupType() {
        return groupType;
    }

    public void setGroupType(SessionGroupType groupType) {
        this.groupType = groupType;
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
