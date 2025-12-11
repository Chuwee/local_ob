package es.onebox.event.common.domain;

import es.onebox.event.common.enums.PriceZoneRestrictionType;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class PriceZoneRestriction implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Double maxItemsMultiplier;
    private List<Integer> requiredPriceZones;
    private PriceZoneRestrictionType restrictionType;

    public Double getMaxItemsMultiplier() {
        return maxItemsMultiplier;
    }

    public void setMaxItemsMultiplier(Double maxItemsMultiplier) {
        this.maxItemsMultiplier = maxItemsMultiplier;
    }

    public List<Integer> getRequiredPriceZones() {
        return requiredPriceZones;
    }

    public void setRequiredPriceZones(List<Integer> requiredPriceZones) {
        this.requiredPriceZones = requiredPriceZones;
    }

    public PriceZoneRestrictionType getRestrictionType() {
        return restrictionType;
    }

    public void setRestrictionType(PriceZoneRestrictionType restrictionType) {
        this.restrictionType = restrictionType;
    }
}
