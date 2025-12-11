package es.onebox.event.priceengine.simulation.dao;

import es.onebox.event.priceengine.simulation.record.EventChannelForCatalogRecord;
import es.onebox.event.priceengine.taxes.domain.ChannelTaxInfo;
import es.onebox.event.priceengine.taxes.utils.TaxSimulationUtils;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoCanalRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelImpuestoRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.jooq.Record;
import org.jooq.SelectFieldOrAsterisk;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static es.onebox.jooq.cpanel.Tables.CPANEL_EVENTO_CANAL;
import static es.onebox.jooq.cpanel.Tables.CPANEL_EVENTO_CANAL_IMPUESTO_RECARGO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_IMPUESTO;

@Repository
public class EventChannelDao extends DaoImpl<CpanelEventoCanalRecord, Integer> {

    protected EventChannelDao() {
        super(CPANEL_EVENTO_CANAL);
    }

    public List<EventChannelForCatalogRecord> getEventChannels(Long eventId) {
        SelectFieldOrAsterisk[] fields = {
                CPANEL_EVENTO_CANAL.IDEVENTOCANAL,
                CPANEL_EVENTO_CANAL.IDEVENTO,
                CPANEL_EVENTO_CANAL.IDCANAL,
                CPANEL_EVENTO_CANAL.ESTADO,
                CPANEL_EVENTO_CANAL.FECHASOLICITUD,
                CPANEL_EVENTO_CANAL.ELEMENTOSCOMTICKET,
                CPANEL_EVENTO_CANAL.TAXONOMIAPROPIA,
                CPANEL_EVENTO_CANAL.IDLISTASUBSCRIPCION,
                CPANEL_EVENTO_CANAL.RELACIONARCHIVADA,
                CPANEL_EVENTO_CANAL.ELEMENTOCOMTICKETTAQUILLA,
                CPANEL_EVENTO_CANAL.ELEMENTOCOMCANAL,
                CPANEL_EVENTO_CANAL.ELEMENTOCOMEMAIL,
                CPANEL_EVENTO_CANAL.ESTADOGENERACIONCOMSERV,
                CPANEL_EVENTO_CANAL.FECHAGENERACIONCOMSERV,
                CPANEL_EVENTO_CANAL.NOINCLUIREVENTOCARTELERA,
                CPANEL_EVENTO_CANAL.APLICARCOSTESCANALESPECIFICOS,
                CPANEL_EVENTO_CANAL.APLICARRECARGOSCANALESPECIFICOS,
                CPANEL_EVENTO_CANAL.SURCHARGESTAXESORIGIN,
                CPANEL_EVENTO_CANAL.PERMITIRDEVOLUCIONCANALES,
                CPANEL_EVENTO_CANAL.PERMITIRPROMOCIONCANALES,
                CPANEL_EVENTO_CANAL.TICKETHANDLING,
                CPANEL_EVENTO_CANAL.CREATE_DATE,
                CPANEL_EVENTO_CANAL.UPDATE_DATE,
                CPANEL_IMPUESTO.IDIMPUESTO,
                CPANEL_IMPUESTO.NOMBRE,
                CPANEL_IMPUESTO.VALOR
        };
        Map<EventChannelForCatalogRecord, List<CpanelImpuestoRecord>> eventChannelChannelSurchargesTaxes =
                dsl.select(fields)
                    .from(CPANEL_EVENTO_CANAL)
                    .leftJoin(CPANEL_EVENTO_CANAL_IMPUESTO_RECARGO).on(CPANEL_EVENTO_CANAL.IDEVENTOCANAL.eq(CPANEL_EVENTO_CANAL_IMPUESTO_RECARGO.IDEVENTOCANAL))
                    .leftJoin(CPANEL_IMPUESTO).on(CPANEL_EVENTO_CANAL_IMPUESTO_RECARGO.IDIMPUESTO.eq(CPANEL_IMPUESTO.IDIMPUESTO))
                    .where(CPANEL_EVENTO_CANAL.IDEVENTO.eq(eventId.intValue()))
                    .fetchGroups(
                            EventChannelDao::buildEventChannelForCatalogRecord,
                            EventChannelDao::buildCpanelImpuestoRecord
                    );
        return buildEventChannelForCatalogRecords(eventChannelChannelSurchargesTaxes);
    }

