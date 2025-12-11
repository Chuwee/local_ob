package es.onebox.mgmt.products.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.event.dto.products.enums.ProductStockType;
import es.onebox.mgmt.products.enums.ProductState;
import es.onebox.mgmt.products.enums.ProductType;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class ProductDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -6959897765333279098L;

    @JsonProperty("product_id")
    private Long productId;
    private IdNameDTO entity;
    private IdNameDTO producer;
    private String name;
    @JsonProperty("product_type")
    private ProductType productType;
    @JsonProperty("product_state")
    private ProductState productState;
    private IdNameDTO tax;
    @JsonProperty("surcharge_tax")
    private IdNameDTO surchargeTax;
    @JsonProperty("create_date")
    private ZonedDateTime createDate;
    @JsonProperty("update_date")
    private ZonedDateTime updateDate;
    @JsonProperty("stock_type")
    ProductStockType stockType;
    @JsonProperty("currency_code")
    String currencyCode;
    @JsonProperty("ticket_template_id")
    private Long ticketTemplateId;
    @JsonProperty("ui_settings")
    ProductUISettingsDTO productUiSettings;
    private ProductSettingDTO settings;
    @JsonProperty("has_sales")
    private Boolean hasSales;

    public ProductDTO() {
    }

    public ProductDTO(Long productId, IdNameDTO entityId, IdNameDTO producer,
                      String name, ProductType productType, ProductState productState,
                      IdNameDTO tax, IdNameDTO surchargeTax, ZonedDateTime createDate, ZonedDateTime updateDate,
                      ProductStockType stockType, String currencyCode, Long ticketTemplateId,
                      ProductUISettingsDTO productUiSettings) {
        this.productId = productId;
        this.entity = entityId;
        this.producer = producer;
        this.name = name;
        this.productType = productType;
        this.productState = productState;
        this.tax = tax;
        this.surchargeTax = surchargeTax;
        this.stockType = stockType;
        this.currencyCode = currencyCode;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.ticketTemplateId = ticketTemplateId;
        this.productUiSettings = productUiSettings;
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

    public ProductStockType getStockType() {
        return stockType;
    }

    public void setStockType(ProductStockType stockType) {
        this.stockType = stockType;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Long getTicketTemplateId() {
        return ticketTemplateId;
    }

    public void setTicketTemplateId(Long ticketTemplateId) {
        this.ticketTemplateId = ticketTemplateId;
    }

    public ProductUISettingsDTO getProductUiSettings() {
        return productUiSettings;
    }

    public void setProductUiSettings(ProductUISettingsDTO productUiSettings) {
        this.productUiSettings = productUiSettings;
    }

    public ProductSettingDTO getSettings() {
        return settings;
    }

    public void setSettings(ProductSettingDTO settings) {
        this.settings = settings;
    }

    public Boolean getHasSales() {
        return hasSales;
    }

    public void setHasSales(Boolean hasSales) {
        this.hasSales = hasSales;
    }
}
