package es.onebox.common.datasources.distribution.dto.order.items.promotion;

import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.io.Serial;
import java.io.Serializable;

public record CollectiveApplied(
        String code,
        IdNameDTO collective
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 9184716443668374989L;
}
