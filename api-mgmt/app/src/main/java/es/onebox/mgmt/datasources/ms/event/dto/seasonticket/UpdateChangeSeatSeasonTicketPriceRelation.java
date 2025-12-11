package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import java.io.Serializable;

public class UpdateChangeSeatSeasonTicketPriceRelation implements Serializable {

    private static final long serialVersionUID = -1990625723418990274L;

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