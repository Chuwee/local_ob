package es.onebox.event.products.dao;

import es.onebox.event.common.utils.JooqUtils;
import es.onebox.event.products.domain.ProductSessionDeliveryPointRecord;
import es.onebox.event.products.enums.DeliveryPointStatus;
import es.onebox.event.products.enums.SelectionType;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.event.sessions.request.SessionSearchFilter;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.CpanelConfigRecinto;
import es.onebox.jooq.cpanel.tables.CpanelDeliveryPoint;
import es.onebox.jooq.cpanel.tables.CpanelEntidadRecintoConfig;
import es.onebox.jooq.cpanel.tables.CpanelProduct;
import es.onebox.jooq.cpanel.tables.CpanelProductEvent;
import es.onebox.jooq.cpanel.tables.CpanelProductSession;
import es.onebox.jooq.cpanel.tables.CpanelProductSessionDeliveryPoint;
import es.onebox.jooq.cpanel.tables.CpanelSesion;
import es.onebox.jooq.cpanel.tables.records.CpanelProductSessionDeliveryPointRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.SelectOnConditionStep;
import org.jooq.SelectSeekStep2;
import org.jooq.SelectSeekStep3;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_DELIVERY_POINT;
import static es.onebox.jooq.cpanel.Tables.CPANEL_PRODUCT;
import static es.onebox.jooq.cpanel.Tables.CPANEL_PRODUCT_EVENT;
import static es.onebox.jooq.cpanel.Tables.CPANEL_PRODUCT_SESSION;
import static es.onebox.jooq.cpanel.Tables.CPANEL_PRODUCT_SESSION_DELIVERY_POINT;
import static es.onebox.jooq.cpanel.Tables.CPANEL_SESION;
import static org.jooq.Operator.AND;

@Repository
public class ProductSessionDeliveryPointDao extends DaoImpl<CpanelProductSessionDeliveryPointRecord, Integer> {

    private static final CpanelProductSessionDeliveryPoint productSessionDeliveryPoint = CPANEL_PRODUCT_SESSION_DELIVERY_POINT.as("productSessionDeliveryPoint");
    private static final CpanelProduct product = CPANEL_PRODUCT.as("product");
    private static final CpanelDeliveryPoint deliveryPoint = CPANEL_DELIVERY_POINT.as("deliveryPoint");
    private static final CpanelSesion session = CPANEL_SESION.as("session");
    private static final CpanelProductEvent productEvent = CPANEL_PRODUCT_EVENT.as("productEvent");

    private static final CpanelProductSession productSession = CPANEL_PRODUCT_SESSION.as("productSession");
    private static final CpanelConfigRecinto configRecinto = Tables.CPANEL_CONFIG_RECINTO.as("configRecinto");
    private static final CpanelEntidadRecintoConfig entidadRecintoConfig = Tables.CPANEL_ENTIDAD_RECINTO_CONFIG.as("entidadRecintoConfig");

    private static final Field<String> JOIN_DELIVERY_POINT_NAME = deliveryPoint.NAME.as("deliveryPointName");
    private static final Field<String> JOIN_SESSION_NAME = session.NOMBRE.as("eventName");
    private static final Field<Timestamp> JOIN_SESSION_START = session.FECHAINICIOSESION.as("start");
    private static final Field<Timestamp> JOIN_SESSION_END = session.FECHAFINSESION.as("end");
    private static final Field<Integer> JOIN_SESSION_SB_SESION = session.SBSESIONRELACIONADA.as("sbSesionRelacionada");
    private static final Field<Integer> JOIN_RECINTO_TEMPLATE_TYPE = configRecinto.TIPOPLANTILLA.as("tipoPlantilla");

    private static final List<Integer> ALLOWED_SESSION_STATUS = Arrays.asList(SessionStatus.SCHEDULED.getId(),
            SessionStatus.READY.getId(), SessionStatus.PREVIEW.getId(), SessionStatus.IN_PROGRESS.getId(),
            SessionStatus.PLANNED.getId());

    private static final SelectFieldOrAsterisk[] JOIN_FIELDS = {
            JOIN_DELIVERY_POINT_NAME,
            JOIN_SESSION_NAME,
            JOIN_SESSION_START,
            JOIN_SESSION_END
    };

    protected ProductSessionDeliveryPointDao() {
        super(CPANEL_PRODUCT_SESSION_DELIVERY_POINT);
    }

