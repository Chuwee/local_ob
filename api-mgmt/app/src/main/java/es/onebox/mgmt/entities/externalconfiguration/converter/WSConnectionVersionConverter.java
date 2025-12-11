package es.onebox.mgmt.entities.externalconfiguration.converter;

import es.onebox.mgmt.datasources.ms.entity.dto.WSConnectionVersion;

import static es.onebox.mgmt.entities.externalconfiguration.enums.WSConnectionVersion.ONE_DOT_X;
import static es.onebox.mgmt.entities.externalconfiguration.enums.WSConnectionVersion.TWO_DOT_FOUR;
import static es.onebox.mgmt.entities.externalconfiguration.enums.WSConnectionVersion.TWO_DOT_FOUR_LEGACY;

public class WSConnectionVersionConverter {

    public static WSConnectionVersion toMs(es.onebox.mgmt.entities.externalconfiguration.enums.WSConnectionVersion wsConnectionVersion) {
        if (wsConnectionVersion == null) {
            return null;
        }
        return switch (wsConnectionVersion) {
            case TWO_DOT_FOUR -> WSConnectionVersion.TWO_DOT_FOUR;
            case TWO_DOT_FOUR_LEGACY -> WSConnectionVersion.TWO_DOT_FOUR_LEGACY;
            case ONE_DOT_X -> WSConnectionVersion.ONE_DOT_X;
        };
    }

    public static es.onebox.mgmt.entities.externalconfiguration.enums.WSConnectionVersion toDto(WSConnectionVersion wsConnectionVersion) {
        if (wsConnectionVersion == null) {
            return null;
        }
        return switch (wsConnectionVersion) {
            case TWO_DOT_FOUR -> TWO_DOT_FOUR;
            case TWO_DOT_FOUR_LEGACY -> TWO_DOT_FOUR_LEGACY;
            case ONE_DOT_X -> ONE_DOT_X;
        };
    }
}
