package es.onebox.common.datasources.ms.client.dto.request;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class AuthOrigin implements Serializable {

    @Serial
    private static final long serialVersionUID = -3100293162579368035L;

    private String origin;
    private String id;
    private Map<String, String> attributes;

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
}
