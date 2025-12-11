package es.onebox.mgmt.datasources.ms.event.dto.products;

import es.onebox.mgmt.products.enums.ticketContent.ProductTicketContentImagePdfType;

import java.io.Serial;
import java.io.Serializable;

public class ProductTicketContentImagePdf implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private ProductTicketContentImagePdfType type;
    private String language;
    private String imageUrl;

    public ProductTicketContentImagePdf(ProductTicketContentImagePdfType type, String language, String imageUrl) {
        this.type = type;
        this.language = language;
        this.imageUrl = imageUrl;
    }

    public ProductTicketContentImagePdf() {
    }

    public ProductTicketContentImagePdfType getType() {
        return type;
    }

    public void setType(ProductTicketContentImagePdfType type) {
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

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
