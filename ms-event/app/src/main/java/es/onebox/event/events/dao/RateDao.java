/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.onebox.event.events.dao;

import es.onebox.event.common.utils.JooqUtils;
import es.onebox.event.events.dao.record.RateRecord;
import es.onebox.event.events.dao.record.RateWithTranslationRecord;
import es.onebox.event.events.dto.RateGroupType;
import es.onebox.event.events.request.RatesFilter;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionTarifaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaRecord;
import es.onebox.jooq.dao.DaoImpl;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static es.onebox.jooq.cpanel.Tables.CPANEL_EXTERNAL_RATE_TYPE;
import static es.onebox.jooq.cpanel.Tables.CPANEL_GRUPO_TARIFA;
import static es.onebox.jooq.cpanel.Tables.CPANEL_SESION;
import static es.onebox.jooq.cpanel.Tables.CPANEL_TARIFA_GRUPO_TARIFA;
import static es.onebox.jooq.cpanel.tables.CpanelDescPorIdioma.CPANEL_DESC_POR_IDIOMA;
import static es.onebox.jooq.cpanel.tables.CpanelIdioma.CPANEL_IDIOMA;
import static es.onebox.jooq.cpanel.tables.CpanelItemDescSequence.CPANEL_ITEM_DESC_SEQUENCE;
import static es.onebox.jooq.cpanel.tables.CpanelSesionTarifa.CPANEL_SESION_TARIFA;
import static es.onebox.jooq.cpanel.tables.CpanelTarifa.CPANEL_TARIFA;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

@Repository
public class RateDao extends DaoImpl<CpanelTarifaRecord, Integer> {

    private static final Field[] FIELDS_EVENT_RATE = new Field[]{
            CPANEL_TARIFA.IDTARIFA,
            CPANEL_TARIFA.IDEVENTO,
            CPANEL_TARIFA.NOMBRE,
            CPANEL_TARIFA.DESCRIPCION,
            CPANEL_TARIFA.DEFECTO,
            CPANEL_TARIFA.ACCESORESTRICTIVO,
            CPANEL_TARIFA.ELEMENTOCOMDESCRIPCION,
            CPANEL_TARIFA.IDGRUPOTARIFA,
            CPANEL_IDIOMA.CODIGO.as("codigoIdioma"),
            CPANEL_DESC_POR_IDIOMA.DESCRIPCION.as("traduccion"),
            CPANEL_GRUPO_TARIFA.NOMBRE.as("nombreGrupoTarifa"),
            CPANEL_TARIFA.POSITION,
            CPANEL_EXTERNAL_RATE_TYPE.ID.as("externalRateTypeId"),
            CPANEL_EXTERNAL_RATE_TYPE.CODE.as("externalRateTypeCode"),
            CPANEL_EXTERNAL_RATE_TYPE.NAME.as("externalRateTypeName")
    };

    private static final Field[] FIELDS_RATE = new Field[]{
            CPANEL_TARIFA.IDTARIFA,
            CPANEL_TARIFA.IDEVENTO,
            CPANEL_TARIFA.NOMBRE,
            CPANEL_TARIFA.DESCRIPCION,
            CPANEL_TARIFA.DEFECTO,
            CPANEL_TARIFA.ACCESORESTRICTIVO,
            CPANEL_TARIFA.ELEMENTOCOMDESCRIPCION,
            CPANEL_TARIFA.IDGRUPOTARIFA,
            CPANEL_IDIOMA.CODIGO.as("codigoIdioma"),
            CPANEL_DESC_POR_IDIOMA.DESCRIPCION.as("traduccion"),
            CPANEL_TARIFA.POSITION,
            CPANEL_EXTERNAL_RATE_TYPE.ID.as("externalRateTypeId"),
            CPANEL_EXTERNAL_RATE_TYPE.CODE.as("externalRateTypeCode"),
            CPANEL_EXTERNAL_RATE_TYPE.NAME.as("externalRateTypeName")
    };

    protected RateDao() {
        super(Tables.CPANEL_TARIFA);
    }

    public List<CpanelTarifaRecord> search(List<Integer> rateIds) {
        return dsl.select()
                .from(Tables.CPANEL_TARIFA)
                .where(Tables.CPANEL_TARIFA.IDTARIFA.in(rateIds))
                .fetchInto(CpanelTarifaRecord.class);
    }

