package es.onebox.event.tickettemplates.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import es.onebox.core.serializer.dto.common.IdDTO;

import java.util.Optional;

public class CommunicationElementDTO extends IdDTO {

    private static final long serialVersionUID = 2L;

    private TicketTemplateTagType tagType;
    private String tag;
    private String language;
    private String value;
    private Optional<String> imageBinary;

    public TicketTemplateTagType getTagType() {
        return tagType;
    }

    public void setTagType(TicketTemplateTagType tagType) {
        this.tagType = tagType;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Optional<String> getImageBinary() {
        return imageBinary;
    }

    public void setImageBinary(Optional<String> imageBinary) {
        this.imageBinary = imageBinary;
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
