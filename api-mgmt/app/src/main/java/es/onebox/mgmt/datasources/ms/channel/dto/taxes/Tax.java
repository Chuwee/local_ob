package es.onebox.mgmt.datasources.ms.channel.dto.taxes;

import java.io.Serial;
import java.io.Serializable;

public class Tax implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
