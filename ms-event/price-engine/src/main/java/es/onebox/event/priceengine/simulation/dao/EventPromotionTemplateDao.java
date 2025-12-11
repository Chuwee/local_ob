package es.onebox.event.priceengine.simulation.dao;

import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.priceengine.simulation.record.EventPromotionConditionRateRecord;
import es.onebox.event.priceengine.simulation.record.EventPromotionRecord;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelPlantillaPromocionEventoRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.jooq.Condition;
import org.jooq.Param;
import org.jooq.Record;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static es.onebox.jooq.cpanel.Tables.CPANEL_PLANTILLA_PROMOCION;
import static es.onebox.jooq.cpanel.Tables.CPANEL_PLANTILLA_PROMOCION_EVENTO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_PROMOCION_CONDICION_TARIFA;

/**
 * @author ignasi
 */
@Repository
public class EventPromotionTemplateDao extends DaoImpl<CpanelPlantillaPromocionEventoRecord, Integer> {

    private static final int DEFAULT_MAX_ALLOWED_PACKET_VALUE = 4194304;

    protected EventPromotionTemplateDao() {
        super(CPANEL_PLANTILLA_PROMOCION_EVENTO);
    }

    public Map<Integer, List<Long>> getPromotedSessionsByPromotionEventIds(final List<Integer> promotionEventIds) {
        Assert.isTrue(promotionEventIds.size() < DEFAULT_MAX_ALLOWED_PACKET_VALUE, "Collection must be less than 'max_allowed_packet' limit size'");
        return dsl.select(CPANEL_PLANTILLA_PROMOCION_EVENTO.IDPROMOCIONEVENTO, Tables.CPANEL_PROMOCION_EVENTO_SESION.IDSESION)
                .from(CPANEL_PLANTILLA_PROMOCION_EVENTO)
                .innerJoin(Tables.CPANEL_PROMOCION_EVENTO_SESION)
                .on(Tables.CPANEL_PROMOCION_EVENTO_SESION.IDPROMOCIONEVENTO.eq(CPANEL_PLANTILLA_PROMOCION_EVENTO.IDPROMOCIONEVENTO))
                .where(CPANEL_PLANTILLA_PROMOCION_EVENTO.IDPROMOCIONEVENTO.in(promotionEventIds))
                .fetchGroups(
                        CPANEL_PLANTILLA_PROMOCION_EVENTO.IDPROMOCIONEVENTO,
                        record -> {
                            Integer sessionId = record.getValue(Tables.CPANEL_PROMOCION_EVENTO_SESION.IDSESION);
                            return Objects.nonNull(sessionId) ? sessionId.longValue() : null;
                        });
    }


    public List<EventPromotionRecord> getPromotionsByEventId(Integer eventId) {
        return dsl.select(this.fields())
                .from(this.from())
                .where(this.where(eventId, null))
                .groupBy(CPANEL_PLANTILLA_PROMOCION_EVENTO.IDPROMOCIONEVENTO)
                .fetch()
                .map(this::buildEventPromotionRecord);
    }

    public List<EventPromotionRecord> getPromotionsByEventPromotionTemplateId(List<Long> eventPromotionTemplates) {
        return dsl.select(this.fields())
                .from(this.from())
                .where(CPANEL_PLANTILLA_PROMOCION_EVENTO.IDPROMOCIONEVENTO.in(eventPromotionTemplates))
                .groupBy(CPANEL_PLANTILLA_PROMOCION_EVENTO.IDPROMOCIONEVENTO)
                .fetch()
                .map(this::buildEventPromotionRecord);
    }

    public List<EventPromotionRecord> getPackApplicablePromotionsByEventId(Integer eventId) {
        return dsl.select(this.fields())
                .from(this.from())
                .where(this.where(eventId, null))
                .and(CPANEL_PLANTILLA_PROMOCION_EVENTO.USAPACKSENTIDAD.isTrue())
                .groupBy(CPANEL_PLANTILLA_PROMOCION_EVENTO.IDPROMOCIONEVENTO)
                .fetch()
                .map(this::buildEventPromotionRecord);
    }

