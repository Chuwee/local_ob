package es.onebox.common.datasources.webhook.dto.fever.product;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import es.onebox.common.datasources.ms.event.dto.CategoryDTO;
import es.onebox.common.datasources.ms.event.enums.ProductState;
import es.onebox.common.datasources.ms.event.enums.ProductStockType;
import es.onebox.common.datasources.ms.event.enums.ProductType;
import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.io.Serializable;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProductDetailDTO implements Serializable {
    private String name;
    private ProductType productType;
    private ProductStockType stockType;
    private ProductState productState;
    private Long currencyId;
    private CategoryDTO category;
    private CategoryDTO customCategory;
    private IdNameDTO tax;
    private IdNameDTO surchargeTax;

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

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

    public ProductState getProductState() {
        return productState;
    }

    public void setProductState(ProductState productState) {
        this.productState = productState;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

    public CategoryDTO getCustomCategory() {
        return customCategory;
    }

    public void setCustomCategory(CategoryDTO customCategory) {
        this.customCategory = customCategory;
    }

    public IdNameDTO getTax() { return tax; }

    public void setTax(IdNameDTO tax) { this.tax = tax; }

    public IdNameDTO getSurchargeTax() { return surchargeTax; }

    public void setSurchargeTax(IdNameDTO surchargeTax) { this.surchargeTax = surchargeTax; }
}
