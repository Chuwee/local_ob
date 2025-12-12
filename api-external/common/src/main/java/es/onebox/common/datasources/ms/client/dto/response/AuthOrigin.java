package es.onebox.common.datasources.ms.client.dto.response;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class AuthOrigin implements Serializable {

    @Serial
    private static final long serialVersionUID = -5539158634124525811L;

    private LoginOrigin origin;
    private String id;
    private Map<String, Object> attributes;

    public LoginOrigin getOrigin() {
        return origin;
    }

    public void setOrigin(LoginOrigin origin) {
        this.origin = origin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
