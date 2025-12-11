package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.datasources.ms.venue.dto.template.AccessibilityType;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VisibilityType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class BaseVenueTagDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    @JsonProperty("price_type")
    private Long priceType;
    private VisibilityType visibility;
    private AccessibilityType accessibility;
    private Long gate;
    @JsonProperty("dynamic_tag1")
    private Long dynamicTag1;
    @JsonProperty("dynamic_tag2")
    private Long dynamicTag2;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPriceType() {
        return priceType;
    }

    public void setPriceType(Long priceType) {
        this.priceType = priceType;
    }

    public VisibilityType getVisibility() {
        return visibility;
    }

    public void setVisibility(VisibilityType visibility) {
        this.visibility = visibility;
    }

    public AccessibilityType getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(AccessibilityType accessibility) {
        this.accessibility = accessibility;
    }

    public Long getGate() {
        return gate;
    }

    public void setGate(Long gate) {
        this.gate = gate;
    }

    public Long getDynamicTag1() {
        return dynamicTag1;
    }

    public void setDynamicTag1(Long dynamicTag1) {
        this.dynamicTag1 = dynamicTag1;
    }

    public Long getDynamicTag2() {
        return dynamicTag2;
    }

    public void setDynamicTag2(Long dynamicTag2) {
        this.dynamicTag2 = dynamicTag2;
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
