package es.onebox.event.surcharges.dao;

import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecargoEventoMercadoSecundarioRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_RANGO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_RANGO_RECARGO_EVENTO_MERCADO_SECUNDARIO;

@Repository
public class RangeSurchargeEventSecondaryMarketDao extends DaoImpl<CpanelRangoRecargoEventoMercadoSecundarioRecord, Integer> {
    protected RangeSurchargeEventSecondaryMarketDao() {
        super(CPANEL_RANGO_RECARGO_EVENTO_MERCADO_SECUNDARIO);
    }

    public List<CpanelRangoRecord> getByEventId(int eventId) {
        return dsl.select(CPANEL_RANGO.fields())
                .from(CPANEL_RANGO)
                .innerJoin(CPANEL_RANGO_RECARGO_EVENTO_MERCADO_SECUNDARIO)
                .on(CPANEL_RANGO.IDRANGO.eq(Tables.CPANEL_RANGO_RECARGO_EVENTO_MERCADO_SECUNDARIO.IDRANGO))
                .where(CPANEL_RANGO_RECARGO_EVENTO_MERCADO_SECUNDARIO.IDEVENTO.eq(eventId))
                .orderBy(CPANEL_RANGO.RANGOMINIMO)
                .fetchInto(CpanelRangoRecord.class);
    }

    public int deleteByEventId(int eventId) {
        return this.dsl.deleteFrom(CPANEL_RANGO_RECARGO_EVENTO_MERCADO_SECUNDARIO)
                .where(CPANEL_RANGO_RECARGO_EVENTO_MERCADO_SECUNDARIO.IDEVENTO.eq(eventId))
                .execute();
    }
}
