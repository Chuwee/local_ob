package es.onebox.event.catalog.elasticsearch.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class PriceZoneRestriction implements Serializable {

    @Serial
    private static final long serialVersionUID = 253609915189981338L;

    private List<Integer> requiredPriceZones;
    private Long required;
    private Long locked;

    public List<Integer> getRequiredPriceZones() {
        return requiredPriceZones;
    }

    public void setRequiredPriceZones(List<Integer> requiredPriceZones) {
        this.requiredPriceZones = requiredPriceZones;
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
}
