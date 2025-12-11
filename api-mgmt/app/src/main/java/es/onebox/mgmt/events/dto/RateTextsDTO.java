package es.onebox.mgmt.events.dto;

import java.io.Serializable;
import java.util.Map;

public class RateTextsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, String> name;

    public RateTextsDTO() {
    }

    public RateTextsDTO(Map<String, String> name) {
        this.name = name;
    }

    public Map<String, String> getName() {
        return name;
    }

    public void setName(Map<String, String> name) {
        this.name = name;
    }
}
