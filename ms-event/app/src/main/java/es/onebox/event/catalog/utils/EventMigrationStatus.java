package es.onebox.event.catalog.utils;

import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class EventMigrationStatus implements Serializable {

    @Serial
    private static final long serialVersionUID = 4131839864548994027L;

    private ZonedDateTime lastExecutionBeginTime;
    private EventIndexationType indexationType;
    private boolean running;

    public EventMigrationStatus(ZonedDateTime lastExecutionBeginTime, EventIndexationType indexationType, boolean running) {
        this.lastExecutionBeginTime = lastExecutionBeginTime;
        this.indexationType = indexationType;
        this.running = running;
    }

    public ZonedDateTime getLastExecutionBeginTime() {
        return lastExecutionBeginTime;
    }

    public void setLastExecutionBeginTime(ZonedDateTime lastExecutionBeginTime) {
        this.lastExecutionBeginTime = lastExecutionBeginTime;
    }

    public EventIndexationType getIndexationType() {
        return indexationType;
    }

    public void setIndexationType(EventIndexationType indexationType) {
        this.indexationType = indexationType;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
