package es.onebox.mgmt.entities.externalconfiguration.converter;

import es.onebox.mgmt.datasources.ms.entity.enums.AvetWSEnvironment;

import static es.onebox.mgmt.entities.externalconfiguration.enums.AvetWSEnvironment.PRE;
import static es.onebox.mgmt.entities.externalconfiguration.enums.AvetWSEnvironment.PRO;

public class AvetWSEnvironmentConverter {

    public static AvetWSEnvironment toMs(es.onebox.mgmt.entities.externalconfiguration.enums.AvetWSEnvironment wsConnectionVersion) {
        if (wsConnectionVersion == null) {
            return null;
        }
        return switch (wsConnectionVersion) {
            case PRE -> AvetWSEnvironment.PRE;
            case PRO -> AvetWSEnvironment.PRO;
        };
    }

    public static es.onebox.mgmt.entities.externalconfiguration.enums.AvetWSEnvironment toDto(AvetWSEnvironment wsConnectionVersion) {
        if (wsConnectionVersion == null) {
            return null;
        }
        return switch (wsConnectionVersion) {
            case PRE -> PRE;
            case PRO -> PRO;
        };
    }
}
