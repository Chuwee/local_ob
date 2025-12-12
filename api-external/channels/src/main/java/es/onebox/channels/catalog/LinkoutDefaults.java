package es.onebox.channels.catalog;

import java.util.Arrays;

public enum LinkoutDefaults {
    PRE("pre", "portal-pre.oneboxtickets.net", "tickets.oneboxtds.net"),
    PRE01("pre01", "portal-pre01.oneboxtickets.net", "channels-pre01.oneboxtickets.net"),
    PRO("pro", "proticketing.com", "tickets.oneboxtds.com");

    private String env;
    private String domainV3;
    private String domainV4;

    LinkoutDefaults(String env, String domainV3, String domainV4) {
        this.env = env;
        this.domainV3 = domainV3;
        this.domainV4 = domainV4;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getDomainV3() {
        return domainV3;
    }

    public void setDomainV3(String domainV3) {
        this.domainV3 = domainV3;
    }

    public String getDomainV4() {
        return domainV4;
    }

    public void setDomainV4(String domainV4) {
        this.domainV4 = domainV4;
    }

    public static LinkoutDefaults fromEnv(String env) {
        return Arrays.stream(LinkoutDefaults.values())
                .filter(l -> l.env.equals(env))
                .findFirst()
                .orElse(null);
    }
}
