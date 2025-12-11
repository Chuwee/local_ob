package es.onebox.event.attributes;

import static es.onebox.jooq.cpanel.Tables.CPANEL_ATRIBUTOS_EVENTO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_EVENTO;

import java.util.List;
import java.util.Map;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.springframework.stereotype.Repository;

import es.onebox.jooq.cpanel.tables.records.CpanelAtributosEventoRecord;
import es.onebox.jooq.dao.DaoImpl;

@Repository
public class EventAttributeDao extends DaoImpl<CpanelAtributosEventoRecord, Integer> {

    private static final Field<Integer> EVENT_ID = CPANEL_EVENTO.IDEVENTO.as("eventId");
    private static final Field<Integer> JOIN_ATTRIBUTE_ID = CPANEL_ATRIBUTOS_EVENTO.IDATRIBUTO.as("attributeId");
    private static final Field<Integer> JOIN_VALUE_ID = CPANEL_ATRIBUTOS_EVENTO.IDVALOR.as("valueId");
    protected EventAttributeDao() {
        super(CPANEL_ATRIBUTOS_EVENTO);
    }

    public List<CpanelAtributosEventoRecord> getEventAttributes(Integer eventId) {
        return dsl.select()
                .from(CPANEL_ATRIBUTOS_EVENTO)
                .where(CPANEL_ATRIBUTOS_EVENTO.IDEVENTO.eq(eventId))
                .fetch().into(CpanelAtributosEventoRecord.class);
    }

    public Map<Integer, List<CpanelAtributosEventoRecord>> getEventsAttributes(List<Long> eventsId) {
        return dsl.select(EVENT_ID, JOIN_ATTRIBUTE_ID, JOIN_VALUE_ID)
                .from(CPANEL_EVENTO)
                .leftJoin(CPANEL_ATRIBUTOS_EVENTO).on(CPANEL_EVENTO.IDEVENTO.eq(CPANEL_ATRIBUTOS_EVENTO.IDEVENTO))
                .where(CPANEL_EVENTO.IDEVENTO.in(eventsId))
                .fetchGroups(
                        r -> r.get(EVENT_ID),
                        (RecordMapper<Record, CpanelAtributosEventoRecord>) record -> {
                            CpanelAtributosEventoRecord eventAttributeDTO = new CpanelAtributosEventoRecord();
                            eventAttributeDTO.setIdatributo(record.getValue(JOIN_ATTRIBUTE_ID));
                            eventAttributeDTO.setIdvalor(record.getValue(JOIN_VALUE_ID));
                            return eventAttributeDTO;
                        });
    }

    public List<CpanelAtributosEventoRecord> getEventAttribute(Integer eventId, Integer attributeId) {
        return dsl.select()
                .from(CPANEL_ATRIBUTOS_EVENTO)
                .where(CPANEL_ATRIBUTOS_EVENTO.IDEVENTO.eq(eventId).and(CPANEL_ATRIBUTOS_EVENTO.IDATRIBUTO.eq(attributeId)))
                .fetch().into(CpanelAtributosEventoRecord.class);
    }

}
