package es.onebox.event.communicationelements.dto;

import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class CommunicationElementDTO<T extends Enum<T>> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    @NotNull
    private T tag;
    @NotNull
    private String language;
    private String value;
    private String imageBinary;
    private String altText;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public T getTag() {
        return tag;
    }

    public void setTag(T tag) {
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

    public String getImageBinary() {
        return imageBinary;
    }

    public void setImageBinary(String imageBinary) {
        this.imageBinary = imageBinary;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
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
