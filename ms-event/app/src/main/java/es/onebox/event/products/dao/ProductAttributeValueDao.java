package es.onebox.event.products.dao;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.products.dto.SearchProductAttributeValueFilterDTO;
import es.onebox.jooq.cpanel.tables.CpanelProductAttribute;
import es.onebox.jooq.cpanel.tables.CpanelProductAttributeValue;
import es.onebox.jooq.cpanel.tables.records.CpanelProductAttributeValueRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_PRODUCT_ATTRIBUTE;
import static es.onebox.jooq.cpanel.Tables.CPANEL_PRODUCT_ATTRIBUTE_VALUE;

@Repository
public class ProductAttributeValueDao extends DaoImpl<CpanelProductAttributeValueRecord, Integer> {
    private static final CpanelProductAttributeValue productAttributeValue = CPANEL_PRODUCT_ATTRIBUTE_VALUE.as("productAttributeValue");
    private static final CpanelProductAttribute productAttribute = CPANEL_PRODUCT_ATTRIBUTE.as("productAttribute");

    protected ProductAttributeValueDao() {
        super(CPANEL_PRODUCT_ATTRIBUTE_VALUE);
    }

    public Long getTotalAttributeValuesByProduct(Long productId) {
        try {
            return dsl.selectCount()
                    .from(productAttributeValue)
                    .innerJoin(productAttribute).on(productAttributeValue.PRODUCTATTRIBUTEID.eq(productAttribute.ATTRIBUTEID))
                    .where(productAttribute.PRODUCTID.eq(productId.intValue()))
                    .fetchOptionalInto(Long.class)
                    .orElse(null);
        } catch (Exception e) {
            throw new OneboxRestException(CoreErrorCode.GENERIC_ERROR, "Error executing query", e);
        }
    }

    public Long getTotalAttributeValues(Long productId, Long attributeId) {
        try {
            return dsl.selectCount()
                    .from(productAttributeValue)
                    .innerJoin(productAttribute).on(productAttributeValue.PRODUCTATTRIBUTEID.eq(productAttribute.ATTRIBUTEID))
                    .where(productAttribute.PRODUCTID.eq(productId.intValue()))
                    .and(productAttribute.ATTRIBUTEID.eq(attributeId.intValue()))
                    .fetchOptionalInto(Long.class)
                    .orElse(null);
        } catch (Exception e) {
            throw new OneboxRestException(CoreErrorCode.GENERIC_ERROR, "Error executing query", e);
        }
    }

    public List<CpanelProductAttributeValueRecord> findByProductId(Long productId) {
        SelectConditionStep<Record> query = dsl
                .select(productAttributeValue.fields())
                .from(productAttributeValue)
                .innerJoin(productAttribute).on(productAttributeValue.PRODUCTATTRIBUTEID.eq(productAttribute.ATTRIBUTEID))
                .where(productAttribute.PRODUCTID.eq(productId.intValue()));

        return query.fetch().into(CpanelProductAttributeValueRecord.class);
    }

    public List<CpanelProductAttributeValueRecord> getProductAttributeValues(Long attributeId,
                                                                             SearchProductAttributeValueFilterDTO filter) {
        List<CpanelProductAttributeValueRecord> result = new ArrayList<>();

        try {
            SelectConditionStep<Record> query = dsl
                    .select(productAttributeValue.fields())
                    .from(productAttributeValue)
                    .innerJoin(productAttribute).on(productAttributeValue.PRODUCTATTRIBUTEID.eq(productAttribute.ATTRIBUTEID))
                    .where(productAttributeValue.PRODUCTATTRIBUTEID.eq(attributeId.intValue()));

            if (filter.getLimit() != null) {
                query.limit(filter.getLimit().intValue());
            }
            if (filter.getOffset() != null) {
                query.offset(filter.getOffset().intValue());
            }

            List<Record> records = query.fetch();

            for (Record recordValue : records) {
                CpanelProductAttributeValueRecord productAttributeValueRecord = buildProductAttributeValueRecord(recordValue);
                result.add(productAttributeValueRecord);
            }
            return result;
        } catch (Exception e) {
            throw new OneboxRestException(CoreErrorCode.GENERIC_ERROR, "Error executing query", e);
        }
    }

    private static CpanelProductAttributeValueRecord buildProductAttributeValueRecord(Record recordValue) {
        return recordValue.into(CpanelProductAttributeValueRecord.class);
    }
}
