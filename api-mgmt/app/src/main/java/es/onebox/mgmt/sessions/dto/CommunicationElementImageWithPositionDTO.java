package es.onebox.mgmt.sessions.dto;

import es.onebox.mgmt.common.CommunicationElementImageDTO;
import es.onebox.mgmt.validation.annotation.ImageWithPosition;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@ImageWithPosition
public class CommunicationElementImageWithPositionDTO<E extends Enum<E>> extends CommunicationElementImageDTO<E> {

    private static final long serialVersionUID = 1L;

    private Integer position;
    private Integer tagId;

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
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
