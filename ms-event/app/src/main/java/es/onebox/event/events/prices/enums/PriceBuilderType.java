package es.onebox.event.events.prices.enums;

import es.onebox.event.events.enums.Provider;

public enum PriceBuilderType {
    DEFAULT,
    SGA;

    public static PriceBuilderType getByProvider(Provider provider) {
        if  (provider == null) {
            return DEFAULT;
        }
        return switch (provider) {
            case SGA -> SGA;
            default -> DEFAULT;
        };
    }
}
