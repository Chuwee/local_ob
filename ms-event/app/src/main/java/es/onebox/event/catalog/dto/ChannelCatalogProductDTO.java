package es.onebox.event.catalog.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.catalog.dto.product.DeliveryPointDTO;
import es.onebox.event.catalog.dto.product.ProductCatalogAttributeDTO;
import es.onebox.event.catalog.dto.product.ProductCatalogCommunicationElementsDTO;
import es.onebox.event.catalog.dto.product.ProductCatalogVariantDTO;
import es.onebox.event.events.dto.TaxonomyDTO;
import es.onebox.event.products.dao.couch.ProductCatalogPrice;
import es.onebox.event.products.dto.ProductTaxInfo;
import es.onebox.event.products.enums.ProductDeliveryTimeUnitType;
import es.onebox.event.products.enums.ProductDeliveryType;
import es.onebox.event.products.enums.ProductState;
import es.onebox.event.products.enums.ProductType;
import es.onebox.event.surcharges.product.ProductSurcharges;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class ChannelCatalogProductDTO implements Serializable {

    private static final long serialVersionUID = 3996389765459169724L;

    private Long id;
    private String name;
    private ProductType type;
    private ProductState state;
    private Boolean checkoutSuggestionEnabled;
    private Boolean standaloneEnabled;
    private Integer currencyId;
    private IdNameDTO entity;
    private IdNameDTO producer;
    private String defaultLanguage;
    private List<String> productLanguages;
    private TaxonomyDTO taxonomy;
    private TaxonomyDTO parentTaxonomy;
    private TaxonomyDTO customTaxonomy;
    private TaxonomyDTO customParentTaxonomy;
    private List<ProductCatalogVariantDTO> variants;
    private ProductCatalogAttributeDTO attribute1;
    private ProductCatalogAttributeDTO attribute2;
    private ProductDeliveryType deliveryType;
    private List<DeliveryPointDTO> deliveryPoints;
    private ProductDeliveryTimeUnitType startTimeUnit;
    private Long startTimeValue;
    private ProductDeliveryTimeUnitType endTimeUnit;
    private Long endTimeValue;
    private ZonedDateTime deliveryDateFrom;
    private ZonedDateTime deliveryDateTo;
    private ProductCatalogPrice price;
    private ProductCatalogCommunicationElementsDTO commElements;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }


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

    public ProductType getType() {
        return type;
    }

    public void setType(ProductType type) {
        this.type = type;
    }

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    public IdNameDTO getEntity() {
        return entity;
    }

    public void setEntity(IdNameDTO entity) {
        this.entity = entity;
    }

    public IdNameDTO getProducer() {
        return producer;
    }

    public void setProducer(IdNameDTO producer) {
        this.producer = producer;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public List<String> getProductLanguages() {
        return productLanguages;
    }

    public void setProductLanguages(List<String> productLanguages) {
        this.productLanguages = productLanguages;
    }

    public List<ProductCatalogVariantDTO> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductCatalogVariantDTO> variants) {
        this.variants = variants;
    }

    public ProductCatalogAttributeDTO getAttribute1() {
        return attribute1;
    }

    public void setAttribute1(ProductCatalogAttributeDTO attribute1) {
        this.attribute1 = attribute1;
    }

    public ProductCatalogAttributeDTO getAttribute2() {
        return attribute2;
    }

    public void setAttribute2(ProductCatalogAttributeDTO attribute2) {
        this.attribute2 = attribute2;
    }

    public ProductDeliveryType getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(ProductDeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }

    public List<DeliveryPointDTO> getDeliveryPoints() {
        return deliveryPoints;
    }

    public void setDeliveryPoints(List<DeliveryPointDTO> deliveryPoints) {
        this.deliveryPoints = deliveryPoints;
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

    public ProductCatalogPrice getPrice() {
        return price;
    }

    public void setPrice(ProductCatalogPrice price) {
        this.price = price;
    }

    public ProductCatalogCommunicationElementsDTO getCommElements() {
        return commElements;
    }

    public void setCommElements(ProductCatalogCommunicationElementsDTO commElements) {
        this.commElements = commElements;
    }

    public Boolean getCheckoutSuggestionEnabled() {
        return checkoutSuggestionEnabled;
    }

    public void setCheckoutSuggestionEnabled(Boolean checkoutSuggestionEnabled) {
        this.checkoutSuggestionEnabled = checkoutSuggestionEnabled;
    }

    public Boolean getStandaloneEnabled() {
        return standaloneEnabled;
    }

    public void setStandaloneEnabled(Boolean standaloneEnabled) {
        this.standaloneEnabled = standaloneEnabled;
    }

    public ProductState getState() {
        return state;
    }

    public void setState(ProductState state) {
        this.state = state;
    }

    public TaxonomyDTO getTaxonomy() {
        return taxonomy;
    }

    public void setTaxonomy(TaxonomyDTO taxonomy) {
        this.taxonomy = taxonomy;
    }

    public TaxonomyDTO getParentTaxonomy() {
        return parentTaxonomy;
    }

    public void setParentTaxonomy(TaxonomyDTO parentTaxonomy) {
        this.parentTaxonomy = parentTaxonomy;
    }

    public TaxonomyDTO getCustomTaxonomy() {
        return customTaxonomy;
    }

    public void setCustomTaxonomy(TaxonomyDTO customTaxonomy) {
        this.customTaxonomy = customTaxonomy;
    }

    public TaxonomyDTO getCustomParentTaxonomy() {
        return customParentTaxonomy;
    }

    public void setCustomParentTaxonomy(TaxonomyDTO customParentTaxonomy) {
        this.customParentTaxonomy = customParentTaxonomy;
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
}
