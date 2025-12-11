package es.onebox.event.events.customertypes.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.events.customertypes.converter.EventCustomerTypeConverter;
import es.onebox.event.events.customertypes.dao.AssignationTrigger;
import es.onebox.event.events.customertypes.dao.CustomerTypeWithTriggerRecord;
import es.onebox.event.events.customertypes.dao.EventCustomerTypeRecord;
import es.onebox.event.events.customertypes.dao.EventCustomerTypesDao;
import es.onebox.event.events.customertypes.dto.EventCustomerTypeDTO;
import es.onebox.event.events.customertypes.dto.UpdateEventCustomerTypeDTO;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelCustomTypeRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoCustomerTypeRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventCustomerTypesService {

    private final EventCustomerTypesDao eventCustomerTypesDao;

    @Autowired
    public EventCustomerTypesService(EventCustomerTypesDao eventCustomerTypesDao) {
        this.eventCustomerTypesDao = eventCustomerTypesDao;
    }

    public List<EventCustomerTypeDTO> getEventCustomerTypes(Integer eventId) {
        List<EventCustomerTypeRecord> eventCustomerTypeRecords = eventCustomerTypesDao.getEventCustomerTypes(eventId);
        return eventCustomerTypeRecords.stream().map(EventCustomerTypeConverter::fromRecord).collect(Collectors.toList());
    }

    @MySQLWrite
    public void putEventCustomerTypes(Integer eventId, List<UpdateEventCustomerTypeDTO> eventCustomerTypes) {

        eventCustomerTypesDao.deleteCustomerTypesByEvent(eventId);
        if (CollectionUtils.isEmpty(eventCustomerTypes)) {
            return;
        }
        List<CustomerTypeWithTriggerRecord> customerTypes = eventCustomerTypesDao.getEntityCustomerTypesWithTrigger(eventId, AssignationTrigger.PURCHASE);
        List<CpanelEventoCustomerTypeRecord> records = eventCustomerTypes.stream()
                .map(eventCustomerTypeDTO -> EventCustomerTypeConverter.fromDTO(eventId, eventCustomerTypeDTO))
                .collect(Collectors.toList());
        validateCustomerTypes(customerTypes, records);
        records.forEach(eventCustomerTypesDao::upsert);
    }

    private void validateCustomerTypes( List<CustomerTypeWithTriggerRecord> customerTypes,
                                       List<CpanelEventoCustomerTypeRecord> records) {
        List<Integer> customerTypeIds = customerTypes.stream()
                .map(CpanelCustomTypeRecord::getId).toList();
        if (records.stream().anyMatch(cpanelEventoCustomerTypeRecord ->
                !customerTypeIds.contains(cpanelEventoCustomerTypeRecord.getCustomertypeid()))) {
            throw new OneboxRestException(MsEventErrorCode.CUSTOMER_TYPE_NOT_FOUND_IN_EVENT);
        }
    }
}
