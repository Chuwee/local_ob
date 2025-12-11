package es.onebox.mgmt.entities.converter;

import es.onebox.mgmt.datasources.integration.avetconfig.dto.CapacityDTO;
import es.onebox.mgmt.events.dto.LoadedCapacityExternalDTO;

public class CapacityConverter {

    public CapacityConverter() {
    }

    public static LoadedCapacityExternalDTO toDTO(CapacityDTO capacity) {
        if (capacity == null) {
            return null;
        } else {
            LoadedCapacityExternalDTO loadedCapacityExternalDTO = new LoadedCapacityExternalDTO();
            loadedCapacityExternalDTO.setId(capacity.getId().longValue());
            loadedCapacityExternalDTO.setCode(String.format("%d.%d.%d", capacity.getClubCode(), capacity.getSeasonCode(), capacity.getCapacityCode()));
            loadedCapacityExternalDTO.setDescription(capacity.getDescription());
            return loadedCapacityExternalDTO;
        }
    }
}
