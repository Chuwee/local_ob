package es.onebox.event.priceengine.simulation.dao;

import es.onebox.event.priceengine.simulation.record.PriceZoneRateVenueConfigCustomRecord;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.CpanelAsignacionGruposVenta;
import es.onebox.jooq.cpanel.tables.CpanelAsignacionZonaPrecios;
import es.onebox.jooq.cpanel.tables.CpanelConfigRecinto;
import es.onebox.jooq.cpanel.tables.CpanelCuposConfig;
import es.onebox.jooq.cpanel.tables.CpanelEntidadRecintoConfig;
import es.onebox.jooq.cpanel.tables.CpanelEventoCanal;
import es.onebox.jooq.cpanel.tables.CpanelSesion;
import es.onebox.jooq.cpanel.tables.CpanelTarifa;
import es.onebox.jooq.cpanel.tables.CpanelZonaNoNumerada;
import es.onebox.jooq.cpanel.tables.CpanelZonaNoNumeradaCupo;
import es.onebox.jooq.cpanel.tables.CpanelZonaPrecioCupo;
import es.onebox.jooq.cpanel.tables.CpanelZonaPreciosConfig;
import es.onebox.jooq.cpanel.tables.records.CpanelAsignacionZonaPreciosRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.jooq.Table;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_ASIGNACION_GRUPOS_VENTA;
import static es.onebox.jooq.cpanel.Tables.CPANEL_BUTACA;
import static es.onebox.jooq.cpanel.Tables.CPANEL_ZONA_PRECIOS_CONFIG;
import static org.jooq.impl.DSL.count;

@Repository
public class AssignmentPriceZoneDao extends DaoImpl<CpanelAsignacionZonaPreciosRecord, Integer> {

    private static final String ALIAS_CPANEL_TARIFA = "rate";
    private static final String ALIAS_CPANEL_SESION = "session";
    private static final String ALIAS_CPANEL_CONFIG_RECINTO = "venueConfig";
    private static final String ALIAS_CPANEL_ZONA_PRECIOS_CONFIG = "pZConfig";
    private static final String ALIAS_CPANEL_ASIGNACION_ZONA_PRECIOS = "asigPZ";
    private static final String ALIAS_CPANEL_ENTIDAD_RECINTO_CONFIG = "entityVenueConfig";

    protected AssignmentPriceZoneDao() {
        super(Tables.CPANEL_ASIGNACION_ZONA_PRECIOS);
    }

    public List<PriceZoneRateVenueConfigCustomRecord> getPriceZonesRatesVenueConfigByEventId(Integer eventId) {
        CpanelTarifa rate = Tables.CPANEL_TARIFA.as(ALIAS_CPANEL_TARIFA);
        CpanelConfigRecinto venueConfig = Tables.CPANEL_CONFIG_RECINTO.as(ALIAS_CPANEL_CONFIG_RECINTO);
        CpanelZonaPreciosConfig priceZoneConfig = CPANEL_ZONA_PRECIOS_CONFIG.as(ALIAS_CPANEL_ZONA_PRECIOS_CONFIG);

        return getDefaultSelectAndJoins()
                .where(rate.IDEVENTO.eq(eventId)).and(venueConfig.ESTADO.ne(0))
                .groupBy(priceZoneConfig.IDZONA, rate.IDTARIFA)
                .fetch().map(this::convertTo);
    }

    private SelectJoinStep<Record> getDefaultSelectAndJoins() {
        CpanelTarifa rate = Tables.CPANEL_TARIFA.as(ALIAS_CPANEL_TARIFA);
        CpanelSesion session = Tables.CPANEL_SESION.as(ALIAS_CPANEL_SESION);
        CpanelConfigRecinto venueConfig = Tables.CPANEL_CONFIG_RECINTO.as(ALIAS_CPANEL_CONFIG_RECINTO);
        CpanelZonaPreciosConfig priceZoneConfig = CPANEL_ZONA_PRECIOS_CONFIG.as(ALIAS_CPANEL_ZONA_PRECIOS_CONFIG);
        CpanelEntidadRecintoConfig entityVenueConfig = Tables.CPANEL_ENTIDAD_RECINTO_CONFIG.as(ALIAS_CPANEL_ENTIDAD_RECINTO_CONFIG);
        CpanelAsignacionZonaPrecios asigPriceZone = Tables.CPANEL_ASIGNACION_ZONA_PRECIOS.as(ALIAS_CPANEL_ASIGNACION_ZONA_PRECIOS);

        return dsl.select(asigPriceZone.PRECIO)
                .select(rate.IDTARIFA, rate.NOMBRE)
                .select(venueConfig.IDCONFIGURACION, venueConfig.NOMBRECONFIGURACION)
                .select(priceZoneConfig.IDZONA, priceZoneConfig.IDCONFIGURACION, priceZoneConfig.CODIGO, priceZoneConfig.DESCRIPCION)
                .from(asigPriceZone)
                .innerJoin(rate).on(rate.IDTARIFA.eq(asigPriceZone.IDTARIFA))
                .innerJoin(priceZoneConfig).on(priceZoneConfig.IDZONA.eq(asigPriceZone.IDZONA))
                .innerJoin(venueConfig).on(venueConfig.IDCONFIGURACION.eq(priceZoneConfig.IDCONFIGURACION))
                .innerJoin(entityVenueConfig).on(entityVenueConfig.IDCONFIGURACION.eq(venueConfig.IDCONFIGURACION))
                .innerJoin(session).on(session.IDRELACIONENTIDADRECINTO.eq(entityVenueConfig.IDRELACIONENTRECINTO));
    }

