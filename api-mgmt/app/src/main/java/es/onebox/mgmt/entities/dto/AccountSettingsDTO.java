package es.onebox.mgmt.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class AccountSettingsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -7635862211619050157L;

    @JsonProperty("queue_config")
    private QueueConfigDTO queueConfig;

    public QueueConfigDTO getQueueConfig() {
        return queueConfig;
    }

    public void setQueueConfig(QueueConfigDTO queueConfig) {
        this.queueConfig = queueConfig;
    }
}