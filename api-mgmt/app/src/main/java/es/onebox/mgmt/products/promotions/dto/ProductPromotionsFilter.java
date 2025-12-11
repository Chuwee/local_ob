package es.onebox.mgmt.products.promotions.dto;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.mgmt.common.promotions.enums.PromotionStatus;
import es.onebox.mgmt.products.promotions.enums.ProductPromotionType;

import java.io.Serial;

public class ProductPromotionsFilter extends BaseRequestFilter {

    @Serial
    private static final long serialVersionUID = 4202402190327460003L;

    private PromotionStatus status;
    private ProductPromotionType type;
    private SortOperator<String> sort;

    public PromotionStatus getStatus() {
        return status;
    }

    public void setStatus(PromotionStatus status) {
        this.status = status;
    }

    public ProductPromotionType getType() {
        return type;
    }

    public void setType(ProductPromotionType type) {
        this.type = type;
    }

    public SortOperator<String> getSort() {
        return sort;
    }

    public void setSort(SortOperator<String> sort) {
        this.sort = sort;
    }
}
