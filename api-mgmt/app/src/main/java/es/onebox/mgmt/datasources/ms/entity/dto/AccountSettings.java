package es.onebox.mgmt.datasources.ms.entity.dto;

import java.io.Serial;
import java.io.Serializable;

public class AccountSettings implements Serializable {

    @Serial
    private static final long serialVersionUID = -5442093730926419754L;

    private QueueConfig queueConfig;

    public QueueConfig getQueueConfig() {
        return queueConfig;
    }

    public void setQueueConfig(QueueConfig queueConfig) {
        this.queueConfig = queueConfig;
    }
}