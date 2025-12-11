package es.onebox.event.loyaltypoints.sessions.dto;

import java.io.Serializable;

public class SessionLoyaltyPointsConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private PointGainDTO pointGain;

    public PointGainDTO getPointGain() { return pointGain; }

    public void setPointGain(PointGainDTO pointGain) { this.pointGain = pointGain; }
}
