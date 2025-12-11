package es.onebox.event.venues.domain;

import es.onebox.couchbase.annotations.CouchDocument;
import es.onebox.couchbase.annotations.Id;

import java.io.Serializable;

@CouchDocument
public class PriceTypeConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long priceTypeId;
    private PriceTypeTranslation priceTypeTranslation;

    public Long getPriceTypeId() {
        return priceTypeId;
    }

    public void setPriceTypeId(Long priceTypeId) {
        this.priceTypeId = priceTypeId;
    }

    public PriceTypeTranslation getPriceTypeTranslation() {
        return priceTypeTranslation;
    }

    public void setPriceTypeTranslation(PriceTypeTranslation priceTypeTranslation) {
        this.priceTypeTranslation = priceTypeTranslation;
    }
}
