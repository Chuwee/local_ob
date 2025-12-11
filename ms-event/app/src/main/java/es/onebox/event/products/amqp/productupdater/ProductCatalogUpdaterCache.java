package es.onebox.event.products.amqp.productupdater;

import es.onebox.event.products.dao.couch.ProductCatalogPrice;
import es.onebox.event.products.dao.couch.ProductCatalogVariantPrice;
import es.onebox.event.products.dao.couch.ProductCommunicationElementDocument;
import es.onebox.event.products.dao.couch.ProductContentDocument;
import es.onebox.event.products.domain.DeliveryPointRecord;
import es.onebox.event.products.domain.ProductChannelRecord;
import es.onebox.event.products.domain.ProductEventRecord;
import es.onebox.event.products.domain.ProductLanguageRecord;
import es.onebox.event.products.domain.ProductSessionRecord;
import es.onebox.event.products.dto.ProductTaxInfo;
import es.onebox.event.surcharges.product.ProductSurcharges;
import es.onebox.event.taxonomy.dao.BaseTaxonomyDao;
import es.onebox.jooq.cpanel.tables.records.CpanelImpuestoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductAttributeRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductAttributeValueRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductVariantRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductVariantSessionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPromocionProductoRecord;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductCatalogUpdaterCache implements Serializable {

    @Serial
    private static final long serialVersionUID = 12342343L;

    private List<CpanelProductVariantRecord> productVariantRecords = new ArrayList<>();
    private Map<Integer, List<CpanelProductVariantSessionRecord>> productVariantSessionRecords = new HashMap<>();
    private ProductCatalogPrice price;
    private CpanelImpuestoRecord tax;
    private CpanelImpuestoRecord surchargeTax;
    private ProductSurcharges surcharges;
    private ProductLanguageRecord defaultLanguage;
    private List<ProductChannelRecord> productChannelRecords = new ArrayList<>();
    private ProductCommunicationElementDocument communicationElements;
    private List<ProductEventRecord> productEventRecords = new ArrayList<>();
    private List<ProductSessionRecord> productSessionRecords = new ArrayList<>();
    private List<CpanelProductAttributeRecord> productAttributeRecords = new ArrayList<>();
    private List<CpanelProductAttributeValueRecord> productAttributeValueRecords = new ArrayList<>();
    private Map<Long, List<DeliveryPointRecord>> sessionDeliveryPoints = new HashMap<>();
    private Map<Long, List<DeliveryPointRecord>> eventDeliveryPoints = new HashMap<>();
    private List<DeliveryPointRecord> productDeliveryPoints = new ArrayList<>();
    private List<ProductContentDocument> attributeContents;
    private List<ProductLanguageRecord> productLanguages;
    private List<CpanelPromocionProductoRecord> promotions;
    private Map<Integer, ProductCatalogVariantPrice> variantPrices = new HashMap<>();
    private Map<Integer, Map<Long, ProductCatalogVariantPrice>> variantSessionPrices = new HashMap<>();
    private List<ProductTaxInfo> taxes;
    private List<ProductTaxInfo> surchargesTaxes;
    private BaseTaxonomyDao.TaxonomyInfo taxonomy;
    private BaseTaxonomyDao.TaxonomyInfo parentTaxonomy;
    private BaseTaxonomyDao.TaxonomyInfo customTaxonomy;
    private BaseTaxonomyDao.TaxonomyInfo customParentTaxonomy;

    public List<CpanelProductVariantRecord> getProductVariantRecords() {
        return productVariantRecords;
    }

    public void setProductVariantRecords(List<CpanelProductVariantRecord> productVariantRecords) {
        this.productVariantRecords = productVariantRecords;
    }

    public Map<Integer, List<CpanelProductVariantSessionRecord>> getProductVariantSessionRecords() {
        return productVariantSessionRecords;
    }

    public void setProductVariantSessionRecords(Map<Integer, List<CpanelProductVariantSessionRecord>> productVariantSessionRecords) {
        this.productVariantSessionRecords = productVariantSessionRecords;
    }

    public CpanelImpuestoRecord getTax() {
        return tax;
    }

    public void setTax(CpanelImpuestoRecord tax) {
        this.tax = tax;
    }

    public CpanelImpuestoRecord getSurchargeTax() {
        return surchargeTax;
    }

    public void setSurchargeTax(CpanelImpuestoRecord surchargeTax) {
        this.surchargeTax = surchargeTax;
    }

    public ProductCatalogPrice getPrice() {
        return price;
    }

    public void setPrice(ProductCatalogPrice price) {
        this.price = price;
    }

    public ProductSurcharges getSurcharges() {
        return surcharges;
    }

    public void setSurcharges(ProductSurcharges surcharge) {
        this.surcharges = surcharge;
    }

    public ProductLanguageRecord getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(ProductLanguageRecord defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public List<ProductChannelRecord> getProductChannelRecords() {
        return productChannelRecords;
    }

    public void setProductChannelRecords(List<ProductChannelRecord> productChannelRecords) {
        this.productChannelRecords = productChannelRecords;
    }

    public ProductCommunicationElementDocument getCommunicationElements() {
        return communicationElements;
    }

    public void setCommunicationElements(ProductCommunicationElementDocument communicationElements) {
        this.communicationElements = communicationElements;
    }

    public List<ProductEventRecord> getProductEventRecords() {
        return productEventRecords;
    }

    public void setProductEventRecords(List<ProductEventRecord> productEventRecords) {
        this.productEventRecords = productEventRecords;
    }

    public List<ProductSessionRecord> getProductSessionRecords() {
        return productSessionRecords;
    }

    public void setProductSessionRecords(List<ProductSessionRecord> productSessionRecords) {
        this.productSessionRecords = productSessionRecords;
    }

    public List<CpanelProductAttributeRecord> getProductAttributeRecords() {
        return productAttributeRecords;
    }

    public void setProductAttributeRecords(List<CpanelProductAttributeRecord> productAttributeRecords) {
        this.productAttributeRecords = productAttributeRecords;
    }

    public Map<Long, List<DeliveryPointRecord>> getSessionDeliveryPoints() {
        return sessionDeliveryPoints;
    }

    public void setSessionDeliveryPoints(Map<Long, List<DeliveryPointRecord>> sessionDeliveryPoints) {
        this.sessionDeliveryPoints = sessionDeliveryPoints;
    }

    public Map<Long, List<DeliveryPointRecord>> getEventDeliveryPoints() {
        return eventDeliveryPoints;
    }

    public void setEventDeliveryPoints(Map<Long, List<DeliveryPointRecord>> eventDeliveryPoints) {
        this.eventDeliveryPoints = eventDeliveryPoints;
    }

    public List<DeliveryPointRecord> getProductDeliveryPoints() {
        return productDeliveryPoints;
    }

    public void setProductDeliveryPoints(List<DeliveryPointRecord> productDeliveryPoints) {
        this.productDeliveryPoints = productDeliveryPoints;
    }

    public List<CpanelProductAttributeValueRecord> getProductAttributeValueRecords() {
        return productAttributeValueRecords;
    }

    public void setProductAttributeValueRecords(List<CpanelProductAttributeValueRecord> productAttributeValueRecords) {
        this.productAttributeValueRecords = productAttributeValueRecords;
    }

    public List<ProductContentDocument> getAttributeContents() {
        return attributeContents;
    }

    public void setAttributeContents(List<ProductContentDocument> attributeContents) {
        this.attributeContents = attributeContents;
    }

    public List<ProductLanguageRecord> getProductLanguages() {
        return productLanguages;
    }

    public void setProductLanguages(List<ProductLanguageRecord> productLanguages) {
        this.productLanguages = productLanguages;
    }

    public List<CpanelPromocionProductoRecord> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<CpanelPromocionProductoRecord> promotions) {
        this.promotions = promotions;
    }

    public Map<Integer, ProductCatalogVariantPrice> getVariantPrices() {
        return variantPrices;
    }

    public void setVariantPrices(Map<Integer, ProductCatalogVariantPrice> variantPrices) {
        this.variantPrices = variantPrices;
    }

    public Map<Integer, Map<Long, ProductCatalogVariantPrice>> getVariantSessionPrices() {
        return variantSessionPrices;
    }

    public void setVariantSessionPrices(Map<Integer, Map<Long, ProductCatalogVariantPrice>> variantSessionPrices) {
        this.variantSessionPrices = variantSessionPrices;
    }

    public List<ProductTaxInfo> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<ProductTaxInfo> taxes) {
        this.taxes = taxes;
    }

    public List<ProductTaxInfo> getSurchargesTaxes() {
        return surchargesTaxes;
    }

    public void setSurchargesTaxes(List<ProductTaxInfo> surchargesTaxes) {
        this.surchargesTaxes = surchargesTaxes;
    }

    public BaseTaxonomyDao.TaxonomyInfo getTaxonomy() {
        return taxonomy;
    }

    public void setTaxonomy(BaseTaxonomyDao.TaxonomyInfo taxonomy) {
        this.taxonomy = taxonomy;
    }

    public BaseTaxonomyDao.TaxonomyInfo getParentTaxonomy() {
        return parentTaxonomy;
    }

    public void setParentTaxonomy(BaseTaxonomyDao.TaxonomyInfo parentTaxonomy) {
        this.parentTaxonomy = parentTaxonomy;
    }

    public BaseTaxonomyDao.TaxonomyInfo getCustomTaxonomy() {
        return customTaxonomy;
    }

    public void setCustomTaxonomy(BaseTaxonomyDao.TaxonomyInfo customTaxonomy) {
        this.customTaxonomy = customTaxonomy;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public BaseTaxonomyDao.TaxonomyInfo getCustomParentTaxonomy() {
        return customParentTaxonomy;
    }

    public void setCustomParentTaxonomy(BaseTaxonomyDao.TaxonomyInfo customParentTaxonomy) {
        this.customParentTaxonomy = customParentTaxonomy;
    }
}
