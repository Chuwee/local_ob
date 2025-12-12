package es.onebox.common.datasources.webhook.dto.fever.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class FeverKafkaMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 4456214312971792147L;

    @JsonProperty("payload")
    private FeverKafkaPayload payload;
    @JsonProperty("schema")
    private FeverKafkaSchema schema;

    public FeverKafkaMessage() {
    }

    public FeverKafkaMessage(FeverKafkaPayload payload, FeverKafkaSchema schema) {
        this.payload = payload;
        this.schema = schema;
    }

    public FeverKafkaPayload getPayload() {
        return payload;
    }

    public void setPayload(FeverKafkaPayload payload) {
        this.payload = payload;
    }

    public FeverKafkaSchema getSchema() {
        return schema;
    }

    public void setSchema(FeverKafkaSchema schema) {
        this.schema = schema;
    }
}
