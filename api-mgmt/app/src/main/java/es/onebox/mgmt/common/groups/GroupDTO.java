package es.onebox.mgmt.common.groups;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.LimitlessValueDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class GroupDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("limit")
    private LimitlessValueDTO limit;
    private GroupAttendeeDTO attendees;
    private GroupCompanionDTO companions;

    public LimitlessValueDTO getLimit() {
        return limit;
    }

    public void setLimit(LimitlessValueDTO limit) {
        this.limit = limit;
    }

    public GroupAttendeeDTO getAttendees() {
        return attendees;
    }

    public void setAttendees(GroupAttendeeDTO attendees) {
        this.attendees = attendees;
    }

    public GroupCompanionDTO getCompanions() {
        return companions;
    }

    public void setCompanions(GroupCompanionDTO companions) {
        this.companions = companions;
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
