package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class TaxesDataDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private TaxesDataTypeDTO type;

    @Min(value = 1, message = "producer_id must be greater than 1")
    @JsonProperty("producer_id")
    private Integer producerId;

    @Min(value = 1, message = "invoice_prefix_id must be greater than 1")
    @JsonProperty("invoice_prefix_id")
    private Integer invoicePrefixId;

    public TaxesDataTypeDTO getType() {
        return type;
    }

    public void setType(TaxesDataTypeDTO type) {
        this.type = type;
    }

    public Integer getProducerId() {
        return producerId;
    }

    public void setProducerId(Integer producerId) {
        this.producerId = producerId;
    }

    public Integer getInvoicePrefixId() {
        return invoicePrefixId;
    }

    public void setInvoicePrefixId(Integer invoicePrefixId) {
        this.invoicePrefixId = invoicePrefixId;
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
