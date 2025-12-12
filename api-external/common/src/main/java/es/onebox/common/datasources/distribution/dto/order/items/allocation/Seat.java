package es.onebox.common.datasources.distribution.dto.order.items.allocation;

import java.io.Serial;
import java.io.Serializable;

public class Seat implements Serializable {

    @Serial
    private static final long serialVersionUID = 2768581985051130164L;

    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
