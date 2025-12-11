package es.onebox.mgmt.events.eventchannel.b2b.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PublishedSeatPriceTypeDTO {

    private Boolean enabled;

    @JsonProperty("price_types_relations")
    private List<SeatPriceTypesRelationsDTO> seatPriceTypesRelations;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<SeatPriceTypesRelationsDTO> getSeatPriceTypesRelations() {
        return seatPriceTypesRelations;
    }

    public void setSeatPriceTypesRelations(List<SeatPriceTypesRelationsDTO> seatPriceTypesRelations) {
        this.seatPriceTypesRelations = seatPriceTypesRelations;
    }
}
