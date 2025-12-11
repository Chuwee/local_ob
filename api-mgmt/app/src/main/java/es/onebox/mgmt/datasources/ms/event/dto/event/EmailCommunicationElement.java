package es.onebox.mgmt.datasources.ms.event.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.datasources.common.dto.BaseCommunicationElement;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class EmailCommunicationElement extends BaseCommunicationElement {

    private static final long serialVersionUID = 1L;

    @JsonProperty("alt_text")
    private String altText;

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
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
