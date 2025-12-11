package es.onebox.event.catalog.dto.presales;

import es.onebox.event.events.enums.Provider;

public enum PresaleResolverType {
    DEFAULT,
    SGA;

    public static PresaleResolverType getByProvider(Provider provider) {
        if (provider == null) {
            return DEFAULT;
        }

        return switch (provider) {
            case SGA -> SGA;
            default -> DEFAULT;
        };
    }
}
