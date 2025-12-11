package es.onebox.event.products.dao;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.priceengine.simulation.record.ProductRecord;
import es.onebox.event.priceengine.simulation.record.ProductDetailRecord;
import es.onebox.event.priceengine.sorting.SortUtils;
import es.onebox.event.products.dto.SearchProductFilterDTO;
import es.onebox.event.products.enums.ProductState;
import es.onebox.event.sorting.ProductField;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.CpanelEntidad;
import es.onebox.jooq.cpanel.tables.CpanelImpuesto;
import es.onebox.jooq.cpanel.tables.CpanelProduct;
import es.onebox.jooq.cpanel.tables.CpanelProductEvent;
import es.onebox.jooq.cpanel.tables.CpanelProductSession;
import es.onebox.jooq.cpanel.tables.CpanelPromotor;
import es.onebox.jooq.cpanel.tables.CpanelTaxonomiaBase;
import es.onebox.jooq.cpanel.tables.CpanelTaxonomiaPropia;
import es.onebox.jooq.cpanel.tables.records.CpanelProductRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.apache.commons.lang3.ArrayUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.SelectSeekStepN;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_PRODUCT;

@Repository
public class ProductDao extends DaoImpl<CpanelProductRecord, Integer> {
    private static final CpanelProduct product = CpanelProduct.CPANEL_PRODUCT.as("product");
    private static final CpanelEntidad productEntity = CpanelEntidad.CPANEL_ENTIDAD.as("entity");
    private static final CpanelPromotor productPromoter = CpanelPromotor.CPANEL_PROMOTOR.as("promoter");
    private static final CpanelImpuesto productTax = CpanelImpuesto.CPANEL_IMPUESTO.as("tax");
    private static final CpanelProductEvent productEvent = CpanelProductEvent.CPANEL_PRODUCT_EVENT.as("event");
    private static final CpanelProductSession productSession = CpanelProductSession.CPANEL_PRODUCT_SESSION.as("session");
    private static final CpanelImpuesto productSurchargeTax = CpanelImpuesto.CPANEL_IMPUESTO.as("surchargeTax");
    private static final CpanelTaxonomiaBase taxonomy = Tables.CPANEL_TAXONOMIA_BASE.as("taxonomiaBase");
    private static final CpanelTaxonomiaPropia customTaxonomy = Tables.CPANEL_TAXONOMIA_PROPIA.as("taxonomiaPropia");
    private static final Field<String> JOIN_ENTITY_NAME = productEntity.NOMBRE.as("entityName");
    private static final Field<String> JOIN_PROMOTER_NAME = productPromoter.NOMBRE.as("promoterName");
    private static final Field<String> JOIN_TAX_NAME = productTax.NOMBRE.as("taxName");
    private static final Field<String> JOIN_SURCHARGE_TAX_NAME = productSurchargeTax.NOMBRE.as("surchargeTaxName");
    private static final Field<String> JOIN_CATEGORY_DESC = taxonomy.DESCRIPCION.as("categoryDesc");
    private static final Field<String> JOIN_CATEGORY_CODE = taxonomy.CODIGO.as("categoryCode");
    private static final Field<String> JOIN_CUSTOM_CATEGORY_DESC = customTaxonomy.DESCRIPCION.as("customCategoryDesc");
    private static final Field<String> JOIN_CUSTOM_CATEGORY_REF = customTaxonomy.REFERENCIA.as("customCategoryRef");

    private static final Field<?>[] JOIN_FIELDS =
            ArrayUtils.addAll(product.fields(), JOIN_ENTITY_NAME, JOIN_PROMOTER_NAME, JOIN_TAX_NAME, JOIN_SURCHARGE_TAX_NAME);
    private static final Field<?>[] DETAILED_JOIN_FIELDS =
            ArrayUtils.addAll(JOIN_FIELDS, JOIN_CATEGORY_DESC, JOIN_CATEGORY_CODE, JOIN_CUSTOM_CATEGORY_DESC, JOIN_CUSTOM_CATEGORY_REF);

    protected ProductDao() {
        super(CPANEL_PRODUCT);
    }

