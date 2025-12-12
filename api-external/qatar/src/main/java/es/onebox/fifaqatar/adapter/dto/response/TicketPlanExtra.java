package es.onebox.fifaqatar.adapter.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class TicketPlanExtra implements Serializable {

    @Serial
    private static final long serialVersionUID = 9046495907781509905L;

    @JsonProperty("timeless")
    private Boolean timeless;
    @JsonProperty("urgency")
    private Boolean urgency;
    @JsonProperty("disallow_indexing")
    private Boolean disallowIndexing;
    @JsonProperty("3ds_required")
    private Boolean required3ds;

    public Boolean getTimeless() {
        return timeless;
    }

    public void setTimeless(Boolean timeless) {
        this.timeless = timeless;
    }

    public Boolean getUrgency() {
        return urgency;
    }

    public void setUrgency(Boolean urgency) {
        this.urgency = urgency;
    }

    public Boolean getDisallowIndexing() {
        return disallowIndexing;
    }

    public void setDisallowIndexing(Boolean disallowIndexing) {
        this.disallowIndexing = disallowIndexing;
    }

    public Boolean getRequired3ds() {
        return required3ds;
    }

    public void setRequired3ds(Boolean required3ds) {
        this.required3ds = required3ds;
    }
}
