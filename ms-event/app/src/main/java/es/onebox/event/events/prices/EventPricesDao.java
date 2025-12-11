
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.onebox.event.events.prices;

import es.onebox.core.utils.common.CommonUtils;
import es.onebox.core.utils.common.NumberUtils;
import es.onebox.event.events.dto.RateGroupType;
import es.onebox.event.events.enums.PriceType;
import es.onebox.event.events.prices.enums.PriceTypeFilter;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelAsignacionZonaPreciosRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.jooq.Condition;
import org.jooq.InsertSetStep;
import org.jooq.Record;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static es.onebox.jooq.cpanel.Tables.CPANEL_ASIGNACION_ZONA_PRECIOS;
import static es.onebox.jooq.cpanel.Tables.CPANEL_CONFIG_RECINTO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_GRUPO_TARIFA;
import static es.onebox.jooq.cpanel.Tables.CPANEL_PRECIOS_GRUPOS;
import static es.onebox.jooq.cpanel.Tables.CPANEL_SESION_TARIFA;
import static es.onebox.jooq.cpanel.Tables.CPANEL_TARIFA;
import static es.onebox.jooq.cpanel.Tables.CPANEL_TARIFA_GRUPO_TARIFA;
import static es.onebox.jooq.cpanel.Tables.CPANEL_ZONA_PRECIOS_CONFIG;

/**
 * @author ignasi
 */
@Repository
public class EventPricesDao extends DaoImpl<CpanelAsignacionZonaPreciosRecord, Integer> {

    protected EventPricesDao() {
        super(CPANEL_ASIGNACION_ZONA_PRECIOS);
    }

    private static final String TIPO_ENTRADA = "tipoEntrada";