    public List<ProductSessionDeliveryPointRecord> findByProductSession(Long productId, Long sessionId) {
        SelectFieldOrAsterisk[] fields = ArrayUtils.addAll(productSessionDeliveryPoint.fields(), JOIN_FIELDS);

        SelectConditionStep<Record> query = dsl
                .select(fields)
                .from(productSessionDeliveryPoint)
                .innerJoin(productEvent).on(productEvent.PRODUCTEVENTID.eq(productSessionDeliveryPoint.PRODUCTEVENTID)).and(productEvent.PRODUCTID.eq(productId.intValue()))
                .innerJoin(product).on(product.PRODUCTID.eq(productEvent.PRODUCTID))
                .innerJoin(deliveryPoint).on(deliveryPoint.DELIVERYPOINTID.eq(productSessionDeliveryPoint.DELIVERYPOINTID))
                .innerJoin(session).on(session.IDSESION.eq(productSessionDeliveryPoint.SESSIONID))
                .where(productSessionDeliveryPoint.SESSIONID.eq(sessionId.intValue()));

        List<Record> result = query.fetch();
        return buildProductSessionDeliveryPointRecord(result, fields.length);
    }

    public List<ProductSessionDeliveryPointRecord> findByProductEvent(
            Long productId,
            Long eventId,
            SessionSearchFilter sessionFilter,
            SelectionType selectionType
    ) {
        SelectFieldOrAsterisk[] restrictedFields = new SelectFieldOrAsterisk[]{
                productEvent.PRODUCTEVENTID.as("productEventId"),
                session.IDSESION.as("sessionId"),
                session.SBSESIONRELACIONADA,
                configRecinto.TIPOPLANTILLA,
                productSessionDeliveryPoint.DELIVERYPOINTID,
                productSessionDeliveryPoint.DEFAULTDELIVERYPOINT,
                productSessionDeliveryPoint.CREATE_DATE,
                productSessionDeliveryPoint.UPDATE_DATE,
                JOIN_DELIVERY_POINT_NAME, JOIN_SESSION_NAME, JOIN_SESSION_START, JOIN_SESSION_END
        };

        Condition where = applyCommonFilters(DSL.trueCondition(), sessionFilter);

        int limit = (sessionFilter != null && sessionFilter.getLimit() != null) ? sessionFilter.getLimit().intValue() : 1000;
        int offset = (sessionFilter != null && sessionFilter.getOffset() != null) ? sessionFilter.getOffset().intValue() : 0;

        List<Integer> pageSessionIds;

        if (SelectionType.RESTRICTED.equals(selectionType)) {
            pageSessionIds = dsl.select(session.IDSESION)
                    .from(productSession)
                    .join(productEvent).on(productEvent.PRODUCTEVENTID.eq(productSession.PRODUCTEVENTID)
                            .and(productEvent.PRODUCTID.eq(productId.intValue())))
                    .join(session).on(session.IDSESION.eq(productSession.SESSIONID)
                            .and(session.IDEVENTO.eq(eventId.intValue())))
                    .where(where)
                    .groupBy(session.IDSESION)
                    .orderBy(DSL.min(session.FECHAINICIOSESION).asc(), session.IDSESION.asc())
                    .limit(limit)
                    .offset(offset)
                    .fetchInto(Integer.class);
        } else {
            pageSessionIds = dsl.select(session.IDSESION)
                    .from(session)
                    .where(where
                            .and(session.IDEVENTO.eq(eventId.intValue()))
                            .and(session.ESTADO.in(ALLOWED_SESSION_STATUS)))
                    .groupBy(session.IDSESION)
                    .orderBy(DSL.min(session.FECHAINICIOSESION).asc(), session.IDSESION.asc())
                    .limit(limit)
                    .offset(offset)
                    .fetchInto(Integer.class);
        }

        if (pageSessionIds.isEmpty()) {
            return List.of();
        }

        if (SelectionType.RESTRICTED.equals(selectionType)) {
            SelectSeekStep3<Record, Timestamp, Integer, String> q = dsl.select(restrictedFields)
                    .from(productSession)
                    .join(productEvent).on(productEvent.PRODUCTEVENTID.eq(productSession.PRODUCTEVENTID)
                            .and(productEvent.PRODUCTID.eq(productId.intValue())))
                    .join(session).on(session.IDSESION.eq(productSession.SESSIONID)
                            .and(session.IDEVENTO.eq(eventId.intValue())))
                    .innerJoin(configRecinto).on(configRecinto.IDEVENTO.eq(session.IDEVENTO))
                    .innerJoin(entidadRecintoConfig).on(entidadRecintoConfig.IDCONFIGURACION.eq(configRecinto.IDCONFIGURACION))
                    .and(entidadRecintoConfig.IDRELACIONENTRECINTO.eq(session.IDRELACIONENTIDADRECINTO))
                    .leftJoin(productSessionDeliveryPoint).on(productSessionDeliveryPoint.PRODUCTEVENTID.eq(productSession.PRODUCTEVENTID)
                            .and(productSessionDeliveryPoint.SESSIONID.eq(productSession.SESSIONID)))
                    .join(product).on(product.PRODUCTID.eq(productEvent.PRODUCTID))
                    .leftJoin(deliveryPoint).on(deliveryPoint.DELIVERYPOINTID.eq(productSessionDeliveryPoint.DELIVERYPOINTID))
                    .where(where.and(session.IDSESION.in(pageSessionIds)))
                    .groupBy(
                            session.IDSESION, session.SBSESIONRELACIONADA, configRecinto.TIPOPLANTILLA,
                            productSessionDeliveryPoint.DELIVERYPOINTID, deliveryPoint.NAME,
                            session.NOMBRE, session.FECHAINICIOSESION, session.FECHAFINSESION, productEvent.PRODUCTEVENTID
                    )
                    .orderBy(session.FECHAINICIOSESION.asc(), session.IDSESION.asc(), deliveryPoint.NAME);

            return buildProductSessionDeliveryPointRecord(q.fetch(), restrictedFields.length);
        } else {
            CpanelProductEvent pe = CPANEL_PRODUCT_EVENT.as("pe");
            CpanelProductSessionDeliveryPoint psdpS = CPANEL_PRODUCT_SESSION_DELIVERY_POINT.as("psdpS");
            CpanelProductSessionDeliveryPoint psdpE = CPANEL_PRODUCT_SESSION_DELIVERY_POINT.as("psdpE");
            CpanelDeliveryPoint dpS = CPANEL_DELIVERY_POINT.as("dpS");
            CpanelDeliveryPoint dpE = CPANEL_DELIVERY_POINT.as("dpE");

            Field<Integer> F_SESSION_ID = session.IDSESION.as("sessionId");
            Field<Integer> F_SB = session.SBSESIONRELACIONADA;
            Field<Integer> F_TIPO = configRecinto.TIPOPLANTILLA;
            Field<Integer> F_DP = DSL.coalesce(psdpS.DELIVERYPOINTID, psdpE.DELIVERYPOINTID).as(CPANEL_PRODUCT_SESSION_DELIVERY_POINT.DELIVERYPOINTID.getName());
            Field<Byte> F_DEF = DSL.coalesce(psdpS.DEFAULTDELIVERYPOINT, psdpE.DEFAULTDELIVERYPOINT).as(CPANEL_PRODUCT_SESSION_DELIVERY_POINT.DEFAULTDELIVERYPOINT.getName());
            Field<Timestamp> F_CD = DSL.coalesce(psdpS.CREATE_DATE, psdpE.CREATE_DATE).as(CPANEL_PRODUCT_SESSION_DELIVERY_POINT.CREATE_DATE.getName());
            Field<Timestamp> F_UD = DSL.coalesce(psdpS.UPDATE_DATE, psdpE.UPDATE_DATE).as(CPANEL_PRODUCT_SESSION_DELIVERY_POINT.UPDATE_DATE.getName());
            Field<String> F_DP_NAME = DSL.coalesce(dpS.NAME, dpE.NAME).as("deliveryPointName");

            SelectFieldOrAsterisk[] unrestrictedFields = new SelectFieldOrAsterisk[]{
                    F_SESSION_ID,
                    F_SB,
                    F_TIPO,
                    F_DP,
                    F_DEF,
                    F_CD,
                    F_UD,
                    F_DP_NAME, JOIN_SESSION_NAME, JOIN_SESSION_START, JOIN_SESSION_END
            };

            SelectSeekStep3<Record, Timestamp, Integer, String> q = dsl.select(unrestrictedFields)
                    .from(session)
                    .leftJoin(pe).on(pe.EVENTID.eq(session.IDEVENTO).and(pe.PRODUCTID.eq(productId.intValue())))
                    .leftJoin(psdpS).on(psdpS.SESSIONID.eq(session.IDSESION).and(psdpS.PRODUCTEVENTID.eq(pe.PRODUCTEVENTID)))
                    .leftJoin(dpS).on(dpS.DELIVERYPOINTID.eq(psdpS.DELIVERYPOINTID))
                    .leftJoin(psdpE).on(psdpE.SESSIONID.eq(session.IDSESION)
                            .and(psdpE.PRODUCTEVENTID.isNull())
                            .and(psdpE.DEFAULTDELIVERYPOINT.eq((byte) 1))
                            .and(psdpS.PRODUCTEVENTID.isNull()))
                    .leftJoin(dpE).on(dpE.DELIVERYPOINTID.eq(psdpE.DELIVERYPOINTID))
                    .innerJoin(configRecinto).on(configRecinto.IDEVENTO.eq(session.IDEVENTO))
                    .innerJoin(entidadRecintoConfig).on(entidadRecintoConfig.IDCONFIGURACION.eq(configRecinto.IDCONFIGURACION))
                    .and(entidadRecintoConfig.IDRELACIONENTRECINTO.eq(session.IDRELACIONENTIDADRECINTO))
                    .where(where
                            .and(session.IDEVENTO.eq(eventId.intValue()))
                            .and(session.ESTADO.in(ALLOWED_SESSION_STATUS))
                            .and(session.IDSESION.in(pageSessionIds)))
                    .groupBy(
                            session.IDSESION, session.SBSESIONRELACIONADA, configRecinto.TIPOPLANTILLA,
                            F_DP, F_DP_NAME,
                            session.NOMBRE, session.FECHAINICIOSESION, session.FECHAFINSESION
                    )
                    .orderBy(session.FECHAINICIOSESION.asc(), session.IDSESION.asc(), F_DP_NAME);

            return buildProductSessionDeliveryPointRecord(q.fetch(), unrestrictedFields.length);
        }
    }

