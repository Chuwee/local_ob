package es.onebox.mgmt.products.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ProductSessionVariantDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4827490551384904803L;

    private Long id;

    @JsonProperty("use_custom_stock")
    private Boolean useCustomStock;
    private Long stock;

    @JsonProperty("use_custom_price")
    private Boolean useCustomPrice;
    private Double price;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getUseCustomStock() {
        return useCustomStock;
    }

    public void setUseCustomStock(Boolean useCustomStock) {
        this.useCustomStock = useCustomStock;
    }

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
    }

    public Boolean getUseCustomPrice() {
        return useCustomPrice;
    }

    public void setUseCustomPrice(Boolean useCustomPrice) {
        this.useCustomPrice = useCustomPrice;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
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
