package es.onebox.event.products.dao.couch;

import es.onebox.couchbase.annotations.Id;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * @author msopena
 */
public class ProductContentDocument implements Serializable {

    @Serial
    private static final long serialVersionUID = -2581626070260784633L;

    @Id(index = 1)
    private Long productId;

    @Id(index = 2)
    private Long attributeId;

    private Map<String, ProductCommunicationElement> languageElements;

    private Map<String, ProductContentDocumentValue> values;

    public ProductContentDocument() {

    }

    public ProductContentDocument(Long productId, Long attributeId) {
        this.productId = productId;
        this.attributeId = attributeId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(Long attributeId) {
        this.attributeId = attributeId;
    }

    public Map<String, ProductCommunicationElement> getLanguageElements() {
        return languageElements;
    }

    public void setLanguageElements(Map<String, ProductCommunicationElement> languageElements) {
        this.languageElements = languageElements;
    }

    public Map<String, ProductContentDocumentValue> getValues() {
        return values;
    }

    public void setValues(Map<String, ProductContentDocumentValue> values) {
        this.values = values;
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
