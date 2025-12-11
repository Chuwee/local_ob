package es.onebox.event.sessions.dto;

import java.io.Serializable;

public class CreatePointGainDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer amount;
    private CreateSessionPointsTypeDTO type;

    public Integer getAmount() { return amount; }

    public void setAmount(Integer amount) { this.amount = amount; }

    public CreateSessionPointsTypeDTO getType() { return type; }

    public void setType(CreateSessionPointsTypeDTO type) { this.type = type; }
}
