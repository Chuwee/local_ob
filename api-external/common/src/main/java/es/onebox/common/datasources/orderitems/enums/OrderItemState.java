package es.onebox.common.datasources.orderitems.enums;

import es.onebox.common.datasources.common.enums.OrderType;

public enum OrderItemState {

    PURCHASE,
    BOOKING,
    ISSUE,
    REFUND,
    SEC_MKT_PURCHASE;

    public static OrderItemState fromOrderState(OrderType orderType) {
        if (orderType == null) {
            return null;
        }
        return valueOf(orderType.name());
    }
}
