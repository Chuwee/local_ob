package es.onebox.event.events.request;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.event.events.dto.RateGroupType;

import java.io.Serializable;

public class RatesFilter extends BaseRequestFilter implements Serializable {

    private static final long serialVersionUID = 1L;

    private RateGroupType type;

    private String externalDescription;


    public RateGroupType getType() {
        return type;
    }

    public void setType(RateGroupType type) {
        this.type = type;
    }

    public String getExternalDescription() {
        return externalDescription;
    }

    public void setExternalDescription(String externalDescription) {
        this.externalDescription = externalDescription;
    }
}
