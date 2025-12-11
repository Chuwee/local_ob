package es.onebox.mgmt.products.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.products.enums.ProductStockType;
import es.onebox.mgmt.products.enums.ProductType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

public class CreateProductDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "entity_id can not be null")
    @JsonProperty("entity_id")
    private Long entityId;
    @NotNull(message = "producer_id can not be null")
    @JsonProperty("producer_id")
    private Long producerId;
    @Length(max = 50, message = "username max size is 50")
    @NotEmpty(message = "name can not be null")
    @Pattern(regexp = "^[^|]*$", message = "Invalid characters. | is not allowed in the name")
    private String name;
    @NotNull(message = "stock_type can not be null")
    @JsonProperty("stock_type")
    private ProductStockType stockType;
    @JsonProperty("currency_code")
    private String currencyCode;

    @NotNull(message = "product_type can not be null")
    @JsonProperty("product_type")
    private ProductType productType;



    public CreateProductDTO() {
    }

    public CreateProductDTO(Long entityId, Long producerId, String name) {
        this.entityId = entityId;
        this.producerId = producerId;
        this.name = name;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getProducerId() {
        return producerId;
    }

    public void setProducerId(Long producerId) {
        this.producerId = producerId;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
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
}