    public List<EventPriceRecord> getBasePricesByEventId(Long eventId, PriceTypeFilter priceType) {

        var individualQuery = dsl.select(
                        Tables.CPANEL_EVENTO.IDEVENTO,
                        CPANEL_CONFIG_RECINTO.IDCONFIGURACION,
                        CPANEL_CONFIG_RECINTO.NOMBRECONFIGURACION,
                        CPANEL_ZONA_PRECIOS_CONFIG.IDZONA,
                        CPANEL_ZONA_PRECIOS_CONFIG.CODIGO,
                        CPANEL_ZONA_PRECIOS_CONFIG.DESCRIPCION,
                        CPANEL_ZONA_PRECIOS_CONFIG.PRIORIDAD,
                        CPANEL_ZONA_PRECIOS_CONFIG.COLOR,
                        CPANEL_ZONA_PRECIOS_CONFIG.RESTRICTIVEACCESS,
                        CPANEL_ASIGNACION_ZONA_PRECIOS.IDTARIFA,
                        CPANEL_TARIFA.NOMBRE,
                        CPANEL_TARIFA.DEFECTO,
                        CPANEL_ASIGNACION_ZONA_PRECIOS.PRECIO.as("PRECIO"),
                        DSL.inline(PriceType.INDIVIDUAL.name()).as(TIPO_ENTRADA)
                )
                .from(CPANEL_ASIGNACION_ZONA_PRECIOS)
                .innerJoin(CPANEL_TARIFA).on(CPANEL_ASIGNACION_ZONA_PRECIOS.IDTARIFA.eq(CPANEL_TARIFA.IDTARIFA))
                .innerJoin(CPANEL_ZONA_PRECIOS_CONFIG).on(CPANEL_ZONA_PRECIOS_CONFIG.IDZONA.eq(CPANEL_ASIGNACION_ZONA_PRECIOS.IDZONA))
                .innerJoin(CPANEL_CONFIG_RECINTO).on(CPANEL_CONFIG_RECINTO.IDCONFIGURACION.eq(CPANEL_ZONA_PRECIOS_CONFIG.IDCONFIGURACION))
                .innerJoin(Tables.CPANEL_EVENTO).on(CPANEL_CONFIG_RECINTO.IDEVENTO.eq(Tables.CPANEL_EVENTO.IDEVENTO))
                .where(Tables.CPANEL_EVENTO.IDEVENTO.eq(eventId.intValue()));

        var groupQuery = dsl.select(
                        Tables.CPANEL_EVENTO.IDEVENTO,
                        CPANEL_CONFIG_RECINTO.IDCONFIGURACION,
                        CPANEL_CONFIG_RECINTO.NOMBRECONFIGURACION,
                        CPANEL_ZONA_PRECIOS_CONFIG.IDZONA,
                        CPANEL_ZONA_PRECIOS_CONFIG.CODIGO,
                        CPANEL_ZONA_PRECIOS_CONFIG.DESCRIPCION,
                        CPANEL_ZONA_PRECIOS_CONFIG.PRIORIDAD,
                        CPANEL_ZONA_PRECIOS_CONFIG.COLOR,
                        CPANEL_ZONA_PRECIOS_CONFIG.RESTRICTIVEACCESS,
                        CPANEL_PRECIOS_GRUPOS.IDTARIFA,
                        CPANEL_TARIFA.NOMBRE,
                        CPANEL_TARIFA.DEFECTO,
                        CPANEL_PRECIOS_GRUPOS.PRECIO.as("PRECIO"),
                        DSL.inline(PriceType.GROUP.name()).as("TIPO_ENTRADA")
                )
                .from(CPANEL_PRECIOS_GRUPOS)
                .innerJoin(CPANEL_TARIFA).on(CPANEL_PRECIOS_GRUPOS.IDTARIFA.eq(CPANEL_TARIFA.IDTARIFA))
                .innerJoin(CPANEL_ZONA_PRECIOS_CONFIG).on(CPANEL_ZONA_PRECIOS_CONFIG.IDZONA.eq(CPANEL_PRECIOS_GRUPOS.IDZONA))
                .innerJoin(CPANEL_CONFIG_RECINTO).on(CPANEL_CONFIG_RECINTO.IDCONFIGURACION.eq(CPANEL_ZONA_PRECIOS_CONFIG.IDCONFIGURACION))
                .innerJoin(Tables.CPANEL_EVENTO).on(CPANEL_CONFIG_RECINTO.IDEVENTO.eq(Tables.CPANEL_EVENTO.IDEVENTO))
                .where(Tables.CPANEL_EVENTO.IDEVENTO.eq(eventId.intValue()));

        var finalQuery = switch (priceType) {
            case INDIVIDUAL -> individualQuery;
            case GROUP -> groupQuery;
            default -> individualQuery.unionAll(groupQuery);
        };

        return finalQuery.fetch().map(r -> {
            EventPriceRecord eventPrice = new EventPriceRecord();
            eventPrice.setEventId(r.get(Tables.CPANEL_EVENTO.IDEVENTO));
            eventPrice.setVenueConfigId(r.get(CPANEL_CONFIG_RECINTO.IDCONFIGURACION));
            eventPrice.setVenueConfigName(r.get(CPANEL_CONFIG_RECINTO.NOMBRECONFIGURACION));
            eventPrice.setPriceZoneId(r.get(CPANEL_ZONA_PRECIOS_CONFIG.IDZONA));
            eventPrice.setPriceZoneCode(r.get(CPANEL_ZONA_PRECIOS_CONFIG.CODIGO));
            eventPrice.setPriceZoneDescription(r.get(CPANEL_ZONA_PRECIOS_CONFIG.DESCRIPCION));
            eventPrice.setRateId(r.get(CPANEL_TARIFA.IDTARIFA));
            eventPrice.setRateName(r.get(CPANEL_TARIFA.NOMBRE));
            eventPrice.setRateDefault(CommonUtils.isTrue(r.get(CPANEL_TARIFA.DEFECTO)));
            eventPrice.setPrice(NumberUtils.zeroIfNull(r.get("PRECIO", Double.class)));
            eventPrice.setPriceZonePriority(NumberUtils.zeroIfNull(r.get(CPANEL_ZONA_PRECIOS_CONFIG.PRIORIDAD).longValue()));
            eventPrice.setPriceZoneColor(r.get(CPANEL_ZONA_PRECIOS_CONFIG.COLOR));
            eventPrice.setPriceZoneRestrictiveAccess(r.get(CPANEL_ZONA_PRECIOS_CONFIG.RESTRICTIVEACCESS));
            eventPrice.setPriceType(PriceType.fromString((String) r.get(TIPO_ENTRADA)));
            return eventPrice;
        });
    }

