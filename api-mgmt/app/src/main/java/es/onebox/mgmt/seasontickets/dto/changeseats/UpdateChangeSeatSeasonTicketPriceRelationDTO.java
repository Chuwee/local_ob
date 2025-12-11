package es.onebox.mgmt.seasontickets.dto.changeseats;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class UpdateChangeSeatSeasonTicketPriceRelationDTO implements Serializable {

    private static final long serialVersionUID = -1990625723418990274L;

    @JsonProperty("relation_id")
    private Long relationId;

    private Double value;

    public Long getRelationId() {
        return relationId;
    }

    public void setRelationId(Long relationId) {
        this.relationId = relationId;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}