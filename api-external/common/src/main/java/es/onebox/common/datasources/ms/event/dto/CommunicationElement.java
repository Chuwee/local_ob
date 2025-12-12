package es.onebox.common.datasources.ms.event.dto;

import es.onebox.core.serializer.dto.common.IdDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.Optional;

public class CommunicationElement extends IdDTO {

    @Serial
    private static final long serialVersionUID = 2L;

    private String tagType;
    private String tag;
    private String language;
    private String value;
    private Optional<String> imageBinary;

    public String getTagType() {
        return tagType;
    }

    public void setTagType(String tagType) {
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
