package es.onebox.ath.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;

public class PayloadRequestDTO {

    @NotNull(message = "Missing order code")
    @JsonProperty("order_code")
    private String orderCode;
    @NotNull(message = "Missing entity id")
    @JsonProperty("entity_id")
    private Long entityId;
    @NotNull(message = "Missing token")
    private String token;

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
