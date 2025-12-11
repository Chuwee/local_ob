package es.onebox.event.catalog.dto.product;

import es.onebox.event.products.dao.couch.ProductCatalogCommunicationElement;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @author msopena
 */
public class ProductCatalogCommunicationElementsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -2581626070260784633L;

    private List<ProductCatalogCommunicationElementDTO> texts;
    private List<ProductCatalogCommunicationElementDTO> images;

    public List<ProductCatalogCommunicationElementDTO> getTexts() {
        return texts;
    }

    public void setTexts(List<ProductCatalogCommunicationElementDTO> texts) {
        this.texts = texts;
    }

    public List<ProductCatalogCommunicationElementDTO> getImages() {
        return images;
    }

    public void setImages(List<ProductCatalogCommunicationElementDTO> images) {
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
