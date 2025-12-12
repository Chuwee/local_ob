package es.onebox.common.datasources.orderitems.enums;

import es.onebox.common.datasources.orders.enums.ProductType;

public enum OrderItemType {

    SEAT,
    PRODUCT,
    GROUP;

    public static OrderItemType fromMsOrder(ProductType productType) {
        if (productType == null) {
            return null;
        }
        return valueOf(productType.name());
    }
}
