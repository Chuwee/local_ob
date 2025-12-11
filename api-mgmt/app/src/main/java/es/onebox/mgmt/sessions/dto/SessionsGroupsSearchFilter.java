package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.sessions.enums.SessionsGroupType;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


public class SessionsGroupsSearchFilter extends SessionSearchFilter {

    private static final long serialVersionUID = 1L;

    @JsonProperty("group_type")
    @NotNull(message = "group_type cannot be null")
    private SessionsGroupType groupType;

    public SessionsGroupType getGroupType() {
        return groupType;
    }

    public void setGroupType(SessionsGroupType groupType) {
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
