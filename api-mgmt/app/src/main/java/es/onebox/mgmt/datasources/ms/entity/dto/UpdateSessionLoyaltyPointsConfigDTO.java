package es.onebox.mgmt.datasources.ms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.loyaltypoints.sessions.dto.PointGainDTO;
import jakarta.validation.Valid;

import java.io.Serial;
import java.io.Serializable;

public class UpdateSessionLoyaltyPointsConfigDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1677075448169485883L;

    @Valid
    @JsonProperty("point_gain")
    private PointGainDTO pointGain;

    public PointGainDTO getPointGain() { return pointGain; }

    public void setPointGain(PointGainDTO pointGain) { this.pointGain = pointGain; }
}
