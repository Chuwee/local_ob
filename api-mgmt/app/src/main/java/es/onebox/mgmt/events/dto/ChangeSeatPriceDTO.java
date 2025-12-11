package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class ChangeSeatPriceDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "type cannot be null")
    private ChangeSeatAmountTypeDTO type;

    @Valid
    private ChangeSeatRefundDTO refund;


    public ChangeSeatAmountTypeDTO getType() {
        return type;
    }

    public void setType(ChangeSeatAmountTypeDTO type) {
        this.type = type;
    }

    public ChangeSeatRefundDTO getRefund() {
        return refund;
    }

    public void setRefund(ChangeSeatRefundDTO refund) {
        this.refund = refund;
    }
}
