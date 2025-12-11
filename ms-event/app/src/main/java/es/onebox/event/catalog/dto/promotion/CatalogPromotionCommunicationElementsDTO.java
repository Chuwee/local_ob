package es.onebox.event.catalog.dto.promotion;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Map;

public class CatalogPromotionCommunicationElementsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, String> name;

    private Map<String, String> description;

    public void setName(Map<String, String> name) {
        this.name = name;
    }

    public Map<String, String> getName() {
        return name;
    }

    public Map<String, String> getDescription() {
        return description;
    }

    public void setDescription(Map<String, String> description) {
        this.description = description;
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
