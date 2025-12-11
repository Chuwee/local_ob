package es.onebox.event.products.dao;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.products.domain.ProductVariantRecord;
import es.onebox.event.products.dto.SearchProductVariantsFilterDTO;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.CpanelPackItem;
import es.onebox.jooq.cpanel.tables.CpanelProduct;
import es.onebox.jooq.cpanel.tables.CpanelProductAttribute;
import es.onebox.jooq.cpanel.tables.CpanelProductAttributeValue;
import es.onebox.jooq.cpanel.tables.CpanelProductVariant;
import es.onebox.jooq.cpanel.tables.records.CpanelProductAttributeValueRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductVariantRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.apache.commons.lang3.ArrayUtils;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static es.onebox.jooq.cpanel.Tables.CPANEL_PRODUCT_VARIANT;

@Repository
public class ProductVariantDao extends DaoImpl<CpanelProductVariantRecord, Integer> {

    private static final CpanelProductVariant productVariant = CpanelProductVariant.CPANEL_PRODUCT_VARIANT.as("productVariant");
    private static final CpanelProduct product = CpanelProduct.CPANEL_PRODUCT.as("product");
    private static final CpanelProductAttribute productAttribute =
            CpanelProductAttribute.CPANEL_PRODUCT_ATTRIBUTE.as("productAttribute");
    private static final CpanelProductAttribute productAttribute2 =
            CpanelProductAttribute.CPANEL_PRODUCT_ATTRIBUTE.as("productAttribute2");
    private static final CpanelProductAttributeValue productAttributeValue =
            CpanelProductAttributeValue.CPANEL_PRODUCT_ATTRIBUTE_VALUE.as("productAttributeValue");
    private static final CpanelProductAttributeValue productAttributeValue2 =
            CpanelProductAttributeValue.CPANEL_PRODUCT_ATTRIBUTE_VALUE.as("productAttributeValue2");

    private static final SelectFieldOrAsterisk[] fields = ArrayUtils.addAll(productVariant.fields(), product.NAME.as("productName"),
            productAttribute.NAME.as("productAttributeName1"), productAttributeValue.NAME.as("productAttributeValueName1"),
            productAttribute2.NAME.as("productAttributeName2"), productAttributeValue2.NAME.as("productAttributeValueName2"));

    protected ProductVariantDao() {
        super(CPANEL_PRODUCT_VARIANT);
    }

    public Long getCountProductVariantsByProductId(Long productId) {
        return dsl.selectCount()
                .from(productVariant)
                .where(productVariant.PRODUCTID.eq(productId.intValue()))
                .fetchOne(0, Long.class);
    }

    public List<CpanelProductVariantRecord> getProductVariantsByProductId(Long productId) {
        return dsl.select(productVariant.fields())
                .from(productVariant)
                .where(productVariant.PRODUCTID.eq(productId.intValue()))
                .fetch().into(CpanelProductVariantRecord.class);
    }

    public List<CpanelProductVariantRecord> getProductVariantsByProductIdAndValueId(Long productId, Long valueId) {
        return dsl.select(productVariant.fields())
                .from(productVariant)
                .where(productVariant.PRODUCTID.eq(productId.intValue()))
                .and(
                        productVariant.VARIANTVALUE1.eq(valueId.intValue())
                                .or(productVariant.VARIANTVALUE2.eq(valueId.intValue()))
                )
                .fetch().into(CpanelProductVariantRecord.class);
    }

    public int updateProductVariantPrices(Set<Long> variants, double price) {
        return dsl.update(productVariant).set(productVariant.PRICE, price).where(productVariant.VARIANTID.in(variants)).execute();
    }

    public ProductVariantRecord findById(Long productId, Long variantId) {
        SelectConditionStep<Record> query = buildBaseQuery(productId, variantId, null);
        return query.fetchOptional().map(ProductVariantDao::buildProductVariant).orElse(null);
    }

