package es.onebox.event.catalog.elasticsearch.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class RatePriceZonesRestriction implements Serializable {

    @Serial
    private static final long serialVersionUID = 92647312189981338L;

    private List<Integer> restrictedPriceZones;
    private Boolean applyToB2b;

    public List<Integer> getRestrictedPriceZones() {
        return restrictedPriceZones;
    }

    public void setRestrictedPriceZones(List<Integer> restrictedPriceZones) {
        this.restrictedPriceZones = restrictedPriceZones;
    }

    public Boolean getApplyToB2b() {
        return applyToB2b;
    }

    public void setApplyToB2b(Boolean applyToB2b) {
        this.applyToB2b = applyToB2b;
    }
}
