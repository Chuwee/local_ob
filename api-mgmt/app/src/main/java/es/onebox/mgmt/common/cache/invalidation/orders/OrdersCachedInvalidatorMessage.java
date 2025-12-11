package es.onebox.mgmt.common.cache.invalidation.orders;

import es.onebox.message.broker.client.message.AbstractNotificationMessage;
import es.onebox.mgmt.common.cache.enums.OrdersCachedMappingsType;

public class OrdersCachedInvalidatorMessage extends AbstractNotificationMessage {
    private Long id;
    private OrdersCachedMappingsType mappingType;
    private static final String MESSAGE_NAME = "invalidation-cached";

    private static final String ROUTING_KEY = "orders-cached";

    public OrdersCachedInvalidatorMessage() {
        super(MESSAGE_NAME, ROUTING_KEY);
    }

    public OrdersCachedInvalidatorMessage(Long id, OrdersCachedMappingsType mappingType) {
        super(MESSAGE_NAME, ROUTING_KEY);
        this.id = id;
        this.mappingType = mappingType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrdersCachedMappingsType getMappingType() {
        return mappingType;
    }

    public void setMappingType(OrdersCachedMappingsType mappingType) {
        this.mappingType = mappingType;
    }
}
