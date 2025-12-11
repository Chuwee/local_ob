package es.onebox.event.catalog.dto.product;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ProductCatalogAttributeDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private Long position;
    private List<ProductCatalogValueDTO> values;
    private Map<String, Map<String, String>> contents;

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

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public List<ProductCatalogValueDTO> getValues() {
        return values;
    }

    public void setValues(List<ProductCatalogValueDTO> values) {
        this.values = values;
    }

    public Map<String, Map<String, String>> getContents() {
        return contents;
    }

    public void setContents(Map<String, Map<String, String>> contents) {
        this.contents = contents;
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

