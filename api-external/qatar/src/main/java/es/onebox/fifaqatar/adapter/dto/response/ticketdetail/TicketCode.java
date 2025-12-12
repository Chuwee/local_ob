package es.onebox.fifaqatar.adapter.dto.response.ticketdetail;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class TicketCode implements Serializable {

    @Serial
    private static final long serialVersionUID = 6341399376813306204L;

    private Integer id;
    private String code;
    private String image;
    @JsonProperty("code_validity")
    private TicketCodeValidity validity;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public TicketCodeValidity getValidity() {
        return validity;
    }

    public void setValidity(TicketCodeValidity validity) {
        this.validity = validity;
    }
}
