package es.onebox.fifaqatar.notification.mapping.entity;

import java.io.Serial;
import java.io.Serializable;

public class GateMapping implements Serializable {

    @Serial
    private static final long serialVersionUID = 8119604435713296027L;

    private Integer sourceId;
    private Integer destinationId;
    private String destinationSectorName;

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public Integer getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(Integer destinationId) {
        this.destinationId = destinationId;
    }

    public String getDestinationSectorName() {
        return destinationSectorName;
    }

    public void setDestinationSectorName(String destinationSectorName) {
        this.destinationSectorName = destinationSectorName;
    }
}
