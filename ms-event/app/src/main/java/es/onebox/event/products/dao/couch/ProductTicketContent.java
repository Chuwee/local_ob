package es.onebox.event.products.dao.couch;

import es.onebox.couchbase.annotations.Id;
import es.onebox.event.common.enums.TicketType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class ProductTicketContent implements Serializable {
    @Serial
    private static final long serialVersionUID = -2581626070260784633L;

    @Id
    private Long productId;

    private Map<TicketType, Map<String, ProductTicketContentValue>> ticketContentByType;

    public ProductTicketContent(Long productId) {
        this.productId = productId;
    }

    public ProductTicketContent() {
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Map<TicketType, Map<String, ProductTicketContentValue>> getTicketContentByType() {
        return ticketContentByType;
    }

    public void setTicketContentByType(Map<TicketType, Map<String, ProductTicketContentValue>> ticketContentByType) {
        this.ticketContentByType = ticketContentByType;
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

