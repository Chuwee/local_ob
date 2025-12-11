package es.onebox.event.catalog.dto.venue.container;

import java.io.Serial;
import java.io.Serializable;

public class VenueSector implements Serializable {

    @Serial
    private static final long serialVersionUID = 771320612982496319L;

    private Integer id;
    private String code;
    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
