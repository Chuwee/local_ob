package es.onebox.bepass.auth;

import es.onebox.bepass.common.BepassEntityConfiguration;
import es.onebox.bepass.exception.BepassErrorCode;
import es.onebox.core.exception.OneboxRestException;

public class BepassAuthContext {

    private static final ThreadLocal<BepassEntityConfiguration> ENTITY_PROVIDER = new ThreadLocal<>();

    private BepassAuthContext() {
    }

    public static void add(BepassEntityConfiguration configuration) {
        if (configuration == null) {
            throw new OneboxRestException(BepassErrorCode.INVALID_REQUEST);
        }
        ENTITY_PROVIDER.set(configuration);
    }

    public static BepassEntityConfiguration get() {
        return ENTITY_PROVIDER.get();
    }

    public static void remove() {
        ENTITY_PROVIDER.remove();
    }


}