    public Long getTotalProductVariants(Long productId,
                                        SearchProductVariantsFilterDTO searchProductVariantsFilterDTO) {
        try {
            SelectConditionStep<Record1<Integer>> query = dsl.selectCount()
                    .from(productVariant)
                    .where(builderWhereClause(productId, null, searchProductVariantsFilterDTO));

            return query.fetchOne().into(Long.class);
        } catch (Exception e) {
            throw new OneboxRestException(CoreErrorCode.GENERIC_ERROR, "Error executing query", e);
        }
    }

    public List<ProductVariantRecord> searchProductVariants(final Long productId,
                                                            final SearchProductVariantsFilterDTO filter) {
        SelectConditionStep<Record> query = buildBaseQuery(productId, null, filter);
        if (filter.getLimit() != null) {
            query.limit(filter.getLimit().intValue());
        }
        if (filter.getOffset() != null) {
            query.offset(filter.getOffset().intValue());
        }

        return query.fetch()
                .map(ProductVariantDao::buildProductVariant);
    }

    public void deleteByValue(Long productId, Long attributeId, Long valueId) {
        dsl.deleteFrom(Tables.CPANEL_PRODUCT_VARIANT)
                .where(CPANEL_PRODUCT_VARIANT.PRODUCTID.eq(productId.intValue()))
                .and(CPANEL_PRODUCT_VARIANT.VARIANTOPTION1.eq(attributeId.intValue()))
                .and(CPANEL_PRODUCT_VARIANT.VARIANTVALUE1.eq(valueId.intValue()))
                .execute();

        dsl.deleteFrom(Tables.CPANEL_PRODUCT_VARIANT)
                .where(CPANEL_PRODUCT_VARIANT.PRODUCTID.eq(productId.intValue()))
                .and(CPANEL_PRODUCT_VARIANT.VARIANTOPTION2.eq(attributeId.intValue()))
                .and(CPANEL_PRODUCT_VARIANT.VARIANTVALUE2.eq(valueId.intValue()))
                .execute();
    }

    private static ProductVariantRecord buildProductVariant(final Record productVariantRecord) {
        ProductVariantRecord result = productVariantRecord.into(ProductVariantRecord.class);
        result.setProductName(productVariantRecord.get(product.NAME.as("productName")));
        result.setProductFirstAttributeName(productVariantRecord.get(productAttribute.NAME.as("productAttributeName1")));
        result.setProductSecondAttributeName(productVariantRecord.get(productAttribute2.NAME.as("productAttributeName2")));
        result.setProductFirstValueName(productVariantRecord.get(productAttributeValue.NAME.as("productAttributeValueName1")));
        result.setProductSecondValueName(productVariantRecord.get(productAttributeValue2.NAME.as("productAttributeValueName2")));

        return result;
    }

    private SelectConditionStep<Record> buildBaseQuery(Long productId, Long variantId, SearchProductVariantsFilterDTO filter) {
        return dsl
                .select(fields)
                .from(productVariant)
                .join(product).on(productVariant.PRODUCTID.eq(product.PRODUCTID))
                .leftJoin(productAttribute).on(productVariant.VARIANTOPTION1.eq(productAttribute.ATTRIBUTEID))
                .leftJoin(productAttribute2).on(productVariant.VARIANTOPTION2.eq(productAttribute2.ATTRIBUTEID))
                .leftJoin(productAttributeValue).on(productVariant.VARIANTVALUE1.eq(productAttributeValue.VALUEID))
                .leftJoin(productAttributeValue2).on(productVariant.VARIANTVALUE2.eq(productAttributeValue2.VALUEID))
                .where(builderWhereClause(productId, variantId, filter));
    }

    private Condition builderWhereClause(Long productId, Long variantId, SearchProductVariantsFilterDTO filter) {
        Condition conditions = DSL.trueCondition();
        conditions = conditions.and(productVariant.PRODUCTID.eq(productId.intValue()));

        if (filter != null) {
            if (filter.getQ() != null) {
                conditions = conditions.and(productVariant.NAME.like("%" + filter.getQ() + "%"));
            }
            if (filter.getIds() != null) {
                conditions = conditions.and(productVariant.VARIANTID.in(filter.getIds()));
            }
            if (filter.getStatus() != null) {
                conditions = conditions.and(productVariant.STATUS.eq(filter.getStatus().getId()));
            }
        }
        if (variantId != null) {
            conditions = conditions.and(productVariant.VARIANTID.eq(variantId.intValue()));
        }

        return conditions;
    }

