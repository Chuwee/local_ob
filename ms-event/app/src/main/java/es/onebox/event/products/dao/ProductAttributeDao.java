package es.onebox.event.products.dao;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.jooq.cpanel.tables.CpanelProduct;
import es.onebox.jooq.cpanel.tables.CpanelProductAttribute;
import es.onebox.jooq.cpanel.tables.records.CpanelProductAttributeRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_PRODUCT_ATTRIBUTE;

@Repository
public class ProductAttributeDao extends DaoImpl<CpanelProductAttributeRecord, Integer> {
    private static final CpanelProductAttribute productAttribute = CPANEL_PRODUCT_ATTRIBUTE.as("productAttribute");
    private static final CpanelProduct product = CpanelProduct.CPANEL_PRODUCT.as("product");

    protected ProductAttributeDao() {
        super(CPANEL_PRODUCT_ATTRIBUTE);
    }

    public Integer getTotalAttributes(Long productId) {
        try {
            return dsl.selectCount()
                    .from(productAttribute)
                    .where(productAttribute.PRODUCTID.eq(productId.intValue()))
                    .fetchOptionalInto(Integer.class)
                    .orElse(null);
        } catch (Exception e) {
            throw new OneboxRestException(CoreErrorCode.GENERIC_ERROR, "Error executing query", e);
        }
    }

    public List<CpanelProductAttributeRecord> findByProductId(Long productId) {
        SelectConditionStep<Record> query = dsl
                .select(productAttribute.fields())
                .from(productAttribute)
                .innerJoin(product).on(productAttribute.PRODUCTID.eq(product.PRODUCTID))
                .where(productAttribute.PRODUCTID.eq(productId.intValue()));

        return query.fetch().into(CpanelProductAttributeRecord.class);
    }
}
