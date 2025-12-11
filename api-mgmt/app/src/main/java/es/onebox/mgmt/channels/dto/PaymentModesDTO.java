package es.onebox.mgmt.channels.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class PaymentModesDTO {

    @JsonProperty("payment_modes")
    private List<IdNameDTO> paymentModes;

    public List<IdNameDTO> getPaymentModes() { return paymentModes; }

    public void setPaymentModes(List<IdNameDTO> paymentModes) { this.paymentModes = paymentModes; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
