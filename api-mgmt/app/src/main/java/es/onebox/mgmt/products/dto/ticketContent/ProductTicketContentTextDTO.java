package es.onebox.mgmt.products.dto.ticketContent;

import es.onebox.mgmt.products.enums.ticketContent.ProductTicketContentTextType;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class ProductTicketContentTextDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "type must not be null")
    private ProductTicketContentTextType type;

    @NotNull(message = "language must not be null")
    private String language;

    @NotNull(message = "value must not be null")
    private String value;

    public ProductTicketContentTextDTO() {
    }

    public ProductTicketContentTextType getType() {
        return type;
    }

    public void setType(ProductTicketContentTextType type) {
        this.type = type;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public @NotNull(message = "value must not be null") String getValue() {
        return value;
    }

    public void setValue(@NotNull(message = "value must not be null") String value) {
        this.value = value;
    }
}
