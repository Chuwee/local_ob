package es.onebox.event.products.dao.couch;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * @author msopena
 */
public class ProductContentDocumentValue implements Serializable {

    @Serial
    private static final long serialVersionUID = -2581626070260784633L;

    private Map<String, ProductCommunicationElement> languageElements;

    public ProductContentDocumentValue() {
    }

    public Map<String, ProductCommunicationElement> getLanguageElements() {
        return languageElements;
    }

    public void setLanguageElements(Map<String, ProductCommunicationElement> languageElements) {
        this.languageElements = languageElements;
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
