package es.onebox.fifaqatar.notification.mapping.entity;

import java.io.Serializable;
import java.util.List;

public class SessionBarcodeMapping implements Serializable {

    private Long sourceSessionId;
    private Long destinationSessionId;
    private List<GateMapping> gates;

    public Long getSourceSessionId() {
        return sourceSessionId;
    }

    public void setSourceSessionId(Long sourceSessionId) {
        this.sourceSessionId = sourceSessionId;
    }

    public Long getDestinationSessionId() {
        return destinationSessionId;
    }

    public void setDestinationSessionId(Long destinationSessionId) {
        this.destinationSessionId = destinationSessionId;
    }

    public List<GateMapping> getGates() {
        return gates;
    }

    public void setGates(List<GateMapping> gates) {
        this.gates = gates;
    }
}
