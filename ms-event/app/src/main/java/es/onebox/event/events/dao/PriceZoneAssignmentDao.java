package es.onebox.event.events.dao;

import es.onebox.core.utils.common.NumberUtils;
import es.onebox.event.events.dto.RateZoneDTO;
import es.onebox.event.events.dao.record.PriceRecord;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelAsignacionZonaPreciosRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelZonaPreciosConfigRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.jooq.Record;
import org.jooq.SelectFieldOrAsterisk;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_ASIGNACION_ZONA_PRECIOS;
import static es.onebox.jooq.cpanel.Tables.CPANEL_CONFIG_RECINTO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_ENTIDAD_RECINTO_CONFIG;
import static es.onebox.jooq.cpanel.Tables.CPANEL_GRUPO_TARIFA;
import static es.onebox.jooq.cpanel.Tables.CPANEL_ZONA_PRECIOS_CONFIG;
import static es.onebox.jooq.cpanel.tables.CpanelSesionTarifa.CPANEL_SESION_TARIFA;
import static es.onebox.jooq.cpanel.tables.CpanelTarifa.CPANEL_TARIFA;

@Repository
public class PriceZoneAssignmentDao extends DaoImpl<CpanelAsignacionZonaPreciosRecord, Integer> {

    protected PriceZoneAssignmentDao() {
        super(CPANEL_ASIGNACION_ZONA_PRECIOS);
    }

    public List<PriceRecord> getPrices(Integer... rateIds) {
        SelectFieldOrAsterisk[] fields = {CPANEL_ZONA_PRECIOS_CONFIG.IDZONA,
                CPANEL_ZONA_PRECIOS_CONFIG.CODIGO,
                CPANEL_ZONA_PRECIOS_CONFIG.DESCRIPCION,
                Tables.CPANEL_TARIFA.IDTARIFA,
                Tables.CPANEL_TARIFA.NOMBRE,
                Tables.CPANEL_TARIFA.IDEVENTO,
                CPANEL_ASIGNACION_ZONA_PRECIOS.PRECIO
        };

        return dsl.select(fields)
                .from(CPANEL_ASIGNACION_ZONA_PRECIOS)
                .innerJoin(CPANEL_ZONA_PRECIOS_CONFIG).on(CPANEL_ZONA_PRECIOS_CONFIG.IDZONA.eq(CPANEL_ASIGNACION_ZONA_PRECIOS.IDZONA))
                .innerJoin(Tables.CPANEL_TARIFA).on(Tables.CPANEL_TARIFA.IDTARIFA.eq(CPANEL_ASIGNACION_ZONA_PRECIOS.IDTARIFA))
                .where(CPANEL_ASIGNACION_ZONA_PRECIOS.IDTARIFA.in(rateIds))
                .fetch().map(this::convertToPackPriceRecord);
    }

    public void deleteByRateId(Integer rateId) {
        dsl.delete(CPANEL_ASIGNACION_ZONA_PRECIOS)
                .where(CPANEL_ASIGNACION_ZONA_PRECIOS.IDTARIFA.eq(rateId))
                .execute();
    }

    public void updatePrices(int pricezone, double newPrice) {
        dsl.update(CPANEL_ASIGNACION_ZONA_PRECIOS)
                .set(CPANEL_ASIGNACION_ZONA_PRECIOS.PRECIO, newPrice)
                .where(CPANEL_ASIGNACION_ZONA_PRECIOS.IDZONA.eq(pricezone))
                .execute();
    }


    public void updatePrices(Integer priceZoneId, Integer rateId, Double newPrice) {
        dsl.update(CPANEL_ASIGNACION_ZONA_PRECIOS)
                .set(CPANEL_ASIGNACION_ZONA_PRECIOS.PRECIO, newPrice)
                .where(CPANEL_ASIGNACION_ZONA_PRECIOS.IDZONA.eq(priceZoneId))
                .and(CPANEL_ASIGNACION_ZONA_PRECIOS.IDTARIFA.eq(rateId))
                .execute();
    }

