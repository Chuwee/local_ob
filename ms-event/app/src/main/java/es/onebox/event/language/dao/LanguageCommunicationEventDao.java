package es.onebox.event.language.dao;

import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelIdiomaComEventoRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LanguageCommunicationEventDao extends DaoImpl<CpanelIdiomaComEventoRecord, Integer> {

    protected LanguageCommunicationEventDao() {
        super(Tables.CPANEL_IDIOMA_COM_EVENTO);
    }

    public List<CpanelIdiomaComEventoRecord> getLanguagesEventCommunication(int eventId) {
        return dsl.select()
                .from(Tables.CPANEL_IDIOMA_COM_EVENTO)
                .where(Tables.CPANEL_IDIOMA_COM_EVENTO.IDEVENTO.eq(eventId))
                .fetch().into(CpanelIdiomaComEventoRecord.class);
    }

}
