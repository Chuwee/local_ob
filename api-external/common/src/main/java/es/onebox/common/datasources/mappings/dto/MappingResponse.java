package es.onebox.common.datasources.mappings.dto;

import java.io.Serial;
import java.io.Serializable;

public class MappingResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 6821459292101226396L;
    private String mappedId;

    public String getMappedId() {
        return mappedId;
    }

    public void setMappedId(String mappedId) {
        this.mappedId = mappedId;
    }
}
