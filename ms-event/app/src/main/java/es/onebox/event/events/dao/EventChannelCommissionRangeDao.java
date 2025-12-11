package es.onebox.event.events.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelRangoComservEventoCanalRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import static es.onebox.jooq.cpanel.tables.CpanelRangoComservEventoCanal.CPANEL_RANGO_COMSERV_EVENTO_CANAL;

@Repository
public class EventChannelCommissionRangeDao extends DaoImpl<CpanelRangoComservEventoCanalRecord, CpanelRangoComservEventoCanalRecord> {

    protected EventChannelCommissionRangeDao() {
        super(CPANEL_RANGO_COMSERV_EVENTO_CANAL);
    }

    public void deleteByEventChannelId(Integer eventChannelId) {
        dsl.delete(CPANEL_RANGO_COMSERV_EVENTO_CANAL)
                .where(CPANEL_RANGO_COMSERV_EVENTO_CANAL.IDEVENTOCANAL.eq(eventChannelId))
                .execute();
    }
}