    public Long countByEventId(Integer eventId) {
        return dsl.selectCount()
                .from(CPANEL_TARIFA)
                .where(CPANEL_TARIFA.IDEVENTO.eq(eventId))
                .fetchOne(0, Long.class);
    }

    public Long countBySessionId(Integer sesionId) {
        return dsl.selectCount()
                .from(CPANEL_TARIFA)
                .leftJoin(CPANEL_SESION_TARIFA).on(CPANEL_TARIFA.IDTARIFA.eq(CPANEL_SESION_TARIFA.IDTARIFA))
                .where(CPANEL_SESION_TARIFA.IDSESION.eq(sesionId))
                .fetchOne(0, Long.class);
    }

    public CpanelTarifaRecord getEventRate(Integer eventId, Integer rateId) {
        return dsl.select().from(CPANEL_TARIFA).
                where(CPANEL_TARIFA.IDEVENTO.eq(eventId)).
                and(CPANEL_TARIFA.IDTARIFA.eq(rateId)).
                fetchOneInto(CpanelTarifaRecord.class);
    }

    public List<CpanelSesionTarifaRecord> getSessionRates(Integer sessionId) {
        return dsl.select().from(CPANEL_SESION_TARIFA).
                where(CPANEL_SESION_TARIFA.IDSESION.eq(sessionId)).
                fetch().into(CpanelSesionTarifaRecord.class);
    }

    public CpanelTarifaRecord getSeasonTicketRate(Integer seasonTicketId, Integer rateId) {
        return getEventRate(seasonTicketId, rateId);
    }

    public List<CpanelTarifaRecord> getEventRates(Integer eventId) {
        return dsl.select().from(CPANEL_TARIFA).
                where(CPANEL_TARIFA.IDEVENTO.eq(eventId)).
                fetch().into(CpanelTarifaRecord.class);
    }

    public List<CpanelTarifaRecord> getRatesBySession(Integer sessionId) {
        return dsl.select(Tables.CPANEL_TARIFA.IDTARIFA, Tables.CPANEL_TARIFA.NOMBRE, Tables.CPANEL_TARIFA.DEFECTO, Tables.CPANEL_TARIFA.EXTERNALRATETYPEID)
                .from(Tables.CPANEL_TARIFA)
                .innerJoin(CPANEL_SESION).on(CPANEL_SESION.IDEVENTO.eq(Tables.CPANEL_TARIFA.IDEVENTO))
                .where(CPANEL_SESION.IDSESION.eq(sessionId))
                .fetchInto(CpanelTarifaRecord.class);
    }

    public List<CpanelTarifaRecord> getSeasonTicketRates(Integer seasonTicketId) {
        return getEventRates(seasonTicketId);
    }

    public Collection<RateRecord> getRatesBySessionId(Integer sesionId, Long limit, Long offset) {
        limit = Optional.ofNullable(limit).orElse((long) Integer.MAX_VALUE);
        offset = Optional.ofNullable(offset).orElse(0L);
        return dsl
                .select(CPANEL_TARIFA.IDTARIFA,
                        CPANEL_TARIFA.IDEVENTO,
                        CPANEL_TARIFA.NOMBRE,
                        CPANEL_TARIFA.DESCRIPCION,
                        CPANEL_SESION_TARIFA.DEFECTO,
                        CPANEL_TARIFA.ACCESORESTRICTIVO,
                        CPANEL_GRUPO_TARIFA.NOMBRE.as("nombreGrupoTarifa"),
                        CPANEL_GRUPO_TARIFA.IDGRUPOTARIFA)
                .from(CPANEL_TARIFA)
                .leftJoin(CPANEL_SESION_TARIFA).on(CPANEL_SESION_TARIFA.IDTARIFA.eq(CPANEL_TARIFA.IDTARIFA))
                .leftJoin(CPANEL_GRUPO_TARIFA).on(CPANEL_GRUPO_TARIFA.IDGRUPOTARIFA.eq(CPANEL_TARIFA.IDGRUPOTARIFA))
                .where(CPANEL_SESION_TARIFA.IDSESION.eq(sesionId))
                .limit(limit.intValue())
                .offset(offset.intValue())
                .fetchInto(RateRecord.class);
    }

