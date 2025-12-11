package es.onebox.mgmt.datasources.ms.event.dto.products;

import es.onebox.mgmt.datasources.ms.event.dto.products.enums.ProductStockType;
import es.onebox.mgmt.datasources.ms.event.dto.products.enums.ProductType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class CreateProduct implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long entityId;
    private Long producerId;
    private String name;
    private ProductStockType stockType;
    private ProductType productType;
    private Long currencyId;

    public CreateProduct() {
    }

    public CreateProduct(Long entityId, Long producerId, String name, ProductStockType stockType,
                         ProductType productType, Long currencyId) {
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

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
