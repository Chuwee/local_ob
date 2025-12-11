package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.io.Serializable;
import java.util.List;

public class VenueTemplatePriceTypeRestrictionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("required_price_types")
    private List<IdNameDTO> requiredPriceTypeIds;

    @JsonProperty("required_tickets_number")
    private Integer requiredTicketsNumber;

    @JsonProperty("locked_tickets_number")
    private Integer lockedTicketsNumber;

    public List<IdNameDTO> getRequiredPriceTypeIds() {
        return requiredPriceTypeIds;
    }

    public void setRequiredPriceTypeIds(List<IdNameDTO> requiredPriceTypeIds) {
        this.requiredPriceTypeIds = requiredPriceTypeIds;
    }

    public Integer getRequiredTicketsNumber() {
        return requiredTicketsNumber;
    }

    public void setRequiredTicketsNumber(Integer requiredTicketsNumber) {
        this.requiredTicketsNumber = requiredTicketsNumber;
    }

    public Integer getLockedTicketsNumber() {
        return lockedTicketsNumber;
    }

    public void setLockedTicketsNumber(Integer lockedTicketsNumber) {
        this.lockedTicketsNumber = lockedTicketsNumber;
    }
}