    private SelectField<?>[] fields() {
        Param<String> semicolon = DSL.val("::");
        return new SelectField<?>[]{
                CPANEL_PLANTILLA_PROMOCION_EVENTO.IDPROMOCIONEVENTO,
                CPANEL_PLANTILLA_PROMOCION_EVENTO.IDEVENTO,
                CPANEL_PLANTILLA_PROMOCION_EVENTO.IDPLANTILLAPROMOCION,
                CPANEL_PLANTILLA_PROMOCION_EVENTO.TIPOCANALCOMPRA,
                CPANEL_PLANTILLA_PROMOCION_EVENTO.TIPOLOCALIDADES,
                CPANEL_PLANTILLA_PROMOCION_EVENTO.TIPOSESIONES,
                CPANEL_PLANTILLA_PROMOCION_EVENTO.USASELECCIONTARIFAS,
                CPANEL_PLANTILLA_PROMOCION_EVENTO.USAPACKSENTIDAD,
                CPANEL_PLANTILLA_PROMOCION.NOMBRE,
                CPANEL_PLANTILLA_PROMOCION.ESTADO,
                CPANEL_PLANTILLA_PROMOCION.ACTIVADA,
                CPANEL_PLANTILLA_PROMOCION.SUBTIPO,
                CPANEL_PLANTILLA_PROMOCION.TIPOPERIODOVALIDEZ,
                CPANEL_PLANTILLA_PROMOCION.PERIODODESDE,
                CPANEL_PLANTILLA_PROMOCION.PERIODOHASTA,
                CPANEL_PLANTILLA_PROMOCION.TIPODESCUENTO,
                CPANEL_PLANTILLA_PROMOCION.VALORDESCUENTOFIJO,
                CPANEL_PLANTILLA_PROMOCION.VALORDESCUENTOPORCENTUAL,
                CPANEL_PLANTILLA_PROMOCION.APLICARCOSTESRECARGOSCANALESPECIFICOS,
                CPANEL_PLANTILLA_PROMOCION.APLICARCOSTESRECARGOSPROMOTORESPECIFICOS,
                CPANEL_PLANTILLA_PROMOCION.ESPROMOCIONPRIVADA,
                CPANEL_PLANTILLA_PROMOCION.ESNOGESTIONABLE,
                CPANEL_PLANTILLA_PROMOCION.ACCESORESTRICTIVO,
                CPANEL_PLANTILLA_PROMOCION.NOACUMULABLE,

                CPANEL_PLANTILLA_PROMOCION.USALIMITEOPERACION,
                CPANEL_PLANTILLA_PROMOCION.LIMITEOPERACION,
                CPANEL_PLANTILLA_PROMOCION.USALIMITEEVENTO,
                CPANEL_PLANTILLA_PROMOCION.LIMITEEVENTO,
                CPANEL_PLANTILLA_PROMOCION.USALIMITESESION,
                CPANEL_PLANTILLA_PROMOCION.LIMITESESION,
                CPANEL_PLANTILLA_PROMOCION.USALIMITEPACKENTRADAS,
                CPANEL_PLANTILLA_PROMOCION.LIMITEPACKENTRADAS,
                CPANEL_PLANTILLA_PROMOCION.USALIMITEMINENTRADAS,
                CPANEL_PLANTILLA_PROMOCION.LIMITEMINENTRADAS,
                CPANEL_PLANTILLA_PROMOCION.USESEVENTUSERCOLLECTIVELIMIT,
                CPANEL_PLANTILLA_PROMOCION.USESSESSIONUSERCOLLECTIVELIMIT,
                CPANEL_PLANTILLA_PROMOCION.EVENTUSERCOLLECTIVELIMIT,
                CPANEL_PLANTILLA_PROMOCION.SESSIONUSERCOLLECTIVELIMIT,
                CPANEL_PLANTILLA_PROMOCION.BLOCKSECONDARYMARKETSALE,
                Tables.CPANEL_COLECTIVO.IDCOLECTIVO,
                Tables.CPANEL_TIPO_COLECTIVO.IDTIPOCOLECTIVO.as("collectiveType"),
                Tables.CPANEL_SUBTIPO_COLECTIVO.IDSUBTIPOCOLECTIVO,
                DSL.field("GROUP_CONCAT(DISTINCT {0} ORDER BY {0} ASC SEPARATOR '||')", SQLDataType.VARCHAR, Tables.CPANEL_PROMOCION_EVENTO_CANAL.IDCANAL).as("channels"),
                DSL.field("GROUP_CONCAT(DISTINCT {0} ORDER BY {0} ASC SEPARATOR '||')", SQLDataType.VARCHAR, Tables.CPANEL_PROMOCION_EVENTO_TARIFA.IDTARIFA).as("rates"),
                DSL.field("GROUP_CONCAT(DISTINCT {0} ORDER BY {0} ASC SEPARATOR '||')", SQLDataType.VARCHAR, Tables.CPANEL_PROMOCION_EVENTO_ZONA_PRECIO.IDZONA).as("priceZones"),
                DSL.field("GROUP_CONCAT(DISTINCT {0} ORDER BY {1} SEPARATOR '||')", SQLDataType.VARCHAR, DSL.concat(Tables.CPANEL_RANGO.RANGOMINIMO, semicolon, Tables.CPANEL_RANGO.RANGOMAXIMO, semicolon, Tables.CPANEL_RANGO.VALOR), Tables.CPANEL_RANGO.RANGOMINIMO).as("ranges")
        };
    }

