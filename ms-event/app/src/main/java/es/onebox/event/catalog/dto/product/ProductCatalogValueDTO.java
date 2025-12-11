package es.onebox.event.catalog.dto.product;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class ProductCatalogValueDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private Integer position;
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

    public Integer getPosition() { return position; }

    public void setPosition(Integer position) { this.position = position; }

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

