package es.onebox.event.common.amqp.refreshdata;

import es.onebox.message.broker.client.message.AbstractNotificationMessage;

import java.io.Serial;

public class ProductMigrationMessage extends AbstractNotificationMessage {

    @Serial
    private static final long serialVersionUID = 34356364782L;

    private static final String MESSAGE_NAME = "";
    private static final String ROUTING_KEY = "";

    private Long productId;

    public ProductMigrationMessage() {
        super(MESSAGE_NAME, ROUTING_KEY);
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
