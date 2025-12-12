package es.onebox.common.datasources.distribution.dto.order.items.allocation;

import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.io.Serial;

public class Sector extends IdNameDTO {
    @Serial
    private static final long serialVersionUID = 4527587468508048103L;

    private String code;

    public Sector() {
        super();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
