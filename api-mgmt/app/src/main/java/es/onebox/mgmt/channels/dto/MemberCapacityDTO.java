package es.onebox.mgmt.channels.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class MemberCapacityDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1728604948746413235L;
    private String name;
    private Long id;
    @JsonProperty("virtual_zone_id")
    private Long virtualZoneId;
    @JsonProperty("venue_template_id")
    private Long venueTemplateId;
    private Boolean main;

    public MemberCapacityDTO() {
    }

    public MemberCapacityDTO(String name, Long avetCapacityId, Long virtualZoneId, Long venueTemplateId, Boolean main) {
        this.name = name;
        this.id = avetCapacityId;
        this.virtualZoneId = virtualZoneId;
        this.venueTemplateId = venueTemplateId;
        this.main = main;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVirtualZoneId() {
        return virtualZoneId;
    }

    public void setVirtualZoneId(Long virtualZoneId) {
        this.virtualZoneId = virtualZoneId;
    }

    public Long getVenueTemplateId() {
        return venueTemplateId;
    }

    public void setVenueTemplateId(Long venueTemplateId) {
        this.venueTemplateId = venueTemplateId;
    }

    public Boolean getMain() {
        return main;
    }

    public void setMain(Boolean main) {
        this.main = main;
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