    public List<EventPriceRecord> getVenueTemplatePrices(Integer venueTemplateId) {
        return getVenueTemplatePrices(venueTemplateId, null, null, null);
    }

    public List<EventPriceRecord> getVenueTemplatePrices(Integer venueTemplateId, Integer eventId, List<Long> sessionIdList, List<Integer> rateGroupList, List<Integer> rateGroupProductList) {

        SelectFieldOrAsterisk[] fields = {CPANEL_ZONA_PRECIOS_CONFIG.IDZONA,
                CPANEL_ZONA_PRECIOS_CONFIG.CODIGO,
                CPANEL_ZONA_PRECIOS_CONFIG.DESCRIPCION,
                CPANEL_TARIFA.IDTARIFA,
                CPANEL_TARIFA.NOMBRE,
                CPANEL_TARIFA.IDEVENTO,
                CPANEL_GRUPO_TARIFA.IDGRUPOTARIFA,
                CPANEL_GRUPO_TARIFA.NOMBRE,
                CPANEL_ZONA_PRECIOS_CONFIG.COLOR
        };
        //Fields query principal
        List<SelectFieldOrAsterisk> selectedFields = Arrays.stream(fields).collect(Collectors.toList());
        selectedFields.add(CPANEL_ASIGNACION_ZONA_PRECIOS.PRECIO);
        selectedFields.add(DSL.inline(PriceType.INDIVIDUAL.name()).as(TIPO_ENTRADA));

        //Fiels query union
        List<SelectFieldOrAsterisk> selectedFieldsUnion = Arrays.stream(fields).collect(Collectors.toList());
        selectedFieldsUnion.add(CPANEL_PRECIOS_GRUPOS.PRECIO);
        selectedFieldsUnion.add(DSL.inline(PriceType.GROUP.name()).as(TIPO_ENTRADA));

        // Query principal
        SelectJoinStep<Record> query = dsl.select(selectedFields)
                .from(CPANEL_ASIGNACION_ZONA_PRECIOS)
                .innerJoin(CPANEL_ZONA_PRECIOS_CONFIG).on(CPANEL_ASIGNACION_ZONA_PRECIOS.IDZONA.eq(CPANEL_ZONA_PRECIOS_CONFIG.IDZONA))
                .innerJoin(CPANEL_TARIFA).on(CPANEL_ASIGNACION_ZONA_PRECIOS.IDTARIFA.eq(CPANEL_TARIFA.IDTARIFA))
                .innerJoin(CPANEL_TARIFA_GRUPO_TARIFA).on(CPANEL_TARIFA_GRUPO_TARIFA.IDTARIFA.eq(CPANEL_TARIFA.IDTARIFA))
                .innerJoin(CPANEL_GRUPO_TARIFA).on(CPANEL_GRUPO_TARIFA.IDGRUPOTARIFA.eq(CPANEL_TARIFA_GRUPO_TARIFA.IDGRUPOTARIFA));

        // Query Union
        SelectJoinStep<Record> queryUnion = dsl.select(selectedFieldsUnion)
                .from(CPANEL_PRECIOS_GRUPOS)
                .innerJoin(CPANEL_ZONA_PRECIOS_CONFIG).on(CPANEL_PRECIOS_GRUPOS.IDZONA.eq(CPANEL_ZONA_PRECIOS_CONFIG.IDZONA))
                .innerJoin(CPANEL_TARIFA).on(CPANEL_PRECIOS_GRUPOS.IDTARIFA.eq(CPANEL_TARIFA.IDTARIFA))
                .innerJoin(CPANEL_TARIFA_GRUPO_TARIFA).on(CPANEL_TARIFA_GRUPO_TARIFA.IDTARIFA.eq(CPANEL_TARIFA.IDTARIFA))
                .innerJoin(CPANEL_GRUPO_TARIFA).on(CPANEL_GRUPO_TARIFA.IDGRUPOTARIFA.eq(CPANEL_TARIFA_GRUPO_TARIFA.IDGRUPOTARIFA));

        //joins for filters
        if (CollectionUtils.isNotEmpty(sessionIdList)) {
            query.innerJoin(CPANEL_SESION_TARIFA).on(CPANEL_SESION_TARIFA.IDTARIFA.eq(CPANEL_TARIFA.IDTARIFA));
            queryUnion.innerJoin(CPANEL_SESION_TARIFA).on(CPANEL_SESION_TARIFA.IDTARIFA.eq(CPANEL_TARIFA.IDTARIFA));
        }

        queryUnion.where(CPANEL_ZONA_PRECIOS_CONFIG.IDCONFIGURACION.eq(venueTemplateId));

        if(Objects.nonNull(eventId)) {
            queryUnion.where().and(CPANEL_TARIFA.IDEVENTO.eq(eventId));
        }

        query.where(buildWhereConditionVenueTemplatePrices(venueTemplateId, eventId, sessionIdList, rateGroupList, rateGroupProductList, RateGroupType.RATE));
        query.unionAll(queryUnion);
        return query.fetch().map(this::convertToEventPriceRecord);
    }

