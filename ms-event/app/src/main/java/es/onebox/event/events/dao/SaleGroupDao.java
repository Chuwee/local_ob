package es.onebox.event.events.dao;

import es.onebox.event.events.dao.record.SaleGroupRecord;
import es.onebox.event.events.domain.VenueTemplateStatus;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelCuposConfigRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.jooq.Field;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_CONFIG_RECINTO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_CUPOS_CONFIG;
import static es.onebox.jooq.cpanel.tables.CpanelAsignacionGruposVenta.CPANEL_ASIGNACION_GRUPOS_VENTA;

@Repository
public class SaleGroupDao extends DaoImpl<CpanelCuposConfigRecord, Integer> {

    protected SaleGroupDao() {
        super(CPANEL_CUPOS_CONFIG);
    }

    private static final Field<?>[] FIELDS = {
            CPANEL_ASIGNACION_GRUPOS_VENTA.IDCANALEVENTO,
            CPANEL_CUPOS_CONFIG.IDCUPO,
            CPANEL_CUPOS_CONFIG.DESCRIPCION,
            CPANEL_CONFIG_RECINTO.NOMBRECONFIGURACION,
            CPANEL_CONFIG_RECINTO.IDCONFIGURACION
    };

    public List<CpanelCuposConfigRecord> getByEventId(Long eventId) {
        return dsl.select(CPANEL_CUPOS_CONFIG.fields())
                .from(CPANEL_CUPOS_CONFIG)
                .innerJoin(CPANEL_CONFIG_RECINTO).on(CPANEL_CONFIG_RECINTO.IDCONFIGURACION.eq(CPANEL_CUPOS_CONFIG.IDCONFIGURACION))
                .where(CPANEL_CONFIG_RECINTO.IDEVENTO.eq(eventId.intValue()))
                .fetch()
                .into(CpanelCuposConfigRecord.class);
    }

    public List<SaleGroupRecord> getByEventIdWithAssignmentsInfo(Integer eventId, Integer channelId) {
        return dsl.select(FIELDS)
                .from(Tables.CPANEL_CUPOS_CONFIG)
                .innerJoin(Tables.CPANEL_CONFIG_RECINTO).on(Tables.CPANEL_CUPOS_CONFIG.IDCONFIGURACION.eq(Tables.CPANEL_CONFIG_RECINTO.IDCONFIGURACION))
                .innerJoin(Tables.CPANEL_CANAL_EVENTO).on(Tables.CPANEL_CANAL_EVENTO.IDEVENTO.eq(Tables.CPANEL_CONFIG_RECINTO.IDEVENTO))
                .leftJoin(CPANEL_ASIGNACION_GRUPOS_VENTA).on(
                        Tables.CPANEL_ASIGNACION_GRUPOS_VENTA.IDCUPO.eq(CPANEL_CUPOS_CONFIG.IDCUPO)
                                .and(Tables.CPANEL_ASIGNACION_GRUPOS_VENTA.IDCANALEVENTO.eq(Tables.CPANEL_CANAL_EVENTO.IDCANALEEVENTO)))
                .where(Tables.CPANEL_CANAL_EVENTO.IDEVENTO.eq(eventId))
                .and(Tables.CPANEL_CANAL_EVENTO.IDCANAL.eq(channelId))
                .and(CPANEL_CONFIG_RECINTO.ESTADO.eq(VenueTemplateStatus.ACTIVE.getId()))
                .fetch()
                .map(this::buildSaleGroup);
    }

    private SaleGroupRecord buildSaleGroup(Record record) {
        SaleGroupRecord saleGroup = new SaleGroupRecord();
        Integer id = record.get(CPANEL_CUPOS_CONFIG.IDCUPO);
        if (id != null) {
            saleGroup.setId(id.longValue());
        }
        Integer canalEvento = record.get(CPANEL_ASIGNACION_GRUPOS_VENTA.IDCANALEVENTO);
        if (canalEvento != null) {
            saleGroup.setChannelEventId(canalEvento.longValue());
        }
        saleGroup.setDescription(record.get(Tables.CPANEL_CUPOS_CONFIG.DESCRIPCION));
        saleGroup.setConfigName(record.get(Tables.CPANEL_CONFIG_RECINTO.NOMBRECONFIGURACION));
        saleGroup.setConfigId(Long.valueOf(record.get(CPANEL_CONFIG_RECINTO.IDCONFIGURACION)));
        return saleGroup;
    }

}
