package es.onebox.internal.sgtm.domains;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class FacebookExternalToolParams implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String pixelId;
    private Map<String, String> schema;


    public String getPixelId() {
        return pixelId;
    }

    public void setPixelId(String pixelId) {
        this.pixelId = pixelId;
    }

    public Map<String, String> getSchema() {
        return schema;
    }

    public void setSchema(Map<String, String> schema) {
        this.schema = schema;
    }
}
