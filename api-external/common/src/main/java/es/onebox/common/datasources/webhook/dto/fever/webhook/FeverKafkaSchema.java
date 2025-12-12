package es.onebox.common.datasources.webhook.dto.fever.webhook;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class FeverKafkaSchema implements Serializable {

    @Serial
    private static final long serialVersionUID = 8246249489784416725L;

    private List<FeverKafkaSchemaField> fields;
    private String name;
    private boolean optional;
    private String type;

    public FeverKafkaSchema() {
    }

    public List<FeverKafkaSchemaField> getFields() {
        return fields;
    }

    public void setFields(List<FeverKafkaSchemaField> fields) {
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
