package es.onebox.circuitcat.venues.dto;

import java.io.Serializable;

public class SectorDTO implements Serializable {

    private static final long serialVersionUID = 8229899478132805382L;

    private String code;
    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
