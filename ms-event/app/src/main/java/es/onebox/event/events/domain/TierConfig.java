package es.onebox.event.events.domain;

import es.onebox.couchbase.annotations.CouchDocument;
import es.onebox.couchbase.annotations.Id;

import java.io.Serializable;

@CouchDocument
public class TierConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long tierId;
    private TierTranslation tierTranslation;

    public Long getTierId() {
        return tierId;
    }

    public void setTierId(Long tierId) {
        this.tierId = tierId;
    }

    public TierTranslation getTierTranslation() {
        return tierTranslation;
    }

    public void setTierTranslation(TierTranslation tierTranslation) {
        this.tierTranslation = tierTranslation;
    }
}
