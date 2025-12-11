package es.onebox.mgmt.datasources.ms.entity.dto;

import java.io.Serial;
import java.io.Serializable;

public record UserLimits(Long biBasicUsed, Long biBasicTotal, Long biBasicLimit, Long biAdvancedUsed, Long biAdvancedTotal,
                         Long biAdvancedLimit, Long biMobileUsed, Long biMobileLimit) implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;
}