    private Table<?> from() {
        return CPANEL_PLANTILLA_PROMOCION_EVENTO.innerJoin(CPANEL_PLANTILLA_PROMOCION).on(CPANEL_PLANTILLA_PROMOCION.IDPLANTILLAPROMOCION.eq(CPANEL_PLANTILLA_PROMOCION_EVENTO.IDPLANTILLAPROMOCION))
                .leftJoin(Tables.CPANEL_PROMOCION_EVENTO_CANAL).on(Tables.CPANEL_PROMOCION_EVENTO_CANAL.IDPROMOCIONEVENTO.eq(CPANEL_PLANTILLA_PROMOCION_EVENTO.IDPROMOCIONEVENTO))
                .leftJoin(Tables.CPANEL_PROMOCION_EVENTO_TARIFA).on(Tables.CPANEL_PROMOCION_EVENTO_TARIFA.IDPROMOCIONEVENTO.eq(CPANEL_PLANTILLA_PROMOCION_EVENTO.IDPROMOCIONEVENTO))
                .leftJoin(Tables.CPANEL_PROMOCION_EVENTO_ZONA_PRECIO).on(Tables.CPANEL_PROMOCION_EVENTO_ZONA_PRECIO.IDPROMOCIONEVENTO.eq(CPANEL_PLANTILLA_PROMOCION_EVENTO.IDPROMOCIONEVENTO))
                .leftJoin(Tables.CPANEL_COLECTIVO).on(Tables.CPANEL_COLECTIVO.IDCOLECTIVO.eq(CPANEL_PLANTILLA_PROMOCION.IDCOLECTIVO))
                .leftJoin(Tables.CPANEL_TIPO_COLECTIVO).on(Tables.CPANEL_TIPO_COLECTIVO.IDTIPOCOLECTIVO.eq(Tables.CPANEL_COLECTIVO.IDTIPOCOLECTIVO))
                .leftJoin(Tables.CPANEL_SUBTIPO_COLECTIVO).on(Tables.CPANEL_SUBTIPO_COLECTIVO.IDSUBTIPOCOLECTIVO.eq(Tables.CPANEL_COLECTIVO.IDSUBTIPOCOLECTIVO))
                .leftJoin(Tables.CPANEL_RANGO_PLANTILLA_PROMOCION).on(Tables.CPANEL_RANGO_PLANTILLA_PROMOCION.IDPLANTILLAPROMOCION.eq(CPANEL_PLANTILLA_PROMOCION.IDPLANTILLAPROMOCION))
                .leftJoin(Tables.CPANEL_RANGO).on(Tables.CPANEL_RANGO.IDRANGO.eq(Tables.CPANEL_RANGO_PLANTILLA_PROMOCION.IDRANGO));
    }

    private Condition where(Integer eventId, List<Integer> eventPromotionTemplateIds) {
        Condition condition = DSL.trueCondition();
        condition = condition.and(CPANEL_PLANTILLA_PROMOCION_EVENTO.IDEVENTO.eq(eventId));
        condition = condition.and(CPANEL_PLANTILLA_PROMOCION.ACTIVADA.eq((byte) 1));
        condition = condition.and(CPANEL_PLANTILLA_PROMOCION.ESTADO.eq(1));

        if (eventPromotionTemplateIds != null) {
            condition = condition.and(CPANEL_PLANTILLA_PROMOCION_EVENTO.IDPROMOCIONEVENTO.in(eventPromotionTemplateIds));
        }

        return condition;
    }

