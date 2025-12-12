package es.onebox.fcb.config;

public final class CouchbaseKeys {

    private CouchbaseKeys() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static final String BUCKET_ONEBOX_INT = "onebox-int";

    public static final String SCOPE_FCB = "fcb";

    public static final String FCB_ORDERS_COLLECTION = "orders";
    public static final String FCB_COUNTERS_COLLECTION = "counters";

    public static final String ORDER_KEY = "order";
    public static final String COUNTER_KEY = "counter";
    public static final String B2C_PEOPLESOFT_COUNTER_KEY = "b2c_peoplesoft_counter";

    public static final String CHANNEL_KEY = "channelErpConfig";
    public static final String CHANNEL_COLLECTION = "channels";

}
