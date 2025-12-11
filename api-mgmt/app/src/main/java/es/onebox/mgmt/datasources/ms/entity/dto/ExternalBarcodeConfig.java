package es.onebox.mgmt.datasources.ms.entity.dto;

import java.io.Serializable;
import java.util.Map;

public class ExternalBarcodeConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private Map<String, Object> properties;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

}