    public List<ProductSessionDeliveryPointRecord> findActivesByProductId(Long productId) {
        SelectOnConditionStep<Record> query = dsl
                .select(productSessionDeliveryPoint.fields())
                .from(productSessionDeliveryPoint)
                .innerJoin(productEvent).on(productEvent.PRODUCTEVENTID.eq(productSessionDeliveryPoint.PRODUCTEVENTID))
                        .and(productEvent.PRODUCTID.eq(productId.intValue()))
                .innerJoin(deliveryPoint).on(deliveryPoint.DELIVERYPOINTID.eq(productSessionDeliveryPoint.DELIVERYPOINTID)
                        .and(deliveryPoint.DELIVERYPOINTSTATUS.eq(DeliveryPointStatus.ACTIVE.getId())))
                .innerJoin(session).on(session.IDSESION.eq(productSessionDeliveryPoint.SESSIONID)
                        .and(session.ESTADO.in(ALLOWED_SESSION_STATUS)))
                .innerJoin(configRecinto).on(configRecinto.IDEVENTO.eq(session.IDEVENTO))
                .innerJoin(entidadRecintoConfig).on(entidadRecintoConfig.IDCONFIGURACION.eq(configRecinto.IDCONFIGURACION));


        return query.fetch().into(ProductSessionDeliveryPointRecord.class);
    }

