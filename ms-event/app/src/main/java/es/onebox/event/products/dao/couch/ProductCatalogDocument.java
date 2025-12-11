package es.onebox.event.products.dao.couch;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.couchbase.annotations.Id;
import es.onebox.event.products.dto.ProductTaxInfo;
import es.onebox.event.products.enums.ProductDeliveryTimeUnitType;
import es.onebox.event.products.enums.ProductDeliveryType;
import es.onebox.event.products.enums.ProductState;
import es.onebox.event.products.enums.ProductStockType;
import es.onebox.event.products.enums.ProductType;
import es.onebox.event.sessions.domain.Tax;
import es.onebox.event.surcharges.product.ProductSurcharges;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

public class ProductCatalogDocument implements Serializable {

    @Serial
    private static final long serialVersionUID = -2581626070260784633L;

    public ProductCatalogDocument() {
    }

    @Id
    private Long id;
    private String name;
    private ProductDeliveryTimeUnitType startTimeUnit;
    private Long startTimeValue;
    private ProductDeliveryTimeUnitType endTimeUnit;
    private Long endTimeValue;
    private IdNameDTO entity;
    // DEPRECATED: Should be preparated for multitaxes scenario
    private Double tax;
    private Double surchargeTax;
    private List<ProductTaxInfo> taxes;
    private List<ProductTaxInfo> surchargesTaxes;
    private ProductSurcharges surcharges;
    private IdNameDTO producer;
    private ProductType type;
    private ProductStockType stockType;
    private ProductDeliveryType deliveryType;
    private ProductCatalogPrice price;
    private ProductCatalogCommElement commElements;
    private List<ProductCatalogVariant> variants;
    private ProductCatalogAttribute attribute1;
    private ProductCatalogAttribute attribute2;
    private List<ProductCatalogChannel> channels;
    private List<EventSessionsDeliveryPoints> events;
    private Set<DeliveryPoint> deliveryPoints;
    private String defaultLanguage;
    private List<String> languages;
    private Integer currencyId;
    private Long ticketTemplateId;
    private List<ProductCatalogPromotion> promotions;
    private ProductState state;
    private Boolean useExternalBarcodes;
    private Boolean hideDeliveryPoint;
    private Boolean hideDeliveryDateTime;
    private ZonedDateTime deliveryDateFrom;
    private ZonedDateTime deliveryDateTo;

    //Taxonomy
    private Integer taxonomyId;
    private String taxonomyCode;
    private String taxonomyDescription;
    private Integer taxonomyParentId;
    private String taxonomyParentDescription;
    private String taxonomyParentCode;
    private Integer customTaxonomyId;
    private String customTaxonomyDescription;
    private String customTaxonomyCode;
    private Integer customParentTaxonomyId;
    private String customParentTaxonomyDescription;
    private String customParentTaxonomyCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProductDeliveryTimeUnitType getStartTimeUnit() {
        return startTimeUnit;
    }

    public void setStartTimeUnit(ProductDeliveryTimeUnitType startTimeUnit) {
        this.startTimeUnit = startTimeUnit;
    }

    public Long getStartTimeValue() {
        return startTimeValue;
    }

    public void setStartTimeValue(Long startTimeValue) {
        this.startTimeValue = startTimeValue;
    }

    public ProductDeliveryTimeUnitType getEndTimeUnit() {
        return endTimeUnit;
    }

    public void setEndTimeUnit(ProductDeliveryTimeUnitType endTimeUnit) {
        this.endTimeUnit = endTimeUnit;
    }

    public Long getEndTimeValue() {
        return endTimeValue;
    }

    public void setEndTimeValue(Long endTimeValue) {
        this.endTimeValue = endTimeValue;
    }

    public IdNameDTO getEntity() {
        return entity;
    }

    public void setEntity(IdNameDTO entity) {
        this.entity = entity;
    }

    public Double getTax() {
        return tax;
    }

    public void setTax(Double tax) {
        this.tax = tax;
    }

    public ProductSurcharges getSurcharges() {
        return surcharges;
    }

    public void setSurcharges(ProductSurcharges surcharges) {
        this.surcharges = surcharges;
    }

    public IdNameDTO getProducer() {
        return producer;
    }

    public void setProducer(IdNameDTO producer) {
        this.producer = producer;
    }

    public ProductType getType() {
        return type;
    }

    public void setType(ProductType type) {
        this.type = type;
    }

    public ProductStockType getStockType() {
        return stockType;
    }

    public void setStockType(ProductStockType stockType) {
        this.stockType = stockType;
    }

    public ProductDeliveryType getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(ProductDeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }

    public ProductCatalogCommElement getCommElements() {
        return commElements;
    }

    public void setCommElements(ProductCatalogCommElement commElements) {
        this.commElements = commElements;
    }