    public List<EventPriceRecord> getVenueTemplatePrices(Integer venueTemplateId, Integer eventId, List<Long> sessionIdList, List<Integer> rateGroupList) {

        SelectFieldOrAsterisk[] fields = {CPANEL_ZONA_PRECIOS_CONFIG.IDZONA,
                CPANEL_ZONA_PRECIOS_CONFIG.CODIGO,
                CPANEL_ZONA_PRECIOS_CONFIG.DESCRIPCION,
                CPANEL_TARIFA.IDTARIFA,
                CPANEL_TARIFA.NOMBRE,
                CPANEL_TARIFA.IDEVENTO,
                CPANEL_GRUPO_TARIFA.IDGRUPOTARIFA,
                CPANEL_GRUPO_TARIFA.NOMBRE,
                CPANEL_ZONA_PRECIOS_CONFIG.COLOR
                };
        //Fields query principal
        List<SelectFieldOrAsterisk> selectedFields = Arrays.stream(fields).collect(Collectors.toList());
        selectedFields.add(CPANEL_ASIGNACION_ZONA_PRECIOS.PRECIO);
        selectedFields.add(DSL.inline(PriceType.INDIVIDUAL.name()).as(TIPO_ENTRADA));

        //Fiels query union
        List<SelectFieldOrAsterisk> selectedFieldsUnion = Arrays.stream(fields).collect(Collectors.toList());
        selectedFieldsUnion.add(CPANEL_PRECIOS_GRUPOS.PRECIO);
        selectedFieldsUnion.add(DSL.inline(PriceType.GROUP.name()).as(TIPO_ENTRADA));


        // Query principal
        SelectJoinStep<Record> query = dsl.select(selectedFields)
                .from(CPANEL_ASIGNACION_ZONA_PRECIOS)
                .innerJoin(CPANEL_ZONA_PRECIOS_CONFIG).on(CPANEL_ASIGNACION_ZONA_PRECIOS.IDZONA.eq(CPANEL_ZONA_PRECIOS_CONFIG.IDZONA))
                .innerJoin(CPANEL_TARIFA).on(CPANEL_ASIGNACION_ZONA_PRECIOS.IDTARIFA.eq(CPANEL_TARIFA.IDTARIFA))
                .leftJoin(CPANEL_GRUPO_TARIFA).on(CPANEL_GRUPO_TARIFA.IDGRUPOTARIFA.eq(CPANEL_TARIFA.IDGRUPOTARIFA));


        // Query Union
        SelectJoinStep<Record> queryUnion = dsl.select(selectedFieldsUnion)
                .from(CPANEL_PRECIOS_GRUPOS)
                .innerJoin(CPANEL_ZONA_PRECIOS_CONFIG).on(CPANEL_PRECIOS_GRUPOS.IDZONA.eq(CPANEL_ZONA_PRECIOS_CONFIG.IDZONA))
                .innerJoin(CPANEL_TARIFA).on(CPANEL_PRECIOS_GRUPOS.IDTARIFA.eq(CPANEL_TARIFA.IDTARIFA))
                .leftJoin(CPANEL_GRUPO_TARIFA).on(CPANEL_GRUPO_TARIFA.IDGRUPOTARIFA.eq(CPANEL_TARIFA.IDGRUPOTARIFA));

        //joins for filters
        if (CollectionUtils.isNotEmpty(sessionIdList)) {
            query.innerJoin(CPANEL_SESION_TARIFA).on(CPANEL_SESION_TARIFA.IDTARIFA.eq(CPANEL_TARIFA.IDTARIFA));
            queryUnion.innerJoin(CPANEL_SESION_TARIFA).on(CPANEL_SESION_TARIFA.IDTARIFA.eq(CPANEL_TARIFA.IDTARIFA));
        }

        queryUnion.where(CPANEL_ZONA_PRECIOS_CONFIG.IDCONFIGURACION.eq(venueTemplateId));

        if(Objects.nonNull(eventId)) {
            queryUnion.where().and(CPANEL_TARIFA.IDEVENTO.eq(eventId));
        }


        query.where(buildWhereConditionVenueTemplatePrices(venueTemplateId, eventId, sessionIdList, rateGroupList));
        query.unionAll(queryUnion);
        return query.fetch().map(this::convertToEventPriceRecord);
    }

