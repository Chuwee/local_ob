package es.onebox.event.products.dto;

import es.onebox.event.products.enums.TicketContentImageType;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ProductTicketContentImageDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "Type cannot be null")
    private TicketContentImageType type;

    @NotNull(message = "Language cannot be null")
    private String language;

    @NotNull(message = "Image url cannot be null")
    private String imageUrl;

    public ProductTicketContentImageDTO() {
    }


    public ProductTicketContentImageDTO(TicketContentImageType type, String language, String imageUrl) {
        this.type = type;
        this.language = language;
        this.imageUrl = imageUrl;
    }

    public TicketContentImageType getType() {
        return type;
    }

    public void setType(TicketContentImageType type) {
        this.type = type;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String value) {
        this.imageUrl = value;
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
