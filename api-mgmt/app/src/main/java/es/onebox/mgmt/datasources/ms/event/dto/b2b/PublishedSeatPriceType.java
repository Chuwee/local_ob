package es.onebox.mgmt.datasources.ms.event.dto.b2b;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class PublishedSeatPriceType implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean enabled;
    private List<SeatPriceTypesRelations> priceTypesRelations;

    public Boolean isEnabled() {
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
