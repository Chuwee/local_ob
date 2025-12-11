package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.groups.GroupDTO;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;

public class UpdateTemplateRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Size(max = 50, message = "name length cannot be above 50 characters")
    private String name;
    private Optional<String> image;
    private GroupDTO groups;
    @JsonProperty("space_id")
    private Long spaceId;
    @JsonProperty("venue_id")
    private Long venueId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Optional<String> getImage() {
        return image;
    }

    public void setImage(Optional<String> image) {
        this.image = image;
    }

    public GroupDTO getGroups() {
        return groups;
    }

    public void setGroups(GroupDTO groups) {
        this.groups = groups;
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

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
