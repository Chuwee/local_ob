package es.onebox.mgmt.channels.externaltools.dto;

import java.util.Map;

public class FacebookExternalToolParams {

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
