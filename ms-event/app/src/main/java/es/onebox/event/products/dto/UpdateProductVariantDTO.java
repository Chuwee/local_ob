package es.onebox.event.products.dto;

import es.onebox.event.products.enums.ProductVariantStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import jakarta.validation.constraints.Min;
import java.io.Serial;
import java.io.Serializable;

public class UpdateProductVariantDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -6959897765333279098L;

    private String sku;
    private Double price;
    @Min(value = 0, message = "stock must be greater than 0")
    private Long stock;
    private ProductVariantStatus status;

    public UpdateProductVariantDTO() {
    }

    public UpdateProductVariantDTO(String sku, Double price, Long stock, ProductVariantStatus status) {
        this.sku = sku;
        this.price = price;
        this.stock = stock;
        this.status = status;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
    }

    public ProductVariantStatus getStatus() {
        return status;
    }

    public void setStatus(ProductVariantStatus status) { this.status = status; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
