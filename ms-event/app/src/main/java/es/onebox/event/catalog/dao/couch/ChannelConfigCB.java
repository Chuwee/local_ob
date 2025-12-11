package es.onebox.event.catalog.dao.couch;

import es.onebox.couchbase.annotations.CouchDocument;
import es.onebox.couchbase.annotations.Id;

import java.io.Serial;
import java.io.Serializable;

@CouchDocument
public class ChannelConfigCB implements Serializable {

    @Serial
    private static final long serialVersionUID = 1197945548738058240L;

    @Id
    private Integer id;
    private Boolean allowPriceTypeTagFilter;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getAllowPriceTypeTagFilter() {
        return allowPriceTypeTagFilter;
    }

    public void setAllowPriceTypeTagFilter(Boolean allowPriceTypeTagFilter) {
        this.allowPriceTypeTagFilter = allowPriceTypeTagFilter;
    }
}
