package es.onebox.mgmt.vouchers.dto;

import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;

@Size(min = 1, max = 50000, message = "The number of vouchers to create must be between 1 and 50000")
public class CreateVoucherBulkRequestDTO extends ArrayList<CreateVoucherRequestDTO> {

    private static final long serialVersionUID = 2L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
