package es.onebox.event.events.dao;

import es.onebox.event.events.dao.record.SaleGroupRecord;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelAsignacionGruposVentaRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.jooq.Field;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static es.onebox.jooq.cpanel.Tables.CPANEL_CUPOS_CONFIG;
import static es.onebox.jooq.cpanel.tables.CpanelAsignacionGruposVenta.CPANEL_ASIGNACION_GRUPOS_VENTA;

@Repository
public class SalesGroupAssignmentDao extends DaoImpl<CpanelAsignacionGruposVentaRecord, CpanelAsignacionGruposVentaRecord> {

    private static final Field<?>[] FIELDS = {
            CPANEL_ASIGNACION_GRUPOS_VENTA.IDCANALEVENTO,
            CPANEL_ASIGNACION_GRUPOS_VENTA.IDCUPO,
            CPANEL_CUPOS_CONFIG.DESCRIPCION,
            CPANEL_CUPOS_CONFIG.CODIGO,
            CPANEL_CUPOS_CONFIG.DEFECTO,
            Tables.CPANEL_CONFIG_RECINTO.NOMBRECONFIGURACION
    };

    protected SalesGroupAssignmentDao() {
        super(CPANEL_ASIGNACION_GRUPOS_VENTA);
    }

    public void deleteByChannelEventId(Integer channelEventId) {
        dsl.delete(CPANEL_ASIGNACION_GRUPOS_VENTA)
                .where(CPANEL_ASIGNACION_GRUPOS_VENTA.IDCANALEVENTO.eq(channelEventId))
                .execute();
    }

    public List<Long> getChannelEventQuotaIds(Integer channelEventId) {
        return dsl.selectDistinct(CPANEL_ASIGNACION_GRUPOS_VENTA.IDCUPO)
                .from(CPANEL_ASIGNACION_GRUPOS_VENTA)
                .where(CPANEL_ASIGNACION_GRUPOS_VENTA.IDCANALEVENTO.eq(channelEventId))
                .fetchInto(Long.class);
    }

    public void bulkInsertByChannelEvent(List<Long> ids, Integer channelEventId) {
        List<CpanelAsignacionGruposVentaRecord> records = ids.stream()
                .filter(Objects::nonNull)
                .map(id -> buildRecord(id.intValue(), channelEventId))
                .collect(Collectors.toList());
        dsl.batchInsert(records).execute();
    }

    private CpanelAsignacionGruposVentaRecord buildRecord(Integer id, Integer channelEventId) {
        CpanelAsignacionGruposVentaRecord record = new CpanelAsignacionGruposVentaRecord();
        record.setIdcupo(id);
        record.setIdcanalevento(channelEventId);
        return record;
    }


    private SaleGroupRecord buildSaleGroup(Record record) {
        SaleGroupRecord saleGroup = new SaleGroupRecord();
        saleGroup.setId(record.get(CPANEL_CUPOS_CONFIG.IDCUPO).longValue());
        Integer canalEvento = record.get(CPANEL_ASIGNACION_GRUPOS_VENTA.IDCANALEVENTO);
        if (canalEvento != null) {
            saleGroup.setChannelEventId(canalEvento.longValue());
        }
        saleGroup.setDescription(record.get(CPANEL_CUPOS_CONFIG.DESCRIPCION));
        saleGroup.setConfigName(record.get(Tables.CPANEL_CONFIG_RECINTO.NOMBRECONFIGURACION));
        saleGroup.setCode(record.get(CPANEL_CUPOS_CONFIG.CODIGO));
        var isDefault = record.get(CPANEL_CUPOS_CONFIG.DEFECTO);
        saleGroup.setDefaultQuota(isDefault != null ? isDefault.intValue() == 1 : Boolean.FALSE);
        return saleGroup;
    }
}
