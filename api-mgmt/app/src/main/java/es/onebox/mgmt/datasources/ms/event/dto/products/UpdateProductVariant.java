package es.onebox.mgmt.datasources.ms.event.dto.products;

import es.onebox.mgmt.products.enums.ProductVariantStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class UpdateProductVariant implements Serializable {
    @Serial
    private static final long serialVersionUID = -6959897765333279098L;

    private String sku;
    private Double price;
    private Long stock;
    private ProductVariantStatus status;

    public UpdateProductVariant() {
    }

    public UpdateProductVariant(String sku, Double price, Long stock, ProductVariantStatus status) {
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

    public ProductVariantStatus getStatus() { return status; }

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