    public List<RateZoneDTO> findIndividualPricesBySession(Integer sessionId) {
        return dsl.select(CPANEL_TARIFA.IDTARIFA, CPANEL_ZONA_PRECIOS_CONFIG.IDZONA).from(CPANEL_ASIGNACION_ZONA_PRECIOS)
                .innerJoin(CPANEL_ZONA_PRECIOS_CONFIG).on(CPANEL_ZONA_PRECIOS_CONFIG.IDZONA.eq(CPANEL_ASIGNACION_ZONA_PRECIOS.IDZONA))
                .innerJoin(CPANEL_TARIFA).on(CPANEL_ASIGNACION_ZONA_PRECIOS.IDTARIFA.eq(CPANEL_TARIFA.IDTARIFA))
                .innerJoin(CPANEL_SESION_TARIFA).on(CPANEL_SESION_TARIFA.IDTARIFA.eq(CPANEL_TARIFA.IDTARIFA))
                .leftOuterJoin(CPANEL_GRUPO_TARIFA).on(CPANEL_GRUPO_TARIFA.IDGRUPOTARIFA.eq(CPANEL_TARIFA.IDGRUPOTARIFA))
                .where(CPANEL_SESION_TARIFA.IDSESION.eq(sessionId))
                .fetch().into(RateZoneDTO.class);
    }

    public List<CpanelZonaPreciosConfigRecord> getVenueTemplatePriceZones(Integer relacionEntidadRecintoId) {
        return dsl.select().
                from(Tables.CPANEL_ZONA_PRECIOS_CONFIG).
                innerJoin(CPANEL_ENTIDAD_RECINTO_CONFIG).on(CPANEL_ZONA_PRECIOS_CONFIG.IDCONFIGURACION.eq(CPANEL_ENTIDAD_RECINTO_CONFIG.IDCONFIGURACION)).
                innerJoin(CPANEL_CONFIG_RECINTO).on(CPANEL_ZONA_PRECIOS_CONFIG.IDCONFIGURACION.eq(CPANEL_CONFIG_RECINTO.IDCONFIGURACION)).
                where(Tables.CPANEL_CONFIG_RECINTO.ESTADO.notEqual(0)).
                and(CPANEL_ENTIDAD_RECINTO_CONFIG.IDRELACIONENTRECINTO.eq(relacionEntidadRecintoId)).
                fetchInto(CpanelZonaPreciosConfigRecord.class);
    }

    public List<CpanelZonaPreciosConfigRecord> getVenueTemplatePriceZonesByTemplateId(Integer venueTemplateId) {
        return dsl.select()
                .from(CPANEL_ZONA_PRECIOS_CONFIG)
                .innerJoin(CPANEL_CONFIG_RECINTO).on(CPANEL_ZONA_PRECIOS_CONFIG.IDCONFIGURACION.eq(CPANEL_CONFIG_RECINTO.IDCONFIGURACION))
                .where(CPANEL_CONFIG_RECINTO.ESTADO.notEqual(0)).and(CPANEL_ZONA_PRECIOS_CONFIG.IDCONFIGURACION.eq(venueTemplateId))
                .fetchInto(CpanelZonaPreciosConfigRecord.class);
    }

    private PriceRecord convertToPackPriceRecord(Record recordFetched) {
        PriceRecord eventPrice = new PriceRecord();
        eventPrice.setPriceZoneId(recordFetched.get(CPANEL_ZONA_PRECIOS_CONFIG.IDZONA));
        eventPrice.setPriceZoneCode(recordFetched.get(CPANEL_ZONA_PRECIOS_CONFIG.CODIGO));
        eventPrice.setPriceZoneDescription(recordFetched.get(CPANEL_ZONA_PRECIOS_CONFIG.DESCRIPCION));
        eventPrice.setRateId(recordFetched.get(Tables.CPANEL_TARIFA.IDTARIFA));
        eventPrice.setRateName(recordFetched.get(Tables.CPANEL_TARIFA.NOMBRE));
        eventPrice.setPrice(NumberUtils.zeroIfNull(recordFetched.get(CPANEL_ASIGNACION_ZONA_PRECIOS.PRECIO)));

        return eventPrice;
    }

}
