package es.onebox.event.products.dao.couch;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ProductCatalogAttribute implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private List<ProductCatalogValue> values;
    private Map<String, Map<String, String>> contents;
    private Long position;
    private Long attributeNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProductCatalogValue> getValues() { return values; }

    public void setValues(List<ProductCatalogValue> values) { this.values = values; }

    public Map<String, Map<String, String>> getContents() { return contents; }

    public void setContents(Map<String, Map<String, String>> contents) { this.contents = contents; }

    public Long getPosition() { return position; }

    public void setPosition(Long position) { this.position = position; }

    public Long getAttributeNumber() { return attributeNumber; }

    public void setAttributeNumber(Long attributeNumber) { this.attributeNumber = attributeNumber; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}

