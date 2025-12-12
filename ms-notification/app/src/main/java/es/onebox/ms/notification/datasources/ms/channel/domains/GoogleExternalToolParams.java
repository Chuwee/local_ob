package es.onebox.ms.notification.datasources.ms.channel.domains;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class GoogleExternalToolParams implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String measurementId;
    private Map<String, String> schema;

    public String getMeasurementId() {
        return measurementId;
    }

    public void setMeasurementId(String measurementId) {
        this.measurementId = measurementId;
    }

    public Map<String, String> getSchema() {
        return schema;
    }

    public void setSchema(Map<String, String> schema) {
        this.schema = schema;
    }
}
