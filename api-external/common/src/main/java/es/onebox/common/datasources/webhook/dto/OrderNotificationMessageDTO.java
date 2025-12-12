package es.onebox.common.datasources.webhook.dto;

import java.util.Map;

public class OrderNotificationMessageDTO {

    private Map<String, String> headers;
    private OrderPayloadDTO payload;
    private String signature;

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public OrderPayloadDTO getPayload() {
        return payload;
    }

    public void setPayload(OrderPayloadDTO payload) {
        this.payload = payload;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "OrderNotificationMessageDTO{" +
                "headers=" + headers +
                ", payload=" + payload +
                ", signature='" + signature + '\'' +
                '}';
    }
}
