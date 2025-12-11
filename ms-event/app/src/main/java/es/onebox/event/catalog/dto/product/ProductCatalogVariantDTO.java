package es.onebox.event.catalog.dto.product;

import es.onebox.event.products.dao.couch.ProductCatalogVariantPrice;
import es.onebox.event.products.dao.couch.ProductStockStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class ProductCatalogVariantDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String sku;
    private ProductCatalogVariantPrice price;
    private Map<Long, ProductCatalogVariantPrice> sessionPrice;
    private ProductStockStatus stock;
    private Integer initialStock;

    private Long attribute1ValueId;
    private Long attribute2ValueId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public ProductCatalogVariantPrice getPrice() {
        return price;
    }

    public void setPrice(ProductCatalogVariantPrice price) {
        this.price = price;
    }

    public Map<Long, ProductCatalogVariantPrice> getSessionPrice() {
        return sessionPrice;
    }

    public void setSessionPrice(Map<Long, ProductCatalogVariantPrice> sessionPrice) {
        this.sessionPrice = sessionPrice;
    }

    public ProductStockStatus getStock() {
        return stock;
    }

    public void setAvailability(ProductStockStatus stock) {
        this.stock = stock;
    }

    public void setStock(ProductStockStatus stock) {
        this.stock = stock;
    }


    public Long getAttribute1ValueId() {
        return attribute1ValueId;
    }

    public void setAttribute1ValueId(Long attribute1ValueId) {
        this.attribute1ValueId = attribute1ValueId;
    }

    public Long getAttribute2ValueId() {
        return attribute2ValueId;
    }

    public void setAttribute2ValueId(Long attribute2ValueId) {
        this.attribute2ValueId = attribute2ValueId;
    }

    public Integer getInitialStock() {
        return initialStock;
    }

    public void setInitialStock(Integer initialStock) {
        this.initialStock = initialStock;
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

