package es.onebox.common.datasources.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.datasources.common.enums.EventType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class Event implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private Entity entity;
    private Entity promoter;
    private EventType type;
    private String reference;
    private Category category;
    @JsonProperty("supra_event")
    private Boolean supraEvent;

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

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public EventType getType() {
        return type;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public Entity getPromoter() {
        return promoter;
    }

    public void setPromoter(Entity promoter) {
        this.promoter = promoter;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Boolean getSupraEvent() {
        return supraEvent;
    }

    public void setSupraEvent(Boolean supraEvent) {
        this.supraEvent = supraEvent;
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