    public Collection<RateRecord> searchRatesByFilter(RatesFilter filter) {
        Long limit = Optional.ofNullable(filter.getLimit()).orElse((long) Integer.MAX_VALUE);
        Long offset = Optional.ofNullable(filter.getOffset()).orElse(0L);
        SelectJoinStep query = dsl
                .select(CPANEL_TARIFA.IDTARIFA,
                        CPANEL_TARIFA.IDEVENTO,
                        CPANEL_TARIFA.NOMBRE,
                        CPANEL_TARIFA.DESCRIPCION,
                        CPANEL_TARIFA.ACCESORESTRICTIVO)
                .from(CPANEL_TARIFA);
        buildJoinClauses(query, filter);
        return query
                .where(buildWhereClause(query, filter))
                .limit(limit.intValue())
                .offset(offset.intValue())
                .fetchInto(RateRecord.class);
    }

    public Long countByRatesFilter(RatesFilter filter) {
        SelectJoinStep query = dsl.selectCount().from(CPANEL_TARIFA);
        buildJoinClauses(query, filter);
        return query
                .where(buildWhereClause(query, filter))
                .fetchOne()
                .into(Long.class);
    }

    private Condition buildWhereClause(SelectJoinStep query, RatesFilter filter) {
        Condition conditions = DSL.trueCondition();
        if (filter.getType() != null){
            conditions = JooqUtils.addConditionEquals(conditions, CPANEL_GRUPO_TARIFA.TIPO, filter.getType().getId().byteValue());
        }

        if (StringUtils.isNotBlank(filter.getExternalDescription())) {
            conditions = JooqUtils.addConditionEquals(conditions, CPANEL_GRUPO_TARIFA.DESCRIPCIONEXTERNA, filter.getExternalDescription());
        }
        return conditions;
    }

    private void buildJoinClauses(SelectJoinStep query, RatesFilter filter) {
        // CPANEL_TARIFA is related to CPANEL_GRUPO_TARIFA in two different ways:
        // 1. Recent developments relate them in a many-to-many relation, involving Type to describe grouping type
        // 2. AVET relate them in many-to-one relation, maintained for compatibility
        if (filter.getType() != null){
            query.innerJoin(CPANEL_TARIFA_GRUPO_TARIFA)
                    .on(CPANEL_TARIFA.IDTARIFA.eq(CPANEL_TARIFA_GRUPO_TARIFA.IDTARIFA))
                    .innerJoin(CPANEL_GRUPO_TARIFA)
                    .on(CPANEL_GRUPO_TARIFA.IDGRUPOTARIFA.eq(CPANEL_TARIFA_GRUPO_TARIFA.IDGRUPOTARIFA));
        } else if (StringUtils.isNotBlank(filter.getExternalDescription())) {
            query.innerJoin(CPANEL_GRUPO_TARIFA).on(CPANEL_GRUPO_TARIFA.IDGRUPOTARIFA.eq(CPANEL_TARIFA.IDGRUPOTARIFA));
        }
    }

    public RateRecord getRatesByDefaultSessionId(Integer sesionId, Long limit, Long offset) {
        limit = Optional.ofNullable(limit).orElse((long) Integer.MAX_VALUE);
        offset = Optional.ofNullable(offset).orElse(0L);
        return dsl.select(CPANEL_TARIFA.IDTARIFA,
                        CPANEL_TARIFA.IDEVENTO,
                        CPANEL_TARIFA.NOMBRE,
                        CPANEL_TARIFA.IDGRUPOTARIFA,
                        CPANEL_TARIFA.DESCRIPCION,
                        CPANEL_SESION_TARIFA.DEFECTO,
                        CPANEL_SESION_TARIFA.DEFECTO,
                        CPANEL_TARIFA.ACCESORESTRICTIVO)
                .from(CPANEL_TARIFA)
                .leftJoin(CPANEL_SESION_TARIFA).on(CPANEL_SESION_TARIFA.IDTARIFA.eq(CPANEL_TARIFA.IDTARIFA))
                .where(CPANEL_SESION_TARIFA.IDSESION.eq(sesionId))
                .and(CPANEL_SESION_TARIFA.DEFECTO.isTrue())
                .limit(limit.intValue())
                .offset(offset.intValue())
                .fetchOneInto(RateRecord.class);
    }

