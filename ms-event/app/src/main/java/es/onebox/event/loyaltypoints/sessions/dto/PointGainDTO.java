package es.onebox.event.loyaltypoints.sessions.dto;

import es.onebox.event.loyaltypoints.sessions.dto.enums.SessionPointsTypeDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public class PointGainDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Min(value = 0, message = "amount must be above 0")
    private Integer amount;
    @NotNull
    private SessionPointsTypeDTO type;

    public Integer getAmount() { return amount; }

    public void setAmount(Integer amount) { this.amount = amount; }

    public SessionPointsTypeDTO getType() { return type; }

    public void setType(SessionPointsTypeDTO type) { this.type = type; }
}
