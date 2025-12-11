package es.onebox.event.events.dao;

import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("appChannelEventDao")
public class ChannelEventDao extends DaoImpl<CpanelCanalEventoRecord, Integer> {

    protected ChannelEventDao() {
        super(Tables.CPANEL_CANAL_EVENTO);
    }

    public List<CpanelCanalEventoRecord> findByChannelIdEventIds(Integer channelId, List<Integer> eventId) {
        return dsl.selectFrom(Tables.CPANEL_CANAL_EVENTO)
                .where(Tables.CPANEL_CANAL_EVENTO.IDCANAL.eq(channelId))
                .and((Tables.CPANEL_CANAL_EVENTO.IDEVENTO.in(eventId)))
                .fetch().into(CpanelCanalEventoRecord.class);
    }
}
