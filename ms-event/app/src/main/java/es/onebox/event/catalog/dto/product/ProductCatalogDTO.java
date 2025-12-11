package es.onebox.event.catalog.dto.product;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.events.dto.TaxonomyDTO;
import es.onebox.event.products.dao.couch.ProductCatalogPrice;
import es.onebox.event.products.dao.couch.ProductStockStatus;
import es.onebox.event.products.enums.ProductDeliveryTimeUnitType;
import es.onebox.event.products.enums.ProductDeliveryType;
import es.onebox.event.products.enums.ProductState;
import es.onebox.event.products.enums.ProductStockType;
import es.onebox.event.products.enums.ProductType;
import es.onebox.event.surcharges.product.ProductSurcharges;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class ProductCatalogDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -2581626070260784633L;


    private Long productId;
    private String name;
    private ProductDeliveryTimeUnitType startTimeUnit;
    private Long startTimeValue;
    private ProductDeliveryTimeUnitType endTimeUnit;
    private Long endTimeValue;
    private IdNameDTO entity;
    private Double tax;
    private Double surchargeTax;
    private ProductSurcharges surcharges;
    private IdNameDTO producer;
    private ProductType type;
    private ProductStockType stockType;
    private ProductDeliveryType deliveryType;
    private ProductCatalogPrice price;
    private ProductStockStatus availability;
    private ProductCatalogCommunicationElementsDTO commElements;
    private List<ProductCatalogVariantDTO> variants;
    private ProductCatalogAttributeDTO attribute1;
    private ProductCatalogAttributeDTO attribute2;
    private List<DeliveryPointDTO> deliveryPoints;
    private String defaultLanguage;
    private List<ProductCatalogSessionDTO> productSessions;
    private List<ProductSessionDTO> sessions;
    private Integer currencyId;
    private Long ticketTemplateId;
    private List<ProductCatalogPromotionDTO> promotions;
    private ProductState state;
    private Boolean useExternalBarcodes;
    private Boolean hideDeliveryPoint;
    private Boolean hideDeliveryDateTime;
    private ZonedDateTime deliveryDateFrom; //fixed dates
    private ZonedDateTime deliveryDateTo;
    private List<ProductTaxInfoDTO> taxes;
    private List<ProductTaxInfoDTO> surchargesTaxes;
    private TaxonomyDTO taxonomy;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
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

    public Double getSurchargeTax() {
        return surchargeTax;
    }

    public void setSurchargeTax(Double surchargeTax) {
        this.surchargeTax = surchargeTax;
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

    public ProductCatalogPrice getPrice() {
        return price;
    }

    public void setPrice(ProductCatalogPrice price) {
        this.price = price;
    }

    public ProductStockStatus getAvailability() {
        return availability;
    }

    public void setAvailability(ProductStockStatus availability) {
        this.availability = availability;
    }

    public ProductCatalogCommunicationElementsDTO getCommElements() {
        return commElements;
    }

    public void setCommElements(ProductCatalogCommunicationElementsDTO commElements) {
        this.commElements = commElements;
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

    public List<DeliveryPointDTO> getDeliveryPoints() {
        return deliveryPoints;
    }

    public void setDeliveryPoints(List<DeliveryPointDTO> deliveryPoints) {
        this.deliveryPoints = deliveryPoints;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public List<ProductCatalogSessionDTO> getProductSessions() {
        return productSessions;
    }

    public void setProductSessions(List<ProductCatalogSessionDTO> productSessions) {
        this.productSessions = productSessions;
    }

    public List<ProductSessionDTO> getSessions() {
        return sessions;
    }

    public void setSessions(List<ProductSessionDTO> sessions) {
        this.sessions = sessions;
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

    public List<ProductCatalogPromotionDTO> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<ProductCatalogPromotionDTO> promotions) {
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

    public List<ProductTaxInfoDTO> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<ProductTaxInfoDTO> taxes) {
        this.taxes = taxes;
    }

    public List<ProductTaxInfoDTO> getSurchargesTaxes() {
        return surchargesTaxes;
    }

    public void setSurchargesTaxes(List<ProductTaxInfoDTO> surchargesTaxes) {
        this.surchargesTaxes = surchargesTaxes;
    }

    public TaxonomyDTO getTaxonomy() {
        return taxonomy;
    }

    public void setTaxonomy(TaxonomyDTO taxonomy) {
        this.taxonomy = taxonomy;
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
