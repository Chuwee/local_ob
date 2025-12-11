package es.onebox.mgmt.products.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.products.enums.ProductVariantStatus;
import jakarta.validation.constraints.Min;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class UpdateProductVariantDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -6959897765333279098L;

    private String sku;
    private Double price;
    @Min(value = 0, message = "stock must be greater than 0")
    private Long stock;
    @JsonProperty("status")
    private ProductVariantStatus productVariantStatus;

    public UpdateProductVariantDTO() {
    }

    public UpdateProductVariantDTO(String sku, Double price, Long stock) {
        this.sku = sku;
        this.price = price;
        this.stock = stock;
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

    public ProductVariantStatus getProductVariantStatus() { return productVariantStatus; }

    public void setProductVariantStatus(ProductVariantStatus productVariantStatus) { this.productVariantStatus = productVariantStatus; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
