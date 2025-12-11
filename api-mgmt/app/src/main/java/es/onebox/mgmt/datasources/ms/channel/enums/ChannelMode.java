package es.onebox.mgmt.datasources.ms.channel.enums;

import java.util.stream.Stream;

public enum ChannelMode {

    TEST(0),
    REAL(1);

    private final int mode;

    ChannelMode(int mode) {
        this.mode = mode;
    }

    public static ChannelMode get(int mode) {
        return Stream.of(values())
                .filter(cm -> cm.mode == mode)
                .findAny()
                .orElse(null);
    }

    public int getMode() {
        return mode;
    }
}
