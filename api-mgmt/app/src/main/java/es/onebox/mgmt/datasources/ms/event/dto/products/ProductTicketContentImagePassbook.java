package es.onebox.mgmt.datasources.ms.event.dto.products;

import es.onebox.mgmt.products.enums.ticketContent.ProductTicketContentImagePassbookType;

import java.io.Serial;
import java.io.Serializable;

public class ProductTicketContentImagePassbook implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private ProductTicketContentImagePassbookType type;
    private String language;
    private String imageUrl;

    public ProductTicketContentImagePassbook(ProductTicketContentImagePassbookType type, String language, String imageUrl) {
        this.type = type;
        this.language = language;
        this.imageUrl = imageUrl;
    }

    public ProductTicketContentImagePassbook() {
    }

    public ProductTicketContentImagePassbookType getType() {
        return type;
    }

    public void setType(ProductTicketContentImagePassbookType type) {
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

