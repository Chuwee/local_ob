package es.onebox.event.products.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.events.dto.CategoryDTO;
import es.onebox.event.products.enums.ProductState;
import es.onebox.event.products.enums.ProductStockType;
import es.onebox.event.products.enums.ProductType;
import es.onebox.event.products.enums.TaxModeDTO;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class ProductDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -6959897765333279098L;

    private Long productId;
    private IdNameDTO entity;
    private IdNameDTO producer;
    private String name;
    private ProductType productType;
    private ProductStockType stockType;
    private ProductState productState;
    private Long currencyId;
    private Long ticketTemplateId;
    private ZonedDateTime createDate;
    private ZonedDateTime updateDate;
    private IdNameDTO tax;
    private IdNameDTO surchargeTax;
    private Boolean hideDeliveryPoint;
    private Boolean hideDeliveryDateTime;
    private CategoryDTO category;
    private CategoryDTO customCategory;
    private TaxModeDTO taxMode;

    public ProductDTO() {
    }

    public ProductDTO(Long productId, IdNameDTO entity, IdNameDTO producer,
                      String name, ProductType productType, ProductState productState,
                      ZonedDateTime createDate, ZonedDateTime updateDate, IdNameDTO tax, IdNameDTO surchargeTax,
                      ProductStockType stockType, Long currencyId, Long ticketTemplateId, Boolean hideDeliveryPoint,
                      Boolean hideDeliveryDateTime) {
        this.productId = productId;
        this.entity = entity;
        this.producer = producer;
        this.name = name;
        this.productType = productType;
        this.productState = productState;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.tax = tax;
        this.surchargeTax = surchargeTax;
        this.stockType = stockType;
        this.currencyId = currencyId;
        this.ticketTemplateId = ticketTemplateId;
        this.hideDeliveryPoint = hideDeliveryPoint;
        this.hideDeliveryDateTime = hideDeliveryDateTime;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public ProductState getProductState() {
        return productState;
    }

    public void setProductState(ProductState productState) {
        this.productState = productState;
    }

    public ZonedDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(ZonedDateTime createDate) {
        this.createDate = createDate;
    }

    public ZonedDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(ZonedDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public IdNameDTO getTax() {
        return tax;
    }

    public void setTax(IdNameDTO tax) {
        this.tax = tax;
    }

    public IdNameDTO getSurchargeTax() {
        return surchargeTax;
    }

    public void setSurchargeTax(IdNameDTO surchargeTax) {
        this.surchargeTax = surchargeTax;
    }

    public ProductStockType getStockType() {
        return stockType;
    }

    public void setStockType(ProductStockType stockType) {
        this.stockType = stockType;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    public Long getTicketTemplateId() {
        return ticketTemplateId;
    }

    public void setTicketTemplateId(Long ticketTemplateId) {
        this.ticketTemplateId = ticketTemplateId;
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

    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

    public CategoryDTO getCustomCategory() {
        return customCategory;
    }

    public void setCustomCategory(CategoryDTO customCategory) {
        this.customCategory = customCategory;
    }

    public TaxModeDTO getTaxMode() {
        return taxMode;
    }

    public void setTaxMode(TaxModeDTO taxMode) {
        this.taxMode = taxMode;
    }
}
