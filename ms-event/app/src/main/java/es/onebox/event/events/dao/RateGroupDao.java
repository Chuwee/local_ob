package es.onebox.event.events.dao;

import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.events.dao.record.RateGroupRecord;
import es.onebox.event.events.dao.record.RateGroupSessionRecord;
import es.onebox.event.events.dao.record.RateGroupWithTranslationRecord;
import es.onebox.event.events.dao.record.RateRecord;
import es.onebox.event.events.dto.RateGroupType;
import es.onebox.event.events.enums.SessionState;
import es.onebox.jooq.cpanel.Tables;
import static es.onebox.jooq.cpanel.Tables.CPANEL_GRUPO_TARIFA;
import static es.onebox.jooq.cpanel.Tables.CPANEL_TARIFA_GRUPO_TARIFA;
import static es.onebox.jooq.cpanel.tables.CpanelDescPorIdioma.CPANEL_DESC_POR_IDIOMA;
import static es.onebox.jooq.cpanel.tables.CpanelIdioma.CPANEL_IDIOMA;
import static es.onebox.jooq.cpanel.tables.CpanelItemDescSequence.CPANEL_ITEM_DESC_SEQUENCE;
import static es.onebox.jooq.cpanel.tables.CpanelSesion.CPANEL_SESION;
import static es.onebox.jooq.cpanel.tables.CpanelSesionTarifa.CPANEL_SESION_TARIFA;
import static es.onebox.jooq.cpanel.tables.CpanelTarifa.CPANEL_TARIFA;
import es.onebox.jooq.cpanel.tables.records.CpanelGrupoTarifaRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class RateGroupDao extends DaoImpl<CpanelGrupoTarifaRecord, Integer> {

    protected RateGroupDao() {
        super(CPANEL_GRUPO_TARIFA);
    }

    public Long countByEventId(Integer eventId) {
        return dsl.selectCount()
                .from(CPANEL_GRUPO_TARIFA)
                .where(CPANEL_GRUPO_TARIFA.IDEVENTO.eq(eventId))
                .fetchOne(0, Long.class);
    }

    public CpanelGrupoTarifaRecord getEventRate(Integer eventId, Integer rateGroupId) {
        return dsl.select().from(CPANEL_GRUPO_TARIFA).
                where(CPANEL_GRUPO_TARIFA.IDEVENTO.eq(eventId)).
                and(CPANEL_GRUPO_TARIFA.IDGRUPOTARIFA.eq(rateGroupId)).
                fetchOneInto(CpanelGrupoTarifaRecord.class);
    }

    public List<RateGroupSessionRecord> getSessionsDefaultRates(Integer eventId) {
        return dsl.select(CPANEL_SESION.IDSESION,
                        CPANEL_TARIFA.IDTARIFA,
                        CPANEL_TARIFA.NOMBRE,
                        CPANEL_TARIFA.IDGRUPOTARIFA
                        ).from(CPANEL_SESION).
                innerJoin(CPANEL_SESION_TARIFA).on(CPANEL_SESION_TARIFA.IDSESION.eq(CPANEL_SESION.IDSESION)).
                innerJoin(CPANEL_TARIFA).on(CPANEL_TARIFA.IDTARIFA.eq(CPANEL_SESION_TARIFA.IDTARIFA)).
                where(CPANEL_SESION.IDEVENTO.eq(eventId)).
                and(CPANEL_SESION_TARIFA.DEFECTO.isTrue()).
                and(Tables.CPANEL_SESION.ESTADO.ne(SessionState.DELETED.value())).
                and(CPANEL_TARIFA.IDGRUPOTARIFA.isNotNull()).
                fetch().into(RateGroupSessionRecord.class);
    }

    public CpanelGrupoTarifaRecord getDefaultGroupRateByEventId(Integer eventId) {
        org.jooq.Record cpanelGrupoTarifaRecord = dsl.select(
                    CPANEL_GRUPO_TARIFA.IDGRUPOTARIFA,
                    CPANEL_GRUPO_TARIFA.NOMBRE,
                    CPANEL_GRUPO_TARIFA.ELEMENTOCOMDESCRIPCION
                ).from(CPANEL_GRUPO_TARIFA).
                where(CPANEL_GRUPO_TARIFA.IDEVENTO.eq(eventId)).
                and(CPANEL_GRUPO_TARIFA.DEFECTO.isTrue()).
                fetchOne();
        return CommonUtils.ifNotNull(cpanelGrupoTarifaRecord, item -> item.into(CpanelGrupoTarifaRecord.class));
    }

    public List<RateGroupSessionRecord> getSessionsRatesByRateId(Integer eventId, Integer rateGroupId) {
        return dsl.select(CPANEL_SESION.IDSESION,
                        CPANEL_TARIFA.IDTARIFA,
                        CPANEL_TARIFA.NOMBRE,
                        CPANEL_TARIFA.IDGRUPOTARIFA
                ).from(CPANEL_SESION).
                innerJoin(CPANEL_SESION_TARIFA).on(CPANEL_SESION_TARIFA.IDSESION.eq(CPANEL_SESION.IDSESION)).
                innerJoin(CPANEL_TARIFA).on(CPANEL_TARIFA.IDTARIFA.eq(CPANEL_SESION_TARIFA.IDTARIFA)).
                where(CPANEL_SESION.IDEVENTO.eq(eventId)).
                and(Tables.CPANEL_SESION.ESTADO.ne(SessionState.DELETED.value())).
                and(CPANEL_TARIFA.IDGRUPOTARIFA.isNotNull()).
                and(CPANEL_TARIFA.IDGRUPOTARIFA.eq(rateGroupId)).
                fetch().into(RateGroupSessionRecord.class);
    }

    public List<RateGroupSessionRecord> getSessionsDefaultRatesBySessionId(Integer sessionId) {
        return dsl.select(CPANEL_SESION.IDSESION,
                        CPANEL_TARIFA.IDTARIFA,
                        CPANEL_TARIFA.NOMBRE,
                        CPANEL_TARIFA.IDGRUPOTARIFA
                ).from(CPANEL_SESION).
                innerJoin(CPANEL_SESION_TARIFA).on(CPANEL_SESION_TARIFA.IDSESION.eq(CPANEL_SESION.IDSESION)).
                innerJoin(CPANEL_TARIFA).on(CPANEL_TARIFA.IDTARIFA.eq(CPANEL_SESION_TARIFA.IDTARIFA)).
                where(CPANEL_SESION.IDSESION.eq(sessionId)).
                and(CPANEL_SESION_TARIFA.DEFECTO.isTrue()).
                and(Tables.CPANEL_SESION.ESTADO.ne(SessionState.DELETED.value())).
                fetch().into(RateGroupSessionRecord.class);
    }

    public List<CpanelGrupoTarifaRecord> getEventRates(Integer eventId) {
        return dsl.select().from(CPANEL_GRUPO_TARIFA).
                where(CPANEL_GRUPO_TARIFA.IDEVENTO.eq(eventId)).
                fetch().into(CpanelGrupoTarifaRecord.class);
    }

    public Collection<RateRecord> getRatesBySessionId(Integer sesionId, Long limit, Long offset) {
        limit = Optional.ofNullable(limit).orElse((long) Integer.MAX_VALUE);
        offset = Optional.ofNullable(offset).orElse(0L);
        return dsl
                .select(CPANEL_GRUPO_TARIFA.IDGRUPOTARIFA,
                        CPANEL_GRUPO_TARIFA.IDEVENTO,
                        CPANEL_GRUPO_TARIFA.NOMBRE,
                        CPANEL_SESION_TARIFA.DEFECTO)
                .from(CPANEL_GRUPO_TARIFA)
                .leftJoin(CPANEL_SESION_TARIFA).on(CPANEL_SESION_TARIFA.IDTARIFA.eq(CPANEL_GRUPO_TARIFA.IDGRUPOTARIFA))
                .where(CPANEL_SESION_TARIFA.IDSESION.eq(sesionId))
                .limit(limit.intValue())
                .offset(offset.intValue())
                .fetchInto(RateRecord.class);
    }

    public List<RateGroupRecord> getRatesGroupByEventId(Integer eventId) {
        return getRatesGroupByEventId(eventId, null, null, null);
    }

    public List<RateGroupRecord> getRatesGroupWithRates(Integer eventId) {

        List<RateGroupWithTranslationRecord> rateWithTranslationAndRatesRecords = dsl
                .select(CPANEL_GRUPO_TARIFA.IDGRUPOTARIFA,
                        CPANEL_GRUPO_TARIFA.IDEVENTO,
                        CPANEL_GRUPO_TARIFA.NOMBRE,
                        CPANEL_GRUPO_TARIFA.DEFECTO,
                        CPANEL_GRUPO_TARIFA.ELEMENTOCOMDESCRIPCION,
                        CPANEL_GRUPO_TARIFA.DESCRIPCIONEXTERNA,
                        CPANEL_IDIOMA.CODIGO.as("codigoIdioma"),
                        CPANEL_DESC_POR_IDIOMA.DESCRIPCION.as("traduccion"),
                        CPANEL_GRUPO_TARIFA.POSITION,
                        CPANEL_TARIFA.IDTARIFA,
                        CPANEL_GRUPO_TARIFA.TIPO)
                .from(CPANEL_GRUPO_TARIFA)
                .leftJoin(CPANEL_ITEM_DESC_SEQUENCE)
                .on(CPANEL_GRUPO_TARIFA.ELEMENTOCOMDESCRIPCION.eq(CPANEL_ITEM_DESC_SEQUENCE.IDITEM))
                .leftJoin(CPANEL_DESC_POR_IDIOMA)
                .on(CPANEL_ITEM_DESC_SEQUENCE.IDITEM.eq(CPANEL_DESC_POR_IDIOMA.IDITEM))
                .leftJoin(CPANEL_IDIOMA)
                .on(CPANEL_DESC_POR_IDIOMA.IDIDIOMA.eq(CPANEL_IDIOMA.IDIDIOMA))
                .join(CPANEL_TARIFA_GRUPO_TARIFA)
                .on(CPANEL_TARIFA_GRUPO_TARIFA.IDGRUPOTARIFA.eq(CPANEL_GRUPO_TARIFA.IDGRUPOTARIFA))
                .join(CPANEL_TARIFA)
                .on(CPANEL_TARIFA_GRUPO_TARIFA.IDTARIFA.eq(CPANEL_TARIFA.IDTARIFA))
                .where(CPANEL_GRUPO_TARIFA.IDEVENTO.eq(eventId))
                .fetchInto(RateGroupWithTranslationRecord.class);

        return new ArrayList<>(rateWithTranslationAndRatesRecords.stream()
                .map(this::getRateGroupRecord)
                .collect(Collectors.toConcurrentMap(
                        RateGroupRecord::getIdGrupoTarifa,
                        Function.identity(),
                        (rateGroupRecord1, rateGroupRecord2) -> {
                            if(rateGroupRecord1.getTranslations() != null && rateGroupRecord2.getTranslations() != null) {
                                rateGroupRecord1.getTranslations().putAll(rateGroupRecord2.getTranslations());
                            }
                            if(rateGroupRecord1.getTarifas() != null && rateGroupRecord2.getTarifas() != null) {
                                rateGroupRecord1.getTarifas().addAll(rateGroupRecord2.getTarifas());
                            }
                            return rateGroupRecord1;
                        }
                )).values());
    }

    public List<RateGroupRecord> getRatesGroupByEventId(Integer eventId, RateGroupType type, Long limit, Long offset) {

        Condition whereCondition = DSL.trueCondition();
        whereCondition = whereCondition.and(CPANEL_GRUPO_TARIFA.IDEVENTO.eq(eventId));
        if (type != null) {
            whereCondition = whereCondition.and(CPANEL_GRUPO_TARIFA.TIPO.eq(type.getId().byteValue()));
        }

        List<RateGroupWithTranslationRecord> rateWithTranslationRecords = dsl
                .select(CPANEL_GRUPO_TARIFA.IDGRUPOTARIFA,
                        CPANEL_GRUPO_TARIFA.IDEVENTO,
                        CPANEL_GRUPO_TARIFA.NOMBRE,
                        CPANEL_GRUPO_TARIFA.DEFECTO,
                        CPANEL_GRUPO_TARIFA.ELEMENTOCOMDESCRIPCION,
                        CPANEL_GRUPO_TARIFA.DESCRIPCIONEXTERNA,
                        CPANEL_IDIOMA.CODIGO.as("codigoIdioma"),
                        CPANEL_DESC_POR_IDIOMA.DESCRIPCION.as("traduccion"),
                        CPANEL_GRUPO_TARIFA.POSITION)
                .from(dsl.select(CPANEL_GRUPO_TARIFA.IDGRUPOTARIFA.as("cropTarifaIds"))
                        .from(CPANEL_GRUPO_TARIFA)
                        .where(whereCondition)
                        .orderBy(CPANEL_GRUPO_TARIFA.IDGRUPOTARIFA)
                        .limit(parseLongToInt(limit, Integer.MAX_VALUE))
                        .offset(parseLongToInt(offset, 0)))
                .leftJoin(CPANEL_GRUPO_TARIFA)
                .on(DSL.field("cropTarifaIds").eq(CPANEL_GRUPO_TARIFA.IDGRUPOTARIFA))
                .leftJoin(CPANEL_ITEM_DESC_SEQUENCE)
                .on(CPANEL_GRUPO_TARIFA.ELEMENTOCOMDESCRIPCION.eq(CPANEL_ITEM_DESC_SEQUENCE.IDITEM))
                .leftJoin(CPANEL_DESC_POR_IDIOMA)
                .on(CPANEL_ITEM_DESC_SEQUENCE.IDITEM.eq(CPANEL_DESC_POR_IDIOMA.IDITEM))
                .leftJoin(CPANEL_IDIOMA)
                .on(CPANEL_DESC_POR_IDIOMA.IDIDIOMA.eq(CPANEL_IDIOMA.IDIDIOMA))
                .fetchInto(RateGroupWithTranslationRecord.class);

        Collection<RateGroupRecord> aggRecords = rateWithTranslationRecords.stream()
                .map(this::getRateGroupRecord)
                .collect(Collectors.toConcurrentMap(
                        RateGroupRecord::getIdGrupoTarifa,
                        Function.identity(),
                        (rateRecord1, rateRecord2) -> {
                            rateRecord1.getTranslations().putAll(rateRecord2.getTranslations());
                            return rateRecord1;
                        }
                )).values();

        return aggRecords.stream().sorted(Comparator.comparing(RateGroupRecord::getNombre)).collect(Collectors.toList());
    }

    private int parseLongToInt(Long value, int defaultValue) {
        int result = Optional.ofNullable(value).map(Long::intValue).orElse(defaultValue);
        if (result == -1) {
            result = defaultValue;
        }
        return result;
    }

    private RateGroupRecord getRateGroupRecord(RateGroupWithTranslationRecord rateWithTranslationRecord) {
        RateGroupRecord rateRecord = new RateGroupRecord();
        rateRecord.setIdGrupoTarifa(rateWithTranslationRecord.getIdGrupoTarifa());
        rateRecord.setIdEvento(rateWithTranslationRecord.getIdEvento());
        rateRecord.setNombre(rateWithTranslationRecord.getNombre());
        rateRecord.setDefecto(rateWithTranslationRecord.getDefecto());
        rateRecord.setDescripcionExterna(rateWithTranslationRecord.getDescripcionExterna());
        rateRecord.setElementoComDescripcion(rateWithTranslationRecord.getElementoComDescripcion());
        rateRecord.setPosition(rateWithTranslationRecord.getPosition());
        if (rateWithTranslationRecord.getCodigoIdioma() != null) {
            Map<String, String> translateMap = new HashMap<>();
            translateMap.put(
                    rateWithTranslationRecord.getCodigoIdioma(),
                    rateWithTranslationRecord.getTraduccion()
            );
            rateRecord.setTranslations(translateMap);
        }
        rateRecord.setTipo(rateWithTranslationRecord.getTipo());
        if (rateWithTranslationRecord.getIdTarifa() != null) {
            Set<Integer> rates = new HashSet<>();
            rates.add(rateWithTranslationRecord.getIdTarifa());
            rateRecord.setTarifas(rates);
        }
        return rateRecord;
    }

    public void createSesionRate(Integer idSesion, Integer rateId) {
        dsl.insertInto(CPANEL_SESION_TARIFA).
                set(CPANEL_SESION_TARIFA.IDSESION, idSesion).
                set(CPANEL_SESION_TARIFA.IDTARIFA, rateId).
                set(CPANEL_SESION_TARIFA.DEFECTO, false).
                execute();
    }

    public void deleteSessionRate(Integer idSesion, Integer rateId) {
        dsl.delete(CPANEL_SESION_TARIFA).
                where(CPANEL_SESION_TARIFA.IDSESION.eq(idSesion)).
                and(CPANEL_SESION_TARIFA.IDTARIFA.eq(rateId)).
                execute();
    }
}
