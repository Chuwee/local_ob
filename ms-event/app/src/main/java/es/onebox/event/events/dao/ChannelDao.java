package es.onebox.event.events.dao;

import es.onebox.event.events.dao.bean.ChannelInfo;
import es.onebox.event.priceengine.taxes.domain.ChannelTaxInfo;
import es.onebox.event.priceengine.taxes.utils.TaxSimulationUtils;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelImpuestoRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.jooq.Record;
import org.jooq.SelectFieldOrAsterisk;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static es.onebox.jooq.cpanel.Tables.CPANEL_CANAL;
import static es.onebox.jooq.cpanel.Tables.CPANEL_CANAL_IMPUESTO_RECARGO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_IMPUESTO;

@Repository
public class ChannelDao extends DaoImpl<CpanelCanalRecord, Integer> {

    protected ChannelDao() {
        super(Tables.CPANEL_CANAL);
    }

    public <T extends Number> List<ChannelInfo> getByIds(List<T> channelIds) {
        SelectFieldOrAsterisk[] fields = {CPANEL_CANAL.IDCANAL, CPANEL_CANAL.NOMBRECANAL, CPANEL_CANAL.IDENTIDAD, CPANEL_CANAL.IDSUBTIPOCANAL,
                CPANEL_CANAL.SURCHARGESTAXESORIGIN, CPANEL_IMPUESTO.IDIMPUESTO, CPANEL_IMPUESTO.NOMBRE, CPANEL_IMPUESTO.VALOR};
        Map<ChannelInfo, List<CpanelImpuestoRecord>> channelChannelSurchargesTaxes =
                dsl.select(fields)
                    .from(CPANEL_CANAL)
                    .leftJoin(CPANEL_CANAL_IMPUESTO_RECARGO).on(CPANEL_CANAL.IDCANAL.eq(CPANEL_CANAL_IMPUESTO_RECARGO.IDCANAL))
                    .leftJoin(CPANEL_IMPUESTO).on(CPANEL_CANAL_IMPUESTO_RECARGO.IDIMPUESTO.eq(CPANEL_IMPUESTO.IDIMPUESTO))
                    .where(CPANEL_CANAL.IDCANAL.in(channelIds))
                    .fetchGroups(
                            r -> new ChannelInfo(
                                    Long.valueOf(r.get(CPANEL_CANAL.IDCANAL)),
                                    r.get(CPANEL_CANAL.NOMBRECANAL),
                                    Long.valueOf(r.get(CPANEL_CANAL.IDENTIDAD)),
                                    r.get(CPANEL_CANAL.IDSUBTIPOCANAL),
                                    r.get(CPANEL_CANAL.SURCHARGESTAXESORIGIN)
                            ),
                            ChannelDao::buildCpanelImpuestoRecord
                    );
        return buildChannelInfo(channelChannelSurchargesTaxes);
    }

    public List<ChannelInfo> getByIdsNotDeleted(List<Integer> channelIds) {
        return dsl.select(CPANEL_CANAL.IDCANAL, CPANEL_CANAL.NOMBRECANAL, CPANEL_CANAL.IDENTIDAD, CPANEL_CANAL.IDSUBTIPOCANAL, CPANEL_CANAL.SURCHARGESTAXESORIGIN)
                .from(CPANEL_CANAL)
                .where(CPANEL_CANAL.IDCANAL.in(channelIds))
                .and(CPANEL_CANAL.ESTADO.notEqual(0))
                .fetch().map(r -> new ChannelInfo(
                        Long.valueOf(r.get(CPANEL_CANAL.IDCANAL)),
                        r.get(CPANEL_CANAL.NOMBRECANAL),
                        Long.valueOf(r.get(CPANEL_CANAL.IDENTIDAD)),
                        r.get(CPANEL_CANAL.IDSUBTIPOCANAL),
                        r.get(CPANEL_CANAL.SURCHARGESTAXESORIGIN)));
    }

    private static List<ChannelInfo> buildChannelInfo(Map<ChannelInfo, List<CpanelImpuestoRecord>> channelChannelTaxes) {
        return channelChannelTaxes.entrySet()
                .stream()
                .map(entry -> {
                    ChannelInfo channelInfo = entry.getKey();
                    if (CollectionUtils.isNotEmpty(entry.getValue())) {
                        channelInfo.setSurchargesTaxes(mapTaxesInfo(entry.getValue()));
                    }
                    return channelInfo;
                })
                .toList();
    }

    private static CpanelImpuestoRecord buildCpanelImpuestoRecord(Record r) {
        CpanelImpuestoRecord record = new CpanelImpuestoRecord();
        record.setIdimpuesto(r.get(CPANEL_IMPUESTO.IDIMPUESTO, Integer.class));
        record.setNombre(r.get(CPANEL_IMPUESTO.NOMBRE, String.class));
        record.setValor(r.get(CPANEL_IMPUESTO.VALOR, Double.class));
        return record;
    }

    private static List<ChannelTaxInfo> mapTaxesInfo(List<CpanelImpuestoRecord> taxes) {
        return taxes.stream()
                .filter(r -> r.getIdimpuesto() != null)
                .map(r -> TaxSimulationUtils.createTaxInfo(r.getIdimpuesto().longValue(), r.getValor(), r.getNombre(), ChannelTaxInfo::new))
                .toList();
    }

}
