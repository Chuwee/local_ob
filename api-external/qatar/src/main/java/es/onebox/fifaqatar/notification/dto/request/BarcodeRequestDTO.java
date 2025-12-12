package es.onebox.fifaqatar.notification.dto.request;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class BarcodeRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -342453539538612428L;

    private Integer sourceSessionId;
    private Integer destinationSessionId;
    private List<String> barcodes;

    public Integer getSourceSessionId() {
        return sourceSessionId;
    }

    public void setSourceSessionId(Integer sourceSessionId) {
        this.sourceSessionId = sourceSessionId;
    }

    public Integer getDestinationSessionId() {
        return destinationSessionId;
    }

    public void setDestinationSessionId(Integer destinationSessionId) {
        this.destinationSessionId = destinationSessionId;
    }

    public List<String> getBarcodes() {
        return barcodes;
    }

    public void setBarcodes(List<String> barcodes) {
        this.barcodes = barcodes;
    }
}
