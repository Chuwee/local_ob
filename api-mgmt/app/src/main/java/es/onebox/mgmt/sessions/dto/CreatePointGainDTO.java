package es.onebox.mgmt.sessions.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class CreatePointGainDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1677075448169485883L;

    @Min(value = 0, message = "amount must be above 0")
    private Integer amount;
    @NotNull
    private CreateSessionPointsTypeDTO type;

    public Integer getAmount() { return amount; }

    public void setAmount(Integer amount) { this.amount = amount; }

    public CreateSessionPointsTypeDTO getType() { return type; }

    public void setType(CreateSessionPointsTypeDTO type) { this.type = type; }
}
