package es.onebox.mgmt.seasontickets.converter;

import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.mgmt.datasources.ms.event.dto.customertypes.CustomerTypeAssignationMode;
import es.onebox.mgmt.datasources.ms.event.dto.customertypes.EventCustomerType;
import es.onebox.mgmt.datasources.ms.event.dto.customertypes.UpdateEventCustomerType;
import es.onebox.mgmt.datasources.ms.event.dto.customertypes.UpdateEventCustomerTypes;
import es.onebox.mgmt.events.enums.CustomerTypeAssignationModeDTO;
import es.onebox.mgmt.seasontickets.dto.customertypes.SeasonTicketCustomerTypeDTO;
import es.onebox.mgmt.seasontickets.dto.customertypes.UpdateSeasonTicketCustomerTypeDTO;
import es.onebox.mgmt.seasontickets.dto.customertypes.UpdateSeasonTicketCustomerTypesDTO;

import java.util.ArrayList;
import java.util.List;

public class SeasonTicketCustomerTypesConverter {

    private SeasonTicketCustomerTypesConverter() {
    }

    public static UpdateEventCustomerTypes toMs(UpdateSeasonTicketCustomerTypesDTO eventCustomerTypesDTO) {
        UpdateEventCustomerTypes eventCustomerTypes = new UpdateEventCustomerTypes();
        eventCustomerTypesDTO.forEach(eventCustomerTypeDTO -> eventCustomerTypes.add(toMs(eventCustomerTypeDTO)));
        return eventCustomerTypes;
    }

    public static UpdateEventCustomerType toMs(UpdateSeasonTicketCustomerTypeDTO eventCustomerTypeDTO) {
        UpdateEventCustomerType eventCustomerType = new UpdateEventCustomerType();
        eventCustomerType.setCustomerTypeId(eventCustomerTypeDTO.getCustomerTypeId());
        eventCustomerType.setMode(CustomerTypeAssignationMode.valueOf(eventCustomerTypeDTO.getMode().name()));
        return eventCustomerType;
    }

    public static List<SeasonTicketCustomerTypeDTO> toDTO(List<EventCustomerType> eventCustomerTypes) {
        List<SeasonTicketCustomerTypeDTO> eventCustomerTypesDTO = new ArrayList<>();
        eventCustomerTypes.forEach(eventCustomerType -> eventCustomerTypesDTO.add(toDTO(eventCustomerType)));
        return eventCustomerTypesDTO;
    }

    public static SeasonTicketCustomerTypeDTO toDTO(EventCustomerType eventCustomerTypes) {
        SeasonTicketCustomerTypeDTO eventCustomerTypeDTO = new SeasonTicketCustomerTypeDTO();
        IdNameCodeDTO customerTypeDTO = new IdNameCodeDTO();
        customerTypeDTO.setId(eventCustomerTypes.getCustomerTypeId().longValue());
        customerTypeDTO.setName(eventCustomerTypes.getName());
        customerTypeDTO.setCode(eventCustomerTypes.getCode());
        eventCustomerTypeDTO.setCustomerType(new IdNameCodeDTO(
                eventCustomerTypes.getCustomerTypeId().longValue(), eventCustomerTypes.getName(), eventCustomerTypes.getCode()));
        eventCustomerTypeDTO.setMode(CustomerTypeAssignationModeDTO.valueOf(eventCustomerTypes.getMode().name()));
        return eventCustomerTypeDTO;
    }

}
