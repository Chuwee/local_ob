package es.onebox.event.products.dao;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.products.domain.ProductEventRecord;
import es.onebox.event.products.dto.ProductEventsFilterDTO;
import es.onebox.event.products.enums.ProductEventStatus;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.jooq.cpanel.tables.CpanelEvento;
import es.onebox.jooq.cpanel.tables.CpanelProduct;
import es.onebox.jooq.cpanel.tables.CpanelProductEvent;
import es.onebox.jooq.cpanel.tables.records.CpanelProductEventRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectFieldOrAsterisk;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static es.onebox.jooq.cpanel.Tables.CPANEL_EVENTO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_PRODUCT;
import static es.onebox.jooq.cpanel.Tables.CPANEL_PRODUCT_EVENT;

@Repository
public class ProductEventDao extends DaoImpl<CpanelProductEventRecord, Integer> {
    private static final CpanelProductEvent productEvent = CpanelProductEvent.CPANEL_PRODUCT_EVENT;
    public static final String FIELD_SESSION_SELECTION_TYPE = CPANEL_PRODUCT_EVENT.SESSIONSSELECTIONTYPE.getName();
    private static final CpanelEvento event = CPANEL_EVENTO;
    private static final CpanelProduct product = CPANEL_PRODUCT;
    private static final Field<String> JOIN_EVENT_NAME = event.NOMBRE.as("eventName");
    private static final Field<String> JOIN_PRODUCT_NAME = product.NAME.as("productName");
    private static final Field<Timestamp> JOIN_START_DATE = event.FECHAINICIO.as("starDate");
    private static final SelectFieldOrAsterisk[] JOIN_FIELDS = {
            JOIN_EVENT_NAME,
            JOIN_PRODUCT_NAME,
            JOIN_START_DATE
    };

    protected ProductEventDao() {
        super(CPANEL_PRODUCT_EVENT);
    }

    public List<ProductEventRecord> findByProductId(Integer productId, boolean includeDeleted) {
        return searchProductEvents(productId, null, includeDeleted);
    }

    public List<ProductEventRecord> searchProductEvents(Integer productId, ProductEventsFilterDTO filter, boolean includeDeleted) {
        List<ProductEventRecord> result = new ArrayList<>();
        SelectFieldOrAsterisk[] fields = ArrayUtils.addAll(productEvent.fields(), JOIN_FIELDS);

        SelectConditionStep<Record> query = dsl
                .select(fields)
                .from(productEvent)
                .innerJoin(event).on(productEvent.EVENTID.eq(event.IDEVENTO))
                .innerJoin(product).on(productEvent.PRODUCTID.eq(product.PRODUCTID))
                .where(buildWhere(productId, filter, includeDeleted));

        List<Record> productRecords = query.fetch();
        for (Record productRecord : productRecords) {
            ProductEventRecord productEventRecord = buildProductEventRecord(productRecord, fields.length);
            result.add(productEventRecord);
        }
        return result;
    }

    private static Condition buildWhere(Integer productId, ProductEventsFilterDTO filter, boolean includeDeleted) {
        Condition condition = productEvent.PRODUCTID.eq(productId);
        if (!includeDeleted) {
            condition = condition.and(productEvent.STATUS.ne(ProductEventStatus.DELETED.getId()));
        }
        if (filter == null) {
            return condition;
        }
        if (CollectionUtils.isNotEmpty(filter.getEventIds())) {
            condition = condition.and(productEvent.EVENTID.in(filter.getEventIds()));
        }
        if (filter.getSessionSelectionType() != null) {
            condition = condition.and(productEvent.SESSIONSSELECTIONTYPE.eq(filter.getSessionSelectionType().getId()));
        }
        if (filter.getStatus() != null) {
            condition = condition.and(productEvent.STATUS.eq(filter.getStatus().getId()));
        }
        if (filter.getEventStatus() != null) {
            List<Integer> eventStatus = filter.getEventStatus().stream().map(EventStatus::getId).toList();
            condition = condition.and(event.ESTADO.in(eventStatus));
        }

        if (filter.getStartDate() != null) {
            condition = condition.and(event.FECHAINICIO.ge(Timestamp.from(filter.getStartDate().getValue().toInstant())));
        }

        return condition;
    }

    public List<CpanelProductEventRecord> findByEventId(Integer eventId, boolean includeDeleted) {
        SelectFieldOrAsterisk[] fields = ArrayUtils.addAll(productEvent.fields(), JOIN_FIELDS);

        SelectConditionStep<Record> query = dsl
                .select(fields)
                .from(productEvent)
                .innerJoin(event).on(productEvent.EVENTID.eq(event.IDEVENTO))
                .innerJoin(product).on(productEvent.PRODUCTID.eq(product.PRODUCTID))
                .where(productEvent.EVENTID.eq(eventId));
        if (!includeDeleted) {
            query.and(productEvent.STATUS.ne(ProductEventStatus.DELETED.getId()));
        }
        return query.fetch().into(CpanelProductEventRecord.class);
    }

    public ProductEventRecord findByProductIdAndEventId(Long productId, Long eventId) {
        SelectFieldOrAsterisk[] fields = ArrayUtils.addAll(productEvent.fields(), JOIN_FIELDS);

        SelectConditionStep<Record> query = dsl
                .select(fields)
                .from(productEvent)
                .innerJoin(event).on(productEvent.EVENTID.eq(event.IDEVENTO))
                .innerJoin(product).on(productEvent.PRODUCTID.eq(product.PRODUCTID))
                .where(productEvent.PRODUCTID.eq(productId.intValue()))
                .and(productEvent.EVENTID.eq(eventId.intValue()))
                .and(productEvent.STATUS.ne(ProductEventStatus.DELETED.getId()));

        try {
            return query.fetchOne().into(ProductEventRecord.class);
        } catch (NullPointerException e) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_EVENT_NOT_FOUND);
        }
    }

    public void updateStatus(Long productId, Long eventId, ProductEventStatus status) {
        dsl.update(productEvent)
                .set(productEvent.STATUS, status.getId())
                .where(productEvent.PRODUCTID.eq(productId.intValue())
                        .and(productEvent.EVENTID.eq(eventId.intValue()))).execute();
    }

    private static ProductEventRecord buildProductEventRecord(Record productRecord,int fields) {
        ProductEventRecord productEventRecord = productRecord.into(ProductEventRecord.class);

        if (fields > productEventRecord.fields().length) {
            productEventRecord.setProductName(productRecord.getValue(JOIN_PRODUCT_NAME));
            productEventRecord.setEventName(productRecord.getValue(JOIN_EVENT_NAME));
            Timestamp timestamp = productRecord.getValue(JOIN_START_DATE);
            if (timestamp != null) {
                productEventRecord.setStartDate(CommonUtils.timestampToZonedDateTime(timestamp));
            }
        }
        return productEventRecord;
    }

    public void updateTargetType(final Integer productEventId, final Map<String, Integer> value) {
        this.dsl.update(productEvent).set(value)
                .where(productEvent.PRODUCTEVENTID.eq(productEventId)).execute();
    }
}
