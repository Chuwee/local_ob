package es.onebox.mgmt.datasources.ms.venue.dto.template;

import java.io.Serializable;

public class RowDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private Integer order;
    private Long sectorId;

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

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Long getSectorId() {
        return sectorId;
    }

    public void setSectorId(Long sectorId) {
        this.sectorId = sectorId;
    }
}
