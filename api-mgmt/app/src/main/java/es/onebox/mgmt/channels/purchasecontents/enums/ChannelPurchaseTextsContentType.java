package es.onebox.mgmt.channels.purchasecontents.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ChannelPurchaseTextsContentType {
    BANNER("BANNER_URL"),
    HEADER_BANNER("HEADER_BANNER_URL");

    private final String key;

    ChannelPurchaseTextsContentType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static ChannelPurchaseTextsContentType fromKey(String key) {
        return Arrays.stream(values()).filter(i -> i.getKey().equals(key)).findFirst().orElse(null);
    }

    public static List<String> getNamesList() {
        return Arrays.stream(values()).map(ChannelPurchaseTextsContentType::getKey).collect(Collectors.toList());
    }
}

