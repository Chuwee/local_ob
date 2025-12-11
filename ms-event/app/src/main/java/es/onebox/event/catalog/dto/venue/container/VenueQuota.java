package es.onebox.event.catalog.dto.venue.container;

import java.io.Serial;
import java.io.Serializable;

public class VenueQuota implements Serializable {

    @Serial
    private static final long serialVersionUID = -8143809322621532400L;

    private Integer id;
    private String name;
    private String code;
    private Boolean defaultQuota;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getDefaultQuota() {
        return defaultQuota;
    }

    public void setDefaultQuota(Boolean defaultQuota) {
        this.defaultQuota = defaultQuota;
    }
}
