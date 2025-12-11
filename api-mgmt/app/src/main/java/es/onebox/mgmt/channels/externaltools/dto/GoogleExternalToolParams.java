package es.onebox.mgmt.channels.externaltools.dto;

import java.util.Map;

public class GoogleExternalToolParams {

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
