package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;

import java.io.Serializable;
import java.util.List;

public class CreateVenueTemplatePriceTypeRestrictionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty
    @JsonProperty("required_price_type_ids")
    private List<Long> requiredPriceTypeIds;

    @JsonProperty("required_tickets_number")
    private Integer requiredTicketsNumber;

    @JsonProperty("locked_tickets_number")
    private Integer lockedTicketsNumber;

    public List<Long> getRequiredPriceTypeIds() {
        return requiredPriceTypeIds;
    }

    public void setRequiredPriceTypeIds(List<Long> requiredPriceTypeIds) {
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
