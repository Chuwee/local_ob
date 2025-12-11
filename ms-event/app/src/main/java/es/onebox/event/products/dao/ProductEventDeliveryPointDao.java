package es.onebox.event.products.dao;

import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.products.domain.ProductEventDeliveryPointRecord;
import es.onebox.event.products.enums.DeliveryPointStatus;
import es.onebox.event.products.enums.ProductEventStatus;
import es.onebox.jooq.cpanel.tables.CpanelDeliveryPoint;
import es.onebox.jooq.cpanel.tables.CpanelEvento;
import es.onebox.jooq.cpanel.tables.CpanelProduct;
import es.onebox.jooq.cpanel.tables.CpanelProductEvent;
import es.onebox.jooq.cpanel.tables.CpanelProductEventDeliveryPoint;
import es.onebox.jooq.cpanel.tables.records.CpanelProductEventDeliveryPointRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.apache.commons.lang3.ArrayUtils;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectFieldOrAsterisk;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_EVENTO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_PRODUCT;
import static es.onebox.jooq.cpanel.Tables.CPANEL_PRODUCT_EVENT;
import static es.onebox.jooq.cpanel.Tables.CPANEL_PRODUCT_EVENT_DELIVERY_POINT;
import static es.onebox.jooq.cpanel.Tables.CPANEL_DELIVERY_POINT;
import static org.jooq.impl.DSL.count;

@Repository
public class ProductEventDeliveryPointDao extends DaoImpl<CpanelProductEventDeliveryPointRecord, Integer> {

    private static final CpanelProductEventDeliveryPoint productEventDeliveryPoint = CPANEL_PRODUCT_EVENT_DELIVERY_POINT.as("productEventDeliveryPoint");
    private static final CpanelProductEvent productEvent = CPANEL_PRODUCT_EVENT.as("productEvent");
    private static final CpanelProduct product = CPANEL_PRODUCT.as("product");
    private static final CpanelDeliveryPoint deliveryPoint = CPANEL_DELIVERY_POINT.as("deliveryPoint");
    private static final CpanelEvento event = CPANEL_EVENTO.as("event");

    private static final Field<Integer> JOIN_PRODUCT_ID = product.PRODUCTID.as("productId");
    private static final Field<String> JOIN_PRODUCT_NAME = product.NAME.as("productName");
    private static final Field<String> JOIN_DELIVERY_POINT_NAME = deliveryPoint.NAME.as("deliveryPointName");
    private static final Field<Integer> JOIN_EVENT_ID = event.IDEVENTO.as("eventId");
    private static final Field<String> JOIN_EVENT_NAME = event.NOMBRE.as("eventName");
    private static final List<Integer> ALLOWED_EVENT_STATUS = Arrays.asList(EventStatus.PLANNED.getId(), EventStatus.IN_PROGRAMMING.getId(), EventStatus.READY.getId(), EventStatus.IN_PROGRESS.getId());

    private static final SelectFieldOrAsterisk[] JOIN_FIELDS = {
            JOIN_PRODUCT_ID,
            JOIN_PRODUCT_NAME,
            JOIN_DELIVERY_POINT_NAME,
            JOIN_EVENT_ID,
            JOIN_EVENT_NAME
    };

    protected ProductEventDeliveryPointDao() {
        super(CPANEL_PRODUCT_EVENT_DELIVERY_POINT);
    }

    public List<ProductEventDeliveryPointRecord> findByProductEvent(Long productId, Long eventId) {
        SelectFieldOrAsterisk[] fields = ArrayUtils.addAll(productEventDeliveryPoint.fields(), JOIN_FIELDS);

        SelectConditionStep query = dsl
                .select(fields)
                .from(productEventDeliveryPoint)
                .innerJoin(productEvent).on(productEvent.PRODUCTEVENTID.eq(productEventDeliveryPoint.PRODUCTEVENTID))
                .innerJoin(product).on(product.PRODUCTID.eq(productEvent.PRODUCTID))
                .innerJoin(deliveryPoint).on(deliveryPoint.DELIVERYPOINTID.eq(productEventDeliveryPoint.DELIVERYPOINTID))
                .innerJoin(event).on(event.IDEVENTO.eq(productEvent.EVENTID))
                .where(productEvent.PRODUCTID.eq(productId.intValue()))
                .and(productEvent.EVENTID.eq(eventId.intValue()));
        List<Record> result = query.fetch();
        return buildProductEventDeliveryPointRecord(result, fields.length);
    }

