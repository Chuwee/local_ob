package es.onebox.mgmt.datasources.common.dto;

import java.io.Serial;
import java.io.Serializable;

public class IdCapacity implements Serializable {

    @Serial
    private static final long serialVersionUID = -4328948294204543824L;

    private Long id;
    private Long capacity;
    private Boolean onSale;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCapacity() {
        return capacity;
    }

    public void setCapacity(Long capacity) {
        this.capacity = capacity;
    }

    public Boolean getOnSale() {
        return onSale;
    }

    public void setOnSale(Boolean onSale) {
        this.onSale = onSale;
    }
}
