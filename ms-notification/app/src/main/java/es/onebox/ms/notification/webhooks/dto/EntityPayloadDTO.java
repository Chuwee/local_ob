package es.onebox.ms.notification.webhooks.dto;

public class EntityPayloadDTO extends PayloadRequest {

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
