package es.onebox.mgmt.datasources.ms.event.dto.pricesimulation;

import java.io.Serializable;

public class VenueConfigBase implements Serializable {

    private static final long serialVersionUID = 3120599417414578544L;

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