    private EventPromotionRecord buildEventPromotionRecord(Record record) {
        EventPromotionRecord eventPromotionRecord = new EventPromotionRecord();
        eventPromotionRecord.setEventPromotionTemplateId(record.get(CPANEL_PLANTILLA_PROMOCION_EVENTO.IDPROMOCIONEVENTO));
        eventPromotionRecord.setSelectedRates(record.get(CPANEL_PLANTILLA_PROMOCION_EVENTO.USASELECCIONTARIFAS));
        eventPromotionRecord.setSelectedChannels(record.get(CPANEL_PLANTILLA_PROMOCION_EVENTO.TIPOCANALCOMPRA));
        eventPromotionRecord.setUseEntityPacks(CommonUtils.isTrue(record.get(CPANEL_PLANTILLA_PROMOCION_EVENTO.USAPACKSENTIDAD)));
        eventPromotionRecord.setSelectedSessions(record.get(CPANEL_PLANTILLA_PROMOCION_EVENTO.TIPOSESIONES));
        eventPromotionRecord.setSelectedPriceZones(record.get(CPANEL_PLANTILLA_PROMOCION_EVENTO.TIPOLOCALIDADES));
        eventPromotionRecord.setEventId(record.get(CPANEL_PLANTILLA_PROMOCION_EVENTO.IDEVENTO));
        eventPromotionRecord.setPromotionTemplateId(record.get(CPANEL_PLANTILLA_PROMOCION_EVENTO.IDPLANTILLAPROMOCION));
        eventPromotionRecord.setName(record.get(CPANEL_PLANTILLA_PROMOCION.NOMBRE));
        eventPromotionRecord.setSubtype(record.get(CPANEL_PLANTILLA_PROMOCION.SUBTIPO));
        eventPromotionRecord.setStatus(record.get(CPANEL_PLANTILLA_PROMOCION.ESTADO));
        eventPromotionRecord.setActive(CommonUtils.isTrue(record.get(CPANEL_PLANTILLA_PROMOCION.ACTIVADA)));
        eventPromotionRecord.setDateFrom(record.get(CPANEL_PLANTILLA_PROMOCION.PERIODODESDE));
        eventPromotionRecord.setDateTo(record.get(CPANEL_PLANTILLA_PROMOCION.PERIODOHASTA));
        eventPromotionRecord.setDiscountType(record.get(CPANEL_PLANTILLA_PROMOCION.TIPODESCUENTO));
        eventPromotionRecord.setFixedDiscountValue(record.get(CPANEL_PLANTILLA_PROMOCION.VALORDESCUENTOFIJO));
        eventPromotionRecord.setPercentualDiscountValue(record.get(CPANEL_PLANTILLA_PROMOCION.VALORDESCUENTOPORCENTUAL));
        eventPromotionRecord.setApplyChannelSpecificCharges(CommonUtils.isTrue(record.get(CPANEL_PLANTILLA_PROMOCION.APLICARCOSTESRECARGOSCANALESPECIFICOS)));
        eventPromotionRecord.setApplyPromoterSpecificCharges(CommonUtils.isTrue(record.get(CPANEL_PLANTILLA_PROMOCION.APLICARCOSTESRECARGOSPROMOTORESPECIFICOS)));
        eventPromotionRecord.setExclusiveSale(CommonUtils.isTrue(record.get(CPANEL_PLANTILLA_PROMOCION.ESPROMOCIONPRIVADA)));
        eventPromotionRecord.setNotCumulative(CommonUtils.isTrue(record.get(CPANEL_PLANTILLA_PROMOCION.NOACUMULABLE)));
        eventPromotionRecord.setCollectiveId(record.get(Tables.CPANEL_COLECTIVO.IDCOLECTIVO));
        eventPromotionRecord.setCollectiveTypeId((Integer) record.get("collectiveType"));
        eventPromotionRecord.setCollectiveSubtypeId(record.get(Tables.CPANEL_SUBTIPO_COLECTIVO.IDSUBTIPOCOLECTIVO));
        eventPromotionRecord.setCollectiveName(record.get(Tables.CPANEL_COLECTIVO.NOMBRE));
        eventPromotionRecord.setChannels((String) record.get("channels"));
        eventPromotionRecord.setRates((String) record.get("rates"));
        eventPromotionRecord.setPriceZones((String) record.get("priceZones"));
        eventPromotionRecord.setRanges((String) record.get("ranges"));
        eventPromotionRecord.setValidationPeriodType(record.get(CPANEL_PLANTILLA_PROMOCION.TIPOPERIODOVALIDEZ));
        eventPromotionRecord.setSelfManaged(CommonUtils.isTrue(record.get(CPANEL_PLANTILLA_PROMOCION.ESNOGESTIONABLE)));
        eventPromotionRecord.setRestrictiveAccess(CommonUtils.isTrue(record.get(CPANEL_PLANTILLA_PROMOCION.ACCESORESTRICTIVO)));


        eventPromotionRecord.setUseLimitByOperation(CommonUtils.isTrue(record.get(CPANEL_PLANTILLA_PROMOCION.USALIMITEOPERACION)));
        eventPromotionRecord.setLimitByOperation(record.get(CPANEL_PLANTILLA_PROMOCION.LIMITEOPERACION));
        eventPromotionRecord.setUseLimitByEvent(CommonUtils.isTrue(record.get(CPANEL_PLANTILLA_PROMOCION.USALIMITEEVENTO)));
        eventPromotionRecord.setLimitByEvent(record.get(CPANEL_PLANTILLA_PROMOCION.LIMITEEVENTO));
        eventPromotionRecord.setUseLimitBySession(CommonUtils.isTrue(record.get(CPANEL_PLANTILLA_PROMOCION.USALIMITESESION)));
        eventPromotionRecord.setLimitBySession(record.get(CPANEL_PLANTILLA_PROMOCION.LIMITESESION));
        eventPromotionRecord.setUseLimitByMinTickets(CommonUtils.isTrue(record.get(CPANEL_PLANTILLA_PROMOCION.USALIMITEMINENTRADAS)));
        eventPromotionRecord.setLimitByMinTickets(record.get(CPANEL_PLANTILLA_PROMOCION.LIMITEMINENTRADAS));
        eventPromotionRecord.setUseLimitByPack(CommonUtils.isTrue(record.get(CPANEL_PLANTILLA_PROMOCION.USALIMITEPACKENTRADAS)));
        eventPromotionRecord.setLimitByPack(record.get(CPANEL_PLANTILLA_PROMOCION.LIMITEPACKENTRADAS));
        eventPromotionRecord.setUsesEventUserCollectiveLimit(CommonUtils.isTrue(record.get(CPANEL_PLANTILLA_PROMOCION.USESEVENTUSERCOLLECTIVELIMIT)));
        eventPromotionRecord.setEventUserCollectiveLimit(record.get(CPANEL_PLANTILLA_PROMOCION.EVENTUSERCOLLECTIVELIMIT));
        eventPromotionRecord.setUsesSessionUserCollectiveLimit(CommonUtils.isTrue(record.get(CPANEL_PLANTILLA_PROMOCION.USESSESSIONUSERCOLLECTIVELIMIT)));
        eventPromotionRecord.setSessionUserCollectiveLimit(record.get(CPANEL_PLANTILLA_PROMOCION.SESSIONUSERCOLLECTIVELIMIT));
        eventPromotionRecord.setBlockSecondaryMarketSale(CommonUtils.isTrue(record.get(CPANEL_PLANTILLA_PROMOCION.BLOCKSECONDARYMARKETSALE)));

        return eventPromotionRecord;
    }

