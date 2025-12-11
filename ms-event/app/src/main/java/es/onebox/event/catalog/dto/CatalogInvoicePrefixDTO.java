package es.onebox.event.catalog.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class CatalogInvoicePrefixDTO implements Serializable {

    private static final long serialVersionUID = 2820104153543850505L;

    private Long id;
    private String prefix;

    public CatalogInvoicePrefixDTO(Long id, String prefix) {
        this.id = id;
        this.prefix = prefix;
    }

    public CatalogInvoicePrefixDTO() {
        super();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
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
