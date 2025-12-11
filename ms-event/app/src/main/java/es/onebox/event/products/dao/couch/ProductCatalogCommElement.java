package es.onebox.event.products.dao.couch;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @author msopena
 */
public class ProductCatalogCommElement implements Serializable {

    @Serial
    private static final long serialVersionUID = -2581626070260784633L;

    private List<ProductCatalogCommunicationElement> texts;
    private List<ProductCatalogCommunicationElement> images;

    public List<ProductCatalogCommunicationElement> getTexts() {
        return texts;
    }

    public void setTexts(List<ProductCatalogCommunicationElement> texts) {
        this.texts = texts;
    }

    public List<ProductCatalogCommunicationElement> getImages() {
        return images;
    }

    public void setImages(List<ProductCatalogCommunicationElement> images) {
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
