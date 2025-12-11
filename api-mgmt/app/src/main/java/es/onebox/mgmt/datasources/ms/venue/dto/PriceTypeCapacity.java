package es.onebox.mgmt.datasources.ms.venue.dto;

import es.onebox.mgmt.datasources.common.dto.IdCapacity;

import java.io.Serializable;
import java.util.List;

public class PriceTypeCapacity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private Long capacity;
    private List<IdCapacity> quotas;

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

    public Long getCapacity() {
        return capacity;
    }

    public void setCapacity(Long capacity) {
        this.capacity = capacity;
    }

    public List<IdCapacity> getQuotas() {
        return quotas;
    }

    public void setQuotas(List<IdCapacity> quotas) {
        this.quotas = quotas;
    }
}
