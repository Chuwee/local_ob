package es.onebox.mgmt.products.dto.ticketContent;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.products.enums.ticketContent.ProductTicketContentImagePdfType;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class ProductTicketContentImagePdfDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "type must not be null")
    private ProductTicketContentImagePdfType type;

    @NotNull(message = "language must not be null")
    private String language;

    @JsonProperty("image_url")
    @NotNull(message = "image url must not be null")
    private String imageUrl;

    public ProductTicketContentImagePdfDTO(ProductTicketContentImagePdfType type, String language, String imageUrl) {
        this.type = type;
        this.language = language;
        this.imageUrl = imageUrl;
    }

    public ProductTicketContentImagePdfDTO() {
    }

    public ProductTicketContentImagePdfType getType() {
        return type;
    }

    public void setType( ProductTicketContentImagePdfType type) {
        this.type = type;
    }

    public  String getLanguage() {
        return language;
    }

    public void setLanguage( String language) {
        this.language = language;
    }

    public  String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl( String imageUrl) {
        this.imageUrl = imageUrl;
    }
}