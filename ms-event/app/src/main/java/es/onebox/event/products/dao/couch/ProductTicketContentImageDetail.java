package es.onebox.event.products.dao.couch;

import es.onebox.event.products.enums.TicketContentImageType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ProductTicketContentImageDetail implements Serializable {
    @Serial
    private static final long serialVersionUID = -2581626070260784633L;

    private TicketContentImageType type;
    private String value;

    public ProductTicketContentImageDetail() {
    }

    public ProductTicketContentImageDetail(TicketContentImageType type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public TicketContentImageType getType() {
        return type;
    }

    public void setType(TicketContentImageType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
