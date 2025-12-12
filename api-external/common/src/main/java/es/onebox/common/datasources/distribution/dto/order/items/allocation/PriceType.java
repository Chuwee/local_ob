package es.onebox.common.datasources.distribution.dto.order.items.allocation;

import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.io.Serial;

public class PriceType extends IdNameDTO {

    @Serial
    private static final long serialVersionUID = -7441331944157505121L;

    public PriceType() {
        super();
    }

    public PriceType(Long id, String name) {
        super(id, name);
    }
}
