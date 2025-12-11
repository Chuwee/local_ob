package es.onebox.event.catalog.elasticsearch.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class RatePeriodRestriction implements Serializable {

    @Serial
    private static final long serialVersionUID = 92647312189981338L;

    private List<String> restrictedPeriods;

    public List<String> getRestrictedPeriods() {
        return restrictedPeriods;
    }

    public void setRestrictedPeriods(List<String> restrictedPeriods) {
        this.restrictedPeriods = restrictedPeriods;
    }
}
