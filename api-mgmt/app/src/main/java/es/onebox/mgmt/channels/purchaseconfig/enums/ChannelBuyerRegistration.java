package es.onebox.mgmt.channels.purchaseconfig.enums;

import java.util.Arrays;

public enum ChannelBuyerRegistration {
    REQUIRED(true, true),
    OPTIONAL(true, false),
    DISABLED(false, false);

    private final boolean useUserLogin;
    private final boolean forceLogin;

    ChannelBuyerRegistration(boolean useUserLogin, boolean forceLogin) {
        this.useUserLogin = useUserLogin;
        this.forceLogin = forceLogin;
    }

    public boolean isUseUserLogin() {
        return useUserLogin;
    }

    public boolean isForceLogin() {
        return forceLogin;
    }

    public static ChannelBuyerRegistration get(boolean useUserLogin, boolean forceLogin) {
        return Arrays.stream(ChannelBuyerRegistration.values())
                .filter(v -> v.forceLogin == forceLogin && v.useUserLogin == useUserLogin)
                .findFirst().orElse(null);
    }
}