    public List<RateRecord> getEventRatesByEventId(Integer eventId, Long limit, Long offset) {
        return getRatesByEventId(eventId, limit, offset, FIELDS_EVENT_RATE);
    }

    public List<RateRecord> getRatesByEventId(Integer eventId) {
        return getRatesByEventId(eventId, null, null, FIELDS_RATE);
    }

    public Collection<RateRecord> getSeasonTicketRates(Integer seasonTicketId, Long limit, Long offset) {
        return getRatesByEventId(seasonTicketId, limit, offset, FIELDS_RATE);
    }

    public List<RateRecord> getRatesByEventId(Integer eventId, Long limit, Long offset, Field[] fields) {

        List<RateWithTranslationRecord> rateWithTranslationRecords = dsl
                .select(fields)
                .from(dsl.select(CPANEL_TARIFA.IDTARIFA.as("cropTarifaIds"))
                        .from(CPANEL_TARIFA)
                        .where(CPANEL_TARIFA.IDEVENTO.eq(eventId))
                        .orderBy(CPANEL_TARIFA.IDTARIFA)
                        .limit(parseLongToInt(limit, Integer.MAX_VALUE))
                        .offset(parseLongToInt(offset, 0)))
                .leftJoin(CPANEL_TARIFA)
                .on(DSL.field("cropTarifaIds").eq(CPANEL_TARIFA.IDTARIFA))
                .leftJoin(CPANEL_ITEM_DESC_SEQUENCE)
                .on(CPANEL_TARIFA.ELEMENTOCOMDESCRIPCION.eq(CPANEL_ITEM_DESC_SEQUENCE.IDITEM))
                .leftJoin(CPANEL_DESC_POR_IDIOMA)
                .on(CPANEL_ITEM_DESC_SEQUENCE.IDITEM.eq(CPANEL_DESC_POR_IDIOMA.IDITEM))
                .leftJoin(CPANEL_IDIOMA)
                .on(CPANEL_DESC_POR_IDIOMA.IDIDIOMA.eq(CPANEL_IDIOMA.IDIDIOMA))
                .leftJoin(CPANEL_GRUPO_TARIFA)
                .on(CPANEL_TARIFA.IDGRUPOTARIFA.eq(CPANEL_GRUPO_TARIFA.IDGRUPOTARIFA))
                .leftJoin(CPANEL_EXTERNAL_RATE_TYPE)
                .on(CPANEL_TARIFA.EXTERNALRATETYPEID.eq(CPANEL_EXTERNAL_RATE_TYPE.ID))
                .fetchInto(RateWithTranslationRecord.class);

        Collection<RateRecord> aggRecords = rateWithTranslationRecords.stream()
                .map(this::getRateRecord)
                .collect(Collectors.toConcurrentMap(
                        RateRecord::getIdTarifa,
                        Function.identity(),
                        (rateRecord1, rateRecord2) -> {
                            rateRecord1.getTranslations().putAll(rateRecord2.getTranslations());
                            return rateRecord1;
                        }
                )).values();

        return aggRecords.stream().sorted(Comparator.comparing(RateRecord::getNombre)).collect(Collectors.toList());
    }

    public List<CpanelTarifaRecord> getRatesByRateGroupId(Integer eventId, Integer rateGroupId) {
        return dsl.select(CPANEL_TARIFA.IDTARIFA,
                        CPANEL_TARIFA.NOMBRE,
                        CPANEL_TARIFA.IDGRUPOTARIFA
                ).from(CPANEL_TARIFA).
                where(CPANEL_TARIFA.IDEVENTO.eq(eventId)).
                and(CPANEL_TARIFA.IDGRUPOTARIFA.isNotNull()).
                and(CPANEL_TARIFA.IDGRUPOTARIFA.eq(rateGroupId)).
                fetch().into(CpanelTarifaRecord.class);
    }

