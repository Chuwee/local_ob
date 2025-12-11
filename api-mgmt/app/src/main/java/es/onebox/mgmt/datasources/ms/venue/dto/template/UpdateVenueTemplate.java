package es.onebox.mgmt.datasources.ms.venue.dto.template;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Optional;

public class UpdateVenueTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private VenueTemplateStatus status;
    private Optional<String> image;
    private Long spaceId;
    private Long venueId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VenueTemplateStatus getStatus() {
        return status;
    }

    public void setStatus(VenueTemplateStatus status) {
        this.status = status;
    }

    public Optional<String> getImage() {
        return image;
    }

    public void setImage(Optional<String> image) {
        this.image = image;
    }

    public Long getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(Long spaceId) {
        this.spaceId = spaceId;
    }

    public Long getVenueId() {
        return venueId;
    }

    public void setVenueId(Long venueId) {
        this.venueId = venueId;
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
