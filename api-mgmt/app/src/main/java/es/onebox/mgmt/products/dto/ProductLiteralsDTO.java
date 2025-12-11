package es.onebox.mgmt.products.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.Collection;
import java.util.HashSet;

@Valid
@Size(max = 100, message = "text contents collection must be less than 100 elements")
public class ProductLiteralsDTO extends HashSet<ProductLiteralDTO> {

    @Serial
    private static final long serialVersionUID = 1L;

    public ProductLiteralsDTO(Collection<ProductLiteralDTO> in) {
        super(in);
    }

    public ProductLiteralsDTO() {
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