    public List<ProductEventDeliveryPointRecord> findActivesByProductId(Long productId) {
        SelectFieldOrAsterisk[] fields = ArrayUtils.addAll(productEventDeliveryPoint.fields(), JOIN_FIELDS);
        SelectConditionStep<Record> query = dsl
                .select(fields)
                .from(productEventDeliveryPoint)
                .innerJoin(productEvent).on(productEvent.PRODUCTEVENTID.eq(productEventDeliveryPoint.PRODUCTEVENTID))
                .innerJoin(deliveryPoint).on(deliveryPoint.DELIVERYPOINTID.eq(productEventDeliveryPoint.DELIVERYPOINTID)
                        .and(deliveryPoint.DELIVERYPOINTSTATUS.eq(DeliveryPointStatus.ACTIVE.getId())))
                .innerJoin(event).on(event.IDEVENTO.eq(productEvent.EVENTID)).and(event.ESTADO.in(ALLOWED_EVENT_STATUS))
                .innerJoin(product).on(product.PRODUCTID.eq(productEvent.PRODUCTID))
                .where(productEvent.PRODUCTID.eq(productId.intValue()));
        List<Record> result = query.fetch();
        return buildProductEventDeliveryPointRecord(result, fields.length);
    }

    public boolean existsProductEventWithoutDelivery(Integer productId) {
        return dsl
                .select(productEvent.PRODUCTEVENTID, count(productEventDeliveryPoint.DELIVERYPOINTID))
                .from(productEvent)
                .leftJoin(productEventDeliveryPoint).on(productEvent.PRODUCTEVENTID.eq(productEventDeliveryPoint.PRODUCTEVENTID))
                .where(productEvent.PRODUCTID.eq(productId))
                .and(productEvent.STATUS.eq(ProductEventStatus.ACTIVE.getId()))
                .groupBy(productEvent.PRODUCTEVENTID)
                .having(count(productEventDeliveryPoint.DELIVERYPOINTID).eq(0))
                .stream().findAny().isPresent();
    }

    private static List<ProductEventDeliveryPointRecord> buildProductEventDeliveryPointRecord(List<Record> productEventDeliveryPoints, int fields) {
        List<ProductEventDeliveryPointRecord> result = new ArrayList<>();
        if(productEventDeliveryPoints == null) {
            return null;
        }

        for(Record record : productEventDeliveryPoints) {
            ProductEventDeliveryPointRecord productEventDeliveryPointRecord = record.into(ProductEventDeliveryPointRecord.class);
            if (fields > productEventDeliveryPointRecord.fields().length) {
                productEventDeliveryPointRecord.setProductId(record.getValue(JOIN_PRODUCT_ID));
                productEventDeliveryPointRecord.setProductName(record.getValue(JOIN_PRODUCT_NAME));
                productEventDeliveryPointRecord.setProductDeliveryPointName(record.getValue(JOIN_DELIVERY_POINT_NAME));
                productEventDeliveryPointRecord.setEventId(record.getValue(JOIN_EVENT_ID));
                productEventDeliveryPointRecord.setEventName(record.getValue(JOIN_EVENT_NAME));
            }
            result.add(productEventDeliveryPointRecord);
        }
        return result;
    }

    public void deleteByProductEventId(Integer productEventId) {
        dsl.deleteFrom(CPANEL_PRODUCT_EVENT_DELIVERY_POINT)
                .where(CPANEL_PRODUCT_EVENT_DELIVERY_POINT.PRODUCTEVENTID.eq(productEventId)).execute();
    }

}
