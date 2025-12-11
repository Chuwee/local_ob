package es.onebox.mgmt.channels.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class MemberCapacityRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -8816522965673341402L;
    private String name;
    private Long id;
    @JsonProperty("virtual_zone_id")
    private Long virtualZoneId;
    @JsonProperty("venue_template_id")
    private Long venueTemplateId;
    private Boolean main;

    public MemberCapacityRequestDTO() {
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