    private static List<ProductSessionDeliveryPointRecord> buildProductSessionDeliveryPointRecord(List<Record> productSessionDeliveryPoints, int fields) {
        List<ProductSessionDeliveryPointRecord> result = new ArrayList<>();
        if (productSessionDeliveryPoints == null) {
            return null;
        }

        for (Record psdpRecord : productSessionDeliveryPoints) {
            ProductSessionDeliveryPointRecord productSessionDeliveryPointRecord = psdpRecord.into(ProductSessionDeliveryPointRecord.class);
            if (fields > productSessionDeliveryPointRecord.fields().length) {
                productSessionDeliveryPointRecord.setProductDeliveryPointName(psdpRecord.getValue(JOIN_DELIVERY_POINT_NAME));
                productSessionDeliveryPointRecord.setSessionName(psdpRecord.getValue(JOIN_SESSION_NAME));
                productSessionDeliveryPointRecord.setSessionStart(psdpRecord.getValue(JOIN_SESSION_START));
                productSessionDeliveryPointRecord.setSessionEnd(psdpRecord.getValue(JOIN_SESSION_END));
                if (psdpRecord.field(JOIN_SESSION_SB_SESION) != null) {
                    productSessionDeliveryPointRecord.setSessionType(psdpRecord.getValue(JOIN_SESSION_SB_SESION));
                }
                if (psdpRecord.field(JOIN_RECINTO_TEMPLATE_TYPE) != null) {
                    productSessionDeliveryPointRecord.setTemplateType(psdpRecord.getValue(JOIN_RECINTO_TEMPLATE_TYPE));
                }
            }
            result.add(productSessionDeliveryPointRecord);
        }
        return result;
    }

