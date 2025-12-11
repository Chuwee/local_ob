package es.onebox.mgmt.products.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

public class CreateProductAttributeDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Length(max = 50, message = "username max size is 50")
    @NotEmpty(message = "name can not be null")
    @Pattern(regexp = "^[^|]*$", message = "Invalid characters. | is not allowed in the name")
    private String name;
    @Min(value = 0, message = "position must be equal or above 0")
    private Integer position;

    public CreateProductAttributeDTO() {
    }

    public CreateProductAttributeDTO(String name, Integer position) {
        this.name = name;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
