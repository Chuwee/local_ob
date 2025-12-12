package es.onebox.common.datasources.ms.channel.dto.config;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record DomainSettings(
        Boolean useCustomDomain,
        DomainMode mode,
        List<CustomDomain> domains
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 7137930853138775167L;
}
