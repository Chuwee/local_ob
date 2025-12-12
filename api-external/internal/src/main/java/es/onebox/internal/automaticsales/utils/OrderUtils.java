package es.onebox.internal.automaticsales.utils;

import es.onebox.common.datasources.distribution.dto.ItemWarning;
import es.onebox.common.datasources.distribution.dto.order.items.ItemAllocationType;
import es.onebox.common.datasources.distribution.dto.order.items.OrderItem;

import java.util.List;
import java.util.Objects;

public class OrderUtils {

    public static List<ItemWarning> filterOrderWarnings(List<OrderItem> items) {
        return items.stream()
                .filter(orderItem -> orderItem.getAllocation().getType().equals(ItemAllocationType.NUMBERED))
                .map(OrderItem::getItemWarnings)
                .toList()
                .stream()
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .distinct()
                .toList();
    }
}
