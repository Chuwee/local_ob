package es.onebox.mgmt.datasources.ms.event.dto.event;

import java.io.Serial;
import java.io.Serializable;

public class PostBookingQuestion implements Serializable {

    @Serial
    private static final long serialVersionUID = -3858951050042545619L;

    private Integer id;
    private String name;
    private Translation label;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Translation getLabel() {
        return label;
    }

    public void setLabel(Translation label) {
        this.label = label;
    }

}
