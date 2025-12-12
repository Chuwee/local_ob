package es.onebox.internal.automaticsales.utils;

import org.junit.Test;
import es.onebox.common.datasources.distribution.dto.ItemWarning;
import es.onebox.common.datasources.distribution.dto.order.items.OrderItem;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;

public class OrderUtilsTest {

    @Test
    public void testValidateVoidItemWarnings(){
        List<OrderItem> items = new ArrayList<>();
        OrderItem item = new OrderItem();
        List<ItemWarning> warnings = new ArrayList<>();
        item.setItemWarnings(warnings);
        items.add(item);
        List<ItemWarning> itemWarnings = items.stream()
                .map(OrderItem::getItemWarnings)
                .toList()
                .stream()
                .flatMap(List::stream)
                .distinct()
                .toList();

        Assertions.assertTrue(itemWarnings.isEmpty());
    }

    @Test
    public void testValidateItemWarnings(){
        List<OrderItem> items = new ArrayList<>();
        OrderItem item = new OrderItem();
        List<ItemWarning> warnings = new ArrayList<>();
        warnings.add(ItemWarning.SESSION_NON_CONSECUTIVE_SEAT);
        item.setItemWarnings(warnings);
        items.add(item);
        List<ItemWarning> itemWarnings = items.stream()
                .map(OrderItem::getItemWarnings)
                .toList()
                .stream()
                .flatMap(List::stream)
                .distinct()
                .toList();

        Assertions.assertEquals(itemWarnings.size(), 1);
        Assertions.assertEquals(itemWarnings.get(0), ItemWarning.SESSION_NON_CONSECUTIVE_SEAT);
    }
}
