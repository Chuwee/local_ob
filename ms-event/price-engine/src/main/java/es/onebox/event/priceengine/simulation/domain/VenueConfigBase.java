package es.onebox.event.priceengine.simulation.domain;

import java.io.Serial;
import java.io.Serializable;

public class VenueConfigBase implements Serializable {

    @Serial
    private static final long serialVersionUID = 8110301424167581920L;

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
