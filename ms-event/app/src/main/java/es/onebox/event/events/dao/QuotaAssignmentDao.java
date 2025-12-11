package es.onebox.event.events.dao;

import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelAsignacionGruposVentaRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class QuotaAssignmentDao extends DaoImpl<CpanelAsignacionGruposVentaRecord, Integer> {

    protected QuotaAssignmentDao() {
        super(Tables.CPANEL_ASIGNACION_GRUPOS_VENTA);
    }

    public List<Integer> getQuotaIdsByChannelEventIdAndVenueTemplateId(Integer channelEventId, Integer venueTemplateId) {
        return dsl.selectDistinct(Tables.CPANEL_ASIGNACION_GRUPOS_VENTA.IDCUPO)
                .from(Tables.CPANEL_ASIGNACION_GRUPOS_VENTA)
                .innerJoin(Tables.CPANEL_CUPOS_CONFIG).on(Tables.CPANEL_CUPOS_CONFIG.IDCUPO.eq(Tables.CPANEL_ASIGNACION_GRUPOS_VENTA.IDCUPO))
                .where(Tables.CPANEL_ASIGNACION_GRUPOS_VENTA.IDCANALEVENTO.eq(channelEventId)
                        .and(Tables.CPANEL_CUPOS_CONFIG.IDCONFIGURACION.eq(venueTemplateId)))
                .fetchInto(Integer.class);
    }

}
