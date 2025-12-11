package es.onebox.mgmt.common.promotions.dto;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.mgmt.common.promotions.enums.PromotionStatus;
import es.onebox.mgmt.datasources.ms.promotion.enums.PromotionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PromotionsFilter extends BaseRequestFilter {

    private static final long serialVersionUID = 1L;

    private PromotionStatus status;
    private PromotionType type;
    private SortOperator<String> sort;
    private Long currencyId;

    public PromotionType getType() {
        return type;
    }

    public void setType(PromotionType type) {
        this.type = type;
    }

    public PromotionStatus getStatus() {
        return status;
    }

    public void setStatus(PromotionStatus status) {
        this.status = status;
    }

    public Long getCurrencyId() { return currencyId; }

    public void setCurrencyId(Long currencyId) { this.currencyId = currencyId; }

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