    public void deleteByProductEventId(Integer productEventId) {
        dsl.deleteFrom(CPANEL_PRODUCT_SESSION_DELIVERY_POINT)
                .where(CPANEL_PRODUCT_SESSION_DELIVERY_POINT.PRODUCTEVENTID.eq(productEventId)).execute();
    }

    public Long countRestrictedByProductEvent(Long productId, Long eventId, SessionSearchFilter sessionFilter) {
        Condition where = applyCommonFilters(DSL.trueCondition(), sessionFilter);

        Table<Record1<Integer>> sub = dsl
                .selectDistinct(session.IDSESION)
                .from(productSession)
                .join(productEvent).on(productEvent.PRODUCTEVENTID.eq(productSession.PRODUCTEVENTID)
                        .and(productEvent.PRODUCTID.eq(productId.intValue())))
                .join(session).on(session.IDSESION.eq(productSession.SESSIONID)
                        .and(session.IDEVENTO.eq(eventId.intValue())))
                .where(where)
                .asTable("s");

        return dsl.selectCount().from(sub).fetchOne(0, Long.class);
    }

    public Long countUnrestrictedByEvent(Long eventId, SessionSearchFilter sessionFilter) {
        Condition where = applyCommonFilters(DSL.trueCondition(), sessionFilter);

        return dsl.selectCount()
                .from(session)
                .where(where
                        .and(session.IDEVENTO.eq(eventId.intValue()))
                        .and(session.ESTADO.in(ALLOWED_SESSION_STATUS)))
                .fetchOne(0, Long.class);
    }

    private Condition applyCommonFilters(Condition where, SessionSearchFilter filter) {
        if (filter == null) return where;

        if (filter.getIds() != null && !filter.getIds().isEmpty()) {
            where = where.and(session.IDSESION.in(filter.getIds().stream().map(Long::intValue).toList()));
        }
        if (filter.getStartDate() != null && !filter.getStartDate().isEmpty()) {
            where = JooqUtils.filterDateWithOperatorToCondition(where, filter.getStartDate(), session.FECHAINICIOSESION, AND);
        }
        if (filter.getEndDate() != null) {
            where = JooqUtils.filterDateWithOperatorToCondition(where, filter.getEndDate(), session.FECHAREALFINSESION, AND, session.FECHAFINSESION);
        }
        if (StringUtils.isNotBlank(filter.getFreeSearch())) {
            where = where.and(session.NOMBRE.like("%" + filter.getFreeSearch() + "%"));
        }
        if (filter.getDaysOfWeek() != null && !filter.getDaysOfWeek().isEmpty() && filter.getOlsonId() != null) {
            java.util.List<Integer> dows = filter.getDaysOfWeek().stream().map(java.time.DayOfWeek::getValue).toList();
            Field<Integer> weekDayNumber = DSL.field(
                    "WEEKDAY(CONVERT_TZ({0}, 'UTC', {1})) + 1",
                    Integer.class,
                    session.FECHAINICIOSESION,
                    DSL.inline(filter.getOlsonId())
            );
            where = where.and(weekDayNumber.in(dows));
        }
        where = applyStatus(filter, where);
        return where;
    }

    private Condition applyStatus(SessionSearchFilter filter, Condition where) {
        if (filter == null || filter.getStatus() == null || filter.getStatus().isEmpty()) return where;

        java.util.List<Integer> ids = filter.getStatus().stream()
                .map(SessionStatus::getId)
                .toList();

        return where.and(session.ESTADO.in(ids));
    }
}
