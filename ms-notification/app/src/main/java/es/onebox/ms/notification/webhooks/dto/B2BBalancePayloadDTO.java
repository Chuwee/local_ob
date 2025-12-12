package es.onebox.ms.notification.webhooks.dto;

public class B2BBalancePayloadDTO extends PayloadRequest {

    private String movementId;

    public String getMovementId() {
        return movementId;
    }

    public void setMovementId(String movementId) {
        this.movementId = movementId;
    }

}
