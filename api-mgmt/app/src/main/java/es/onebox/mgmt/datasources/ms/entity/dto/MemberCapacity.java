package es.onebox.mgmt.datasources.ms.entity.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class MemberCapacity implements Serializable {

    @Serial
    private static final long serialVersionUID = 7169908241167898054L;
    private String name;
    private Long avetCapacityId;
    private Boolean main;
    private Long virtualZoneId;
    private Long venueTemplateId;

    public MemberCapacity() {
    }

    public MemberCapacity(String name, Long avetCapacityId, Boolean main, Long virtualZoneId, Long venueTemplateId) {
        this.name = name;
        this.avetCapacityId = avetCapacityId;
        this.main = main;
        this.virtualZoneId = virtualZoneId;
        this.venueTemplateId = venueTemplateId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getAvetCapacityId() {
        return avetCapacityId;
    }

    public void setAvetCapacityId(Long avetCapacityId) {
        this.avetCapacityId = avetCapacityId;
    }

    public Boolean isMain() {
        return main;
    }

    public void setMain(Boolean main) {
        this.main = main;
    }

    public Long getVirtualZoneId() {
        return virtualZoneId;
    }

    public void setVirtualZoneId(Long virtualZoneId) {
        this.virtualZoneId = virtualZoneId;
    }

    public Boolean getMain() {
        return main;
    }

    public Long getVenueTemplateId() {
        return venueTemplateId;
    }

    public void setVenueTemplateId(Long venueTemplateId) {
        this.venueTemplateId = venueTemplateId;
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
