package es.onebox.mgmt.datasources.ms.event.dto.event;

import java.io.Serializable;

public class AdditionalConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Long avetPriceId;

    // JSON or JPA constructor
    private AdditionalConfigDTO() {
        this(null);
    }

    public AdditionalConfigDTO(Long avetPriceId) {
        this.avetPriceId = avetPriceId;
    }

    public Long getAvetPriceId() {
        return avetPriceId;
    }
}
