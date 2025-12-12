package es.onebox.common.datasources.distribution.dto.order.items.allocation;

import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.io.Serial;

public class Row extends IdNameDTO {

    @Serial
    private static final long serialVersionUID = 1222817057065893569L;
    private String block;
    private Long order;

    public Row(){
        super();
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }
}
