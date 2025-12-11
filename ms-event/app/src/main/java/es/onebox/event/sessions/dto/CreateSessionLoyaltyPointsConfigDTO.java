package es.onebox.event.sessions.dto;

import java.io.Serializable;

public class CreateSessionLoyaltyPointsConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private CreatePointGainDTO pointGain;

    public CreatePointGainDTO getPointGain() { return pointGain; }

    public void setPointGain(CreatePointGainDTO pointGain) { this.pointGain = pointGain; }
}
