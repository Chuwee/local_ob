package es.onebox.event.seasontickets.dto.renewals;

import java.io.Serializable;

public class RenewalEntityDTO implements Serializable {
    private static final long serialVersionUID = -4204567562089322275L;

    public RenewalEntityDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

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
