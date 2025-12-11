package es.onebox.event.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class EventTemplateRestrictionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<IdNameDTO> requiredPriceTypes;

    private IdNameDTO lockedPriceType;

    private Integer requiredTickets;

    private Integer lockedTickets;

    public List<IdNameDTO> getRequiredPriceTypes() {
        return requiredPriceTypes;
    }

    public void setRequiredPriceTypes(List<IdNameDTO> requiredPriceTypes) {
        this.requiredPriceTypes = requiredPriceTypes;
    }

    public IdNameDTO getLockedPriceType() {
        return lockedPriceType;
    }

    public void setLockedPriceType(IdNameDTO lockedPriceType) {
        this.lockedPriceType = lockedPriceType;
    }

    public Integer getRequiredTickets() {
        return requiredTickets;
    }

    public void setRequiredTickets(Integer requiredTickets) {
        this.requiredTickets = requiredTickets;
    }

    public Integer getLockedTickets() {
        return lockedTickets;
    }

    public void setLockedTickets(Integer lockedTickets) {
        this.lockedTickets = lockedTickets;
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
