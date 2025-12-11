package es.onebox.mgmt.venues.dto.elementsinfo;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

public class VenueTemplateElementRestrictionCheckDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Length(max = 150, message = "text max size is 150")
    @NotNull
    private String text;
    private Integer position;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
