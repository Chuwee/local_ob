package es.onebox.event.events.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecargoCanalEventoInvRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_RANGO;
import static es.onebox.jooq.cpanel.tables.CpanelRangoRecargoCanalEventoInv.CPANEL_RANGO_RECARGO_CANAL_EVENTO_INV;

@Repository
public class ChannelEventInvitationSurchargeRangeDao extends DaoImpl<CpanelRangoRecargoCanalEventoInvRecord, CpanelRangoRecargoCanalEventoInvRecord> {

    protected ChannelEventInvitationSurchargeRangeDao() {
        super(CPANEL_RANGO_RECARGO_CANAL_EVENTO_INV);
    }

    public void deleteByChannelEventId(Integer channelEventId) {
        dsl.delete(CPANEL_RANGO_RECARGO_CANAL_EVENTO_INV)
                .where(CPANEL_RANGO_RECARGO_CANAL_EVENTO_INV.IDCANALEVENTO.eq(channelEventId))
                .execute();
    }

    public List<CpanelRangoRecord> getByChannelEventId(Integer channelEventId) {
        return dsl.select(CPANEL_RANGO.fields())
                .from(CPANEL_RANGO)
                .innerJoin(CPANEL_RANGO_RECARGO_CANAL_EVENTO_INV).on(CPANEL_RANGO_RECARGO_CANAL_EVENTO_INV.IDRANGO.eq(CPANEL_RANGO.IDRANGO))
                .where(CPANEL_RANGO_RECARGO_CANAL_EVENTO_INV.IDCANALEVENTO.eq(channelEventId))
                .orderBy(CPANEL_RANGO.RANGOMINIMO)
                .fetch()
                .into(CpanelRangoRecord.class);
    }
}
