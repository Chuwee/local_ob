package es.onebox.event.catalog.elasticsearch.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.common.enums.RatePriceZoneCriteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class RateRelationsRestriction implements Serializable {

    @Serial
    private static final long serialVersionUID = 253609915189981338L;

    private List<IdNameDTO> requiredRates;
    private Long required;
    private Long locked;
    private List<Integer> restrictedPriceZones;
    private RatePriceZoneCriteria priceTypeCriteria;
    private Boolean applyToB2b;

    public List<IdNameDTO> getRequiredRates() {
        return requiredRates;
    }

    public void setRequiredRates(List<IdNameDTO> requiredRates) {
        this.requiredRates = requiredRates;
    }

    public Long getRequired() {
        return required;
    }

    public void setRequired(Long required) {
        this.required = required;
    }

    public Long getLocked() {
        return locked;
    }

    public void setLocked(Long locked) {
        this.locked = locked;
    }

    public List<Integer> getRestrictedPriceZones() {
        return restrictedPriceZones;
    }

    public void setRestrictedPriceZones(List<Integer> restrictedPriceZones) {
        this.restrictedPriceZones = restrictedPriceZones;
    }

    public RatePriceZoneCriteria getPriceTypeCriteria() {
        return priceTypeCriteria;
    }

    public void setPriceTypeCriteria(RatePriceZoneCriteria priceTypeCriteria) {
        this.priceTypeCriteria = priceTypeCriteria;
    }

    public Boolean getApplyToB2b() {
        return applyToB2b;
    }

    public void setApplyToB2b(Boolean applyToB2b) {
        this.applyToB2b = applyToB2b;
    }
}
