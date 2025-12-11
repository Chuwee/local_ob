package es.onebox.event.events.customertypes.converter;

import es.onebox.event.events.customertypes.dao.EventCustomerTypeRecord;
import es.onebox.event.events.customertypes.dto.EventCustomerTypeDTO;
import es.onebox.event.events.customertypes.dto.UpdateEventCustomerTypeDTO;
import es.onebox.event.events.customertypes.enums.CustomerTypeAssignationMode;
import es.onebox.jooq.cpanel.enums.CpanelEventoCustomerTypeAssignationmode;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoCustomerTypeRecord;

import java.util.Objects;

public class EventCustomerTypeConverter {

    private EventCustomerTypeConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static EventCustomerTypeDTO fromRecord(EventCustomerTypeRecord record) {
        if (Objects.isNull(record)){
            return null;
        }
        EventCustomerTypeDTO eventCustomerTypeDTO = new EventCustomerTypeDTO();
        eventCustomerTypeDTO.setEventId(record.getEventid());
        eventCustomerTypeDTO.setCustomerTypeId(record.getCustomertypeid());
        eventCustomerTypeDTO.setMode(CustomerTypeAssignationMode.valueOf(record.getAssignationmode().toString()));
        eventCustomerTypeDTO.setCode(record.getCode());
        eventCustomerTypeDTO.setName(record.getName());
        return eventCustomerTypeDTO;
    }

    public static CpanelEventoCustomerTypeRecord fromDTO(Integer eventId, UpdateEventCustomerTypeDTO dto) {
        if (Objects.isNull(dto)){
            return null;
        }
        CpanelEventoCustomerTypeRecord record = new CpanelEventoCustomerTypeRecord();
        record.setEventid(eventId);
        record.setCustomertypeid(dto.getCustomerTypeId());
        if (Objects.nonNull(dto.getMode())) {
            record.setAssignationmode(CpanelEventoCustomerTypeAssignationmode.valueOf(dto.getMode().toString()));
        }
        return record;
    }

}
