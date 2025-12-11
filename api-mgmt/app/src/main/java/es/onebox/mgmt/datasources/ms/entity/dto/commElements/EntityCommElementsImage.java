package es.onebox.mgmt.datasources.ms.entity.dto.commElements;

import es.onebox.mgmt.datasources.common.dto.BaseCommunicationElement;
import es.onebox.mgmt.entities.enums.EntityImageContentResponseType;
import es.onebox.mgmt.entities.enums.EntityImageContentType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class EntityCommElementsImage extends BaseCommunicationElement {

    @Serial
    private static final long serialVersionUID = -3336322430271297841L;

    private Integer tagId;
    private Integer position;
    private EntityImageContentType type;
    private Long languageId;

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

    public EntityImageContentType getType() {
        return type;
    }

    public void setType(EntityImageContentType type) {
        this.type = type;
    }

    public Long getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Long languageId) {
        this.languageId = languageId;
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
