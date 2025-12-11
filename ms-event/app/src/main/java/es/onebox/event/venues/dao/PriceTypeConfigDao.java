package es.onebox.event.venues.dao;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.request.PriceTypeBaseFilter;
import es.onebox.event.common.request.PriceTypeFilter;
import es.onebox.event.common.utils.JooqUtils;
import es.onebox.event.events.dao.record.PriceTypeConfigCustomRecord;
import es.onebox.event.priceengine.sorting.SortUtils;
import es.onebox.event.sessions.dao.record.ZonaPreciosConfigRecord;
import es.onebox.event.sorting.PriceTypeField;
import es.onebox.jooq.cpanel.tables.CpanelConfigRecinto;
import es.onebox.jooq.cpanel.tables.CpanelZonaPreciosConfig;
import es.onebox.jooq.cpanel.tables.records.CpanelConfigRecintoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelZonaPreciosConfigRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.SelectConditionStep;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

import static es.onebox.jooq.cpanel.Tables.CPANEL_CONFIG_RECINTO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_ZONA_PRECIOS_CONFIG;
import static es.onebox.jooq.cpanel.Tables.CPANEL_ZONA_PRECIO_ETIQUETA;
import static es.onebox.jooq.cpanel.Tables.CPANEL_ZONA_PRECIO_ETIQUETA_SESION;

@Repository
public class PriceTypeConfigDao extends DaoImpl<CpanelZonaPreciosConfigRecord, Integer> {

    private static final String FIELD_GATE_ID = "gateId";
    private static final Integer DELETED_STATE = 0;

    private static final CpanelConfigRecinto configRecinto = CPANEL_CONFIG_RECINTO.as(PriceTypeField.Alias.CONFIG_RECINTO);
    private static final CpanelZonaPreciosConfig zonaPreciosConfig = CPANEL_ZONA_PRECIOS_CONFIG.as(PriceTypeField.Alias.ZONA_PRECIOS);

    protected PriceTypeConfigDao() {
        super(CPANEL_ZONA_PRECIOS_CONFIG);
    }

    public List<ZonaPreciosConfigRecord> getPriceZone(Long venueConfigId, PriceTypeBaseFilter filter) {
        try {
            SelectConditionStep query = dsl.select(ArrayUtils.addAll(CPANEL_ZONA_PRECIOS_CONFIG.fields(), CPANEL_ZONA_PRECIO_ETIQUETA.IDETIQUETA.as(FIELD_GATE_ID)))
                    .from(CPANEL_ZONA_PRECIOS_CONFIG)
                    .leftJoin(CPANEL_ZONA_PRECIO_ETIQUETA).on(CPANEL_ZONA_PRECIOS_CONFIG.IDZONA.eq(CPANEL_ZONA_PRECIO_ETIQUETA.IDZONAPRECIO))
                    .where(CPANEL_ZONA_PRECIOS_CONFIG.IDCONFIGURACION.eq(venueConfigId.intValue()));
            JooqUtils.fillFilter(query, filter);
            return query.fetch((RecordMapper<Record, ZonaPreciosConfigRecord>) this::mapToZonaPreciosConfigRecord);
        } catch (Exception e) {
            throw new OneboxRestException(CoreErrorCode.GENERIC_ERROR, "Error executing query", e);
        }
    }

    public List<ZonaPreciosConfigRecord> getPriceZoneBySession(Long venueConfigId, PriceTypeBaseFilter filter, Long sessionId) {
        try {
            SelectConditionStep query = dsl.select(ArrayUtils.addAll(CPANEL_ZONA_PRECIOS_CONFIG.fields(), CPANEL_ZONA_PRECIO_ETIQUETA_SESION.IDETIQUETA.as(FIELD_GATE_ID)))
                    .from(CPANEL_ZONA_PRECIOS_CONFIG)
                    .leftJoin(CPANEL_ZONA_PRECIO_ETIQUETA_SESION)
                    .on(CPANEL_ZONA_PRECIOS_CONFIG.IDZONA.eq(CPANEL_ZONA_PRECIO_ETIQUETA_SESION.IDZONAPRECIO).and(CPANEL_ZONA_PRECIO_ETIQUETA_SESION.IDSESION.eq(sessionId.intValue())))
                    .where(CPANEL_ZONA_PRECIOS_CONFIG.IDCONFIGURACION.eq(venueConfigId.intValue()));
            JooqUtils.fillFilter(query, filter);
            return query.fetch((RecordMapper<Record, ZonaPreciosConfigRecord>) this::mapToZonaPreciosConfigRecord);
        } catch (Exception e) {
            throw new OneboxRestException(CoreErrorCode.GENERIC_ERROR, "Error executing query", e);
        }
    }

    public Long countTotalByVenueConfigId(Long venueConfigId) {
        try {
            return dsl.selectCount()
                    .from(CPANEL_ZONA_PRECIOS_CONFIG)
                    .where(CPANEL_ZONA_PRECIOS_CONFIG.IDCONFIGURACION.eq(venueConfigId.intValue()))
                    .fetchOne(0, Long.class);
        } catch (Exception e) {
            throw new OneboxRestException(CoreErrorCode.GENERIC_ERROR, "Error executing query", e);
        }
    }

