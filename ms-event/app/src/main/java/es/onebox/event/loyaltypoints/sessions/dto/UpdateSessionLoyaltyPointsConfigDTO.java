package es.onebox.event.loyaltypoints.sessions.dto;

import jakarta.validation.Valid;
import java.io.Serializable;

public class UpdateSessionLoyaltyPointsConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Valid
    private PointGainDTO pointGain;

    public PointGainDTO getPointGain() { return pointGain; }

    public void setPointGain(PointGainDTO pointGain) { this.pointGain = pointGain; }
}