    public List<EventPromotionConditionRateRecord> getPromotionConditionRate(List<Integer> promotionEventIds) {
        return dsl.select(CPANEL_PROMOCION_CONDICION_TARIFA.IDPROMOEVENTO, CPANEL_PROMOCION_CONDICION_TARIFA.IDTARIFA, CPANEL_PROMOCION_CONDICION_TARIFA.CANTIDAD)
                .from(CPANEL_PROMOCION_CONDICION_TARIFA)
                .where(CPANEL_PROMOCION_CONDICION_TARIFA.IDPROMOEVENTO.in(promotionEventIds))
                .fetch()
                .map(this::buildPromotionRateCondition);
    }

    private EventPromotionConditionRateRecord buildPromotionRateCondition(Record record) {
        EventPromotionConditionRateRecord eventPromotionConditionRateRecord = new EventPromotionConditionRateRecord();
        eventPromotionConditionRateRecord.setIdPromotionEvent(record.get(CPANEL_PROMOCION_CONDICION_TARIFA.IDPROMOEVENTO));
        eventPromotionConditionRateRecord.setId(record.get(CPANEL_PROMOCION_CONDICION_TARIFA.IDTARIFA));
        eventPromotionConditionRateRecord.setQuantity(record.get(CPANEL_PROMOCION_CONDICION_TARIFA.CANTIDAD));
        return eventPromotionConditionRateRecord;
    }

}
