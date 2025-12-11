package es.onebox.event.events.dao;

import es.onebox.event.events.dao.record.EventLanguageRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelIdiomaComEventoRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static es.onebox.jooq.cpanel.Tables.CPANEL_IDIOMA;
import static es.onebox.jooq.cpanel.Tables.CPANEL_IDIOMA_COM_EVENTO;

@Repository
public class EventLanguageDao extends DaoImpl<CpanelIdiomaComEventoRecord, Integer> {

    public EventLanguageDao() {
        super(CPANEL_IDIOMA_COM_EVENTO);
    }

    public List<EventLanguageRecord> findByEventId(Long eventId) {
        return dsl
                .select(CPANEL_IDIOMA_COM_EVENTO.IDIDIOMA.as("id"),
                        CPANEL_IDIOMA_COM_EVENTO.DEFECTO.as("isDefault"),
                        CPANEL_IDIOMA.CODIGO.as("code"))
                .from(CPANEL_IDIOMA_COM_EVENTO)
                .leftJoin(CPANEL_IDIOMA).on(CPANEL_IDIOMA_COM_EVENTO.IDIDIOMA.eq(CPANEL_IDIOMA.IDIDIOMA))
                .where(CPANEL_IDIOMA_COM_EVENTO.IDEVENTO.eq(eventId.intValue()))
                .fetchInto(EventLanguageRecord.class);
    }

    public List<EventLanguageRecord> findByEventIds(List<Long> eventIds) {
        return dsl
                .select(CPANEL_IDIOMA_COM_EVENTO.IDIDIOMA.as("id"),
                        CPANEL_IDIOMA_COM_EVENTO.DEFECTO.as("isDefault"),
                        CPANEL_IDIOMA.CODIGO.as("code"),
                        CPANEL_IDIOMA_COM_EVENTO.IDEVENTO.as("eventId"))
                .from(CPANEL_IDIOMA_COM_EVENTO)
                .leftJoin(CPANEL_IDIOMA).on(CPANEL_IDIOMA_COM_EVENTO.IDIDIOMA.eq(CPANEL_IDIOMA.IDIDIOMA))
                .where(CPANEL_IDIOMA_COM_EVENTO.IDEVENTO.in(eventIds.stream().map(Long::intValue).collect(Collectors.toList())))
                .fetchInto(EventLanguageRecord.class);
    }

    public void deleteByEvent(Long eventId) {
        dsl.delete(CPANEL_IDIOMA_COM_EVENTO).
                where(CPANEL_IDIOMA_COM_EVENTO.IDEVENTO.eq(eventId.intValue())).
                execute();
    }
}
