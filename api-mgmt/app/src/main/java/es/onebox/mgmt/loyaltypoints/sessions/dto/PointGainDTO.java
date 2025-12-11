package es.onebox.mgmt.loyaltypoints.sessions.dto;

import es.onebox.mgmt.loyaltypoints.sessions.dto.enums.SessionPointsTypeDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class PointGainDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1677075448169485883L;

    @Min(value = 0, message = "amount must be above 0")
    private Integer amount;
    @NotNull
    private SessionPointsTypeDTO type;

    public Integer getAmount() { return amount; }

    public void setAmount(Integer amount) { this.amount = amount; }

    public SessionPointsTypeDTO getType() { return type; }

    public void setType(SessionPointsTypeDTO type) { this.type = type; }
}
