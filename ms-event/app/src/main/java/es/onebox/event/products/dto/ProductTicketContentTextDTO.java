package es.onebox.event.products.dto;

import es.onebox.event.products.enums.TicketContentTextType;

import java.io.Serial;
import java.io.Serializable;

public class ProductTicketContentTextDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private TicketContentTextType type;
    private String language;
    private String value;

    public ProductTicketContentTextDTO(TicketContentTextType type, String language, String value) {
        this.type = type;
        this.language = language;
        this.value = value;
    }

    public ProductTicketContentTextDTO() {
    }

    public TicketContentTextType getType() {
        return type;
    }

    public void setType(TicketContentTextType type) {
        this.type = type;
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
}
