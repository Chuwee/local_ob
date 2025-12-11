package es.onebox.event.venues.domain;

import java.io.Serializable;
import java.util.Map;

public class PriceTypeTranslation implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, String> name;
    private Map<String, String> description;


    public Map<String, String> getName() {
        return name;
    }

    public void setName(Map<String, String> name) {
        this.name = name;
    }

    public Map<String, String> getDescription() {
        return description;
    }

    public void setDescription(Map<String, String> description) {
        this.description = description;
    }
}
