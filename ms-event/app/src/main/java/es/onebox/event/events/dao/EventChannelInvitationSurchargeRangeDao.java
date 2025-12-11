package es.onebox.event.events.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecargoEventoCanalInvRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import static es.onebox.jooq.cpanel.tables.CpanelRangoRecargoEventoCanalInv.CPANEL_RANGO_RECARGO_EVENTO_CANAL_INV;

@Repository
public class EventChannelInvitationSurchargeRangeDao extends DaoImpl<CpanelRangoRecargoEventoCanalInvRecord, CpanelRangoRecargoEventoCanalInvRecord> {

    protected EventChannelInvitationSurchargeRangeDao() {
        super(CPANEL_RANGO_RECARGO_EVENTO_CANAL_INV);
    }

    public void deleteByEventChannelId(Integer eventChannelId) {
        dsl.delete(CPANEL_RANGO_RECARGO_EVENTO_CANAL_INV)
                .where(CPANEL_RANGO_RECARGO_EVENTO_CANAL_INV.IDEVENTOCANAL.eq(eventChannelId))
                .execute();
    }
}
