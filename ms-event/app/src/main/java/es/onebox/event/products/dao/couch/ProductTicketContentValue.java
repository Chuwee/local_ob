package es.onebox.event.products.dao.couch;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ProductTicketContentValue implements Serializable {
    @Serial
    private static final long serialVersionUID = -2581626070260784633L;

    private List<ProductTicketContentTextDetail> texts;
    private List<ProductTicketContentImageDetail> images;

    public ProductTicketContentValue() {
    }

    public ProductTicketContentValue(List<ProductTicketContentTextDetail> texts, List<ProductTicketContentImageDetail> images) {
        this.texts = texts;
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

    public List<ProductTicketContentTextDetail> getTexts() {
        return texts;
    }

    public void setTexts(List<ProductTicketContentTextDetail> texts) {
        this.texts = texts;
    }

    public List<ProductTicketContentImageDetail> getImages() {
        return images;
    }

    public void setImages(List<ProductTicketContentImageDetail> images) {
        this.images = images;
    }
}