    public ProductDetailRecord findProductDetails(int productId) {

        SelectConditionStep<Record> query = dsl.select(DETAILED_JOIN_FIELDS)
                .from(product)
                .innerJoin(productEntity).on(product.ENTITYID.eq(productEntity.IDENTIDAD))
                .innerJoin(productPromoter).on(product.PRODUCERID.eq(productPromoter.IDPROMOTOR))
                .leftJoin(productTax).on(product.TAXID.eq(productTax.IDIMPUESTO))
                .leftJoin(productSurchargeTax).on(product.SURCHAGETAXID.eq(productSurchargeTax.IDIMPUESTO))
                .leftJoin(taxonomy).on(taxonomy.IDTAXONOMIA.eq(product.TAXONOMYID))
                .leftJoin(customTaxonomy).on(customTaxonomy.IDTAXONOMIA.eq(product.CUSTOMTAXONOMYID))
                .where(product.PRODUCTID.eq(productId));
        try {
            return buildProductDetailRecord(query.fetchOne());
        } catch (NullPointerException e) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_NOT_FOUND);
        }
    }

    public List<CpanelProductRecord> getProductsInfo(List<Integer> productIds) {
        return dsl.select(product.PRODUCTID, product.NAME)
                .from(product)
                .where(product.PRODUCTID.in(productIds))
                .fetchInto(CpanelProductRecord.class);
    }

    public Long getTotalProducts(SearchProductFilterDTO searchProductFilterDTO) {
        try {
            SelectConditionStep<Record1<Integer>> query = dsl.selectCount()
                    .from(product)
                    .innerJoin(productEntity).on(product.ENTITYID.eq(productEntity.IDENTIDAD))
                    .innerJoin(productPromoter).on(product.PRODUCERID.eq(productPromoter.IDPROMOTOR))
                    .leftJoin(productTax).on(product.TAXID.eq(productTax.IDIMPUESTO))
                    .leftJoin(productSurchargeTax).on(product.SURCHAGETAXID.eq(productSurchargeTax.IDIMPUESTO))
                    .where(builderWhereClause(searchProductFilterDTO));

            return query.fetchOne().into(Long.class);
        } catch (Exception e) {
            throw new OneboxRestException(CoreErrorCode.GENERIC_ERROR, "Error executing query", e);
        }
    }

    public List<CpanelProductRecord> findProducts(Long entityId, String name) {
        try {
            SelectConditionStep<Record> query = dsl.select(product.fields())
                    .from(product)
                    .where(product.ENTITYID.eq(entityId.intValue()))
                    .and(product.NAME.eq(name))
                    .and(product.STATE.ne(ProductState.DELETED.getId()));

            return query.fetch().into(CpanelProductRecord.class);
        } catch (Exception e) {
            throw new OneboxRestException(CoreErrorCode.GENERIC_ERROR, "Error executing query", e);
        }
    }

    public List<ProductRecord> getProducts(SearchProductFilterDTO searchProductFilterDTO) {
        List<ProductRecord> result = new ArrayList<>();
        try {
            SelectFieldOrAsterisk[] fields = ArrayUtils.addAll(JOIN_FIELDS);

            SelectSeekStepN<Record> query = dsl.select(fields)
                    .from(product)
                    .innerJoin(productEntity).on(product.ENTITYID.eq(productEntity.IDENTIDAD))
                    .innerJoin(productPromoter).on(product.PRODUCERID.eq(productPromoter.IDPROMOTOR))
                    .leftJoin(productTax).on(product.TAXID.eq(productTax.IDIMPUESTO))
                    .leftJoin(productSurchargeTax).on(product.SURCHAGETAXID.eq(productSurchargeTax.IDIMPUESTO))
                    .where(builderWhereClause(searchProductFilterDTO))
                    .orderBy(SortUtils.buildSort(searchProductFilterDTO.getSort(), ProductField::byName));

            if (searchProductFilterDTO.getLimit() != null) {
                query.limit(searchProductFilterDTO.getLimit().intValue());
            }
            if (searchProductFilterDTO.getOffset() != null) {
                query.offset(searchProductFilterDTO.getOffset().intValue());
            }

            List<Record> records = query.fetch();

            for (Record product : records) {
                ProductRecord productRecord = buildProductRecord(product);
                result.add(productRecord);
            }
            return result;
        } catch (Exception e) {
            throw new OneboxRestException(CoreErrorCode.GENERIC_ERROR, "Error executing query", e);
        }
    }

    private static ProductRecord buildProductRecord(Record product) {
        return buildProductRecord(product, ProductRecord.class);
    }

    private static <E extends ProductRecord> E buildProductRecord(Record product, Class<E> clazz) {
        E productRecord = product.into(clazz);

        productRecord.setEntityName(product.getValue(JOIN_ENTITY_NAME));
        productRecord.setProducerName(product.getValue(JOIN_PROMOTER_NAME));
        productRecord.setTaxName(product.getValue(JOIN_TAX_NAME));
        productRecord.setSurchargeTaxName(product.getValue(JOIN_SURCHARGE_TAX_NAME));

        return productRecord;
    }

    private static ProductDetailRecord buildProductDetailRecord(Record product) {
        ProductDetailRecord productDetailRecord = buildProductRecord(product, ProductDetailRecord.class);

        productDetailRecord.setCategoryDescription(product.getValue(JOIN_CATEGORY_DESC));
        productDetailRecord.setCategoryCode(product.getValue(JOIN_CATEGORY_CODE));
        productDetailRecord.setCustomCategoryDescription(product.getValue(JOIN_CUSTOM_CATEGORY_DESC));
        productDetailRecord.setCustomCategoryRef(product.getValue(JOIN_CUSTOM_CATEGORY_REF));

        return productDetailRecord;
    }

    private Condition builderWhereClause(SearchProductFilterDTO filter) {
        Condition conditions = DSL.trueCondition();
        conditions = conditions.and(product.STATE.notEqual(ProductState.DELETED.getId()));
        if (filter.getIds() != null) {
            conditions = conditions.and(product.PRODUCTID.in(filter.getIds()));
        }
        if (filter.getEntityIds() != null) {
            conditions = conditions.and(product.ENTITYID.in(filter.getEntityIds()));
        }
        if (filter.getProducerId() != null) {
            conditions = conditions.and(product.PRODUCERID.eq(filter.getProducerId().intValue()));
        }
        if (filter.getQ() != null) {
            conditions = conditions.and(product.NAME.like("%" + filter.getQ() + "%"));
        }
        if (filter.getProductState() != null) {
            conditions = conditions.and(product.STATE.in(filter.getProductState()));
        }
        if (filter.getStockType() != null) {
            conditions = conditions.and(product.STOCKTYPE.in(filter.getStockType()));
        }
        if (filter.getProductType() != null) {
            conditions = conditions.and(product.TYPE.in(filter.getProductType()));
        }
        if (filter.getOperatorId() != null) {
            conditions = conditions.and(productEntity.IDOPERADORA.eq(filter.getOperatorId().intValue()));
        }
        if (filter.getCurrencyId() != null) {
            conditions = conditions.and(product.IDCURRENCY.eq(filter.getCurrencyId().intValue()));
        }
        if (filter.getEventIds() != null || filter.getEventSessionSelectionType() != null || filter.getSessionIds() != null) {
            Condition productEventConditions = productEvent.PRODUCTID.eq(product.PRODUCTID);

            if (filter.getEventIds() != null) {
                productEventConditions = productEventConditions.and(productEvent.EVENTID.in(filter.getEventIds()));
            }

            if (filter.getEventSessionSelectionType() != null) {
                productEventConditions = productEventConditions.and(productEvent.SESSIONSSELECTIONTYPE.eq(filter.getEventSessionSelectionType().getId()));
            }

            if (filter.getSessionIds() != null) {
                productEventConditions = productEventConditions.andExists(
                        dsl.selectOne()
                                .from(productSession)
                                .where(productSession.PRODUCTEVENTID.eq(productEvent.PRODUCTEVENTID))
                                .and(productSession.SESSIONID.in(filter.getSessionIds())));

            }

            conditions = conditions.andExists(
                    dsl.selectOne()
                            .from(productEvent)
                            .where(productEventConditions)
            );
        }


        return conditions;
    }
}
