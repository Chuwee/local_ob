package es.onebox.mgmt.integrations.authvendor.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class AuthVendorDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 146643558880755024L;

    private String id;
    private Map<String, String> properties;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
