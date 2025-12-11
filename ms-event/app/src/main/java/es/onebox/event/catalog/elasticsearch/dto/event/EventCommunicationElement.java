package es.onebox.event.catalog.elasticsearch.dto.event;

import es.onebox.event.catalog.elasticsearch.dto.CommunicationElement;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * Created by mmolinero on 26/02/19.
 */
public class EventCommunicationElement extends CommunicationElement implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer tagOrder;

    public EventCommunicationElement() {
        super();
    }

    public Integer getTagOrder() {
        return tagOrder;
    }

    public void setTagOrder(Integer tagOrder) {
        this.tagOrder = tagOrder;
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
