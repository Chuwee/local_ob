package es.onebox.event.common.amqp.refreshdata;

import es.onebox.message.broker.client.message.AbstractNotificationMessage;

import java.io.Serial;

public class RefreshDataMessage extends AbstractNotificationMessage {

    @Serial
    private static final long serialVersionUID = 1L;

    public enum Type {
        EVENT_REFRESH,
        SEASON_TICKET_REFRESH,
        SESSION_REFRESH;
    }

    private Type type;
    private Long id;
    private String origin;
    private String refreshType;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getRefreshType() {
        return refreshType;
    }

    public void setRefreshType(String refreshType) {
        this.refreshType = refreshType;
    }
}
