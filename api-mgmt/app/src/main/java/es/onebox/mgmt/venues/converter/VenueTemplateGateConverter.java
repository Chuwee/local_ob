package es.onebox.mgmt.venues.converter;

import es.onebox.mgmt.datasources.ms.venue.dto.template.Gate;
import es.onebox.mgmt.venues.dto.GateDTO;

public class VenueTemplateGateConverter {

    private VenueTemplateGateConverter() {
    }

    public static GateDTO fromMsEvent(Gate gate) {
        if (gate == null) {
            return null;
        }

        GateDTO gateDTO = new GateDTO();
        gateDTO.setId(gate.getId());
        gateDTO.setName(gate.getName());
        gateDTO.setCode(gate.getCode());
        gateDTO.setColor(gate.getColor());
        gateDTO.setDefault(gate.getDefault());

        return gateDTO;
    }
}
