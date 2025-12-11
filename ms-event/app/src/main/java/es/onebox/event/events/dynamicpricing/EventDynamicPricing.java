package es.onebox.event.events.dynamicpricing;

import es.onebox.couchbase.annotations.CouchDocument;

import java.io.Serializable;
import java.util.List;

@CouchDocument
public class EventDynamicPricing implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
