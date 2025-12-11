package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class RatePriceTypeRestrictionDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    @JsonProperty("apply_to_b2b")
    private Boolean applyToB2B;
    @JsonProperty("restricted_price_type_ids")
    private List<Integer> restrictedPriceTypeIds;

    public Boolean getApplyToB2B() {
        return applyToB2B;
    }

    public void setApplyToB2B(Boolean applyToB2B) {
        this.applyToB2B = applyToB2B;
    }

    public List<Integer> getRestrictedPriceTypeIds() {
        return restrictedPriceTypeIds;
    }

    public void setRestrictedPriceTypeIds(List<Integer> restrictedPriceTypeIds) {
        this.restrictedPriceTypeIds = restrictedPriceTypeIds;
    }

}
