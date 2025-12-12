package es.onebox.common.url;

public class ExternalTicketParams {
    private String entityId;
    private Long sessionId;
    private String orderId;
    private Long productId;

    public ExternalTicketParams(String entityId, Long sessionId, String orderId, Long productId) {
        this.entityId = entityId;
        this.sessionId = sessionId;
        this.orderId = orderId;
        this.productId = productId;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
