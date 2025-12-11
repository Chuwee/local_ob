package es.onebox.mgmt.datasources.ms.event.dto.products;

import es.onebox.mgmt.datasources.common.dto.BaseCommunicationElement;
import es.onebox.mgmt.products.enums.ProductCommunicationElementsImagesType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class ProductCommunicationElementImage extends BaseCommunicationElement {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer tagId;
    private Integer position;
    private ProductCommunicationElementsImagesType type;

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public ProductCommunicationElementsImagesType getType() {
        return type;
    }

    public void setType(ProductCommunicationElementsImagesType type) {
        this.type = type;
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