    public List<PriceZoneRateVenueConfigCustomRecord> getPriceZonesRatesGroupSalesVenueConfigByEventId(Integer channelEventId) {
        List<PriceZoneRateVenueConfigCustomRecord> numberedArea =
                getSelectWithGroupSales(channelEventId);
        List<PriceZoneRateVenueConfigCustomRecord> notNumberedArea =
                getSelectWithGroupSalesNotNumbered(channelEventId);
        List<PriceZoneRateVenueConfigCustomRecord> result = new ArrayList<>(numberedArea);
        notNumberedArea.stream()
                        .filter(item -> isNotPresentInResult(result, item.getPriceZoneConfig().getIdzona(), item.getIdtarifa()))
                        .forEach(result::add);
        return result;
    }

    private List<PriceZoneRateVenueConfigCustomRecord> getSelectWithGroupSales(Integer channelEventId) {
        CpanelCuposConfig quota = Tables.CPANEL_CUPOS_CONFIG;
        CpanelEventoCanal eventChannel = Tables.CPANEL_EVENTO_CANAL;
        CpanelTarifa rate = Tables.CPANEL_TARIFA.as(ALIAS_CPANEL_TARIFA);
        CpanelSesion session = Tables.CPANEL_SESION.as(ALIAS_CPANEL_SESION);
        CpanelAsignacionGruposVenta saleGroup = Tables.CPANEL_ASIGNACION_GRUPOS_VENTA;
        CpanelConfigRecinto venueConfig = Tables.CPANEL_CONFIG_RECINTO.as(ALIAS_CPANEL_CONFIG_RECINTO);
        CpanelZonaPreciosConfig priceZoneConfig = CPANEL_ZONA_PRECIOS_CONFIG.as(ALIAS_CPANEL_ZONA_PRECIOS_CONFIG);

        Table<?> seatPriceTypeQuota = dsl.select(CPANEL_BUTACA.ZONA_PRECIOS, CPANEL_ASIGNACION_GRUPOS_VENTA.IDCUPO, count().as("cant_butacas"))
                .from(CPANEL_BUTACA)
                .join(CPANEL_ASIGNACION_GRUPOS_VENTA).on(CPANEL_BUTACA.CUPO.eq(CPANEL_ASIGNACION_GRUPOS_VENTA.IDCUPO).and(CPANEL_ASIGNACION_GRUPOS_VENTA.IDCANALEVENTO.eq(channelEventId)))
                .groupBy(CPANEL_BUTACA.ZONA_PRECIOS, CPANEL_ASIGNACION_GRUPOS_VENTA.IDCUPO)
                .asTable("seat_price_type_quota");


        CpanelZonaPrecioCupo numberedPriceZone = Tables.CPANEL_ZONA_PRECIO_CUPO;

        List<PriceZoneRateVenueConfigCustomRecord> seatRecords = getDefaultSelectAndJoins()
                .innerJoin(eventChannel).on(eventChannel.IDEVENTO.eq(session.IDEVENTO))
                .innerJoin(seatPriceTypeQuota).on(seatPriceTypeQuota.field(CPANEL_BUTACA.ZONA_PRECIOS).eq(priceZoneConfig.IDZONA))
                .innerJoin(quota).on(quota.IDCUPO.eq(seatPriceTypeQuota.field(CPANEL_ASIGNACION_GRUPOS_VENTA.IDCUPO)))
                .innerJoin(saleGroup).on(saleGroup.IDCUPO.eq(quota.IDCUPO))
                .where(saleGroup.IDCANALEVENTO.eq(channelEventId)).and(venueConfig.ESTADO.ne(0))
                .groupBy(priceZoneConfig.IDZONA, rate.IDTARIFA)
                .fetch().map(this::convertTo);
        List<PriceZoneRateVenueConfigCustomRecord>  numberedPriceZoneRecords = getDefaultSelectAndJoins()
                .innerJoin(eventChannel).on(eventChannel.IDEVENTO.eq(session.IDEVENTO))
                .innerJoin(numberedPriceZone).on(numberedPriceZone.IDZONAPRECIO.eq(priceZoneConfig.IDZONA))
                .innerJoin(quota).on(quota.IDCUPO.eq(numberedPriceZone.IDCUPO))
                .innerJoin(saleGroup).on(saleGroup.IDCUPO.eq(quota.IDCUPO))
                .where(saleGroup.IDCANALEVENTO.eq(channelEventId)).and(venueConfig.ESTADO.ne(0))
                .groupBy(priceZoneConfig.IDZONA, rate.IDTARIFA)
                .fetch().map(this::convertTo);
        List<PriceZoneRateVenueConfigCustomRecord> result = new ArrayList<>(seatRecords);
        numberedPriceZoneRecords.stream()
                .filter(item -> isNotPresentInResult(result, item.getPriceZoneConfig().getIdzona(), item.getIdtarifa()))
                .forEach(result::add);
        return result;
    }

