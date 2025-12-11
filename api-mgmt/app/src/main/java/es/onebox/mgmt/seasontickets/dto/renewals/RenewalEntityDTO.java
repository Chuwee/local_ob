package es.onebox.mgmt.seasontickets.dto.renewals;

import java.io.Serializable;

public class RenewalEntityDTO implements Serializable  {
    private static final long serialVersionUID = -1899095256812677457L;

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
