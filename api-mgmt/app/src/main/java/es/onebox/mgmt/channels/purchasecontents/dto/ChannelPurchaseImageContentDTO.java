package es.onebox.mgmt.channels.purchasecontents.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.validation.annotation.LanguageIETF;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ChannelPurchaseImageContentDTO<T extends Enum<T>> implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "type cannot be null")
    private T type;

    @LanguageIETF
    @NotNull(message = "language cannot be null")
    private String language;

    @JsonProperty("image")
    @NotNull(message = "image cannot be null")
    private String imageBinary;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("alt_text")
    private String altText;

    public T getType() {
        return type;
    }

    public void setType(T type) {
        this.type = type;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getImageBinary() {
        return imageBinary;
    }

    public void setImageBinary(String imageBinary) {
        this.imageBinary = imageBinary;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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