    private EventPriceRecord convertToEventPriceRecord(Record recordFetched) {
        EventPriceRecord eventPrice = new EventPriceRecord();
        eventPrice.setPriceZoneId(recordFetched.get(CPANEL_ZONA_PRECIOS_CONFIG.IDZONA));
        eventPrice.setPriceZoneCode(recordFetched.get(CPANEL_ZONA_PRECIOS_CONFIG.CODIGO));
        eventPrice.setPriceZoneDescription(recordFetched.get(CPANEL_ZONA_PRECIOS_CONFIG.DESCRIPCION));
        eventPrice.setRateId(recordFetched.get(CPANEL_TARIFA.IDTARIFA));
        eventPrice.setRateName(recordFetched.get(CPANEL_TARIFA.NOMBRE));
        eventPrice.setEventId(recordFetched.get(CPANEL_TARIFA.IDEVENTO));
        eventPrice.setPrice(NumberUtils.zeroIfNull(recordFetched.get(CPANEL_ASIGNACION_ZONA_PRECIOS.PRECIO)));
        eventPrice.setPriceType(PriceType.fromString((String) recordFetched.get(TIPO_ENTRADA)));
        eventPrice.setPriceZoneColor(recordFetched.get(CPANEL_ZONA_PRECIOS_CONFIG.COLOR));

        if (recordFetched.get(CPANEL_GRUPO_TARIFA.IDGRUPOTARIFA) != null) {
            eventPrice.setRateGroupId(recordFetched.get(CPANEL_GRUPO_TARIFA.IDGRUPOTARIFA));
        }
        if (recordFetched.get(CPANEL_GRUPO_TARIFA.NOMBRE) != null) {
            eventPrice.setRateGroupName(recordFetched.get(CPANEL_GRUPO_TARIFA.NOMBRE));
        }
        return eventPrice;
    }

    private Condition buildWhereConditionVenueTemplatePrices(Integer venueTemplateId, Integer eventId, List<Long> sessionIdList, List<Integer> groupRateList) {
        Condition condition = DSL.trueCondition();
        condition = condition.and(CPANEL_ZONA_PRECIOS_CONFIG.IDCONFIGURACION.eq(venueTemplateId));

        if (CollectionUtils.isNotEmpty(sessionIdList)) {
            condition = condition.and(CPANEL_SESION_TARIFA.IDSESION.in(sessionIdList));
        }
        if (CollectionUtils.isNotEmpty(groupRateList)) {
            condition = condition.and(CPANEL_TARIFA.IDGRUPOTARIFA.in(groupRateList));
        }
        if(Objects.nonNull(eventId)) {
            condition = condition.and(CPANEL_TARIFA.IDEVENTO.eq(eventId));
        }
        return condition;
    }

