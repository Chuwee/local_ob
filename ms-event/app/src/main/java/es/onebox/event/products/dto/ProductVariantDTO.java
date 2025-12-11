package es.onebox.event.products.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.products.enums.ProductVariantStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class ProductVariantDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -6959897765333279098L;

    private Long id;
    private IdNameDTO product;
    private String name;
    private String sku;
    private Double price;
    private IdNameDTO variantOption1;
    private IdNameDTO variantOption2;
    private IdNameDTO variantValue1;
    private IdNameDTO variantValue2;
    private Integer stock;
    private ProductVariantStatus productVariantStatus;
    private ZonedDateTime createDate;
    private ZonedDateTime updateDate;

    public ProductVariantDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IdNameDTO getProduct() {
        return product;
    }

    public void setProduct(IdNameDTO product) {
        this.product = product;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
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

    public IdNameDTO getVariantOption1() {
        return variantOption1;
    }

    public void setVariantOption1(IdNameDTO variantOption1) {
        this.variantOption1 = variantOption1;
    }

    public IdNameDTO getVariantOption2() {
        return variantOption2;
    }

    public void setVariantOption2(IdNameDTO variantOption2) {
        this.variantOption2 = variantOption2;
    }

    public IdNameDTO getVariantValue1() {
        return variantValue1;
    }

    public void setVariantValue1(IdNameDTO variantValue1) {
        this.variantValue1 = variantValue1;
    }

    public IdNameDTO getVariantValue2() {
        return variantValue2;
    }

    public void setVariantValue2(IdNameDTO variantValue2) {
        this.variantValue2 = variantValue2;
    }

    public ProductVariantStatus getProductVariantStatus() {
        return productVariantStatus;
    }

    public void setProductVariantStatus(ProductVariantStatus productVariantStatus) {
        this.productVariantStatus = productVariantStatus;
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
