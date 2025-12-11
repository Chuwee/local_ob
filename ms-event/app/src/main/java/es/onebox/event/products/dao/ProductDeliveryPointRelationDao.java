package es.onebox.event.products.dao;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.products.domain.ProductDeliveryPointRelationRecord;
import es.onebox.event.products.dto.SearchProductDeliveryPointRelationFilterDTO;
import es.onebox.event.products.enums.DeliveryPointStatus;
import es.onebox.jooq.cpanel.tables.CpanelDeliveryPoint;
import es.onebox.jooq.cpanel.tables.CpanelProduct;
import es.onebox.jooq.cpanel.tables.CpanelProductDeliveryPoint;
import es.onebox.jooq.cpanel.tables.records.CpanelProductDeliveryPointRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.apache.commons.lang3.ArrayUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_DELIVERY_POINT;
import static es.onebox.jooq.cpanel.Tables.CPANEL_PRODUCT;
import static es.onebox.jooq.cpanel.Tables.CPANEL_PRODUCT_DELIVERY_POINT;

@Repository
public class ProductDeliveryPointRelationDao extends DaoImpl<CpanelProductDeliveryPointRecord, Integer> {

    private static final CpanelProductDeliveryPoint productDeliveryPoint = CPANEL_PRODUCT_DELIVERY_POINT.as("productDeliveryPoint");
    private static final CpanelProduct product = CPANEL_PRODUCT.as("product");
    private static final CpanelDeliveryPoint deliveryPoint = CPANEL_DELIVERY_POINT.as("deliveryPoint");

    private static final Field<String> JOIN_PRODUCT_NAME = product.NAME.as("productName");
    private static final Field<String> JOIN_DELIVERY_POINT_NAME = deliveryPoint.NAME.as("deliveryPointName");

    private static final SelectFieldOrAsterisk[] JOIN_FIELDS = {
            JOIN_PRODUCT_NAME,
            JOIN_DELIVERY_POINT_NAME
    };

    protected ProductDeliveryPointRelationDao() {
        super(CPANEL_PRODUCT_DELIVERY_POINT);
    }

    public ProductDeliveryPointRelationRecord findByRelationId(Long productId, Long deliveryPointId) {
        SelectFieldOrAsterisk[] fields = ArrayUtils.addAll(productDeliveryPoint.fields(), JOIN_FIELDS);

        SelectConditionStep query = dsl
                .select(fields)
                .from(productDeliveryPoint)
                .innerJoin(product).on(product.PRODUCTID.eq(productDeliveryPoint.PRODUCTID))
                .innerJoin(deliveryPoint).on(deliveryPoint.DELIVERYPOINTID.eq(productDeliveryPoint.DELIVERYPOINTID))
                .where(productDeliveryPoint.PRODUCTID.eq(productId.intValue()))
                .and(productDeliveryPoint.DELIVERYPOINTID.eq(deliveryPointId.intValue()));
        Record result = query.fetchOne();
        return buildProductDeliveryPointRelationRecord(result, fields.length);
    }

    public List<ProductDeliveryPointRelationRecord> findByProductId(Long productId) {
        SelectConditionStep<Record> query = dsl
                .select(productDeliveryPoint.fields())
                .from(productDeliveryPoint)
                .innerJoin(deliveryPoint).on(deliveryPoint.DELIVERYPOINTID.eq(productDeliveryPoint.DELIVERYPOINTID)
                        .and(deliveryPoint.DELIVERYPOINTSTATUS.eq(DeliveryPointStatus.ACTIVE.getId())))
                .where(productDeliveryPoint.PRODUCTID.eq(productId.intValue()));

        return query.fetch().into(ProductDeliveryPointRelationRecord.class);
    }

    public Long getTotalProductDeliveryPointsRelations(Long productId, SearchProductDeliveryPointRelationFilterDTO searchProductDeliveryPointFilterRelationFilterDTO) {
        try {
            SelectJoinStep query = dsl.selectCount()
                    .from(productDeliveryPoint)
                    .innerJoin(product).on(product.PRODUCTID.eq(productDeliveryPoint.PRODUCTID))
                    .innerJoin(deliveryPoint).on(deliveryPoint.DELIVERYPOINTID.eq(productDeliveryPoint.DELIVERYPOINTID));

            query.where(builderWhereClause(productId, searchProductDeliveryPointFilterRelationFilterDTO));

            return query.fetchOne().into(Long.class);
        } catch (Exception e) {
            throw new OneboxRestException(CoreErrorCode.GENERIC_ERROR, "Error executing query", e);
        }
    }

    public List<ProductDeliveryPointRelationRecord> getProductDeliveryPointsRelations(Long productId, SearchProductDeliveryPointRelationFilterDTO searchProductDeliveryPointRelationFilterDTO) {
        List<ProductDeliveryPointRelationRecord> result = new ArrayList<>();
        try {
            SelectFieldOrAsterisk[] fields = ArrayUtils.addAll(productDeliveryPoint.fields(), JOIN_FIELDS);

            SelectConditionStep query = dsl.select(fields)
                    .from(productDeliveryPoint)
                    .innerJoin(product).on(product.PRODUCTID.eq(productDeliveryPoint.PRODUCTID))
                    .innerJoin(deliveryPoint).on(deliveryPoint.DELIVERYPOINTID.eq(productDeliveryPoint.DELIVERYPOINTID))
                    .where(builderWhereClause(productId, searchProductDeliveryPointRelationFilterDTO));

            if (searchProductDeliveryPointRelationFilterDTO != null) {
                if (searchProductDeliveryPointRelationFilterDTO.getLimit() != null) {
                    query.limit(searchProductDeliveryPointRelationFilterDTO.getLimit().intValue());
                }
                if (searchProductDeliveryPointRelationFilterDTO.getOffset() != null) {
                    query.offset(searchProductDeliveryPointRelationFilterDTO.getOffset().intValue());
                }
            }

            List<Record> records = query.fetch();
            for (Record record : records) {
                ProductDeliveryPointRelationRecord productDeliveryPointRelationRecord = buildProductDeliveryPointRelationRecord(record, fields.length);
                result.add(productDeliveryPointRelationRecord);
            }
            return result;
        } catch (Exception e) {
            throw new OneboxRestException(CoreErrorCode.GENERIC_ERROR, "Error executing query", e);
        }
    }

    private Condition builderWhereClause(Long productId, SearchProductDeliveryPointRelationFilterDTO filter) {
        Condition conditions = DSL.trueCondition();
        if (filter != null && filter.getEntityIds() != null) {
            conditions = conditions.and(deliveryPoint.ENTITYID.in(filter.getEntityIds()));
        }
        if (productId != null) {
            conditions = conditions.and(product.PRODUCTID.eq(productId.intValue()));
        }
        return conditions;
    }

    private static ProductDeliveryPointRelationRecord buildProductDeliveryPointRelationRecord(Record productDeliveryPointRelationRecord, int fields) {
        if (productDeliveryPointRelationRecord == null) {
            return null;
        }
        ProductDeliveryPointRelationRecord productDeliveryPointRelation = productDeliveryPointRelationRecord.into(ProductDeliveryPointRelationRecord.class);

        //Add join fields only if has been added to base event fields
        if (fields > productDeliveryPointRelation.fields().length) {
            productDeliveryPointRelation.setProductName(productDeliveryPointRelationRecord.getValue(JOIN_PRODUCT_NAME));
            productDeliveryPointRelation.setProductDeliveryPointName(productDeliveryPointRelationRecord.getValue(JOIN_DELIVERY_POINT_NAME));
        }

        return productDeliveryPointRelation;
    }

}
