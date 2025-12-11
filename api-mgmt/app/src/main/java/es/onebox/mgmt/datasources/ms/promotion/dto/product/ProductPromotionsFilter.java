package es.onebox.mgmt.datasources.ms.promotion.dto.product;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.mgmt.datasources.ms.promotion.enums.PromotionActivationStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ProductPromotionsFilter extends BaseRequestFilter {

    private static final long serialVersionUID = 1L;

    private Integer promotionId;
    private PromotionActivationStatus status;
    private ProductPromotionType type;
    private SortOperator<String> sort;

    public ProductPromotionsFilter() {
    }

    public ProductPromotionsFilter(Integer promotionId) {
        this.promotionId = promotionId;
    }

    public Integer getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Integer promotionId) {
        this.promotionId = promotionId;
    }

    public ProductPromotionType getType() {
        return type;
    }

    public void setType(ProductPromotionType type) {
        this.type = type;
    }

    public PromotionActivationStatus getStatus() {
        return status;
    }

    public void setStatus(PromotionActivationStatus status) {
        this.status = status;
    }

    public SortOperator<String> getSort() {
        return sort;
    }

    public void setSort(SortOperator<String> sort) {
        this.sort = sort;
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
