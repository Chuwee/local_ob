package es.onebox.event.products.dto;

import es.onebox.event.products.enums.ProductCommunicationElementsImagesType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;

public class CreateProductCommunicationElementImageDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer tagId;
    @NotNull(message = "position can not be null")
    private Integer position;

    @NotNull(message = "type can not be null")
    private ProductCommunicationElementsImagesType type;
    private Long id;
    private String tag;
    private String language;
    private Long languageId;
    private String value;
    private String altText;
    @NotNull(message = "image can not be null")
    private Optional<String> imageBinary;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Long getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Long languageId) {
        this.languageId = languageId;
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

