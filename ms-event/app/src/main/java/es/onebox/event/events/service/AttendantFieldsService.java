package es.onebox.event.events.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.core.serializer.dto.response.MetadataBuilder;
import es.onebox.event.events.converter.AttendantFieldConverter;
import es.onebox.event.events.converter.FieldConverter;
import es.onebox.event.events.dao.AttendantFieldDao;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dao.FieldDao;
import es.onebox.event.events.dto.AttendantFieldsDTO;
import es.onebox.event.events.dto.FieldsDTO;
import es.onebox.event.events.dto.UpdateAttendantFieldDTO;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.cpanel.tables.records.CpanelEventFieldRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AttendantFieldsService {

    private final EventDao eventDao;
    private final AttendantFieldDao attendantFieldDao;
    private final FieldDao fieldDao;
    public static final int ATTENDANT_FIELD_NAME = 1;
    public static final int ATTENDANT_FIELD_SURNAME = 2;
    public static final int ATTENDANT_FIELD_ID = 3;

    @Autowired
    public AttendantFieldsService(final AttendantFieldDao attendantFieldDao, final EventDao eventDao, final FieldDao fieldDao){
        this.attendantFieldDao = attendantFieldDao;
        this.eventDao = eventDao;
        this.fieldDao = fieldDao;
    }

    @MySQLRead
    public FieldsDTO getAvailableFields(){
        FieldsDTO response = new FieldsDTO();
        Metadata metadata = new Metadata();
        metadata.setOffset(0L);
        metadata.setLimit(1000L);
        metadata.setTotal(fieldDao.count());
        response.setMetadata(metadata);
        response.setData(FieldConverter.convert(fieldDao.getFields()));
        return response;
    }


    @MySQLRead
    public AttendantFieldsDTO getAttendantFields(Integer eventId, BaseRequestFilter filter) {
        checkEvent(eventId);
        AttendantFieldsDTO response = new AttendantFieldsDTO();
        response.setMetadata(
            MetadataBuilder.build(filter, attendantFieldDao.countByEventId(eventId)));
        response.setData(AttendantFieldConverter.convert(
            attendantFieldDao.getAttendantFields(eventId, filter.getLimit(), filter.getOffset())));
        return response;
    }

    public void createAttendantFields(final Integer eventId, final Set<UpdateAttendantFieldDTO> fields) {
        checkEvent(eventId);
        CpanelEventFieldRecord record;
        attendantFieldDao.deleteAttendantFields(eventId);

        Set<CpanelEventFieldRecord> cpanelFields = AttendantFieldConverter.convert(eventId, fields);
        attendantFieldDao.createAttendantFields(eventId, cpanelFields);
    }

    public void createAvetDefaultAttendantFields(final Integer eventId){
        Set<UpdateAttendantFieldDTO> fields = new HashSet<>();

        UpdateAttendantFieldDTO nameField = buildDefaultAttendantField(ATTENDANT_FIELD_NAME, 0);
        fields.add(nameField);

        UpdateAttendantFieldDTO surnameField = buildDefaultAttendantField(ATTENDANT_FIELD_SURNAME, 1);
        fields.add(surnameField);

        UpdateAttendantFieldDTO idField = buildDefaultAttendantField(ATTENDANT_FIELD_ID, 2);
        fields.add(idField);

        this.createAttendantFields(eventId, fields);
    }

    private UpdateAttendantFieldDTO buildDefaultAttendantField(Integer fieldId, Integer order) {
        UpdateAttendantFieldDTO defaultField = new UpdateAttendantFieldDTO();
        defaultField.setOrder(order.byteValue());
        defaultField.setFieldId(fieldId);
        defaultField.setMandatory(Boolean.TRUE);
        defaultField.setMaxLength(250);
        defaultField.setMinLength(0);

        return defaultField;
    }

    private CpanelEventoRecord checkEvent(Integer eventId) {
        try {
            return eventDao.getById(eventId);
        } catch (EntityNotFoundException ex) {
            throw OneboxRestException.builder(MsEventErrorCode.EVENT_NOT_FOUND).
                    setMessage("Event: " + eventId + " not found").build();
        }
    }
}
