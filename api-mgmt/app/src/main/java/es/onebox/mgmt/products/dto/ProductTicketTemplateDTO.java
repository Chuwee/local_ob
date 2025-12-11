package es.onebox.mgmt.products.dto;

import es.onebox.mgmt.events.enums.EventTicketTemplateType;
import es.onebox.mgmt.products.enums.ProductTicketTemplateType;
import es.onebox.mgmt.tickettemplates.enums.TicketTemplateFormat;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ProductTicketTemplateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private ProductTicketTemplateType type;
    private TicketTemplateFormat format;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductTicketTemplateType getType() {
        return type;
    }

    public void setType(ProductTicketTemplateType type) {
        this.type = type;
    }

    public TicketTemplateFormat getFormat() {
        return format;
    }

    public void setFormat(TicketTemplateFormat format) {
        this.format = format;
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
