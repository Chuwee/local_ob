package es.onebox.common.datasources.webhook.dto.fever.product;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import es.onebox.common.datasources.ms.event.enums.ProductCommunicationElementsImagesType;

import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProductCommunicationElementImageFeverDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -3456789012345678901L;

    private Integer tagId;
    private Integer position;
    private Long id;
    private String tag;
    private String language;
    private ProductCommunicationElementsImagesType type;
    private String value;
    private Optional<String> imageBinary;
    private String altText;

    public ProductCommunicationElementImageFeverDTO() {
    }

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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public ProductCommunicationElementsImagesType getType() {
        return type;
    }

    public void setType(ProductCommunicationElementsImagesType type) {
        this.type = type;
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
}
