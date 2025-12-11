package es.onebox.event.events.dto;

import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.List;

public class RatePriceZoneRestrictionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean applyToB2b;
    private List<Integer> restrictedPriceZoneIds;

    public Boolean getApplyToB2b() {
        return applyToB2b;
    }

    public void setApplyToB2b(Boolean applyToB2b) {
        this.applyToB2b = applyToB2b;
    }

    public List<Integer> getRestrictedPriceZoneIds() {
        return restrictedPriceZoneIds;
    }

    public void setRestrictedPriceZoneIds(List<Integer> restrictedPriceZoneIds) {
        this.restrictedPriceZoneIds = restrictedPriceZoneIds;
    }

    public boolean isEmpty() {
        return (CollectionUtils.isEmpty(restrictedPriceZoneIds));
    }
}
