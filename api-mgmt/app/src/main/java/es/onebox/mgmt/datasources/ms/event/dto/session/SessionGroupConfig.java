package es.onebox.mgmt.datasources.ms.event.dto.session;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class SessionGroupConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer maxGroups;
    private Integer minAttendees;
    private Integer maxAttendees;
    private Integer minCompanions;
    private Integer maxCompanions;
    private Boolean companionsOccupyCapacity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMaxGroups() {
        return maxGroups;
    }

    public void setMaxGroups(Integer maxGroups) {
        this.maxGroups = maxGroups;
    }

    public Integer getMinAttendees() {
        return minAttendees;
    }

    public void setMinAttendees(Integer minAttendees) {
        this.minAttendees = minAttendees;
    }

    public Integer getMaxAttendees() {
        return maxAttendees;
    }

    public void setMaxAttendees(Integer maxAttendees) {
        this.maxAttendees = maxAttendees;
    }

    public Integer getMinCompanions() {
        return minCompanions;
    }

    public void setMinCompanions(Integer minCompanions) {
        this.minCompanions = minCompanions;
    }

    public Integer getMaxCompanions() {
        return maxCompanions;
    }

    public void setMaxCompanions(Integer maxCompanions) {
        this.maxCompanions = maxCompanions;
    }

    public Boolean getCompanionsOccupyCapacity() {
        return companionsOccupyCapacity;
    }

    public void setCompanionsOccupyCapacity(Boolean companionsOccupyCapacity) {
        this.companionsOccupyCapacity = companionsOccupyCapacity;
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
