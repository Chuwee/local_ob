package es.onebox.mgmt.venues.dto.elementsinfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class VenueTemplateElementInfoBulkRequestDTO extends VenueTemplateElementInfoSearchDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 113265668879944424L;

    @JsonProperty("all_elements")
    private Boolean allElements;
    private List<String> elements;

    public Boolean getAllElements() {
        return allElements;
    }

    public void setAllElements(Boolean allElements) {
        this.allElements = allElements;
    }

    public List<String> getElements() {
        return elements;
    }

    public void setElements(List<String> elements) {
        this.elements = elements;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
