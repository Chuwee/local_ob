package es.onebox.mgmt.channels.promotions.dto;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.mgmt.common.promotions.enums.PromotionStatus;
import es.onebox.mgmt.datasources.ms.promotion.enums.ChannelPromotionType;

import java.io.Serial;

public class ChannelPromotionsFilter extends BaseRequestFilter {

    @Serial
    private static final long serialVersionUID = 4202402190327460003L;

    private PromotionStatus status;
    private ChannelPromotionType type;
    private SortOperator<String> sort;

    public PromotionStatus getStatus() {
        return status;
    }

    public void setStatus(PromotionStatus status) {
        this.status = status;
    }

    public ChannelPromotionType getType() {
        return type;
    }

    public void setType(ChannelPromotionType type) {
        this.type = type;
    }

    public SortOperator<String> getSort() {
        return sort;
    }

    public void setSort(SortOperator<String> sort) {
        this.sort = sort;
    }
}