    public Optional<CpanelEventoCanalRecord> getEventChannel(int eventId, int channelId) {
        return dsl.select(CPANEL_EVENTO_CANAL.fields())
                .from(CPANEL_EVENTO_CANAL)
                .where(CPANEL_EVENTO_CANAL.IDEVENTO.eq(eventId)
                        .and(CPANEL_EVENTO_CANAL.IDCANAL.eq(channelId)))
                .fetchOptional()
                .map(record -> record.into(CpanelEventoCanalRecord.class));
    }

    private static List<EventChannelForCatalogRecord> buildEventChannelForCatalogRecords(Map<EventChannelForCatalogRecord, List<CpanelImpuestoRecord>> eventChannelChannelSurchargesTaxes) {
        return eventChannelChannelSurchargesTaxes.entrySet()
                .stream()
                .map(entry -> {
                    EventChannelForCatalogRecord eventChannel = entry.getKey();
                    if (CollectionUtils.isNotEmpty(entry.getValue())) {
                        eventChannel.setSurchargesTaxes(mapTaxesInfo(entry.getValue()));
                    }
                    return eventChannel;
                })
                .toList();
    }

    private static EventChannelForCatalogRecord buildEventChannelForCatalogRecord(Record r) {
        EventChannelForCatalogRecord record = new EventChannelForCatalogRecord();
        record.setIdeventocanal(r.get(CPANEL_EVENTO_CANAL.IDEVENTOCANAL, Integer.class));
        record.setIdeventocanal(r.get(CPANEL_EVENTO_CANAL.IDEVENTOCANAL, Integer.class));
        record.setIdevento(r.get(CPANEL_EVENTO_CANAL.IDEVENTO, Integer.class));
        record.setIdcanal(r.get(CPANEL_EVENTO_CANAL.IDCANAL, Integer.class));
        record.setEstado(r.get(CPANEL_EVENTO_CANAL.ESTADO, Integer.class));
        record.setFechasolicitud(r.get(CPANEL_EVENTO_CANAL.FECHASOLICITUD, java.sql.Timestamp.class));
        record.setElementoscomticket(r.get(CPANEL_EVENTO_CANAL.ELEMENTOSCOMTICKET, Integer.class));
        record.setTaxonomiapropia(r.get(CPANEL_EVENTO_CANAL.TAXONOMIAPROPIA, Integer.class));
        record.setIdlistasubscripcion(r.get(CPANEL_EVENTO_CANAL.IDLISTASUBSCRIPCION, Integer.class));
        record.setRelacionarchivada(r.get(CPANEL_EVENTO_CANAL.RELACIONARCHIVADA, Byte.class));
        record.setElementocomtickettaquilla(r.get(CPANEL_EVENTO_CANAL.ELEMENTOCOMTICKETTAQUILLA, Integer.class));
        record.setElementocomcanal(r.get(CPANEL_EVENTO_CANAL.ELEMENTOCOMCANAL, Integer.class));
        record.setElementocomemail(r.get(CPANEL_EVENTO_CANAL.ELEMENTOCOMEMAIL, Integer.class));
        record.setEstadogeneracioncomserv(r.get(CPANEL_EVENTO_CANAL.ESTADOGENERACIONCOMSERV, Byte.class));
        record.setFechageneracioncomserv(r.get(CPANEL_EVENTO_CANAL.FECHAGENERACIONCOMSERV, java.sql.Timestamp.class));
        record.setNoincluireventocartelera(r.get(CPANEL_EVENTO_CANAL.NOINCLUIREVENTOCARTELERA, Byte.class));
        record.setAplicarcostescanalespecificos(r.get(CPANEL_EVENTO_CANAL.APLICARCOSTESCANALESPECIFICOS, Byte.class));
        record.setAplicarrecargoscanalespecificos(r.get(CPANEL_EVENTO_CANAL.APLICARRECARGOSCANALESPECIFICOS, Byte.class));
        record.setSurchargestaxesorigin(r.get(CPANEL_EVENTO_CANAL.SURCHARGESTAXESORIGIN, Integer.class));
        record.setPermitirdevolucioncanales(r.get(CPANEL_EVENTO_CANAL.PERMITIRDEVOLUCIONCANALES, Byte.class));
        record.setPermitirpromocioncanales(r.get(CPANEL_EVENTO_CANAL.PERMITIRPROMOCIONCANALES, Boolean.class));
        record.setTickethandling(r.get(CPANEL_EVENTO_CANAL.TICKETHANDLING, Integer.class));
        record.setCreateDate(r.get(CPANEL_EVENTO_CANAL.CREATE_DATE, java.sql.Timestamp.class));
        record.setUpdateDate(r.get(CPANEL_EVENTO_CANAL.UPDATE_DATE, java.sql.Timestamp.class));
        return record;
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
