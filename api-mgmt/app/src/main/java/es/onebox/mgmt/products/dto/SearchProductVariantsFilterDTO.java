package es.onebox.mgmt.products.dto;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.mgmt.products.enums.ProductVariantStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class SearchProductVariantsFilterDTO extends BaseRequestFilter implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String q;
    private ProductVariantStatus status;
    private Integer stock;
    private List<Long> ids;

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public ProductVariantStatus getStatus() {
        return status;
    }

    public void setStatus(ProductVariantStatus status) {
        this.status = status;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
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