    public Set<String> getExistingVariantKeys(Long productId, Integer attributeId1, List<CpanelProductAttributeValueRecord> valuesForFirstAttribute,
                                              Integer attributeId2, List<CpanelProductAttributeValueRecord> valuesForSecondAttribute) {

        Condition conditions = buildVariantConditions(productId, attributeId1, valuesForFirstAttribute, attributeId2, valuesForSecondAttribute);

        return dsl.select(productVariant.VARIANTOPTION1,
                        productVariant.VARIANTVALUE1,
                        productVariant.VARIANTOPTION2,
                        productVariant.VARIANTVALUE2)
                .from(productVariant)
                .where(conditions)
                .fetch()
                .stream()
                .map(variantKey -> String.format("%d:%d:%d:%d",
                        variantKey.get(productVariant.VARIANTOPTION1),
                        variantKey.get(productVariant.VARIANTVALUE1),
                        variantKey.get(productVariant.VARIANTOPTION2),
                        variantKey.get(productVariant.VARIANTVALUE2)))
                .collect(Collectors.toSet());
    }

    public List<CpanelProductVariantRecord> getProductVariantsByAttributes(
            Long productId,
            Integer attributeId1,
            List<CpanelProductAttributeValueRecord> valuesForFirstAttribute,
            Integer attributeId2,
            List<CpanelProductAttributeValueRecord> valuesForSecondAttribute) {

        Condition conditions = buildVariantConditions(productId, attributeId1, valuesForFirstAttribute, attributeId2, valuesForSecondAttribute);

        return dsl.select(productVariant.VARIANTID, productVariant.NAME)
                .from(productVariant)
                .where(conditions)
                .fetchInto(CpanelProductVariantRecord.class);
    }

    private Condition buildVariantConditions(Long productId, Integer attributeId1,
                                             List<CpanelProductAttributeValueRecord> valuesForFirstAttribute,
                                             Integer attributeId2, List<CpanelProductAttributeValueRecord> valuesForSecondAttribute) {
        Set<Integer> valuesIdSet1 = valuesForFirstAttribute.stream()
                .map(CpanelProductAttributeValueRecord::getValueid)
                .collect(Collectors.toSet());

        Set<Integer> valuesIdSet2 = null;
        if (valuesForSecondAttribute != null) {
            valuesIdSet2 = valuesForSecondAttribute.stream()
                    .map(CpanelProductAttributeValueRecord::getValueid)
                    .collect(Collectors.toSet());
        }

        Condition condition = DSL.trueCondition()
                .and(productVariant.PRODUCTID.eq(productId.intValue()))
                .and(productVariant.VARIANTOPTION1.eq(attributeId1))
                .and(productVariant.VARIANTVALUE1.in(valuesIdSet1));
        if (valuesIdSet2 != null) {
            condition.and(productVariant.VARIANTOPTION2.eq(attributeId2))
                    .and(productVariant.VARIANTVALUE2.in(valuesIdSet2));
        }
        return condition;
    }

    public Map<Integer, Double> getProductVariantPricesByPackId(Integer packId) {
        CpanelPackItem packItem = Tables.CPANEL_PACK_ITEM;
        return dsl.select(packItem.IDPACKITEM, productVariant.PRICE)
                .from(packItem)
                .join(productVariant).on(productVariant.VARIANTID.eq(packItem.IDVARIANTE))
                .where(packItem.IDPACK.eq(packId))
                .fetch()
                .collect(Collectors.toMap(r -> r.get(packItem.IDPACKITEM),
                        r -> r.get(productVariant.PRICE)));
    }
}