    private Condition buildWhereConditionVenueTemplatePrices(Integer venueTemplateId, Integer eventId, List<Long> sessionIdList, List<Integer> groupRateList, List<Integer> groupRateProductList, RateGroupType type) {
        Condition condition = DSL.trueCondition();
        condition = condition.and(CPANEL_ZONA_PRECIOS_CONFIG.IDCONFIGURACION.eq(venueTemplateId));

        if (CollectionUtils.isNotEmpty(sessionIdList)) {
            condition = condition.and(CPANEL_SESION_TARIFA.IDSESION.in(sessionIdList));
        }
        if (CollectionUtils.isNotEmpty(groupRateProductList)) {
            condition = condition.and(CPANEL_TARIFA_GRUPO_TARIFA.IDTARIFA.in(
                    dsl.select(CPANEL_TARIFA_GRUPO_TARIFA.IDTARIFA)
                    .from(CPANEL_TARIFA_GRUPO_TARIFA)
                    .where(CPANEL_TARIFA_GRUPO_TARIFA.IDGRUPOTARIFA.in(groupRateProductList))
            ));
        }
        if(Objects.nonNull(eventId)) {
            condition = condition.and(CPANEL_TARIFA.IDEVENTO.eq(eventId));
        }
        if(Objects.nonNull(type)) {
            condition = condition.and(CPANEL_GRUPO_TARIFA.TIPO.eq(type.getId().byteValue()));
        }
        if (CollectionUtils.isNotEmpty(groupRateList)) {
            condition = condition.and(CPANEL_GRUPO_TARIFA.IDGRUPOTARIFA.in(groupRateList));
        }

        return condition;
    }

    public int updateIndividual(Integer priceTypeId, Integer rateId, Double price, Integer idExerno) {
        return dsl.update(CPANEL_ASIGNACION_ZONA_PRECIOS).
                set(CPANEL_ASIGNACION_ZONA_PRECIOS.PRECIO, price).
                set(CPANEL_ASIGNACION_ZONA_PRECIOS.IDEXTERNO, idExerno).
                where(CPANEL_ASIGNACION_ZONA_PRECIOS.IDZONA.eq(priceTypeId)).
                and(CPANEL_ASIGNACION_ZONA_PRECIOS.IDTARIFA.eq(rateId)).
                execute();
    }

    public int updateGroup(Integer priceTypeId, Integer rateId, Double price) {
        return dsl.update(CPANEL_PRECIOS_GRUPOS).
                set(CPANEL_PRECIOS_GRUPOS.PRECIO, price).
                where(CPANEL_PRECIOS_GRUPOS.IDZONA.eq(priceTypeId)).
                and(CPANEL_PRECIOS_GRUPOS.IDTARIFA.eq(rateId)).
                execute();
    }

    public Integer findGroup(Integer priceTypeId, Integer rateId) {
        Object foundId = dsl.select(CPANEL_PRECIOS_GRUPOS.IDZONA).from(CPANEL_PRECIOS_GRUPOS).
                where(CPANEL_PRECIOS_GRUPOS.IDZONA.eq(priceTypeId)).
                and(CPANEL_PRECIOS_GRUPOS.IDTARIFA.eq(rateId)).
                fetchOne();
        return foundId != null ? ((Record) foundId).into(Integer.class) : null;
    }

    public int addGroup(Integer priceTypeId, Integer rateId, Double price) {
        return dsl.insertInto(CPANEL_PRECIOS_GRUPOS).
                set(CPANEL_PRECIOS_GRUPOS.IDZONA, priceTypeId).
                set(CPANEL_PRECIOS_GRUPOS.IDTARIFA, rateId).
                set(CPANEL_PRECIOS_GRUPOS.PRECIO, price).
                execute();
    }

    public void bulkInsertIndividual(List<CpanelAsignacionZonaPreciosRecord> assigmentPriceZoneList) {
        InsertSetStep<CpanelAsignacionZonaPreciosRecord> insertSetStep = dsl.insertInto(CPANEL_ASIGNACION_ZONA_PRECIOS);
        for (int i = 0; i < assigmentPriceZoneList.size() - 1; i++) {
            insertSetStep.set(assigmentPriceZoneList.get(i)).newRecord();
        }
        insertSetStep.set(assigmentPriceZoneList.get(assigmentPriceZoneList.size() - 1)).execute();
    }
}
