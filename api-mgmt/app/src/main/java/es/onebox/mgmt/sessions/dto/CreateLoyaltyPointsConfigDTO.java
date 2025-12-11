package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

import java.io.Serial;
import java.io.Serializable;

public class CreateLoyaltyPointsConfigDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1677075448169485883L;

    @Valid
    @JsonProperty("point_gain")
    private CreatePointGainDTO pointGain;

    public CreatePointGainDTO getPointGain() { return pointGain; }

    public void setPointGain(CreatePointGainDTO pointGain) { this.pointGain = pointGain; }
}
