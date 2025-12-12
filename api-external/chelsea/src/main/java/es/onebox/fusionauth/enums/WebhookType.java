package es.onebox.fusionauth.enums;

public enum WebhookType {
    USER_CREATE("user.create"),
    USER_UPDATE("user.update"),
    USER_DELETE("user.delete"),
    USER_CREATE_COMPLETE("user.create.complete"),
    USER_UPDATE_COMPLETE("user.update.complete"),
    USER_DELETE_COMPLETE("user.delete.complete");

    private final String type;

    WebhookType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static WebhookType fromType(String type) {
        for (WebhookType eventType : values()) {
            if (eventType.type.equals(type)) {
                return eventType;
            }
        }
        return null;
    }
}