    public List<ProductCatalogVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductCatalogVariant> variants) {
        this.variants = variants;
    }

    public ProductCatalogAttribute getAttribute1() {
        return attribute1;
    }

    public void setAttribute1(ProductCatalogAttribute attribute1) {
        this.attribute1 = attribute1;
    }

    public ProductCatalogAttribute getAttribute2() {
        return attribute2;
    }

    public void setAttribute2(ProductCatalogAttribute attribute2) {
        this.attribute2 = attribute2;
    }

    public List<EventSessionsDeliveryPoints> getEvents() {
        return events;
    }

    public void setEvents(List<EventSessionsDeliveryPoints> events) {
        this.events = events;
    }

    public Set<DeliveryPoint> getDeliveryPoints() {
        return deliveryPoints;
    }

    public void setDeliveryPoints(Set<DeliveryPoint> deliveryPoints) {
        this.deliveryPoints = deliveryPoints;
    }

    public List<ProductCatalogChannel> getChannels() {
        return channels;
    }

    public void setChannels(List<ProductCatalogChannel> channels) {
        this.channels = channels;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    public Long getTicketTemplateId() {
        return ticketTemplateId;
    }

    public void setTicketTemplateId(Long ticketTemplateId) {
        this.ticketTemplateId = ticketTemplateId;
    }

    public List<ProductCatalogPromotion> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<ProductCatalogPromotion> promotions) {
        this.promotions = promotions;
    }

    public ProductState getState() {
        return state;
    }

    public void setState(ProductState state) {
        this.state = state;
    }

    public Boolean getUseExternalBarcodes() {
        return useExternalBarcodes;
    }

    public void setUseExternalBarcodes(Boolean useExternalBarcodes) {
        this.useExternalBarcodes = useExternalBarcodes;
    }

    public ProductCatalogPrice getPrice() {
        return price;
    }

    public void setPrice(ProductCatalogPrice price) {
        this.price = price;
    }

    public Boolean getHideDeliveryPoint() {
        return hideDeliveryPoint;
    }

    public void setHideDeliveryPoint(Boolean hideDeliveryPoint) {
        this.hideDeliveryPoint = hideDeliveryPoint;
    }

    public Boolean getHideDeliveryDateTime() {
        return hideDeliveryDateTime;
    }

    public void setHideDeliveryDateTime(Boolean hideDeliveryDateTime) {
        this.hideDeliveryDateTime = hideDeliveryDateTime;
    }

    public ZonedDateTime getDeliveryDateFrom() {
        return deliveryDateFrom;
    }

    public void setDeliveryDateFrom(ZonedDateTime deliveryDateFrom) {
        this.deliveryDateFrom = deliveryDateFrom;
    }

    public ZonedDateTime getDeliveryDateTo() {
        return deliveryDateTo;
    }

    public void setDeliveryDateTo(ZonedDateTime deliveryDateTo) {
        this.deliveryDateTo = deliveryDateTo;
    }

    public List<ProductTaxInfo> getSurchargesTaxes() {
        return surchargesTaxes;
    }

    public void setSurchargesTaxes(List<ProductTaxInfo> surchargesTaxes) {
        this.surchargesTaxes = surchargesTaxes;
    }


    public Double getSurchargeTax() {
        return surchargeTax;
    }

    public void setSurchargeTax(Double surchargeTax) {
        this.surchargeTax = surchargeTax;
    }

    public List<ProductTaxInfo> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<ProductTaxInfo> taxes) {
        this.taxes = taxes;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public Integer getTaxonomyId() {
        return taxonomyId;
    }

    public void setTaxonomyId(Integer taxonomyId) {
        this.taxonomyId = taxonomyId;
    }

    public String getTaxonomyCode() {
        return taxonomyCode;
    }

    public void setTaxonomyCode(String taxonomyCode) {
        this.taxonomyCode = taxonomyCode;
    }

    public String getTaxonomyDescription() {
        return taxonomyDescription;
    }

    public void setTaxonomyDescription(String taxonomyDescription) {
        this.taxonomyDescription = taxonomyDescription;
    }

    public Integer getTaxonomyParentId() {
        return taxonomyParentId;
    }

    public void setTaxonomyParentId(Integer taxonomyParentId) {
        this.taxonomyParentId = taxonomyParentId;
    }

    public String getTaxonomyParentDescription() {
        return taxonomyParentDescription;
    }

    public void setTaxonomyParentDescription(String taxonomyParentDescription) {
        this.taxonomyParentDescription = taxonomyParentDescription;
    }

    public String getTaxonomyParentCode() {
        return taxonomyParentCode;
    }

    public void setTaxonomyParentCode(String taxonomyParentCode) {
        this.taxonomyParentCode = taxonomyParentCode;
    }

    public Integer getCustomTaxonomyId() {
        return customTaxonomyId;
    }

    public void setCustomTaxonomyId(Integer customTaxonomyId) {
        this.customTaxonomyId = customTaxonomyId;
    }

    public String getCustomTaxonomyDescription() {
        return customTaxonomyDescription;
    }

    public void setCustomTaxonomyDescription(String customTaxonomyDescription) {
        this.customTaxonomyDescription = customTaxonomyDescription;
    }

    public String getCustomTaxonomyCode() {
        return customTaxonomyCode;
    }

    public void setCustomTaxonomyCode(String customTaxonomyCode) {
        this.customTaxonomyCode = customTaxonomyCode;
    }

    public Integer getCustomParentTaxonomyId() {
        return customParentTaxonomyId;
    }

    public void setCustomParentTaxonomyId(Integer customParentTaxonomyId) {
        this.customParentTaxonomyId = customParentTaxonomyId;
    }

    public String getCustomParentTaxonomyDescription() {
        return customParentTaxonomyDescription;
    }

    public void setCustomParentTaxonomyDescription(String customParentTaxonomyDescription) {
        this.customParentTaxonomyDescription = customParentTaxonomyDescription;
    }

    public String getCustomParentTaxonomyCode() {
        return customParentTaxonomyCode;
    }

    public void setCustomParentTaxonomyCode(String customParentTaxonomyCode) {
        this.customParentTaxonomyCode = customParentTaxonomyCode;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
