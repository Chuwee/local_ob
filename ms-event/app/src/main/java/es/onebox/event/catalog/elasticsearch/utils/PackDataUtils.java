package es.onebox.event.catalog.elasticsearch.utils;

public class PackDataUtils {

    private PackDataUtils() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static final String PACK_INDEX = "packdata";
    public static final String KEY_CHANNEL_PACK = "channelPack";

    private static final String GENERIC_SEPARATOR = "|";

    public static String getChannelPackKey(Long channelId, Long packId) {
        return KEY_CHANNEL_PACK + GENERIC_SEPARATOR + channelId + GENERIC_SEPARATOR + packId;
    }

}
