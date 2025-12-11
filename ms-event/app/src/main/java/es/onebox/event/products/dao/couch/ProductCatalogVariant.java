package es.onebox.event.products.dao.couch;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Map;

public class ProductCatalogVariant implements Serializable {
    @Serial
    private static final long serialVersionUID = 832460566673130282L;

    private Long id;
    private String name;
    private String sku;
    private ProductCatalogVariantPrice price;
    private Map<Long, ProductCatalogVariantPrice> sessionPrice;
    private Integer initialStock;
    private ZonedDateTime createDate;
    private ZonedDateTime updateDate;

    private Long value1;
    private Long value2;

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

    public Integer getInitialStock() {
        return initialStock;
    }

    public void setInitialStock(Integer initialStock) {
        this.initialStock = initialStock;
    }

    public Long getValue1() {
        return value1;
    }

    public void setValue1(Long value1) {
        this.value1 = value1;
    }

    public Long getValue2() {
        return value2;
    }

    public void setValue2(Long value2) {
        this.value2 = value2;
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

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}

