package es.onebox.event.products.dto;

import es.onebox.event.products.enums.ProductStockType;
import es.onebox.event.products.enums.ProductType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

public class CreateProductDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "entityId can not be null")
    private Long entityId;
    @NotNull(message = "producerId can not be null")
    private Long producerId;
    @Length(max = 50, message = "username max size is 50")
    @NotEmpty(message = "name can not be null")
    @Pattern(regexp = "^[^|]*$", message = "Invalid characters. | is not allowed in the name")
    private String name;
    @NotNull(message = "stockType can not be null")
    private ProductStockType stockType;
    @NotNull(message = "productType can not be null")
    private ProductType productType;
    @NotNull(message = "currencyId can not be null")
    private Long currencyId;

    public CreateProductDTO() {
    }

    public CreateProductDTO(Long entityId, Long producerId, String name, ProductStockType stockType, ProductType productType, Long currencyId) {
        this.entityId = entityId;
        this.producerId = producerId;
        this.name = name;
        this.stockType = stockType;
        this.productType = productType;
        this.currencyId = currencyId;
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

    public ProductStockType getStockType() {
        return stockType;
    }

    public void setStockType(ProductStockType stockType) {
        this.stockType = stockType;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

}
