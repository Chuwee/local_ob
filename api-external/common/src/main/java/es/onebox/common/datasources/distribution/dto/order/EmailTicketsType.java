package es.onebox.common.datasources.distribution.dto.order;

public enum EmailTicketsType {
    DEFAULT, DEFERRED;
    public static EmailTicketsType getByBoolean(boolean isDeferred) {
        return isDeferred ? DEFERRED : DEFAULT;
    }
}
