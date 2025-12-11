package es.onebox.mgmt.events.eventchannel.b2b.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class PublishedSeatPriceTypeRequestDTO implements Serializable {

    public Boolean enabled;

    @JsonProperty("price_types_relations")
    public List<SeatPriceTypesRelationsRequestDTO> seatPriceTypes;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<SeatPriceTypesRelationsRequestDTO> getSeatPriceTypes() {
        return seatPriceTypes;
    }

    public void setSeatPriceTypes(List<SeatPriceTypesRelationsRequestDTO> seatPriceTypes) {
        this.seatPriceTypes = seatPriceTypes;
    }
}
