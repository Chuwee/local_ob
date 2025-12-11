package es.onebox.event.pricesengine.dto;

import java.io.Serializable;

public class VenueConfigBaseDTO implements Serializable {

    private static final long serialVersionUID = 8110301424167581920L;

    private Long id;
    private String name;

    public VenueConfigBaseDTO() {
    }

    public VenueConfigBaseDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

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