    public List<CpanelTarifaRecord> getRatesByRateGroupIdAndType(Integer eventId, Integer rateGroupId, @NotNull RateGroupType type) {
        return dsl.select(CPANEL_TARIFA.IDTARIFA,
                CPANEL_TARIFA.NOMBRE,
                CPANEL_TARIFA.IDGRUPOTARIFA)
            .from(CPANEL_TARIFA)
            .innerJoin(CPANEL_TARIFA_GRUPO_TARIFA).on(CPANEL_TARIFA.IDTARIFA.eq(CPANEL_TARIFA_GRUPO_TARIFA.IDTARIFA))
            .innerJoin(CPANEL_GRUPO_TARIFA).on(CPANEL_GRUPO_TARIFA.IDGRUPOTARIFA.eq(CPANEL_TARIFA_GRUPO_TARIFA.IDGRUPOTARIFA))
            .where(CPANEL_TARIFA.IDEVENTO.eq(eventId))
            .and(CPANEL_GRUPO_TARIFA.TIPO.eq(type.getId().byteValue()))
            .and(CPANEL_GRUPO_TARIFA.IDGRUPOTARIFA.eq(rateGroupId))
            .fetch().into(CpanelTarifaRecord.class);
    }

    private int parseLongToInt(Long value, int defaultValue) {
        int result = Optional.ofNullable(value).map(Long::intValue).orElse(defaultValue);
        if (result == -1) {
            result = defaultValue;
        }
        return result;
    }

    private RateRecord getRateRecord(RateWithTranslationRecord rateWithTranslationRecord) {
        RateRecord rateRecord = new RateRecord();
        rateRecord.setIdTarifa(rateWithTranslationRecord.getIdTarifa());
        rateRecord.setIdEvento(rateWithTranslationRecord.getIdEvento());
        rateRecord.setNombre(rateWithTranslationRecord.getNombre());
        rateRecord.setDescripcion(rateWithTranslationRecord.getDescripcion());
        rateRecord.setDefecto(rateWithTranslationRecord.getDefecto());
        rateRecord.setAccesoRestrictivo(rateWithTranslationRecord.getAccesoRestrictivo());
        rateRecord.setElementoComDescripcion(rateWithTranslationRecord.getElementoComDescripcion());
        rateRecord.setIdGrupoTarifa(
                rateWithTranslationRecord.getIdGrupoTarifa() != null ?
                        Integer.valueOf(rateWithTranslationRecord.getIdGrupoTarifa()) : null
        );
        rateRecord.setNombreGrupoTarifa(rateWithTranslationRecord.getNombreGrupoTarifa());
        if (rateWithTranslationRecord.getCodigoIdioma() != null) {
            Map<String, String> translateMap = new HashMap<>();
            translateMap.put(
                    rateWithTranslationRecord.getCodigoIdioma(),
                    rateWithTranslationRecord.getTraduccion());
            rateRecord.setTranslations(translateMap);
        }
        rateRecord.setPosition(rateWithTranslationRecord.getPosition());
        rateRecord.setExternalRateTypeId(rateWithTranslationRecord.getExternalRateTypeId());
        rateRecord.setExternalRateTypeCode(rateWithTranslationRecord.getExternalRateTypeCode());
        rateRecord.setExternalRateTypeName(rateWithTranslationRecord.getExternalRateTypeName());
        return rateRecord;
    }

	public Map<Integer, List<CpanelTarifaRecord>> getRatesBySessionIds(List<Integer> sessionIds) {
		return dsl
				.select(Tables.CPANEL_TARIFA.IDTARIFA, Tables.CPANEL_TARIFA.NOMBRE, Tables.CPANEL_TARIFA.DEFECTO,
						Tables.CPANEL_TARIFA.EXTERNALRATETYPEID, CPANEL_SESION.IDSESION)
				.from(Tables.CPANEL_TARIFA)
				.innerJoin(CPANEL_SESION).on(CPANEL_SESION.IDEVENTO.eq(Tables.CPANEL_TARIFA.IDEVENTO))
				.where(CPANEL_SESION.IDSESION.in(sessionIds))
				.fetchGroups(
						r -> r.get(CPANEL_SESION.IDSESION),
						r -> r.into(CpanelTarifaRecord.class));
	}
}
