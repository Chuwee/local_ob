package es.onebox.event.events.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class PublishedSeatPriceTypeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private Boolean enabled;
    private List<SeatPriceTypesRelationsDTO> priceTypesRelations;

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<SeatPriceTypesRelationsDTO> getPriceTypesRelations() {
        return priceTypesRelations;
    }

    public void setPriceTypesRelations(List<SeatPriceTypesRelationsDTO> priceTypesRelations) {
        this.priceTypesRelations = priceTypesRelations;
    }
}