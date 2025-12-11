package es.onebox.event.events.customertypes.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelCustomTypeRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoCustomerTypeRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.jooq.Field;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_CUSTOM_TYPE;
import static es.onebox.jooq.cpanel.Tables.CPANEL_CUSTOM_TYPE_TRIGGER;
import static es.onebox.jooq.cpanel.Tables.CPANEL_EVENTO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_EVENTO_CUSTOMER_TYPE;

@Repository
public class EventCustomerTypesDao extends DaoImpl<CpanelEventoCustomerTypeRecord, Integer> {

    protected EventCustomerTypesDao() {
        super(CPANEL_EVENTO_CUSTOMER_TYPE);
    }

    private static final Field[] FIELDS = new Field[] {
            CPANEL_EVENTO_CUSTOMER_TYPE.EVENTID,
            CPANEL_EVENTO_CUSTOMER_TYPE.CUSTOMERTYPEID,
            CPANEL_EVENTO_CUSTOMER_TYPE.ASSIGNATIONMODE,
            CPANEL_CUSTOM_TYPE.NAME,
            CPANEL_CUSTOM_TYPE.CODE,
    };

    private static final Field[] FIELDS_CUSTOM_TYPES_WITH_TRIGGERS = new Field[] {
            CPANEL_CUSTOM_TYPE.NAME,
            CPANEL_CUSTOM_TYPE.CODE,
            CPANEL_CUSTOM_TYPE.ID,
            CPANEL_CUSTOM_TYPE.ASSIGNATIONTYPE,
            CPANEL_CUSTOM_TYPE_TRIGGER.ID_TRIGGER,
            CPANEL_CUSTOM_TYPE_TRIGGER.HANDLER
    };

    public List<EventCustomerTypeRecord> getEventCustomerTypes(Integer eventId) {
        return dsl.select(FIELDS)
                .from(CPANEL_EVENTO_CUSTOMER_TYPE)
                .innerJoin(CPANEL_CUSTOM_TYPE).on(CPANEL_CUSTOM_TYPE.ID.eq(CPANEL_EVENTO_CUSTOMER_TYPE.CUSTOMERTYPEID))
                .where(CPANEL_EVENTO_CUSTOMER_TYPE.EVENTID.eq(eventId))
                .fetch()
                .map(this::buildRecord);
    }

    public List<CpanelCustomTypeRecord> getCustomerTypesByEvent(Long eventId) {
        return dsl.select(CPANEL_CUSTOM_TYPE.fields())
                .from(CPANEL_CUSTOM_TYPE)
                .innerJoin(CPANEL_EVENTO).on(CPANEL_EVENTO.IDENTIDAD.eq(CPANEL_CUSTOM_TYPE.ENTITYID).and(CPANEL_EVENTO.IDEVENTO.eq(eventId.intValue())))
                .fetch()
                .into(CpanelCustomTypeRecord.class);
    }

    public List<CustomerTypeWithTriggerRecord> getEntityCustomerTypesWithTrigger(Integer eventId, AssignationTrigger trigger) {
        return dsl.select(FIELDS_CUSTOM_TYPES_WITH_TRIGGERS)
                .from(CPANEL_CUSTOM_TYPE)
                .innerJoin(CPANEL_EVENTO).on(CPANEL_EVENTO.IDENTIDAD.eq(CPANEL_CUSTOM_TYPE.ENTITYID).and(CPANEL_EVENTO.IDEVENTO.eq(eventId)))
                .innerJoin(CPANEL_CUSTOM_TYPE_TRIGGER).on(CPANEL_CUSTOM_TYPE_TRIGGER.ID_CUSTOM_TYPE.eq(CPANEL_CUSTOM_TYPE.ID))
                .where(CPANEL_CUSTOM_TYPE_TRIGGER.ID_TRIGGER.eq(trigger.getType()))
                .fetch()
                .map(this::buildCustomerTypeRecord);
    }


    public void deleteCustomerTypesByEvent(Integer eventId) {
        dsl.deleteFrom(CPANEL_EVENTO_CUSTOMER_TYPE)
                .where(CPANEL_EVENTO_CUSTOMER_TYPE.EVENTID.eq(eventId))
                .execute();
    }

    public void upsert(CpanelEventoCustomerTypeRecord record) {
        dsl.insertInto(CPANEL_EVENTO_CUSTOMER_TYPE, CPANEL_EVENTO_CUSTOMER_TYPE.EVENTID,
                        CPANEL_EVENTO_CUSTOMER_TYPE.CUSTOMERTYPEID, CPANEL_EVENTO_CUSTOMER_TYPE.ASSIGNATIONMODE)
                .values(record.getEventid(), record.getCustomertypeid(), record.getAssignationmode())
                .onDuplicateKeyUpdate()
                .set(CPANEL_EVENTO_CUSTOMER_TYPE.ASSIGNATIONMODE, record.getAssignationmode())
                .execute();
    }

    private CustomerTypeWithTriggerRecord buildCustomerTypeRecord(Record record) {
        CustomerTypeWithTriggerRecord customTypeAssignationTriggerRecord = new CustomerTypeWithTriggerRecord();
        customTypeAssignationTriggerRecord.setId(record.getValue(CPANEL_CUSTOM_TYPE.ID));
        customTypeAssignationTriggerRecord.setName(record.getValue(CPANEL_CUSTOM_TYPE.NAME));
        customTypeAssignationTriggerRecord.setCode(record.getValue(CPANEL_CUSTOM_TYPE.CODE));
        customTypeAssignationTriggerRecord.setAssignationtype(record.getValue(CPANEL_CUSTOM_TYPE.ASSIGNATIONTYPE));
        customTypeAssignationTriggerRecord.setTrigger(AssignationTrigger.fromValue(record.getValue(CPANEL_CUSTOM_TYPE_TRIGGER.ID_TRIGGER)));
        customTypeAssignationTriggerRecord.setHandler(record.getValue(CPANEL_CUSTOM_TYPE_TRIGGER.HANDLER));

        return customTypeAssignationTriggerRecord;
    }

    private EventCustomerTypeRecord buildRecord(Record record) {
        EventCustomerTypeRecord eventCustomerTypeRecord = new EventCustomerTypeRecord();
        eventCustomerTypeRecord.setEventid(record.get(CPANEL_EVENTO_CUSTOMER_TYPE.EVENTID));
        eventCustomerTypeRecord.setCustomertypeid(record.get(CPANEL_EVENTO_CUSTOMER_TYPE.CUSTOMERTYPEID));
        eventCustomerTypeRecord.setAssignationmode(record.get(CPANEL_EVENTO_CUSTOMER_TYPE.ASSIGNATIONMODE));
        eventCustomerTypeRecord.setCode(record.get(CPANEL_CUSTOM_TYPE.CODE));
        eventCustomerTypeRecord.setName(record.get(CPANEL_CUSTOM_TYPE.NAME));
        return eventCustomerTypeRecord;
    }
}
