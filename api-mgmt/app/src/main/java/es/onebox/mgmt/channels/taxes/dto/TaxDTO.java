package es.onebox.mgmt.channels.taxes.dto;

import java.io.Serial;
import java.io.Serializable;

public class TaxDTO implements Serializable {

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
