package es.onebox.mgmt.events.dto;

import es.onebox.core.serializer.dto.common.IdCodeDTO;

import java.io.Serializable;

public class LoadedCapacityExternalDTO extends IdCodeDTO implements Serializable {

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
