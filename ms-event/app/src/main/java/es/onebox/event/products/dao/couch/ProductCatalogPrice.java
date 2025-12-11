package es.onebox.event.products.dao.couch;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ProductCatalogPrice implements Serializable {

    @Serial
    private static final long serialVersionUID = 4069984761140363099L;

    private ProductCatalogPriceDetail min;
    private ProductCatalogPriceDetail max;

    private ProductCatalogPriceDetail minNet;
    private ProductCatalogPriceDetail maxNet;

    private ProductCatalogPricePromotedDetail minPromoted;
    private ProductCatalogPricePromotedDetail minNetPromoted;

    private ProductCatalogPriceDetail minFinal;
    private ProductCatalogPriceDetail maxFinal;
    private ProductCatalogPricePromotedDetail minFinalPromoted;

    public ProductCatalogPriceDetail getMin() {
        return min;
    }

    public void setMin(ProductCatalogPriceDetail min) {
        this.min = min;
    }

    public ProductCatalogPriceDetail getMax() {
        return max;
    }

    public void setMax(ProductCatalogPriceDetail max) {
        this.max = max;
    }

    public ProductCatalogPriceDetail getMinNet() {
        return minNet;
    }

    public void setMinNet(ProductCatalogPriceDetail minNet) {
        this.minNet = minNet;
    }

    public ProductCatalogPriceDetail getMaxNet() {
        return maxNet;
    }

    public void setMaxNet(ProductCatalogPriceDetail maxNet) {
        this.maxNet = maxNet;
    }

    public ProductCatalogPricePromotedDetail getMinPromoted() {
        return minPromoted;
    }

    public void setMinPromoted(ProductCatalogPricePromotedDetail minPromoted) {
        this.minPromoted = minPromoted;
    }

    public ProductCatalogPricePromotedDetail getMinNetPromoted() {
        return minNetPromoted;
    }

    public void setMinNetPromoted(ProductCatalogPricePromotedDetail minNetPromoted) {
        this.minNetPromoted = minNetPromoted;
    }

    public ProductCatalogPriceDetail getMinFinal() {
        return minFinal;
    }

    public void setMinFinal(ProductCatalogPriceDetail minFinal) {
        this.minFinal = minFinal;
    }

    public ProductCatalogPriceDetail getMaxFinal() {
        return maxFinal;
    }

    public void setMaxFinal(ProductCatalogPriceDetail maxFinal) {
        this.maxFinal = maxFinal;
    }

    public ProductCatalogPricePromotedDetail getMinFinalPromoted() {
        return minFinalPromoted;
    }

    public void setMinFinalPromoted(ProductCatalogPricePromotedDetail minFinalPromoted) {
        this.minFinalPromoted = minFinalPromoted;
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

