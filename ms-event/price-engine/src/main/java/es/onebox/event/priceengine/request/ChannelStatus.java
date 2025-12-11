package es.onebox.event.priceengine.request;

public enum ChannelStatus {

    DELETED(0),
    ACTIVE(1),
    BLOCKED(2),
    BLOCKED_TEMPORARILY(3),
    PENDING(4);

    private int id;

    ChannelStatus(Integer id) {
        this.id = id;
    }

    public static ChannelStatus get(int id) {
        return values()[id];
    }

    public int getId() {
        return id;
    }
}
