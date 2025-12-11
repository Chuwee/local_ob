package es.onebox.event.events.domain;

import java.io.Serializable;
import java.util.List;

public class PublishedSeatPriceType implements Serializable {

    private Boolean enabled;
    private List<SeatPriceTypesRelations> priceTypesRelations;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<SeatPriceTypesRelations> getPriceTypesRelations() {
        return priceTypesRelations;
    }

    public void setPriceTypesRelations(List<SeatPriceTypesRelations> priceTypesRelations) {
        this.priceTypesRelations = priceTypesRelations;
    }
}
