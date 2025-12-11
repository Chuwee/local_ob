package es.onebox.mgmt.events.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.io.Serializable;

public class UpdateCapacityExternalDTO extends IdNameDTO implements Serializable {

    private Byte seasonId;

    public Byte getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(Byte seasonId) {
        this.seasonId = seasonId;
    }

}