    private List<PriceZoneRateVenueConfigCustomRecord> getSelectWithGroupSalesNotNumbered(Integer channelEventId) {
        CpanelCuposConfig quota = Tables.CPANEL_CUPOS_CONFIG;
        CpanelEventoCanal eventChannel = Tables.CPANEL_EVENTO_CANAL;
        CpanelTarifa rate = Tables.CPANEL_TARIFA.as(ALIAS_CPANEL_TARIFA);
        CpanelSesion session = Tables.CPANEL_SESION.as(ALIAS_CPANEL_SESION);
        CpanelAsignacionGruposVenta saleGroup = Tables.CPANEL_ASIGNACION_GRUPOS_VENTA;
        CpanelConfigRecinto venueConfig = Tables.CPANEL_CONFIG_RECINTO.as(ALIAS_CPANEL_CONFIG_RECINTO);
        CpanelZonaPreciosConfig priceZoneConfig = CPANEL_ZONA_PRECIOS_CONFIG.as(ALIAS_CPANEL_ZONA_PRECIOS_CONFIG);
        CpanelZonaNoNumerada notNumberedZone = Tables.CPANEL_ZONA_NO_NUMERADA;
        CpanelZonaNoNumeradaCupo notNumberedZoneQuota = Tables.CPANEL_ZONA_NO_NUMERADA_CUPO;

        return getDefaultSelectAndJoins()
                .innerJoin(eventChannel).on(eventChannel.IDEVENTO.eq(session.IDEVENTO))
                .innerJoin(notNumberedZone).on(notNumberedZone.ZONA_PRECIOS.eq(priceZoneConfig.IDZONA))
                .innerJoin(notNumberedZoneQuota).on(notNumberedZoneQuota.IDZONA.eq(notNumberedZone.IDZONA))
                .innerJoin(quota).on(quota.IDCUPO.eq(notNumberedZoneQuota.IDCUPO))
                .innerJoin(saleGroup).on(saleGroup.IDCUPO.eq(quota.IDCUPO))
                .where(saleGroup.IDCANALEVENTO.eq(channelEventId)).and(venueConfig.ESTADO.ne(0))
                .groupBy(priceZoneConfig.IDZONA, rate.IDTARIFA)
                .fetch().map(this::convertTo);
    }

    private PriceZoneRateVenueConfigCustomRecord convertTo(Record recordWithInfo) {
        PriceZoneRateVenueConfigCustomRecord result = recordWithInfo.into(PriceZoneRateVenueConfigCustomRecord.class);
        result.setRate(recordWithInfo.into(CpanelTarifa.CPANEL_TARIFA.as(ALIAS_CPANEL_TARIFA)));
        result.setVenueConfig(recordWithInfo.into(CpanelConfigRecinto.CPANEL_CONFIG_RECINTO.as(ALIAS_CPANEL_CONFIG_RECINTO)));
        result.setPriceZoneConfig(recordWithInfo.into(CpanelZonaPreciosConfig.CPANEL_ZONA_PRECIOS_CONFIG.as(ALIAS_CPANEL_ZONA_PRECIOS_CONFIG)));
        return result;
    }

    private static boolean isNotPresentInResult(List<PriceZoneRateVenueConfigCustomRecord> result, Integer zoneId, Integer rateId) {
        return result.stream().noneMatch(item -> item.getPriceZoneConfig().getIdzona().equals(zoneId)
                                                 && item.getRate().getIdtarifa().equals(rateId));
    }
}
