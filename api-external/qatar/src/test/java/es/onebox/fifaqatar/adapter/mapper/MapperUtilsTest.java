package es.onebox.fifaqatar.adapter.mapper;

import es.onebox.common.datasources.orderitems.dto.OrderItem;
import es.onebox.common.datasources.orderitems.dto.transfer.OrderItemTransfer;
import es.onebox.common.datasources.orderitems.dto.transfer.TransferReceiver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class MapperUtilsTest {

    @Test
    void test_transferredItems() {
        final String CUSTOMER_ID = "XXXX";
        final String RECEIVER_CUSTOMER_ID = "AAAA";

        List<OrderItem> items = new ArrayList<>();
        items.add(mockOrderItem(CUSTOMER_ID, null));
        items.add(mockOrderItem(CUSTOMER_ID, null));
        items.add(mockOrderItem(null, null));
        items.add(mockOrderItem(CUSTOMER_ID, RECEIVER_CUSTOMER_ID));

        List<OrderItem> ownerItems = MapperUtils.filterOrderItemsByOwnerAndNotTransferred(items, CUSTOMER_ID);
        Assertions.assertEquals(2, ownerItems.size());
        List<OrderItem> receiverItems = MapperUtils.filterOrderItemsByReceiver(items, RECEIVER_CUSTOMER_ID);
        Assertions.assertEquals(1, receiverItems.size());

    }

    private OrderItem mockOrderItem(String customerId, String receiverCustomerId) {
        OrderItem item = new OrderItem();
        item.setUserId(customerId);
        if (receiverCustomerId != null) {
            OrderItemTransfer transfer = new OrderItemTransfer();
            transfer.setStatus("TRANSFERRED");
            TransferReceiver receiver = new TransferReceiver();
            receiver.setCustomerId(receiverCustomerId);
            transfer.setReceiver(receiver);
            item.setTransfer(transfer);
        }

        return item;
    }
}
