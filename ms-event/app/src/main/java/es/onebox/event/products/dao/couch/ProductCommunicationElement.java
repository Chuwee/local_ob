package es.onebox.event.products.dao.couch;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author msopena
 */
public class ProductCommunicationElement implements Serializable {

    @Serial
    private static final long serialVersionUID = -2581626070260784633L;

    private List<ProductCommunicationElementDetail> texts;
    private List<ProductCommunicationElementDetail> images;

    public List<ProductCommunicationElementDetail> getTexts() {
        return texts;
    }

    public void setTexts(List<ProductCommunicationElementDetail> texts) {
        this.texts = texts;
    }

    public List<ProductCommunicationElementDetail> getImages() {
        return images;
    }

    public void setImages(List<ProductCommunicationElementDetail> images) {
        this.images = images;
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
