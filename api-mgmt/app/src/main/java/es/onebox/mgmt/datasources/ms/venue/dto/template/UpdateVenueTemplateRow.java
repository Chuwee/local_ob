package es.onebox.mgmt.datasources.ms.venue.dto.template;

import java.io.Serializable;

public class UpdateVenueTemplateRow implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer sectorId;
    private String name;
    private Integer order;

    public UpdateVenueTemplateRow() {
    }

    public Integer getSectorId() {
        return sectorId;
    }

    public void setSectorId(Integer sectorId) {
        this.sectorId = sectorId;
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
}
