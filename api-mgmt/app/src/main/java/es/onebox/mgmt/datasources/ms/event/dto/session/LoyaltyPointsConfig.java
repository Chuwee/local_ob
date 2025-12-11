package es.onebox.mgmt.datasources.ms.event.dto.session;

import java.io.Serializable;

public class LoyaltyPointsConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private PointGain pointGain;

    public PointGain getPointGain() { return pointGain; }

    public void setPointGain(PointGain pointGain) { this.pointGain = pointGain; }
}
