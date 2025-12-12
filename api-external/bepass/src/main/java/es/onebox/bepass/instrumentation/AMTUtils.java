package es.onebox.bepass.instrumentation;

import es.onebox.bepass.common.BepassEntityConfiguration;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class AMTUtils {

    public static final String ENTITY_ID = "entity.id";
    public static final String BEPASS_TENANT = "bepass.tenant";

    private AMTUtils() {
    }

    public static String resolveTenant(BepassEntityConfiguration config) {
        String[] split = StringUtils.split(config.tenantId(), "-");
        if (ArrayUtils.isNotEmpty(split)) {
            return split[0];
        }
        return null;
    }
}
