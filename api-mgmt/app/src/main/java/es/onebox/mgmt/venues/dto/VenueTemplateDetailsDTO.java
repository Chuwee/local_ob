package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.common.groups.GroupDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Map;

public class VenueTemplateDetailsDTO extends BaseVenueTemplateDTO {

    private static final long serialVersionUID = 1L;

    @JsonProperty("available_capacity")
    private Integer availableCapacity;
    private BaseVenueDTO venue;
    private IdNameDTO space;

    @Deprecated //See groups.*
    @JsonProperty("max_groups")
    private Integer maxGroups;
    @JsonProperty("min_attendees")
    private Integer minAttendees;
    @JsonProperty("max_attendees")
    private Integer maxAttendees;
    @JsonProperty("min_companions")
    private Integer minCompanions;
    @JsonProperty("max_companions")
    private Integer maxCompanions;
    @JsonProperty("companions_occupy_capacity")
    private Boolean companionsOccupyCapacity;
    @JsonProperty("external_data")
    private Map<String, Object> externalData;

    private GroupDTO groups;

    public Integer getAvailableCapacity() {
        return availableCapacity;
    }

    public void setAvailableCapacity(Integer availableCapacity) {
        this.availableCapacity = availableCapacity;
    }

    @Override
    public BaseVenueDTO getVenue() {
        return venue;
    }

    public void setVenue(BaseVenueDTO venue) {
        this.venue = venue;
    }

    public IdNameDTO getSpace() {
        return space;
    }

    public void setSpace(IdNameDTO space) {
        this.space = space;
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

    public GroupDTO getGroups() {
        return groups;
    }

    public void setGroups(GroupDTO groups) {
        this.groups = groups;
    }

    public Map<String, Object> getExternalData() {
        return externalData;
    }

    public void setExternalData(Map<String, Object> externalData) {
        this.externalData = externalData;
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