    public List<CpanelZonaPreciosConfigRecord> getPriceZoneByEventId(Long eventId) {
        try {
            return dsl.select(CPANEL_ZONA_PRECIOS_CONFIG.fields())
                    .from(CPANEL_ZONA_PRECIOS_CONFIG)
                    .join(CPANEL_CONFIG_RECINTO).on(CPANEL_ZONA_PRECIOS_CONFIG.IDCONFIGURACION.eq(CPANEL_CONFIG_RECINTO.IDCONFIGURACION))
                    .and(CPANEL_CONFIG_RECINTO.IDEVENTO.eq(eventId.intValue()))
                    .fetchInto(CpanelZonaPreciosConfigRecord.class);
        } catch (Exception e) {
            throw new OneboxRestException(CoreErrorCode.GENERIC_ERROR, "Error executing query", e);
        }
    }


    public List<CpanelZonaPreciosConfigRecord> findByVenueTemplateId(Integer venueTemplateId) {
        return dsl.select(CPANEL_ZONA_PRECIOS_CONFIG.fields())
                .from(CPANEL_ZONA_PRECIOS_CONFIG)
                .where(CPANEL_ZONA_PRECIOS_CONFIG.IDCONFIGURACION.eq(venueTemplateId))
                .fetchInto(CpanelZonaPreciosConfigRecord.class);
    }

    private ZonaPreciosConfigRecord mapToZonaPreciosConfigRecord(Record record) {
        if (record == null || record.get(CPANEL_ZONA_PRECIOS_CONFIG.IDZONA) == null) {
            return null;
        }

        ZonaPreciosConfigRecord zonaPreciosConfigRecord = record.into(ZonaPreciosConfigRecord.class);
        if (record.getValue(FIELD_GATE_ID, Long.class) != null) {
            zonaPreciosConfigRecord.setGateId(record.getValue(FIELD_GATE_ID, Long.class).longValue());
        }

        return zonaPreciosConfigRecord;
    }

    public List<PriceTypeConfigCustomRecord> getPriceTypeWithVenueConfigByEventId(Long eventId, PriceTypeFilter filter) {
        try {
            SelectConditionStep query = dsl.select(configRecinto.IDCONFIGURACION, configRecinto.NOMBRECONFIGURACION)
                    .select(zonaPreciosConfig.IDZONA, zonaPreciosConfig.DESCRIPCION)
                    .from(zonaPreciosConfig)
                    .innerJoin(configRecinto).on(zonaPreciosConfig.IDCONFIGURACION.eq(configRecinto.IDCONFIGURACION))
                    .where(configRecinto.IDEVENTO.eq(eventId.intValue())).and(configRecinto.ESTADO.ne(DELETED_STATE));

            fillFilter(query, filter);
            return query.fetch().map((RecordMapper<Record, PriceTypeConfigCustomRecord>) this::mapToPriceTypeConfigCustomRecord);
        } catch (Exception e) {
            throw new OneboxRestException(CoreErrorCode.GENERIC_ERROR, "Error executing query", e);
        }
    }

    public Long getTotalPriceTypeWithVenueConfigByEventId(Long eventId, PriceTypeFilter filter) {
        try {
            SelectConditionStep query = dsl.selectCount()
                    .from(zonaPreciosConfig)
                    .innerJoin(configRecinto).on(zonaPreciosConfig.IDCONFIGURACION.eq(configRecinto.IDCONFIGURACION))
                    .where(configRecinto.IDEVENTO.eq(eventId.intValue())).and(configRecinto.ESTADO.ne(DELETED_STATE));
            fillSortAndQ(query, filter);
            return query.fetchOne().into(Long.class);
        } catch (Exception e) {
            throw new OneboxRestException(CoreErrorCode.GENERIC_ERROR, "Error executing query", e);
        }
    }

    private void fillFilter(SelectConditionStep query, PriceTypeFilter filter) {
        fillSortAndQ(query, filter);
        JooqUtils.fillFilter(query, filter);
    }

    private void fillSortAndQ(SelectConditionStep query, PriceTypeFilter filter) {
        if (Objects.nonNull(filter)) {
            if (StringUtils.isNotBlank(filter.getQ())) {
                query.and(configRecinto.NOMBRECONFIGURACION.like("%" + filter.getQ() + "%")
                        .or(zonaPreciosConfig.DESCRIPCION.like("%" + filter.getQ() + "%")));
            }
            if (Objects.nonNull(filter.getSort()) && CollectionUtils.isNotEmpty(filter.getSort().getSortDirections())) {
                query.orderBy(SortUtils.buildSort(filter.getSort(), PriceTypeField::byName));
            } else { //default order by
                query.orderBy(configRecinto.NOMBRECONFIGURACION, zonaPreciosConfig.DESCRIPCION);
            }

        }
    }

    private PriceTypeConfigCustomRecord mapToPriceTypeConfigCustomRecord(Record recordWithInfo) {
        PriceTypeConfigCustomRecord result = recordWithInfo.into(PriceTypeConfigCustomRecord.class);
        result.setVenueConfig(recordWithInfo.into(CpanelConfigRecintoRecord.class));
        return result;
    }

}
