package es.onebox.mgmt.events.converter;

import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.mgmt.datasources.ms.event.dto.customertypes.CustomerTypeAssignationMode;
import es.onebox.mgmt.datasources.ms.event.dto.customertypes.EventCustomerType;
import es.onebox.mgmt.datasources.ms.event.dto.customertypes.UpdateEventCustomerType;
import es.onebox.mgmt.datasources.ms.event.dto.customertypes.UpdateEventCustomerTypes;
import es.onebox.mgmt.events.dto.EventCustomerTypeDTO;
import es.onebox.mgmt.events.dto.UpdateEventCustomerTypeDTO;
import es.onebox.mgmt.events.dto.UpdateEventCustomerTypesDTO;
import es.onebox.mgmt.events.enums.CustomerTypeAssignationModeDTO;

import java.util.ArrayList;
import java.util.List;

public class EventCustomerTypesConverter {

    private EventCustomerTypesConverter() {
    }

    public static UpdateEventCustomerTypes toMs(UpdateEventCustomerTypesDTO eventCustomerTypesDTO) {
        UpdateEventCustomerTypes eventCustomerTypes = new UpdateEventCustomerTypes();
        eventCustomerTypesDTO.forEach(eventCustomerTypeDTO -> eventCustomerTypes.add(toMs(eventCustomerTypeDTO)));
        return eventCustomerTypes;
    }

    public static UpdateEventCustomerType toMs(UpdateEventCustomerTypeDTO eventCustomerTypeDTO) {
        UpdateEventCustomerType eventCustomerType = new UpdateEventCustomerType();
        eventCustomerType.setCustomerTypeId(eventCustomerTypeDTO.getCustomerTypeId());
        eventCustomerType.setMode(CustomerTypeAssignationMode.valueOf(eventCustomerTypeDTO.getMode().name()));
        return eventCustomerType;
    }

    public static List<EventCustomerTypeDTO> toDTO(List<EventCustomerType> eventCustomerTypes) {
        List<EventCustomerTypeDTO> eventCustomerTypesDTO = new ArrayList<>();
        eventCustomerTypes.forEach(eventCustomerType -> eventCustomerTypesDTO.add(toDTO(eventCustomerType)));
        return eventCustomerTypesDTO;
    }

    public static EventCustomerTypeDTO toDTO(EventCustomerType eventCustomerTypes) {
        EventCustomerTypeDTO eventCustomerTypeDTO = new EventCustomerTypeDTO();
        eventCustomerTypeDTO.setCustomerType(new IdNameCodeDTO(
                eventCustomerTypes.getCustomerTypeId().longValue(), eventCustomerTypes.getName(), eventCustomerTypes.getCode()));
        eventCustomerTypeDTO.setMode(CustomerTypeAssignationModeDTO.valueOf(eventCustomerTypes.getMode().name()));
        return eventCustomerTypeDTO;
    }

}
