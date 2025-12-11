package es.onebox.event.products.dao.couch;

import es.onebox.couchbase.annotations.CouchDocument;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

@CouchDocument
public class ProductTicketLiterals extends HashMap<String, Object> {

    @Serial
    private static final long serialVersionUID = -1016334916654124049L;

    public ProductTicketLiterals() {
    }

    public ProductTicketLiterals(Map<String, Object> texts) {
        super(texts);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
