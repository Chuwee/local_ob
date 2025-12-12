package es.onebox.common.datasources.webhook.dto.fever.webhook;

import java.io.Serial;
import java.io.Serializable;

public class FeverKafkaSchemaField implements Serializable {

    @Serial
    private static final long serialVersionUID = 4456214312971792147L;

    private String field;
    private String name;
    private boolean optional;
    private String type;
    private Integer version;

    public FeverKafkaSchemaField() {
    }

    public FeverKafkaSchemaField(String field, String type) {
        this.field = field;
        this.type = type;
    }

    public FeverKafkaSchemaField(String field, String name, String type, Integer version) {
        this.field = field;
        this.name = name;
        this.type = type;
        this.version = version;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getOptional() {
        return optional;
    }

    public void setOptional(Boolean optional) {
        this.optional = optional;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
