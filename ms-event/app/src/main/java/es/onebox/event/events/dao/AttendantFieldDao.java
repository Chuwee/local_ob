package es.onebox.event.events.dao;

import es.onebox.event.attendants.enums.FieldGroup;
import es.onebox.event.events.dao.record.AttendantFieldRecord;
import es.onebox.event.events.dao.record.AttendantFieldValidatorRecord;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.CpanelEventFieldValidator;
import es.onebox.jooq.cpanel.tables.CpanelTipoValidacion;
import es.onebox.jooq.cpanel.tables.records.CpanelEventFieldRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.jooq.Field;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static es.onebox.jooq.cpanel.tables.CpanelEventField.CPANEL_EVENT_FIELD;
import static es.onebox.jooq.cpanel.tables.CpanelField.CPANEL_FIELD;

@Repository
public class AttendantFieldDao extends DaoImpl<CpanelEventFieldRecord, Integer> {

    private static final CpanelEventFieldValidator eventFieldValidator = Tables.CPANEL_EVENT_FIELD_VALIDATOR.as("eventFieldValidator");
    private static final CpanelTipoValidacion validationType = Tables.CPANEL_TIPO_VALIDACION.as("validationType");

    protected AttendantFieldDao() {
        super(Tables.CPANEL_EVENT_FIELD);
    }

    private static final Field[] FIELDS = new Field[] {
            CPANEL_EVENT_FIELD.FIELDID,
            CPANEL_EVENT_FIELD.EVENTFIELDID,
            CPANEL_FIELD.SID,
            CPANEL_FIELD.FIELDTYPE,
            CPANEL_EVENT_FIELD.MINLENGTH,
            CPANEL_EVENT_FIELD.MAXLENGTH,
            CPANEL_EVENT_FIELD.MANDATORY,
            CPANEL_EVENT_FIELD.FIELDORDER
    };

    public Long countByEventId(Integer eventId) {
        return dsl.selectCount()
                .from(CPANEL_EVENT_FIELD)
                .innerJoin(CPANEL_FIELD).on(CPANEL_FIELD.ID.eq(CPANEL_EVENT_FIELD.FIELDID))
                .where(CPANEL_EVENT_FIELD.EVENTID.eq(eventId))
                .fetchOne(0, Long.class);
    }

    public List<AttendantFieldRecord> getAttendantFields(Integer eventId, Long limit, Long offset) {
        return dsl.select(FIELDS)
                .from(CPANEL_EVENT_FIELD)
                .innerJoin(CPANEL_FIELD).on(CPANEL_FIELD.ID.eq(CPANEL_EVENT_FIELD.FIELDID))
                .where(CPANEL_EVENT_FIELD.EVENTID.eq(eventId))
                .and(CPANEL_FIELD.FIELDGROUP.like(FieldGroup.EVENT_ATTENDANT.name()))
                .limit(limit.intValue())
                .offset(offset.intValue())
                .fetch()
                .map(this::buildRecord);
    }

    public void deleteAttendantFields(Integer eventId) {
        dsl.deleteFrom(Tables.CPANEL_EVENT_FIELD)
                .where(CPANEL_EVENT_FIELD.EVENTID.eq(eventId))
                .execute();
    }

    public void createAttendantFields(final Integer eventId, final Set<CpanelEventFieldRecord> fields) {
        for(CpanelEventFieldRecord field : fields) {
            dsl.insertInto(Tables.CPANEL_EVENT_FIELD)
                    .set(CPANEL_EVENT_FIELD.EVENTID, eventId)
                    .set(CPANEL_EVENT_FIELD.FIELDID, field.getFieldid())
                    .set(CPANEL_EVENT_FIELD.MINLENGTH, field.getMinlength())
                    .set(CPANEL_EVENT_FIELD.MAXLENGTH, field.getMaxlength())
                    .set(CPANEL_EVENT_FIELD.FIELDORDER, field.getFieldorder())
                    .set(CPANEL_EVENT_FIELD.MANDATORY, field.getMandatory())
                    .execute();
        }
    }

    public List<CpanelEventFieldRecord> getEventFieldByEventAndFieldGroup(Integer eventId, FieldGroup fieldGroup) {
        return dsl.select(FIELDS)
                .from(CPANEL_EVENT_FIELD)
                .innerJoin(CPANEL_FIELD).on(CPANEL_FIELD.ID.eq(CPANEL_EVENT_FIELD.FIELDID))
                .where(CPANEL_EVENT_FIELD.EVENTID.eq(eventId))
                .and(CPANEL_FIELD.FIELDGROUP.like(fieldGroup.name()))
                .fetch()
                .map(this::buildRecord);
    }

    public List<AttendantFieldRecord> getEventFieldsByEventId(Long eventId) {
        return dsl.select(FIELDS)
                .from(CPANEL_EVENT_FIELD)
                .innerJoin(CPANEL_FIELD).on(CPANEL_FIELD.ID.eq(CPANEL_EVENT_FIELD.FIELDID))
                .where(CPANEL_EVENT_FIELD.EVENTID.eq(eventId.intValue()))
                .and(CPANEL_FIELD.FIELDGROUP.like(FieldGroup.EVENT_ATTENDANT.name()))
                .fetch()
                .map(this::buildRecord);
    }

    private AttendantFieldRecord buildRecord(Record record) {
        AttendantFieldRecord eventFieldRecord = new AttendantFieldRecord();
        eventFieldRecord.setSid(record.get(CPANEL_FIELD.SID));
        eventFieldRecord.setFieldType(record.get(CPANEL_FIELD.FIELDTYPE));
        eventFieldRecord.setFieldid(record.get(CPANEL_EVENT_FIELD.FIELDID));
        eventFieldRecord.setMinlength(record.get(CPANEL_EVENT_FIELD.MINLENGTH));
        eventFieldRecord.setMaxlength(record.get(CPANEL_EVENT_FIELD.MAXLENGTH));
        eventFieldRecord.setMandatory(record.get(CPANEL_EVENT_FIELD.MANDATORY));
        eventFieldRecord.setEventfieldid(record.get(CPANEL_EVENT_FIELD.EVENTFIELDID));
        eventFieldRecord.setFieldorder(record.get(CPANEL_EVENT_FIELD.FIELDORDER));
        return eventFieldRecord;
    }


    public List<AttendantFieldValidatorRecord> getEventFieldsValidators(List<Integer> eventFieldIds) {
        return dsl.select(
                        eventFieldValidator.EVENTFIELDID,
                        validationType.TIPOVALIDACION,
                        validationType.REGEXPTEXT,
                        validationType.JAVACLASS)
                .from(eventFieldValidator)
                .join(CPANEL_EVENT_FIELD).on(CPANEL_EVENT_FIELD.EVENTFIELDID.eq(eventFieldValidator.EVENTFIELDID))
                .join(validationType).on(validationType.IDTIPOVALIDACION.eq(eventFieldValidator.VALIDATORID))
                .where(CPANEL_EVENT_FIELD.EVENTFIELDID.in(eventFieldIds))
                .fetch().map(r -> new AttendantFieldValidatorRecord(
                        r.get(eventFieldValidator.EVENTFIELDID),
                        r.get(validationType.TIPOVALIDACION),
                        r.get(validationType.REGEXPTEXT),
                        r.get(validationType.JAVACLASS)));

    }
}
